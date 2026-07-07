package me.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.myotus.client.gui.widgets.button.MyoCycleOverlayButton;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class UnitedTerminalScreen<M extends UnitedTerminalMenu> extends ETTerminalBaseScreen<Recipe<RecipeInput>, M> {
    private final MyoCycleOverlayButton cycleRecipeTypeButton;

    public UnitedTerminalScreen(M menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.cycleRecipeTypeButton = new MyoCycleOverlayButton(
                (Runnable) this::cycleRecipeType,
                (Runnable) this::cycleRecipeTypeBackwards,
                this::selectedRecipeTypeItem,
                () -> List.of(selectedRecipeTypeTooltip()));
        widgets.add("cycleRecipeType", this.cycleRecipeTypeButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
    }

    private void cycleRecipeType() {
        this.getMenu().selectNextRecipeType();
    }

    private void cycleRecipeTypeBackwards() {
        this.getMenu().selectPreviousRecipeType();
    }

    private Component selectedRecipeTypeTooltip() {
        return Component.translatable(this.getMenu().getSelectedRecipeType().getTranslateKey());
    }

    private Item selectedRecipeTypeItem() {
        var recipeType = this.getMenu().getSelectedRecipeType();
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(recipeType.modid(), recipeType.blockId()));
        return item == Items.AIR ? Items.CRAFTING_TABLE : item;
    }
}
