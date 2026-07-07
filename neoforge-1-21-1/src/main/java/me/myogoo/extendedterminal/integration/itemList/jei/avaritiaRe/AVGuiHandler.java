package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.SculkTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.JeiTableHolderGuiHandler;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;

@ReAvaritia
@JEI
@JEIGuiHandler
public class AVGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SculkTerminalScreen.class, new JeiTableHolderGuiHandler<>(SculkCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(NetherTerminalScreen.class, new JeiTableHolderGuiHandler<>(NetherCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(EndTerminalScreen.class, new JeiTableHolderGuiHandler<>(EndCraftingTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(ExtremeTerminalScreen.class, new JeiTableHolderGuiHandler<>(ExtremeCraftingTableCategory.RECIPE_TYPE));
    }
}
