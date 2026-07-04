package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaRe.handler;

import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AVJeiUnitedRecipeTransferHandler extends AVJeiRecipeTransferHandler<UnitedTerminalMenu>{
    public AVJeiUnitedRecipeTransferHandler( RecipeType<RecipeHolder<ITierCraftingRecipe>> recipeType, IRecipeTransferHandlerHelper helper) {
        super(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, recipeType, helper);
    }
}
