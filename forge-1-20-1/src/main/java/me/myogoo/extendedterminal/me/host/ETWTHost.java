package me.myogoo.extendedterminal.me.host;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.InternalInventory;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.ANVIL_INVENTORY;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.SMITHING_INVENTORY;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.STONECUTTER_INVENTORY;

public class ETWTHost extends WCTMenuHost implements IViewCellStorage, IETTerminalHost {
    private static final String MODE_TAG = "mode";
    private static final String RECIPE_TAG = "stonecuttingRecipeId";
    private static final String CRAFTING_GRID_TAG = "craftingGrid";
    private static final String SMITHING_GRID_TAG = "smithingGrid";
    private static final String STONECUTTER_GRID_TAG = "stoneCutterGrid";
    private static final String ANVIL_GRID_TAG = "anvilInv";

    private AppEngInternalInventory craftingGrid;
    private AppEngInternalInventory smithingGrid;
    private AppEngInternalInventory stoneCutterGrid;
    private AppEngInternalInventory anvilInv;

    private ETTerminalMode mode;
    @Nullable
    private ResourceLocation stonecuttingRecipeId;
    @Nullable
    private ExperienceMath.ExperienceSource rememberedAnvilExperienceSource;

    public ETWTHost(Player player, @Nullable Integer inventorySlot, ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, itemStack, returnToMainMenu);
        ensureInventories();
        if (this.mode == null) {
            readFromNbt();
        }
    }

    private void ensureInventories() {
        if (this.craftingGrid == null) {
            this.craftingGrid = new AppEngInternalInventory(this, ETMenuType.ET_TERMINAL.getGridSize());
        }
        if (this.smithingGrid == null) {
            this.smithingGrid = new AppEngInternalInventory(this, 3);
        }
        if (this.stoneCutterGrid == null) {
            this.stoneCutterGrid = new AppEngInternalInventory(this, 1);
        }
        if (this.anvilInv == null) {
            this.anvilInv = new AppEngInternalInventory(this, 2);
        }
    }

    @Override
    protected void readFromNbt() {
        super.readFromNbt();
        ensureInventories();

        CompoundTag tag = getItemStack().getOrCreateTag();
        try {
            if (tag.contains(MODE_TAG, Tag.TAG_STRING)) {
                this.mode = ETTerminalMode.valueOf(tag.getString(MODE_TAG));
            } else {
                this.mode = ETTerminalMode.loadableValues().get(0);
            }
        } catch (IllegalArgumentException ignored) {
            this.mode = ETTerminalMode.loadableValues().get(0);
        }

        this.stonecuttingRecipeId = tag.contains(RECIPE_TAG, Tag.TAG_STRING)
                ? ResourceLocation.tryParse(tag.getString(RECIPE_TAG))
                : null;
        this.rememberedAnvilExperienceSource = readAnvilExperienceSource(tag);

        this.craftingGrid.readFromNBT(tag, CRAFTING_GRID_TAG);
        this.smithingGrid.readFromNBT(tag, SMITHING_GRID_TAG);
        this.stoneCutterGrid.readFromNBT(tag, STONECUTTER_GRID_TAG);
        this.anvilInv.readFromNBT(tag, ANVIL_GRID_TAG);
    }

    @Override
    public void saveChanges() {
        ensureInventories();
        super.saveChanges();

        CompoundTag tag = getItemStack().getOrCreateTag();
        tag.putString(MODE_TAG, this.mode.name());
        if (this.stonecuttingRecipeId != null) {
            tag.putString(RECIPE_TAG, this.stonecuttingRecipeId.toString());
        } else {
            tag.remove(RECIPE_TAG);
        }
        if (this.rememberedAnvilExperienceSource != null) {
            tag.putString("anvilExperienceSource", this.rememberedAnvilExperienceSource.name());
        } else {
            tag.remove("anvilExperienceSource");
        }

        this.craftingGrid.writeToNBT(tag, CRAFTING_GRID_TAG);
        this.smithingGrid.writeToNBT(tag, SMITHING_GRID_TAG);
        this.stoneCutterGrid.writeToNBT(tag, STONECUTTER_GRID_TAG);
        this.anvilInv.writeToNBT(tag, ANVIL_GRID_TAG);
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        ensureInventories();
        if (id.equals(ETMenuType.ET_TERMINAL.getCraftingInventory())) {
            return this.craftingGrid;
        } else if (id.equals(SMITHING_INVENTORY)) {
            return this.smithingGrid;
        } else if (id.equals(STONECUTTER_INVENTORY)) {
            return this.stoneCutterGrid;
        } else if (id.equals(ANVIL_INVENTORY)) {
            return this.anvilInv;
        }
        return super.getSubInventory(id);
    }

    @Override
    public ETTerminalMode getMode() {
        return this.mode;
    }

    @Override
    public void setMode(ETTerminalMode mode) {
        this.mode = mode;
        saveChanges();
    }

    @Override
    public @Nullable ResourceLocation getStoneCutterRecipeId() {
        return this.stonecuttingRecipeId;
    }

    @Override
    public void setStoneCutterRecipeId(@Nullable ResourceLocation stonecuttingRecipeId) {
        this.stonecuttingRecipeId = stonecuttingRecipeId;
        saveChanges();
    }

    @Override
    public @Nullable ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() {
        return this.rememberedAnvilExperienceSource;
    }

    @Override
    public void setRememberedAnvilExperienceSource(@Nullable ExperienceMath.ExperienceSource source) {
        this.rememberedAnvilExperienceSource = source;
        saveChanges();
    }

    private static @Nullable ExperienceMath.ExperienceSource readAnvilExperienceSource(CompoundTag data) {
        if (!data.contains("anvilExperienceSource", Tag.TAG_STRING)) {
            return null;
        }
        try {
            return ExperienceMath.ExperienceSource.valueOf(data.getString("anvilExperienceSource"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
