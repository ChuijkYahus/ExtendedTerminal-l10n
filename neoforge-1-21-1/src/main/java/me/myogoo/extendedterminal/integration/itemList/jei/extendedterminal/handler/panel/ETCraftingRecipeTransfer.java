package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel;

import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.ETTerminalBaseRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;

public class ETCraftingRecipeTransfer<T extends ETTerminalMenu> extends ETTerminalBaseRecipeTransfer<T, RecipeHolder<CraftingRecipe>> {
    public ETCraftingRecipeTransfer(MenuType<T> menuType, Class<T> containerClass, IRecipeTransferHandlerHelper helper) {
        super(menuType, containerClass, helper);
    }


    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, RecipeHolder<CraftingRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        boolean craftingMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = helper.getGuiSlotIndexToIngredientMap(recipeHolder);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();

            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }
        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftingMissing);
            }
        } else {
            ETCraftingRecipeTransferHelper.performTransfer(menu, recipeHolder, craftingMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, RecipeHolder<CraftingRecipe> recipeHolder) {
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipeHolder.value());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }
}
