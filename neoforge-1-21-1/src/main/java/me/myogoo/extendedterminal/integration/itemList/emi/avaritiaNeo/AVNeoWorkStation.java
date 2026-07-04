package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaNeo;

import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.integration.itemList.emi.avaritiaNeo.handler.AVNeoTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;

import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import net.byAqua3.avaritia.compat.emi.AvaritiaEMIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;

@AvaritiaNeo
@EMI
@RecipeCategory
public class AVNeoWorkStation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(NeoExtremeTerminalMenu.TYPE, new AVNeoTerminalRecipeHandler<>(AvaritiaEMIPlugin.EXTREME_CRAFTING, NeoExtremeTerminalMenu.class, ETMenuType.NEO_EXTREME_TERMINAL));
        registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new AVNeoTerminalRecipeHandler<>(AvaritiaEMIPlugin.EXTREME_CRAFTING, UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL));
    }
}
