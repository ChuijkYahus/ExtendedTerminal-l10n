package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.integration.itemList.jei.ETJeiPlugin;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.MyoBaseClickableArea;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UnitedTerminalGuiHandler implements IGuiContainerHandler<UnitedTerminalScreen<UnitedTerminalMenu>> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(UnitedTerminalScreen<UnitedTerminalMenu> screen) {
        return screen.getExclusionZones();
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(UnitedTerminalScreen<UnitedTerminalMenu> screen, double guiMouseX, double guiMouseY) {
        var menu = screen.getMenu();

        List<IGuiClickableArea>  guiClickableAreas = new ArrayList<>();
        for(var type: MyoRecipeType.values()) {
            if(type.isActive()) {
                guiClickableAreas.add(new UnitedGuiClickableArea(type, menu));
            }
        }
        return guiClickableAreas;
    }


    private static class UnitedGuiClickableArea extends MyoBaseClickableArea {
        private final UnitedTerminalMenu menu;
        private final MyoRecipeType targetRecipeType;
        public UnitedGuiClickableArea(MyoRecipeType recipeType, UnitedTerminalMenu menu) {
            this.targetRecipeType = recipeType;
            this.menu = menu;
        }

        @Override
        public @NotNull Rect2i getArea() {
            if(menu.getSelectedRecipeType() == targetRecipeType) {
                var slot = menu.getSlots(ETSlotSemantics.EXTENDED_CRAFTING_UNIVERSAL_RESULT).getFirst();
                return Direction.LEFT.getArea(slot);
            }
            return DummyRect2i;
        }

        @Override
        public void onClick(@NotNull IFocusFactory focusFactory, @NotNull IRecipesGui recipesGui) {
            if(targetRecipeType == menu.getSelectedRecipeType()) {
                recipesGui.showTypes(List.of(recipeTypeFor(targetRecipeType)));
            }
        }

        @Override
        public boolean isTooltipEnabled() {
            return targetRecipeType == menu.getSelectedRecipeType();
        }

        private static RecipeType<?> recipeTypeFor(MyoRecipeType type) {
            return ETJeiPlugin.recipeTypeFor(ResourceLocation.fromNamespaceAndPath(type.modid(), type.jeiUid()));
        }
    }
}
