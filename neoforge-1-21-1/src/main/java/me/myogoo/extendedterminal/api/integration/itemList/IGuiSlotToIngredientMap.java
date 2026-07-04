package me.myogoo.extendedterminal.api.integration.itemList;

import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public interface IGuiSlotToIngredientMap {
    Map<Integer, Ingredient> jei(ETTerminalBaseMenu<?> menu, MyoTableRecipe recipe);
    Map<Integer, Ingredient> emi(ETTerminalBaseMenu<?> menu, MyoTableRecipe recipe);

}
