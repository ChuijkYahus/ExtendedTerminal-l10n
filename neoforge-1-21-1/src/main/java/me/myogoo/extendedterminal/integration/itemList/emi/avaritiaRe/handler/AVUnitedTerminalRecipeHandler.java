package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe.handler;

import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.world.item.crafting.Recipe;

public class AVUnitedTerminalRecipeHandler extends AVTerminalRecipeHandler<UnitedTerminalMenu> {
    private final MyoRecipeType recipeType;

    public AVUnitedTerminalRecipeHandler(EmiRecipeCategory category, MyoRecipeType recipeType) {
        super(category, UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL);
        this.recipeType = recipeType;
    }

    @Override
    protected void performTransfer(UnitedTerminalMenu menu, MyoTableRecipe recipe, boolean craftMissing) {
        super.performTransfer(menu, recipe, craftMissing, recipeType);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return recipe instanceof ITierCraftingRecipe;
    }
}
