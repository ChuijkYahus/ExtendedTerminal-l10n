package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@EMI
@RecipeCategory
public class ETWorkStation {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.SMITHING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        registry.addWorkstation(VanillaEmiRecipeCategories.STONECUTTING, EmiStack.of(ETParts.ET_TERMINAL_PART));
        if (ETMenuType.UNITED_TERMINAL.canLoad()) {
            registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(ETParts.UNITED_TERMINAL_PART));
        }
    }
}
