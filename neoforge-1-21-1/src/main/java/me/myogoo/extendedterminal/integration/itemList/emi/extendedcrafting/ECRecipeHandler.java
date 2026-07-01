package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting.handler.ECTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

import static me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting.ECWorkStation.*;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;

@ExtendedCrafting
@EMI
@RecipeTransfer
public class ECRecipeHandler {
    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.addDeferredRecipes(recipe -> {
            registry.addRecipeHandler(BasicTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(BASIC_TABLE_CATEGORY_ID), BasicTerminalMenu.class, ETMenuType.BASIC_TERMINAL));
            registry.addRecipeHandler(AdvancedTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), AdvancedTerminalMenu.class, ETMenuType.ADVANCED_TERMINAL));
            registry.addRecipeHandler(EliteTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(ELITE_TABLE_CATEGORY_ID), EliteTerminalMenu.class, ETMenuType.ELITE_TERMINAL));
            registry.addRecipeHandler(UltimateTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), UltimateTerminalMenu.class, ETMenuType.ULTIMATE_TERMINAL));
            //registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(BASIC_TABLE_CATEGORY_ID), UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL, UnitedRecipeKind.EXTENDED_CRAFTING_BASIC));
            //registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL, UnitedRecipeKind.EXTENDED_CRAFTING_ADVANCED));
            //registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(ELITE_TABLE_CATEGORY_ID), UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL, UnitedRecipeKind.EXTENDED_CRAFTING_ELITE));
            //registry.addRecipeHandler(UnitedTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), UnitedTerminalMenu.class, ETMenuType.UNITED_TERMINAL, UnitedRecipeKind.EXTENDED_CRAFTING_ULTIMATE));
        });
    }
}
