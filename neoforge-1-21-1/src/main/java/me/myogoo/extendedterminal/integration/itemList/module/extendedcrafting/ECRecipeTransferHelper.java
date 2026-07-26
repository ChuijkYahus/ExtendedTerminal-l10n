package me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting;

import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;

public class ECRecipeTransferHelper {
    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalBaseMenu<?> menu, MyoTableRecipe recipe) {
        int gridSideLength = menu.getCraftingGridWidth();
        var raw = recipe.get().getIngredients();
        List<Ingredient> ingredients;

        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            ingredients = shapedRecipe.ensureFittedCraftingGrid();
            width = shapedRecipe.width();
            height = shapedRecipe.height();
            offsetX = Math.floorDiv(gridSideLength - shapedRecipe.width(), 2);
            offsetY = Math.floorDiv(gridSideLength - shapedRecipe.height(), 2);
        } else {
            ingredients = raw;
        }

        int max = gridSideLength * gridSideLength;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;

            var guiSlot = (y + offsetY) * gridSideLength + (x + offsetX);
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
        return result;
    }
}
