package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting.handler;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ECUnitedJeiRecipeTransferHandler extends ECJeiRecipeTransferHandler<UnitedTerminalMenu>  {
    public ECUnitedJeiRecipeTransferHandler(Class<UnitedTerminalMenu> containerClass, MenuType<UnitedTerminalMenu> container, RecipeType<RecipeHolder<ITableRecipe>> recipeType, IRecipeTransferHandlerHelper helper) {
        super(containerClass, container, recipeType, helper);
    }

    @Override
    protected void performTransfer(UnitedTerminalMenu menu, ITableRecipeAdapter recipe, boolean craftMissing, ResourceLocation recipeId) {
        super.performTransfer(menu, recipe, craftMissing, recipeId);
    }
}
