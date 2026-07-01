package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaNeo.handler;

import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.itemList.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.avaritia.AVNeoRecipeTransferHelper;
import me.myogoo.extendedterminal.integration.itemList.module.avaritia.AVRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Map;

public class AVNeoTerminalRecipeHandler<T extends ETTerminalBaseMenu<?>> extends AbstractEmiTableRecipeHandler<T> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;

    public AVNeoTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
    }

    @Override
    protected Result transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        Result setup;
        if ((setup = transferSetup(holder, emiRecipe, menuType.getGridSideLength())) != null) {
            return setup;
        }

        if (holder == null || !(holder.value() instanceof RecipeExtremeCrafting tableRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }
        return doTransfer(menu, ITableRecipeAdapter.of(tableRecipe), holder.id(), doTransfer);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(this.category);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        return AVNeoRecipeTransferHelper.GuiSlotToIngredientMap.emi(menu, recipe);
    }
}
