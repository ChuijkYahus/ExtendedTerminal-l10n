package me.myogoo.extendedterminal.integration.jei.extendedcrafting.handler;

import me.myogoo.extendedterminal.adapter.recipe.TableRecipeAdapters;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.integration.jei.handler.AbstractTableRecipeHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static appeng.integration.modules.jeirei.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.jeirei.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;
import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.ensureFittedCraftingGrid;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;

public class ECJeiRecipeTransferHandler<T extends ETTerminalBaseMenu<?>> extends AbstractTableRecipeHandler<T, ITableRecipe> {
    private final IRecipeTransferHandlerHelper helper;
    @Nullable
    private final UnitedTerminalMenu.UnitedRecipeKind unitedRecipeKind;

    public ECJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> container, RecipeType<ITableRecipe> recipeType, IRecipeTransferHandlerHelper helper) {
        this(containerClass, container, recipeType, helper, null);
    }

    public ECJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> container, RecipeType<ITableRecipe> recipeType,
                                      IRecipeTransferHandlerHelper helper,
                                      @Nullable UnitedTerminalMenu.UnitedRecipeKind unitedRecipeKind) {
        super(containerClass, container, recipeType);
        this.helper = helper;
        this.unitedRecipeKind = unitedRecipeKind;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, ITableRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        var adapterRecipe = TableRecipeAdapters.of(recipe);

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            var missingSlotViews = getMissingSlotViews(menu, inputSlots, missingSlots.missingSlots(),
                    getDisplayedInputSlotKeys(menu, slotToIngredientMap));
            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftMissing,
                        getDisplayedInputSlotKeys(menu, slotToIngredientMap));
            }
        } else {
            performTransfer(menu, adapterRecipe, craftMissing,
                    menu instanceof UnitedTerminalMenu && unitedRecipeKind != null
                            ? unitedRecipeKind
                            : UnitedTerminalMenu.UnitedRecipeKind.fromExtendedCraftingTier(adapterRecipe.tier()));
        }
        return Result.createSuccessful();
    }

    @Override
    public Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter<?> recipe) {
        int gridSideLength = menu.getCraftingGridWidth();
        var raw = recipe.recipe().getIngredients();
        List<Ingredient> ingredients;

        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof IShapedTableRecipeAdapter<?> shapedRecipe) {
            ingredients = ensureFittedCraftingGrid(shapedRecipe);
            width = shapedRecipe.width();
            height = shapedRecipe.height();
            offsetX = Math.floorDiv(gridSideLength - shapedRecipe.width(),2);
            offsetY = Math.floorDiv(gridSideLength - shapedRecipe.height(),2);
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
