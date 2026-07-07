package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.MyoBaseClickableArea;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ETTerminalGuiHandler implements IGuiContainerHandler<ETTerminalScreen<?>> {

    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(ETTerminalScreen<?> screen) {
        return screen.getExclusionZones();
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(ETTerminalScreen<?> screen, double guiMouseX, double guiMouseY) {
        var menu = screen.getMenu();

        return List.of(
                new ETGuiClickableArea(ETTerminalMode.CRAFTING, menu),
                new ETGuiClickableArea(ETTerminalMode.SMITHING, menu),
                new ETGuiClickableArea(ETTerminalMode.STONECUTTING, menu),
                new ETGuiClickableArea(ETTerminalMode.ANVIL, menu)
        );
    }

    private static class ETGuiClickableArea extends MyoBaseClickableArea {
        private final ETTerminalMenu menu;
        private final ETTerminalMode targetMode;

        public ETGuiClickableArea(ETTerminalMode mode, ETTerminalMenu menu) {
            this.targetMode = mode;
            this.menu = menu;
        }

        @Override
        public @NotNull Rect2i getArea() {
            if (targetMode == menu.getMode()) {
                var slot = menu.getSlots(targetMode.getOutputSlotSemantics()).getFirst();
                if (targetMode == ETTerminalMode.STONECUTTING) {
                    return Direction.UP.getArea(slot);
                }
                return Direction.LEFT.getArea(slot);
            } else {
                return DummyRect2i;
            }
        }

        @Override
        public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
            if (targetMode == menu.getMode()) {
                recipesGui.showTypes(List.of(recipeTypeFor(targetMode)));
            }
        }

        @Override
        public boolean isTooltipEnabled() {
            return targetMode == menu.getMode();
        }

        private static RecipeType<?> recipeTypeFor(ETTerminalMode mode) {
            return switch (mode) {
                case CRAFTING -> RecipeTypes.CRAFTING;
                case SMITHING -> RecipeTypes.SMITHING;
                case STONECUTTING -> RecipeTypes.STONECUTTING;
                case ANVIL -> RecipeTypes.ANVIL;
            };
        }
    }
}
