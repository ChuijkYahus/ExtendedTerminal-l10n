package me.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.myotus.client.gui.widgets.button.MyoCycleOverlayButton;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class UnitedTerminalScreen<M extends UnitedTerminalMenu> extends ETTerminalBaseScreen<Recipe<RecipeInput>, M> {
    private final MyoCycleOverlayButton cycleRecipeKindButton;

    public UnitedTerminalScreen(M menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.cycleRecipeKindButton = new MyoCycleOverlayButton(
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
        return Component.translatable(this.getMenu().getSelectedRecipeType().getTranslateKey());
    }

    private Item selectedRecipeKindItem() {
        var recipeType = this.getMenu().getSelectedRecipeType();
        var item = BuiltInRegistries.ITEM.get(recipeType.getIcon());
        return item == Items.AIR ? Items.CRAFTING_TABLE : item;
    }
}
