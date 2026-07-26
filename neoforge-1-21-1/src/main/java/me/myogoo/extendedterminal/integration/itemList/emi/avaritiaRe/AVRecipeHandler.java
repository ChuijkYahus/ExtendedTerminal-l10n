package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe;

import me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe.handler.AVUnitedTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.*;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe.handler.AVTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;

@ReAvaritia
@EMI
@RecipeTransfer
public class AVRecipeHandler {
    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.addRecipeHandler(SculkTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(SculkCraftingTableCategory.CATEGORY, SculkTerminalMenu.class, ETMenuType.SCULK_TERMINAL));
        registry.addRecipeHandler(NetherTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(NetherCraftingTableCategory.CATEGORY, NetherTerminalMenu.class, ETMenuType.NETHER_TERMINAL));
        registry.addRecipeHandler(EndTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(EndCraftingTableCategory.CATEGORY, EndTerminalMenu.class, ETMenuType.END_TERMINAL));
        registry.addRecipeHandler(ExtremeTerminalMenu.TYPE, new AVTerminalRecipeHandler<>(ExtremeCraftingTableCategory.CATEGORY, ExtremeTerminalMenu.class, ETMenuType.EXTREME_TERMINAL));
        registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new AVUnitedTerminalRecipeHandler(SculkCraftingTableCategory.CATEGORY, MyoRecipeType.SCULK));
        registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new AVUnitedTerminalRecipeHandler(NetherCraftingTableCategory.CATEGORY, MyoRecipeType.Nether));
        registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new AVUnitedTerminalRecipeHandler(EndCraftingTableCategory.CATEGORY, MyoRecipeType.END));
        registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new AVUnitedTerminalRecipeHandler(ExtremeCraftingTableCategory.CATEGORY, MyoRecipeType.EXTREME));
    }
}
