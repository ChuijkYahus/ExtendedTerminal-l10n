package me.myogoo.extendedterminal.client.screen.extendedcrafting.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.Icon;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import net.minecraft.world.item.crafting.Recipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.myotus.client.gui.widgets.button.MyoCycleButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class UnitedWTScreen extends ETTerminalBaseScreen<Recipe<?>, UnitedWTMenu> {
    private final MyoCycleButton cycleRecipeKindButton;

    public UnitedWTScreen(UnitedWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.cycleRecipeKindButton = new MyoCycleButton(
                () -> Icon.ARROW_RIGHT,
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
        return selectedRecipeKindLabel();
    }

    private Component selectedRecipeKindLabel() {
        return Component.translatable(this.getMenu().getSelectedRecipeKind().labelKey());
    }

    private Item selectedRecipeKindItem() {
        var kind = this.getMenu().getSelectedRecipeKind();
        return icon(kind.iconNamespace(), kind.iconPath());
    }

    private Item icon(String namespace, String path) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
        return item == Items.AIR ? Items.CRAFTING_TABLE : item;
    }
}
