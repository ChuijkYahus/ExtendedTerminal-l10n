package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaNeo;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import net.byAqua3.avaritia.compat.emi.AvaritiaEMIPlugin;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;


@AvaritiaNeo
@EMI
@RecipeTransfer
public class AVNeoRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(AvaritiaEMIPlugin.EXTREME_CRAFTING, EmiStack.of(ETParts.NEO_EXTREME_TERMINAL_PART));
        registry.addWorkstation(AvaritiaEMIPlugin.EXTREME_CRAFTING, EmiStack.of(ETParts.UNITED_TERMINAL_PART));
    }
}
