package me.myogoo.extendedterminal.integration.jei.handler;

import appeng.core.localization.ItemModText;
import appeng.integration.modules.jeirei.TransferHelper;
import appeng.menu.me.items.CraftingTermMenu;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.client.ae2helpers.ETAutoCraftingWatcher;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket;
import me.myogoo.myotus.api.MyotusAPI;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static appeng.integration.modules.jeirei.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;
import static appeng.integration.modules.jeirei.TransferHelper.RED_SLOT_HIGHLIGHT_COLOR;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.findGoodTemplateItems;
import static me.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public abstract class AbstractTableRecipeHandler<T extends ETTerminalBaseMenu<?>, R extends Recipe<?>> implements IRecipeTransferHandler<T, R> {
    private final Class<T> containerClass;
    private final MenuType<T> menuType;
    private final RecipeType<R> recipeType;

    public AbstractTableRecipeHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<R> recipeType) {
        this.containerClass = containerClass;
        this.menuType = menuType;
        this.recipeType = recipeType;
    }

    @Override
    public Class<? extends T> getContainerClass() {
        return containerClass;
    }

    @Override
    public Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public RecipeType<R> getRecipeType() {
        return recipeType;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter<?> recipe);

    protected Set<Integer> getDisplayedInputSlotKeys(T menu, Map<Integer, Ingredient> slotToIngredientMap) {
        return menu instanceof UnitedTerminalMenu ? slotToIngredientMap.keySet() : Set.of();
    }

    protected List<IRecipeSlotView> getMissingSlotViews(T menu, List<IRecipeSlotView> inputSlots,
                                                        Set<Integer> missingSlots, Set<Integer> inputSlotKeys) {
        if (menu instanceof UnitedTerminalMenu && !inputSlotKeys.isEmpty()) {
            var inputSlotsByKey = getRecipeInputSlots(inputSlots, inputSlotKeys);
            return missingSlots.stream()
                    .map(inputSlotsByKey::get)
                    .filter(slotView -> slotView != null)
                    .toList();
        }

        return missingSlots.stream()
                .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                .filter(slotView -> slotView != null)
                .toList();
    }

    private static Map<Integer, IRecipeSlotView> getRecipeInputSlots(List<IRecipeSlotView> inputSlots,
                                                                     Set<Integer> inputSlotKeys) {
        var sortedKeys = inputSlotKeys.stream().sorted().toList();
        var displayedSlots = inputSlots.stream()
                .filter(slot -> !slot.isEmpty())
                .toList();

        var result = new HashMap<Integer, IRecipeSlotView>(sortedKeys.size());
        for (int i = 0; i < sortedKeys.size() && i < displayedSlots.size(); i++) {
            result.put(sortedKeys.get(i), displayedSlots.get(i));
        }
        return result;
    }

    protected void performTransfer(T menu, @Nullable ITableRecipeAdapter<?> recipe, boolean craftMissing) {
        performTransfer(menu, recipe, craftMissing, null);
    }

    protected void performTransfer(T menu, @Nullable ITableRecipeAdapter<?> recipe, boolean craftMissing,
                                   UnitedTerminalMenu.UnitedRecipeKind unitedRecipeKind) {
        var templateItems = findGoodTemplateItems(recipe, menu);
        int recipeWidth = NOT_SET_RECIPE_SIZE;
        int recipeHeight = NOT_SET_RECIPE_SIZE;
        if (recipe instanceof IShapedTableRecipeAdapter<?> shapedRecipe) {
            recipeWidth = shapedRecipe.width();
            recipeHeight = shapedRecipe.height();
        }
        ETAutoCraftingWatcher.INSTANCE.preparePending(menu, getGuiSlotToIngredientMap(menu, recipe), craftMissing);
        var message = new ETFillCraftingGridFromRecipePacket(recipe.recipeId(), templateItems, craftMissing, recipeWidth, recipeHeight, unitedRecipeKind);
        MyotusAPI.network().sendToServer(message);
    }

    protected static abstract class Result implements IRecipeTransferError {
        @Override
        public Type getType() {
            return Type.COSMETIC;
        }

        @Nullable
        public static IRecipeTransferError createSuccessful() {
            return null;
        }

        public static IRecipeTransferError createNotApplicable(IRecipeTransferHandlerHelper helper) {
            return helper.createInternalError();
        }

        public static IRecipeTransferError createInCompatibleError(IRecipeTransferHandlerHelper helper) {
            return helper.createUserErrorWithTooltip(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        public static IRecipeTransferError createRecipeToLargeError(IRecipeTransferHandlerHelper helper) {
            return helper.createUserErrorWithTooltip(ItemModText.RECIPE_TOO_LARGE.text());
        }

        public static final class PartiallyCraftable extends Result {
            private final CraftingTermMenu.MissingIngredientSlots missingSlots;
            private final boolean craftMissing;
            private final int color;
            private final Set<Integer> inputSlotKeys;

            public PartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots, int color, boolean craftMissing) {
                this(missingSlots, color, craftMissing, Set.of());
            }

            public PartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots, int color,
                                      boolean craftMissing, Set<Integer> inputSlotKeys) {
                this.missingSlots = missingSlots;
                this.craftMissing = craftMissing;
                this.color = color;
                this.inputSlotKeys = Set.copyOf(inputSlotKeys);
            }

            @Override
            public int getButtonHighlightColor() {
                return color;
            }

            @Override
            public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots, int recipeX,
                                  int recipeY) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(recipeX, recipeY, 0);

                // 1) draw slot highlights
                var slotViews = slots.getSlotViews(RecipeIngredientRole.INPUT);
                if (inputSlotKeys.isEmpty()) {
                    for (int i = 0; i < slotViews.size(); i++) {
                        var slotView = slotViews.get(i);
                        boolean missing = missingSlots.missingSlots().contains(i);
                        boolean craftable = missingSlots.craftableSlots().contains(i);
                        if (missing || craftable) {
                            slotView.drawHighlight(
                                    guiGraphics,
                                    missing ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR);
                        }
                    }
                } else {
                    for (var entry : getRecipeInputSlots(slotViews, inputSlotKeys).entrySet()) {
                        boolean missing = missingSlots.missingSlots().contains(entry.getKey());
                        boolean craftable = missingSlots.craftableSlots().contains(entry.getKey());
                        if (missing || craftable) {
                            entry.getValue().drawHighlight(
                                    guiGraphics,
                                    missing ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR);
                        }
                    }
                }

                poseStack.popPose();
            }

            @Override
            public int getMissingCountHint() {
                return missingSlots.missingSlots().size();
            }

            @Override
            public void getTooltip(ITooltipBuilder tooltip) {
                tooltip.addAll(TransferHelper.createCraftingTooltip(missingSlots, craftMissing));
            }
        }
    }
}
