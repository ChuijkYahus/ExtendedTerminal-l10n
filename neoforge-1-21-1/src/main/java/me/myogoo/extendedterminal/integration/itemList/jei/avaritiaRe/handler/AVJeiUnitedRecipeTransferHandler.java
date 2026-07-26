package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaRe.handler;

import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AVJeiUnitedRecipeTransferHandler extends AVJeiRecipeTransferHandler<UnitedTerminalMenu>{
    private final MyoRecipeType recipeType;

    public AVJeiUnitedRecipeTransferHandler(RecipeType<RecipeHolder<ITierCraftingRecipe>> recipeType,
                                            IRecipeTransferHandlerHelper helper,
                                            MyoRecipeType myoRecipeType) {
        super(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, recipeType, helper);
        this.recipeType = myoRecipeType;
    }

    @Override
    protected void performTransfer(UnitedTerminalMenu menu, MyoTableRecipe recipe, boolean craftMissing) {
        super.performTransfer(menu, recipe, craftMissing, recipeType);
    }
}
