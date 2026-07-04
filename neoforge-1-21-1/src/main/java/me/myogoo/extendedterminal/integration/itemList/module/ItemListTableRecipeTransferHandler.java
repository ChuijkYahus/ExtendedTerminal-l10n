package me.myogoo.extendedterminal.integration.itemList.module;

import appeng.core.network.ServerboundPacket;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.client.ae2helpers.ETAutoCraftingWatcher;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public abstract class ItemListTableRecipeTransferHandler<T extends ETTerminalBaseMenu<?>> {
    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe);

    protected void performTransfer(T menu, MyoTableRecipe recipe, boolean craftMissing) {
        var templateItems = recipe.findGoodTemplateItems(menu);
        int recipeWidth = NOT_SET_RECIPE_SIZE;
        int recipeHeight = NOT_SET_RECIPE_SIZE;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            recipeWidth = shapedRecipe.width();
            recipeHeight = shapedRecipe.height();
        }

        ETAutoCraftingWatcher.INSTANCE.preparePending(menu, getGuiSlotToIngredientMap(menu, recipe), craftMissing);
        ServerboundPacket message = new FillTableCraftingGridFromRecipePacket(recipe.id(), templateItems, craftMissing,
                recipeWidth, recipeHeight);
        PacketDistributor.sendToServer(message);
    }
}