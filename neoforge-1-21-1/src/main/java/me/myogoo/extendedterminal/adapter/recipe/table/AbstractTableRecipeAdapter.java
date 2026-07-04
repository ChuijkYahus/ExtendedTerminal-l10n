package me.myogoo.extendedterminal.adapter.recipe.table;

import appeng.api.stacks.AEItemKey;
import appeng.integration.modules.itemlists.EncodingHelper;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.VANILLA;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Map;

import static me.myogoo.extendedterminal.integration.itemList.module.ItemListTermCraftingHelper.ENTRY_COMPARATOR;

abstract class AbstractTableRecipeAdapter implements MyoTableRecipe {
    protected final Recipe<?> recipe;
    protected final int recipeTier;
    protected final ResourceLocation recipeId;

    AbstractTableRecipeAdapter(Recipe<?> recipe, int tier, ResourceLocation recipeId) {
        this.recipeTier = tier;
        this.recipe = recipe;
        this.recipeId = recipeId;
    }

    @Override
    public NonNullList<ItemStack> findGoodTemplateItems(ETTerminalBaseMenu<?> menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);

        var templateItems = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
        var ingredients = this.ensureFittedCraftingGrid();
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                var stack = ingredientPriorities.entrySet()
                        .stream()
                        .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .map(e -> ((AEItemKey) e.getKey()).toStack())
                        .orElse(ingredient.getItems()[0]);
                templateItems.set(i, stack);
            }
        }
        return templateItems;
    }


    @Override
    public int gridSize() {
        return this.sideLength() * this.sideLength();
    }

    @Override
    public int sideLength() {
        return this.tier() * 2 + 1;
    }

    @Override
    public ResourceLocation id() {
        return this.recipeId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Recipe<?>> R get() {
        return (R) this.recipe;
    }

    @Override
    public int tier() {
        return this.recipeTier;
    }

    @Override
    public RecipeHolder<Recipe<?>> castHolderRecipe() {
        return new RecipeHolder<>(id(), recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(MyoTableInput input, MyoRecipeType recipeType) {
        var annotation = recipeType.getMyomodAnnotation();

        if (annotation == ExtendedCrafting.class) {
            return ((ITableRecipe) this.recipe).getRemainingItems(input.cast(TableCraftingInput.class));
        }

        if (annotation == ReAvaritia.class) {
            return ((ITierCraftingRecipe) this.recipe).getRemainingItems(input.cast(TierInput.class));
        }

        if (annotation == AvaritiaNeo.class) {
            return ((RecipeExtremeCrafting) this.recipe).getRemainingItems(input.cast());
        }

        return ((CraftingRecipe) this.recipe).getRemainingItems(input.cast());
    }

    @Override
    public boolean matches(MyoTableInput input, Level level, MyoRecipeType recipeType) {
        if (recipeType == null || !recipeType.isActive()) {
            return false;
        }

        var annotation = recipeType.getMyomodAnnotation();

        if (annotation == ExtendedCrafting.class) {
            if (this.recipe instanceof ITableRecipe tableRecipe) {
                return tableRecipe.matches(input.cast(TableCraftingInput.class), level);
            }
        }

        if (annotation == ReAvaritia.class) {
            if (this.recipe instanceof ITierCraftingRecipe tableRecipe) {
                return tableRecipe.matches(input.cast(TierInput.class), level);
            }
        }

        if (annotation == AvaritiaNeo.class) {
            if (this.recipe instanceof RecipeExtremeCrafting tableRecipe) {
                return tableRecipe.matches(input.cast(), level);
            }
        }

        if (annotation == VANILLA.class) {
            return ((CraftingRecipe) this.recipe).matches(input.cast(), level);
        }
        return false;
    }

    @Override
    public ItemStack assemble(MyoTableInput input, Level level, MyoRecipeType recipeType) {
        if (recipeType == null || !recipeType.isActive()) {
            return ItemStack.EMPTY;
        }

        var annotation = recipeType.getMyomodAnnotation();
        var registryAccess = level.registryAccess();

        if (annotation == ExtendedCrafting.class) {
            if (this.recipe instanceof ITableRecipe tableRecipe) {
                return MagicRecipe.assemble(tableRecipe, input, registryAccess);
            }
        }

        if (annotation == ReAvaritia.class) {
            if (this.recipe instanceof ITierCraftingRecipe tableRecipe) {
                return MagicRecipe.assemble(tableRecipe, input, registryAccess);
            }
        }

        if (annotation == AvaritiaNeo.class) {
            if (this.recipe instanceof CraftingRecipe tableRecipe) {
                return MagicRecipe.assemble(tableRecipe, input, registryAccess);
            }
        }

        if (annotation == VANILLA.class) {
            return MagicRecipe.assemble((CraftingRecipe) this.recipe, input, registryAccess);
        }

        return ItemStack.EMPTY;
    }

    private static class MagicRecipe {
        public static ItemStack assemble(ITableRecipe recipe, MyoTableInput input, RegistryAccess registryAccess) {
            return recipe.assemble(input.cast(TableCraftingInput.class), registryAccess);
        }

        public static ItemStack assemble(ITierCraftingRecipe recipe, MyoTableInput input, RegistryAccess registryAccess) {
            return recipe.assemble(input.cast(TierInput.class), registryAccess);
        }

        public static ItemStack assemble(CraftingRecipe recipe, MyoTableInput input, RegistryAccess registryAccess) {
            return recipe.assemble(input.cast(), registryAccess);
        }

        public static ItemStack assemble(RecipeExtremeCrafting recipe, MyoTableInput input, RegistryAccess registryAccess) {
            return recipe.assemble(input.cast(), registryAccess);
        }
    }
}
