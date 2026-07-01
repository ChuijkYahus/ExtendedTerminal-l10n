package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;

@ExtendedCrafting
@JEI
@RecipeCategory
public class ECRecipeCatalyst{
    @MyotusSubscriber
    public static void init(IRecipeCatalystRegistration registration) {
        if (ExtendedCraftingConfig.INSTANCE.getBasicConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.BASIC_TERMINAL_PART, BasicTableCategory.RECIPE_TYPE);
        if (ExtendedCraftingConfig.INSTANCE.getAdvancedConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.ADVANCED_TERMINAL_PART, AdvancedTableCategory.RECIPE_TYPE);
        if (ExtendedCraftingConfig.INSTANCE.getEliteConfig().enableTerminal())
            registration.addRecipeCatalyst(ETParts.ELITE_TERMINAL_PART, EliteTableCategory.RECIPE_TYPE);
        if (ExtendedCraftingConfig.INSTANCE.getUltimateConfig().enableTerminal()) {
            registration.addRecipeCatalyst(ETParts.ULTIMATE_TERMINAL_PART, UltimateTableCategory.RECIPE_TYPE);
        }

        registration.addRecipeCatalyst(ETParts.UNITED_TERMINAL_PART, BasicTableCategory.RECIPE_TYPE,AdvancedTableCategory.RECIPE_TYPE, EliteTableCategory.RECIPE_TYPE, UltimateTableCategory.RECIPE_TYPE);

    }
}
