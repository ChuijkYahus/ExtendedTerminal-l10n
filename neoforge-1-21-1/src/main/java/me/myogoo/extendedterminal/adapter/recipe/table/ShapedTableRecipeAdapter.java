package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class ShapedTableRecipeAdapter extends AbstractTableRecipeAdapter implements IShapedTableRecipeAdapter {
    private final int recipeWidth;
    private final int recipeHeight;
    public ShapedTableRecipeAdapter(Recipe<?> recipe, int tier, int width, int height, ResourceLocation recipeId) {
        super(recipe, tier, recipeId);
        this.recipeWidth = width;
        this.recipeHeight = height;
    }

    public ShapedTableRecipeAdapter(ShapedTableRecipe recipe, ResourceLocation recipeId) {
        this(recipe, recipe.getTier(), recipe.getWidth(), recipe.getHeight(), recipeId);
    }

    public ShapedTableRecipeAdapter(ShapedTableCraftingRecipe recipe, ResourceLocation recipeId) {
        this(recipe, recipe.getTier(), recipe.getWidth(), recipe.getHeight(), recipeId);
    }

    public ShapedTableRecipeAdapter(RecipeExtremeShaped recipe, ResourceLocation recipeId) {
        this(recipe, 4, recipe.getWidth(), recipe.getHeight(), recipeId);
    }

    public ShapedTableRecipeAdapter(ShapedRecipe recipe, ResourceLocation recipeId) {
        this(recipe, 1, recipe.getWidth(), recipe.getHeight(), recipeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Recipe<?>> R get() {
        return (R) this.recipe;
    }


    @Override
    public NonNullList<Ingredient> ensureFittedCraftingGrid() {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients = NonNullList.withSize(height() * width(), Ingredient.EMPTY);

        for (int h = 0; h < height(); h++) {
            for (int w = 0; w < width(); w++) {
                int index = w + h * width();
                if (index < ingredients.size()) {
                    expandedIngredients.set(index, ingredients.get(index));
                } else {
                    expandedIngredients.set(index, Ingredient.EMPTY);
                }
            }
        }
        return expandedIngredients;
    }

    @Override
    public int width() {
        return this.recipeWidth;
    }

    @Override
    public int height() {
        return this.recipeHeight;
    }
}
