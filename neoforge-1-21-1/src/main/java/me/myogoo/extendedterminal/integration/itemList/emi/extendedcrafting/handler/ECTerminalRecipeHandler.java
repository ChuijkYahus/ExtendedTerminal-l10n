package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;

public class ECTerminalRecipeHandler<T extends ETTerminalBaseMenu<?>> extends AbstractEmiTableRecipeHandler<T> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;
    private final @Nullable MyoRecipeType recipeType;

    public ECTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType) {
        this(category, containerClass, menuType, null);
    }

    public ECTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType,
                                   @Nullable MyoRecipeType recipeType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
        this.recipeType = recipeType;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        if (recipe instanceof ITableRecipe tableRecipe) {
            return emiRecipe.getCategory().equals(this.category);
        }
        return false;
    }

    @Override
    protected Result transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        Result setup;
        if ((setup = transferSetup(holder, emiRecipe, menuType.getGridSideLength())) != null) {
            return setup;
        }

        if (holder == null || !(holder.value() instanceof ITableRecipe tableRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }
        return doTransfer(menu, MyoTableRecipe.of(tableRecipe, holder.id()), doTransfer);
    }

    @Override
    protected void performTransfer(T menu, MyoTableRecipe recipe, boolean craftMissing) {
        if (menu instanceof UnitedTerminalMenu && recipeType != null) {
            super.performTransfer(menu, recipe, craftMissing, recipeType);
        } else {
            super.performTransfer(menu, recipe, craftMissing);
        }
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        return ECRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
    }
}
