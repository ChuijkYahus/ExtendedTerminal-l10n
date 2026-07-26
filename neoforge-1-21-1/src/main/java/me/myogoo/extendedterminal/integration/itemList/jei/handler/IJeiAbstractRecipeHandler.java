package me.myogoo.extendedterminal.integration.itemList.jei.handler;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.core.localization.ItemModText;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.me.items.CraftingTermMenu;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

    import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IJeiAbstractRecipeHandler {
    int BLUE_SLOT_HIGHLIGHT_COLOR = TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;
    int RED_SLOT_HIGHLIGHT_COLOR = TransferHelper.RED_SLOT_HIGHLIGHT_COLOR;

    static Map<Integer, IRecipeSlotView> getInputSlotViewsByKey(List<IRecipeSlotView> inputSlots,
                                                                Set<Integer> inputSlotKeys) {
        if (inputSlotKeys.isEmpty()) {
            var result = new HashMap<Integer, IRecipeSlotView>(inputSlots.size());
            for (int i = 0; i < inputSlots.size(); i++) {
                result.put(i, inputSlots.get(i));
            }
            return result;
        }

        var sortedKeys = inputSlotKeys.stream().sorted().toList();
        var nonEmptyInputSlots = inputSlots.stream()
                .filter(slotView -> !slotView.isEmpty())
                .toList();
        var result = new HashMap<Integer, IRecipeSlotView>(sortedKeys.size());
        for (int i = 0; i < sortedKeys.size() && i < nonEmptyInputSlots.size(); i++) {
            result.put(sortedKeys.get(i), nonEmptyInputSlots.get(i));
        }
        return result;
    }

    abstract class Result implements IRecipeTransferError {
        @Override
        public @NotNull Type getType() {
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

        public static IRecipeTransferError createNotSupportedError(IRecipeTransferHandlerHelper helper) {
            return helper.createUserErrorWithTooltip(Component.translatable(ETTranslationKey.JEI.JEI_NOT_SUPPORTED_ERROR.key()));
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

            public PartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots, int color, boolean craftMissing,
                                      Set<Integer> inputSlotKeys) {
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
                    for (var entry : IJeiAbstractRecipeHandler
                            .getInputSlotViewsByKey(slotViews, inputSlotKeys)
                            .entrySet()) {
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
                tooltip.addAll(TransferHelper.createCraftingTooltip(missingSlots, craftMissing, false));
            }
        }
    }
}
