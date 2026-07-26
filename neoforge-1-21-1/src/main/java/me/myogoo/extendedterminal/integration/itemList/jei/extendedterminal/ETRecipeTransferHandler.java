package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal;

import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETCraftingRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETSmithingRecipeTransfer;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel.ETStonecutterRecipeTransfer;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@JEI
@RecipeTransfer
public class ETRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var config = ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig();
        var helper = registration.getTransferHelper();

        if (config.enableCraftingPanel())
            registration.addRecipeTransferHandler(new ETCraftingRecipeTransfer(ETTerminalMenu.TYPE, ETTerminalMenu.class, helper), RecipeTypes.CRAFTING);
        if (config.enableSmithingPanel())
            registration.addRecipeTransferHandler(new ETSmithingRecipeTransfer(ETTerminalMenu.TYPE, ETTerminalMenu.class, helper), RecipeTypes.SMITHING);
        if (config.enableStonecutterPanel())
            registration.addRecipeTransferHandler(new ETStonecutterRecipeTransfer(ETTerminalMenu.TYPE, ETTerminalMenu.class, helper), RecipeTypes.STONECUTTING);
        if (ETMenuType.UNITED_TERMINAL.canLoad())
            registration.addRecipeTransferHandler(new ETCraftingRecipeTransfer<>(UnitedTerminalMenu.TYPE, UnitedTerminalMenu.class, helper), RecipeTypes.CRAFTING);
    }
}
