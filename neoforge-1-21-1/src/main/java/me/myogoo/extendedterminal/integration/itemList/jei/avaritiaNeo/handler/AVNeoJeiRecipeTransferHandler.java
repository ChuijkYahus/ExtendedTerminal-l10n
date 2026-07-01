package me.myogoo.extendedterminal.integration.itemList.jei.avaritiaNeo.handler;

import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import appeng.core.localization.ItemModText;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.AbstractJeiTableRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.jei.handler.IJeiAbstractRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.avaritia.AVNeoRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;

public class AVNeoJeiRecipeTransferHandler<T extends ETTerminalBaseMenu<?>>
        extends AbstractJeiTableRecipeHandler<T, RecipeExtremeCrafting>
        implements IRecipeTransferHandler<T, RecipeExtremeCrafting>, IJeiAbstractRecipeHandler {

    private final IRecipeTransferHandlerHelper helper;

    public AVNeoJeiRecipeTransferHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<RecipeExtremeCrafting> recipeType, IRecipeTransferHandlerHelper helper) {
        super(containerClass, menuType, recipeType);
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, RecipeExtremeCrafting recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }

        boolean craftMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        var adapterRecipe = ITableRecipeAdapter.of(recipe);

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
            performTransfer(menu, adapterRecipe, craftMissing, () -> {
                var level = menu.getPlayer().level();
                var recipeManager = level.getRecipeManager();
                var findRecipe = recipeManager.getAllRecipesFor(AvaritiaRecipes.EXTREME_CRAFTING.get()).stream().filter(x -> x.value().equals(recipe)).toList();
                return !findRecipe.isEmpty() ? findRecipe.getFirst() : null;
            });
        }

        return Result.createSuccessful();
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        return AVNeoRecipeTransferHelper.GuiSlotToIngredientMap.jei(menu, recipe);
    }

    private <R extends Recipe<?>> void performTransfer(T menu, ITableRecipeAdapter recipe, boolean craftMissing,
                                                       Supplier<RecipeHolder<R>> supplier) {
        var recipeHolder = supplier.get();
        if (recipeHolder != null) {
            performTransfer(menu, recipe, craftMissing, recipeHolder.id());
        }
    }
}
