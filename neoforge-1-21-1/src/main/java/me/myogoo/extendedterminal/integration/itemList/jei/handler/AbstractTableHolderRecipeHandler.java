package me.myogoo.extendedterminal.integration.itemList.jei.handler;

import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTableRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractTableHolderRecipeHandler<T extends ETTerminalBaseMenu<?>, R extends Recipe<?>, H extends RecipeHolder<R>>
        extends ItemListTableRecipeTransferHandler<T>
        implements IRecipeTransferHandler<T, H>, IJeiAbstractRecipeHandler {
    private final Class<T> containerClass;
    private final MenuType<T> menuType;
    private final RecipeType<H> recipeType;

    public AbstractTableHolderRecipeHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<H> recipeType) {
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
    public @NotNull RecipeType<H> getRecipeType() {
        return recipeType;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe);
}
