package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.core.network.ServerboundPacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.items.storage.ViewCellItem;
import appeng.me.storage.NullInventory;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.recipe.ETRecipeTransferPlanner;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FillTableCraftingGridFromRecipePacket extends FillRecipeBasePacket implements ServerboundPacket {
    public static final int NOT_SET_RECIPE_SIZE = -1;

    public static final StreamCodec<RegistryFriendlyByteBuf, FillTableCraftingGridFromRecipePacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    FillTableCraftingGridFromRecipePacket::write,
                    FillTableCraftingGridFromRecipePacket::decode);

    public static final CustomPacketPayload.Type<FillTableCraftingGridFromRecipePacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_table_crafting_grid_from_recipe"));

    protected final List<ItemStack> ingredientTemplates;
    protected final boolean craftMissing;
    protected final int recipeWidth;
    protected final int recipeHeight;
    protected final @Nullable ResourceLocation recipeId;


    @Override
    public CustomPacketPayload.@NotNull Type<FillTableCraftingGridFromRecipePacket> type() {
        return TYPE;
    }

    public FillTableCraftingGridFromRecipePacket(
            @Nullable ResourceLocation recipeId,
            List<ItemStack> ingredientTemplates,
            boolean craftMissing,
            int recipeWidth,
            int recipeHeight
    ) {
        this.recipeId = recipeId;
        this.ingredientTemplates = NonNullList.copyOf(ingredientTemplates.stream().map(ItemStack::copy).toList());
        this.craftMissing = craftMissing;
        this.recipeWidth = recipeWidth;
        this.recipeHeight = recipeHeight;
    }

    public void write(RegistryFriendlyByteBuf stream) {
        if (recipeId != null) {
            stream.writeBoolean(true);
            stream.writeResourceLocation(recipeId);
        } else {
            stream.writeBoolean(false);
        }

        stream.writeInt(ingredientTemplates.size());
        for (var ingredientTemplate : ingredientTemplates) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(stream, ingredientTemplate);
        }
        stream.writeBoolean(craftMissing);
        stream.writeInt(recipeWidth);
        stream.writeInt(recipeHeight);
    }

    public static FillTableCraftingGridFromRecipePacket decode(RegistryFriendlyByteBuf stream) {
        ResourceLocation recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceLocation();
        }
        var ingredientTemplates = NonNullList.withSize(stream.readInt(), ItemStack.EMPTY);
        ingredientTemplates.replaceAll(ignored -> ItemStack.OPTIONAL_STREAM_CODEC.decode(stream));
        var craftMissing = stream.readBoolean();
        int recipeWidth = stream.readInt();
        int recipeHeight = stream.readInt();

        return new FillTableCraftingGridFromRecipePacket(recipeId, ingredientTemplates, craftMissing, recipeWidth, recipeHeight);
    }


    @Override
    protected NonNullList<Ingredient> getDesiredIngredients(Player player) {
        Recipe<?> recipe = null;
        if (recipeId != null) {
            recipe = player.level().getRecipeManager().byKey(this.recipeId)
                    .map(holder -> (Recipe<?>) holder.value())
                    .orElse(null);
        }
        return ETRecipeTransferPlanner.desiredIngredients(recipe, ingredientTemplates, recipeWidth, recipeHeight);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        // Setup and verification
        var menu = player.containerMenu;
        if (!(menu instanceof ICraftingGridMenu cct)) {
            // Server might have closed the menu before the client-packet is processed. This is not an error.
            return;
        }

        var energy = cct.getEnergySource();
        @Nullable
        ICraftingService craftingService;
        @Nullable
        IStorageService storageService;
        MEStorage networkStorage;
        KeyCounter cachedStorage;

        @Nullable
        var node = cct.getGridNode();
        if (node != null && cct.getLinkStatus().connected()) {
            craftingService = node.getGrid().getCraftingService();
            storageService = node.getGrid().getStorageService();
            networkStorage = storageService.getInventory();
            cachedStorage = storageService.getCachedInventory();
        } else {
            craftingService = null;
            storageService = null;
            networkStorage = NullInventory.of();
            cachedStorage = new KeyCounter();
        }

        var craftMatrix = cct.getCraftingMatrix();
        // We'll try to use the best possible ingredients based on what's available in the network
        var filter = ViewCellItem.createItemFilter(cct.getViewCells());
        var ingredients = getDesiredIngredients(player);

        // Prepare to autocraft some stuff
        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        // Handle each slot
        for (var x = 0; x < craftMatrix.size(); x++) {
            var currentItem = craftMatrix.getStackInSlot(x);
            var ingredient = ingredients.get(x);

            // Move out items blocking the grid
            if (!currentItem.isEmpty()) {
                // Put away old item, if not correct
                if (ingredient.test(currentItem)) {
                    // Grid already has an item that matches the ingredient
                    continue;
                } else {
                    var in = AEItemKey.of(currentItem);
                    var inserted = StorageHelper.poweredInsert(energy, networkStorage, in, currentItem.getCount(),
                            cct.getActionSource());
                    if (inserted > 0) {
                        touchedGridStorage = true;
                    }
                    if (inserted < currentItem.getCount()) {
                        currentItem = currentItem.copy();
                        currentItem.shrink((int) inserted);
                    } else {
                        currentItem = ItemStack.EMPTY;
                    }

                    // If more is remaining, try moving it to the player inventory
                    player.getInventory().add(currentItem);

                    craftMatrix.setItemDirect(x, currentItem.isEmpty() ? ItemStack.EMPTY : currentItem);
                }
            }

            if (ingredient.isEmpty()) {
                continue;
            }

            // Try to find the best item for this slot. Sort by the amount available in the last tick,
            // then try to extract from most to least available item until 1 can be extracted.
            if (currentItem.isEmpty()) {
                var request = findBestMatchingItemStack(ingredient, filter, cachedStorage);
                for (var what : request) {
                    var extracted = StorageHelper.poweredExtraction(energy, networkStorage, what, 1,
                            cct.getActionSource());
                    if (extracted > 0) {
                        touchedGridStorage = true;
                        currentItem = what.toStack(Ints.saturatedCast(extracted));
                        break;
                    }
                }
            }

            // If still nothing, try taking it from the player inventory
            if (currentItem.isEmpty()) {
                currentItem = takeIngredientFromPlayer(cct, player, ingredient);
            }
            craftMatrix.setItemDirect(x, currentItem);

            // If we couldn't find the item, schedule its autocrafting
            if (currentItem.isEmpty() && craftMissing && craftingService != null) {
                int slot = x;
                findCraftableKey(ingredient, craftingService).ifPresent(key -> {
                    toAutoCraft.computeIfAbsent(key, k -> new IntArrayList()).add(slot);
                });
            }
        }

        menu.slotsChanged(craftMatrix.toContainer());

        if (!toAutoCraft.isEmpty()) {
            // Invalidate the grid storage cache if we modified it. The crafting plan will use
            // the outdated cached inventory otherwise.
            if (touchedGridStorage) {
                storageService.invalidateCache();
            }

            // This must be the last call since it changes the menu!
            var stacks = toAutoCraft.entrySet().stream()
                    .map(e -> new ICraftingGridMenu.AutoCraftEntry(e.getKey(), e.getValue())).toList();
            cct.startAutoCrafting(stacks);
        }
    }
}
