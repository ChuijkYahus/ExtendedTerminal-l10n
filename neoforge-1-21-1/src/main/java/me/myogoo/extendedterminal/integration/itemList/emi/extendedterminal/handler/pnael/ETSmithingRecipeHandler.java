package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.ETTerminalBaseRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.emi.handler.EmiTransferResult;
import me.myogoo.extendedterminal.integration.itemList.module.extendedterminal.ETSmithingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.Map;

public class ETSmithingRecipeHandler<T extends ETTerminalMenu> extends ETTerminalBaseRecipeHandler<T> {
    public ETSmithingRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    protected Result transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = holder != null ? holder.id() : null;
        var recipe = holder != null ? holder.value() : null;
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if(!craftingRecipe || recipe == null) {
            return EmiTransferResult.Result.createNotApplicable();
        }
        if(!recipe.canCraftInDimensions(3,1)) {
            return EmiTransferResult.Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        if(!(recipe instanceof SmithingRecipe smithingRecipe)) {
            return EmiTransferResult.Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        var slotToIngredientMap = ETSmithingRecipeTransferHelper.getGuiSlotToIngredientMap(menu, smithingRecipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return EmiTransferResult.Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new EmiTransferResult.Result.PartiallyCraftable(missingSlots);
            }
        } else {
            boolean craftingMissing = AbstractContainerScreen.hasControlDown();
            ETSmithingRecipeTransferHelper.performTransfer(menu, (RecipeHolder<SmithingRecipe>) holder, craftingMissing);
        }

        return EmiTransferResult.Result.createSuccessful();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.SMITHING);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.SMITHING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        return Map.of();
    }
}
