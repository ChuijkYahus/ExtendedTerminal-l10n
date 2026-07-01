package me.myogoo.extendedterminal.me.host;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.SupplierInternalInventory;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
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

import static me.myogoo.extendedterminal.init.ETDataComponent.*;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.*;

public class ETWTHost extends WCTMenuHost implements IViewCellStorage, IETTerminalHost {
    private final SupplierInternalInventory<InternalInventory> craftingGrid;
    private final SupplierInternalInventory<InternalInventory> smithingGrid;
    private final SupplierInternalInventory<InternalInventory> stoneCutterGrid;
    private final SupplierInternalInventory<InternalInventory> anvilInv;

    private ETTerminalMode mode;
    private @Nullable ResourceLocation stonecuttingRecipeId = null;
    private @Nullable ExperienceMath.ExperienceSource rememberedAnvilExperienceSource = null;

    public ETWTHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, CRAFTING_INV, 9)));
        this.smithingGrid = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, SMITHING_INV, 3)));
        this.stoneCutterGrid = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, STONECUTTER_INV, 1)));
        this.anvilInv = new SupplierInternalInventory(new StackDependentSupplier(this::getItemStack, (stack) -> createInv(player,(ItemStack) stack, ANVIL_INV, 2)));

        var tag = this.getItemStack().getOrDefault(ET_TERMINAL_HOST_TAG, new CompoundTag());
        if(tag.contains("mode", Tag.TAG_STRING))
            this.mode = ETTerminalMode.valueOf(tag.getString("mode"));
        else
            this.mode = ETTerminalMode.loadableValues().getFirst();

        if(tag.contains("stonecuttingRecipeId", Tag.TAG_STRING))
            this.stonecuttingRecipeId = ResourceLocation.tryParse(tag.getString("stonecuttingRecipeId"));
        this.rememberedAnvilExperienceSource = readAnvilExperienceSource(tag);
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ETMenuType.ET_TERMINAL.getCraftingInventory())) {
            return craftingGrid;
        } else if (id.equals(SmithingInventory)) {
            return smithingGrid;
        } else if (id.equals(StoneCutterInventory)) {
            return stoneCutterGrid;
        } else if (id.equals(AnvilInventory)) {
            return anvilInv;
        }
        return super.getSubInventory(id);
    }

    @Override
    public ETTerminalMode getMode() {
        return mode;
    }

    @Override
    public void setMode(ETTerminalMode mode) {
        this.mode = mode;
        markForSave();
    }

    @Override
    public @Nullable ResourceLocation getStoneCutterRecipeId() {
        return stonecuttingRecipeId;
    }

    @Override
    public void setStoneCutterRecipeId(@Nullable ResourceLocation stonecuttingRecipeId) {
        this.stonecuttingRecipeId = stonecuttingRecipeId;
        markForSave();
    }

    @Override
    public @Nullable ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() {
        return this.rememberedAnvilExperienceSource;
    }

    @Override
    public void setRememberedAnvilExperienceSource(@Nullable ExperienceMath.ExperienceSource source) {
        this.rememberedAnvilExperienceSource = source;
        markForSave();
    }

    public void markForSave() {
        CompoundTag tag = (CompoundTag)this.getItemStack().getOrDefault(ET_TERMINAL_HOST_TAG, new CompoundTag());
        tag.putString("mode", this.mode.name());
        if(this.stonecuttingRecipeId != null) {
            tag.putString("stonecuttingRecipeId", this.stonecuttingRecipeId.toString());
        }
        if(this.rememberedAnvilExperienceSource != null) {
            tag.putString("anvilExperienceSource", this.rememberedAnvilExperienceSource.name());
        } else {
            tag.remove("anvilExperienceSource");
        }
        this.getItemStack().set(ET_TERMINAL_HOST_TAG, tag);
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
