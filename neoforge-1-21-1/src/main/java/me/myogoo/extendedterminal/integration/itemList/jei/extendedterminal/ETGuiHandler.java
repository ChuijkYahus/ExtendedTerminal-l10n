package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal;

import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.ETTerminalGuiHandler;
import me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.UnitedTerminalGuiHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;

@JEI
@JEIGuiHandler
public class ETGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(ETTerminalScreen.class, new ETTerminalGuiHandler());
        registration.addGenericGuiContainerHandler(UnitedTerminalScreen.class, new UnitedTerminalGuiHandler());
    }
}
