package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ETTerminalGuiHandler implements IGuiContainerHandler<ETTerminalScreen<?>> {

    @Override
    public List<Rect2i> getGuiExtraAreas(ETTerminalScreen screen) {
        return screen.getExclusionZones();
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(ETTerminalScreen<?> screen, double guiMouseX, double guiMouseY) {
        var menu = screen.getMenu();
        var mode = menu.getMode();

        return List.of(
                new ETGuiClickableArea(ETTerminalMode.CRAFTING, menu),
                new ETGuiClickableArea(ETTerminalMode.SMITHING, menu),
                new ETGuiClickableArea(ETTerminalMode.STONECUTTING, menu),
                new ETGuiClickableArea(ETTerminalMode.ANVIL, menu)
        );
    }

    private static class ETGuiClickableArea implements IGuiClickableArea {
        private final static Rect2i DummyRect2i = new Rect2i(0, 0, 0, 0);
        private static final List<RecipeType<?>> DEFAULT_RECIPE_TYPES = List.of(
                RecipeTypes.CRAFTING,
                RecipeTypes.SMITHING,
                RecipeTypes.STONECUTTING,
                RecipeTypes.ANVIL
        );

        private final ArrayList<RecipeType<?>> recipeTypes;
        private final ETTerminalMenu menu;
        private final ETTerminalMode targetMode;
        private final SelectedRecipePrimaryComparator comparator;

        public ETGuiClickableArea(ETTerminalMode mode, ETTerminalMenu menu) {
            var outputSlotPos = menu.getSlots(ETSlotSemantics.ANVIL_RESULT).getFirst();
            this.targetMode = mode;
            this.menu = menu;
            this.recipeTypes = new ArrayList<>(DEFAULT_RECIPE_TYPES);
            this.comparator = new SelectedRecipePrimaryComparator(mode);
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
                recipeTypes.sort(comparator);
                recipesGui.showTypes(recipeTypes);
            }
        }

        @Override
        public boolean isTooltipEnabled() {
            return  targetMode == menu.getMode();
        }

        enum Direction {
            UP, DOWN, LEFT, RIGHT;

            Rect2i getArea(Slot slot) {
                return switch (this) {
                    case UP -> new Rect2i(slot.x, slot.y + 24, 24, 24);
                    case DOWN -> new Rect2i(slot.x, slot.y - 24, 24, 24);
                    case LEFT -> new Rect2i(slot.x - 40, slot.y, 24, 24);
                    case RIGHT -> new Rect2i(slot.x + 40, slot.y, 24, 24);
                };
            }
        }

        record SelectedRecipePrimaryComparator(ETTerminalMode mode) implements Comparator<RecipeType<?>> {
            @Override
            public int compare(RecipeType<?> o1, RecipeType<?> o2) {
                var selected = selectedRecipeType();

                if (selected == null) {
                    return 0;
                }

                boolean firstSelected = o1 == selected;
                boolean secondSelected = o2 == selected;

                if (firstSelected == secondSelected) {
                    return 0;
                }

                return firstSelected ? -1 : 1;
            }

            private RecipeType<?> selectedRecipeType() {
                return switch (this.mode) {
                    case CRAFTING -> RecipeTypes.CRAFTING;
                    case STONECUTTING -> RecipeTypes.STONECUTTING;
                    case SMITHING -> RecipeTypes.SMITHING;
                    case ANVIL -> RecipeTypes.ANVIL;
                };
            }

        }
    }
}
