package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting.handler.ECJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;

@ExtendedCrafting
@JEI
@RecipeTransfer
public class ECRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(BasicTerminalMenu.class, BasicTerminalMenu.TYPE, BasicTableCategory.RECIPE_TYPE, helper), BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(AdvancedTerminalMenu.class, AdvancedTerminalMenu.TYPE, AdvancedTableCategory.RECIPE_TYPE, helper), AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(EliteTerminalMenu.class, EliteTerminalMenu.TYPE, EliteTableCategory.RECIPE_TYPE, helper), EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(UltimateTerminalMenu.class, UltimateTerminalMenu.TYPE, UltimateTableCategory.RECIPE_TYPE, helper), UltimateTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, BasicTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeType.EXTENDED_CRAFTING_BASIC), BasicTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, AdvancedTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeType.EXTENDED_CRAFTING_ADVANCED), AdvancedTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, EliteTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeType.EXTENDED_CRAFTING_ELITE), EliteTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new ECJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, UltimateTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeType.EXTENDED_CRAFTING_ULTIMATE), UltimateTableCategory.RECIPE_TYPE);
    }
}
