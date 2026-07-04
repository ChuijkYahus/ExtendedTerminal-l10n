package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.ETTerminalBaseRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Map;
import java.util.Optional;

public class ETCraftingRecipeHandler<T extends ETTerminalMenu> extends ETTerminalBaseRecipeHandler<T> {
    public ETCraftingRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    protected Result transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = holder != null ? holder.id() : null;
        var recipe = holder != null ? holder.value() : null;
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }
        if (recipe != null && !recipe.canCraftInDimensions(3, 3)) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        if (recipe == null) {
            recipe = createFakeRecipe(emiRecipe);
        }
        if (!(recipe instanceof CraftingRecipe cRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, MyoTableRecipe.of(cRecipe, holder.id()));
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots);
            }
        } else {
            boolean craftingMissing = AbstractContainerScreen.hasControlDown();
            if (holder != null) {
                ETCraftingRecipeTransferHelper.performTransfer(menu, (RecipeHolder<CraftingRecipe>) holder,
                        craftingMissing);
            } else {
                ETCraftingRecipeTransferHelper.performTransfer(menu, cRecipe, null, craftingMissing);
            }
        }
        return Result.createSuccessful();
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe.get());
    }

    private Recipe<?> createFakeRecipe(EmiRecipe recipe) {
        var ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

        for (int i = 0; i < Math.min(recipe.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(recipe.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        var pattern = new ShapedRecipePattern(3, 3, ingredients, Optional.empty());
        return new ShapedRecipe("", CraftingBookCategory.MISC, pattern, ItemStack.EMPTY);
    }
}
