package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaRe;

import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.myotus.api.annotation.itemList.RecipeTransfer;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.integration.itemList.jei.avaritiaRe.handler.AVJeiRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;

@JEI
@RecipeTransfer
@ReAvaritia
public class AVRecipeTransferHandler {
    @MyotusSubscriber
    public static void init(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(SculkTerminalMenu.class, SculkTerminalMenu.TYPE, SculkCraftingTableCategory.RECIPE_TYPE, helper), SculkCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(NetherTerminalMenu.class, NetherTerminalMenu.TYPE, NetherCraftingTableCategory.RECIPE_TYPE, helper), NetherCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(EndTerminalMenu.class, EndTerminalMenu.TYPE, EndCraftingTableCategory.RECIPE_TYPE, helper), EndCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(ExtremeTerminalMenu.class, ExtremeTerminalMenu.TYPE, ExtremeCraftingTableCategory.RECIPE_TYPE, helper), ExtremeCraftingTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, SculkCraftingTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeKind.RE_AVARITIA_SCULK), SculkCraftingTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, NetherCraftingTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeKind.RE_AVARITIA_NETHER), NetherCraftingTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, EndCraftingTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeKind.RE_AVARITIA_END), EndCraftingTableCategory.RECIPE_TYPE);
        //registration.addRecipeTransferHandler(new AVJeiRecipeTransferHandler<>(UnitedTerminalMenu.class, UnitedTerminalMenu.TYPE, ExtremeCraftingTableCategory.RECIPE_TYPE, helper, UnitedTerminalMenu.UnitedRecipeKind.RE_AVARITIA_EXTREME), ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
