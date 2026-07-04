package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe.handler;

import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.world.item.crafting.Recipe;

public class AVUnitedTerminalRecipeHandler extends AVTerminalRecipeHandler<UnitedTerminalMenu> {
    public AVUnitedTerminalRecipeHandler(EmiRecipeCategory category) {
        super(category, UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return recipe instanceof ITierCraftingRecipe;
    }
}
