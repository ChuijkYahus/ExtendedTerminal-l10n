package me.myogoo.extendedterminal.me.host;

import appeng.api.inventories.InternalInventory;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.SupplierInternalInventory;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static me.myogoo.extendedterminal.init.ETDataComponent.*;

public class UnitedWTHost extends WCTMenuHost implements IUnitedTerminalHost {
    private static final String REMEMBER_RECIPE_TYPE = "rememberUnitedRecipeType";
    private static final String SELECTED_RECIPE_TYPE = "selectedUnitedRecipeType";

    private final SupplierInternalInventory<InternalInventory> craftingGrid;

    public UnitedWTHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory<>(new StackDependentSupplier(this::getItemStack,
                stack -> createInv(player, (ItemStack) stack, CRAFTING_INV,
                        ETMenuType.UNITED_TERMINAL.getGridSize())));
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ETMenuType.UNITED_TERMINAL.getCraftingInventory())) {
            return craftingGrid;
        }
        return super.getSubInventory(id);
    }

    @Override
    public boolean shouldRememberRecipeType() {
        var tag = getHostTag();
        return !tag.contains(REMEMBER_RECIPE_TYPE, Tag.TAG_BYTE) || tag.getBoolean(REMEMBER_RECIPE_TYPE);
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        var tag = getHostTag();
        tag.putBoolean(REMEMBER_RECIPE_TYPE, remember);
        if (!remember) {
            tag.remove(SELECTED_RECIPE_TYPE);
        }
        saveHostTag(tag);
    }

    @Override
    public @Nullable MyoRecipeType getLastRecipeType() {
        var tag = getHostTag();
        if (!tag.contains(SELECTED_RECIPE_TYPE, Tag.TAG_STRING)) {
            return null;
        }
        return MyoRecipeType.valueOf(tag.getString(SELECTED_RECIPE_TYPE));
    }

    @Override
    public void setLastRecipeType(@Nullable MyoRecipeType recipeType) {
        var tag = getHostTag();
        if (recipeType == null) {
            tag.remove(SELECTED_RECIPE_TYPE);
        } else {
            tag.putString(SELECTED_RECIPE_TYPE, recipeType.name());
        }
        saveHostTag(tag);
    }

    private CompoundTag getHostTag() {
        return this.getItemStack().getOrDefault(ET_TERMINAL_HOST_TAG, new CompoundTag()).copy();
    }

    private void saveHostTag(CompoundTag tag) {
        this.getItemStack().set(ET_TERMINAL_HOST_TAG, tag);
    }
}
