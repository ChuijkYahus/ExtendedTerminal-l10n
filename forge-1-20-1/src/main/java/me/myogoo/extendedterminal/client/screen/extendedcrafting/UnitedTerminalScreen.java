package me.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.Icon;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import net.minecraft.world.item.crafting.Recipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.myotus.client.gui.widgets.button.MyoCycleButton;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class UnitedTerminalScreen extends ETTerminalBaseScreen<Recipe<?>, UnitedTerminalMenu> {
    private MyoCycleButton cycleRecipeKindButton;

    public UnitedTerminalScreen(UnitedTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.cycleRecipeKindButton = new MyoCycleButton(
                (Runnable) this::cycleRecipeKind,
                (Runnable) this::cycleRecipeKindBackwards,
                this::selectedRecipeKindItem,
                () -> List.of(selectedRecipeKindTooltip()));
        widgets.add("cycleRecipeKind", this.cycleRecipeKindButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.cycleRecipeKindButton.setVisibility(this.getMenu().hasMultipleRecipeKinds());
    }

    private void cycleRecipeKind() {
        this.getMenu().selectNextRecipeKind();
    }

    private void cycleRecipeKindBackwards() {
        this.getMenu().selectPreviousRecipeKind();
    }

    private Component selectedRecipeKindTooltip() {
        return Component.translatable(this.getMenu().getSelectedRecipeKind().labelKey());
    }

    private Item selectedRecipeKindItem() {
        var kind = this.getMenu().getSelectedRecipeKind();
        return icon(kind.iconNamespace(), kind.iconPath());
    }

    private Item icon(String namespace, String path) {
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(namespace, path));
        return item == Items.AIR ? Items.CRAFTING_TABLE : item;
    }
}
