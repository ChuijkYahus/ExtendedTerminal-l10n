package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler.panel;

import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.AbstractTableHolderRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.IJeiAbstractRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;

public class ETCraftingRecipeTransfer<T extends ETTerminalBaseMenu<?>>
        extends AbstractTableHolderRecipeHandler<T, CraftingRecipe, RecipeHolder<CraftingRecipe>> {
    private final IRecipeTransferHandlerHelper helper;

    public ETCraftingRecipeTransfer(MenuType<T> menuType, Class<T> containerClass, IRecipeTransferHandlerHelper helper) {
        super(containerClass, menuType, RecipeTypes.CRAFTING);
        this.helper = helper;
    }


    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, RecipeHolder<CraftingRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        boolean craftingMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var adapterRecipe = MyoTableRecipe.of(recipe, recipeHolder.id());
        var slotToIngredientMap = menu instanceof UnitedTerminalMenu
                ? getGuiSlotToIngredientMap(menu, adapterRecipe)
                : helper.getGuiSlotIndexToIngredientMap(recipeHolder);
        Set<Integer> inputSlotKeys = menu instanceof UnitedTerminalMenu
                ? slotToIngredientMap.keySet()
                : Set.of();
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            var inputSlotsByKey = IJeiAbstractRecipeHandler.getInputSlotViewsByKey(inputSlots, inputSlotKeys);
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(inputSlotsByKey::get)
                    .filter(Objects::nonNull)
                    .toList();

            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }
        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftingMissing, inputSlotKeys);
            }
        } else {
            if (menu instanceof UnitedTerminalMenu) {
                performTransfer(menu, adapterRecipe, craftingMissing, MyoRecipeType.VANILLA);
            } else {
                ETCraftingRecipeTransferHelper.performTransfer((ETTerminalMenu) menu, recipeHolder, craftingMissing);
            }
        }

        return Result.createSuccessful();
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        if (menu instanceof UnitedTerminalMenu unitedMenu) {
            return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(unitedMenu, recipe);
        }
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap((ETTerminalMenu) menu, recipe.get());
    }
}
