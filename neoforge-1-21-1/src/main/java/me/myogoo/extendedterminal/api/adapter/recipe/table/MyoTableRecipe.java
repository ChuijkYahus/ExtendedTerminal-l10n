package me.myogoo.extendedterminal.api.adapter.recipe.table;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.adapter.recipe.table.ShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.adapter.recipe.table.ShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.MyoBaseRecipe;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import net.minecraft.world.level.Level;

public interface MyoTableRecipe extends MyoBaseRecipe {
    int tier();

    int gridSize();

    int sideLength();

    NonNullList<Ingredient> ensureFittedCraftingGrid();

    NonNullList<ItemStack> findGoodTemplateItems(ETTerminalBaseMenu<?> menu);

    ResourceLocation id();

    boolean matches(MyoTableInput input, Level level, MyoRecipeType recipeType);

    ItemStack assemble(MyoTableInput input, Level level, MyoRecipeType recipeType);

    NonNullList<ItemStack> getRemainingItems(MyoTableInput input, MyoRecipeType recipeType);

    RecipeHolder<Recipe<?>> castHolderRecipe();

    static MyoTableRecipe of(CraftingRecipe recipe, ResourceLocation id) {
        if (recipe instanceof ShapedRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        } else {
            return new ShapelessTableRecipeAdapter(recipe, id);
        }
    }

    @ExtendedCrafting
    static MyoTableRecipe of(ITableRecipe recipe, ResourceLocation id) {
        if (recipe instanceof ShapedTableRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof ShapelessTableRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        }
        throw new IllegalArgumentException("Unknown ITableRecipe implementation: " + recipe.getClass().getName());
    }

    @ReAvaritia
    static MyoTableRecipe of(ITierCraftingRecipe recipe, ResourceLocation id) {
        if (recipe instanceof ShapedTableCraftingRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof ShapelessTableCraftingRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        }
        throw new IllegalArgumentException("Unknown ITierCraftingRecipe implementation: " + recipe.getClass().getName());
    }

    @AvaritiaNeo
    static MyoTableRecipe of(RecipeExtremeCrafting recipe, ResourceLocation id) {
        if (recipe instanceof RecipeExtremeShaped shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof RecipeExtremeShapeless shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        }
        throw new IllegalArgumentException("Unknown RecipeExtremeCrafting implementation: " + recipe.getClass().getName());
    }
}
