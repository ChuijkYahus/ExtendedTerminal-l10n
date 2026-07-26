package me.myogoo.extendedterminal.integration.itemList.module.extendedterminal;

import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.FillCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.client.ae2helpers.ETAutoCraftingWatcher;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ETCraftingRecipeTransferHelper {
    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, CraftingRecipe recipe) {
        var ingredients = recipe.getIngredients();

        int width;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            width = shapedRecipe.getWidth();
        } else {
            width = 3;
        }

        var result = new HashMap<Integer, Ingredient>(ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            var guiSlot = (i / width) * 3 + (i % width);
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                result.put(guiSlot, ingredient);
            }
        }
        return result;
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(UnitedTerminalMenu menu,
                                                                     MyoTableRecipe recipe) {
        int gridSideLength = menu.getCraftingGridWidth();
        List<Ingredient> ingredients;
        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;

        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            ingredients = shapedRecipe.ensureFittedCraftingGrid();
            width = shapedRecipe.width();
            offsetX = Math.floorDiv(gridSideLength - shapedRecipe.width(), 2);
            offsetY = Math.floorDiv(gridSideLength - shapedRecipe.height(), 2);
        } else {
            ingredients = recipe.get().getIngredients();
        }

        int count = Math.min(ingredients.size(), gridSideLength * gridSideLength);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;
            int guiSlot = (y + offsetY) * gridSideLength + (x + offsetX);
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                result.put(guiSlot, ingredient);
            }
        }
        return result;
    }

    public static void performTransfer(ETTerminalMenu menu, RecipeHolder<CraftingRecipe> recipeHolder,
            boolean craftingMissing) {
        performTransfer(menu, recipeHolder.value(), recipeHolder.id(), craftingMissing);
    }

    public static void performTransfer(ETTerminalMenu menu, CraftingRecipe recipe, ResourceLocation recipeId,
            boolean craftingMissing) {
        var templateItems = MyoTableRecipe.of(recipe, recipeId).findGoodTemplateItems(menu);

        if (recipeId != null && menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            ExtendedTerminal.LOGGER.warn(
                    "ETCraftingRecipeTransfer#performTransfer: recipe with id {} not found in recipe manager",
                    recipeId);
            recipeId = null;
        }

        ETAutoCraftingWatcher.INSTANCE.preparePending(menu, getGuiSlotToIngredientMap(menu, recipe), craftingMissing);
        ServerboundPacket message = new FillCraftingGridFromRecipePacket(recipeId, templateItems, craftingMissing);
        PacketDistributor.sendToServer(message);
    }
}
