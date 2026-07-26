package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael.ETCraftingRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael.ETSmithingRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael.ETStonecutterRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@EMI
@RecipeTransfer
public class ETRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETCraftingRecipeHandler<>(ETTerminalMenu.class));
        registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETSmithingRecipeHandler<>(ETTerminalMenu.class));
        registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETStonecutterRecipeHandler<>(ETTerminalMenu.class));
        if (ETMenuType.UNITED_TERMINAL.canLoad()) {
            registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new ETCraftingRecipeHandler<>(UnitedTerminalMenu.class));
        }
    }
}
