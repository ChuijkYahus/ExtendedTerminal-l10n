package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.itemList.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Map;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import org.jetbrains.annotations.Nullable;

public class ECTerminalRecipeHandler<T extends ETTerminalBaseMenu<?>> extends AbstractEmiTableRecipeHandler<T> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;

    public ECTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
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
        var adapterRecipe = ITableRecipeAdapter.of(tableRecipe);
        return doTransfer(menu, adapterRecipe, holder.id(), doTransfer);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        return ECRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
    }
}
