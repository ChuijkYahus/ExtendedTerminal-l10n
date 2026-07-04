package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ShapelessTableRecipeAdapter extends AbstractTableRecipeAdapter implements IShapelessTableRecipeAdapter {
    private ShapelessTableRecipeAdapter(Recipe<?> recipe, int tier, ResourceLocation recipeId) {
        super(recipe, tier, recipeId);
    }

    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe, ResourceLocation recipeId) {
        this(recipe, recipe.getTier(), recipeId);
    }

    public ShapelessTableRecipeAdapter(ShapelessTableCraftingRecipe recipe, ResourceLocation recipeId) {
        this(recipe, recipe.getTier(), recipeId);
    }

    public ShapelessTableRecipeAdapter(RecipeExtremeShapeless recipe, ResourceLocation recipeId) {
        this(recipe, 4, recipeId);
    }

    public ShapelessTableRecipeAdapter(ShapelessRecipe recipe, ResourceLocation recipeId) {
        this(recipe, 1, recipeId);
    }

    public ShapelessTableRecipeAdapter(CraftingRecipe recipe, ResourceLocation recipeId) {
        this(recipe, 1, recipeId);
    }


    @Override
    public NonNullList<Ingredient> ensureFittedCraftingGrid() {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients = NonNullList.withSize(gridSize(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            expandedIngredients.set(i, ingredients.get(i));
        }
        return expandedIngredients;
    }


}
