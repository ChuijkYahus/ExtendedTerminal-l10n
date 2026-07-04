package me.myogoo.extendedterminal.integration.itemList.emi.avaritiaRe.handler;

import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import appeng.core.localization.ItemModText;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.*;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.module.avaritia.AVRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.item.crafting.*;

import java.util.Map;

public class AVTerminalRecipeHandler<T extends ETTerminalBaseMenu<?>> extends AbstractEmiTableRecipeHandler<T> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;

    public AVTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
    }

    @Override
    protected Result transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        Result setup;
        if ((setup = transferSetup(holder, emiRecipe, menuType.getGridSideLength())) != null) {
            return setup;
        }

        if (holder == null || !(holder.value() instanceof ITierCraftingRecipe tableRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }
        return doTransfer(menu, MyoTableRecipe.of(tableRecipe, holder.id()), doTransfer);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        if (recipe instanceof ITierCraftingRecipe tableRecipe) {
            return emiRecipe.getCategory().equals(getCategory(tableRecipe.getTier()));
        }
        return false;
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        return AVRecipeTransferHelper.GuiSlotToIngredientMap.emi(menu, recipe);
    }

    private EmiRecipeCategory getCategory(int tier) {
        return switch (tier) {
            case 1 -> SculkCraftingTableCategory.CATEGORY;
            case 2 -> NetherCraftingTableCategory.CATEGORY;
            case 3 -> EndCraftingTableCategory.CATEGORY;
            case 4 -> ExtremeCraftingTableCategory.CATEGORY;
            default -> throw new IllegalArgumentException("Invalid tier: " + tier);
        };
    }
}
