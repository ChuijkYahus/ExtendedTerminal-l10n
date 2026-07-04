package me.myogoo.extendedterminal.integration.itemList.jei.handler;

import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTableRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractJeiTableRecipeHandler<T extends ETTerminalBaseMenu<?>, R extends Recipe<?>>
        extends ItemListTableRecipeTransferHandler<T>
        implements IRecipeTransferHandler<T, R>, IJeiAbstractRecipeHandler {
    private final Class<T> containerClass;
    private final MenuType<T> menuType;
    private final RecipeType<R> recipeType;

    public AbstractJeiTableRecipeHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<R> recipeType) {
        this.containerClass = containerClass;
        this.menuType = menuType;
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull Class<? extends T> getContainerClass() {
        return containerClass;
    }

    @Override
    public @NotNull Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public @NotNull RecipeType<R> getRecipeType() {
        return recipeType;
    }

    protected IRecipeTransferError transferSetup(IRecipeTransferHandlerHelper helper, T menu, Recipe<?> recipe) {
        if (recipe.getIngredients().isEmpty()) {
            return Result.createInCompatibleError(helper);
        }

        if (!recipe.canCraftInDimensions(menu.getCraftingGridWidth(), menu.getCraftingGridHeight())) {
            return Result.createRecipeToLargeError(helper);
        }
        return null;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe);
}
