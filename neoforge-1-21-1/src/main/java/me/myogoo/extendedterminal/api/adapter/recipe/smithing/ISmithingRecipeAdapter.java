package me.myogoo.extendedterminal.api.adapter.recipe.smithing;

import me.myogoo.extendedterminal.adapter.recipe.smithing.SmithingTransformRecipeAdapter;
import me.myogoo.extendedterminal.adapter.recipe.smithing.SmithingTrimRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.MyoBaseRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public interface ISmithingRecipeAdapter extends MyoBaseRecipe {
    NonNullList<Ingredient> getIngredients();

    boolean isTemplateIngredient(ItemStack item);
    boolean isBaseIngredient(ItemStack item);
    boolean isAdditionIngredient(ItemStack item);
    boolean isInComplete();

    static ISmithingRecipeAdapter of(SmithingRecipe recipe) {
        if(recipe instanceof SmithingTransformRecipe transformRecipe) {
            return new SmithingTransformRecipeAdapter(transformRecipe);
        } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
            return new SmithingTrimRecipeAdapter(trimRecipe);
        }
        throw new IllegalArgumentException("Unsupported SmithingRecipe type: " + recipe.getClass().getName());
    }
}
