package me.myogoo.extendedterminal.integration.itemList.jei.extendedcrafting.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.AbstractTableHolderRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.extendedcrafting.ECRecipeTransferHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;

public class ECJeiRecipeTransferHandler<T extends ETTerminalBaseMenu<?>> extends AbstractTableHolderRecipeHandler<T, ITableRecipe, RecipeHolder<ITableRecipe>> {
    private final IRecipeTransferHandlerHelper helper;

    public ECJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> container, RecipeType<RecipeHolder<ITableRecipe>> recipeType,
                                      IRecipeTransferHandlerHelper helper) {
        super(containerClass, container, recipeType);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(@NotNull T menu, RecipeHolder<ITableRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        var adapterRecipe = MyoTableRecipe.of(recipe, recipeHolder.id());

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftMissing);
            }
        } else {
            performTransfer(menu, adapterRecipe, craftMissing);
        }
        return Result.createSuccessful();
    }

    @Override
    public Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        return ECRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe);
    }

}
