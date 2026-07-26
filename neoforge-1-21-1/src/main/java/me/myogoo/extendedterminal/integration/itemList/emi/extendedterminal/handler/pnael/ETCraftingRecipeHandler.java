package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.integration.itemList.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.integration.itemList.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ETCraftingRecipeHandler<T extends ETTerminalBaseMenu<?>> extends AbstractEmiTableRecipeHandler<T> {
    public ETCraftingRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = super.getInputSources(menu);
        if (menu instanceof ETTerminalMenu) {
            slots.addAll(menu.getSlots(ETSlotSemantics.STONECUTTING_INPUT));
            slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_BASE));
            slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_TEMPLATE));
            slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_ADDITION));
        }
        return slots;
    }

    @Override
    protected Result transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = holder != null ? holder.id() : null;
        var recipe = holder != null ? holder.value() : null;
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }
        if (recipe != null && !recipe.canCraftInDimensions(3, 3)) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        if (recipe == null) {
            recipe = createFakeRecipe(emiRecipe, menu instanceof UnitedTerminalMenu);
        }
        if (!(recipe instanceof CraftingRecipe cRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        var adapterRecipe = MyoTableRecipe.of(cRecipe, recipeId);
        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        Set<Integer> inputSlotKeys = menu instanceof UnitedTerminalMenu
                ? slotToIngredientMap.keySet()
                : Set.of();
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots(), inputSlotKeys);
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots, inputSlotKeys);
            }
        } else {
            boolean craftingMissing = AbstractContainerScreen.hasControlDown();
            if (menu instanceof UnitedTerminalMenu) {
                performTransfer(menu, adapterRecipe, craftingMissing, MyoRecipeType.VANILLA);
            } else if (holder != null) {
                ETCraftingRecipeTransferHelper.performTransfer((ETTerminalMenu) menu,
                        (RecipeHolder<CraftingRecipe>) holder,
                        craftingMissing);
            } else {
                ETCraftingRecipeTransferHelper.performTransfer((ETTerminalMenu) menu, cRecipe, null,
                        craftingMissing);
            }
        }
        return Result.createSuccessful();
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, MyoTableRecipe recipe) {
        if (menu instanceof UnitedTerminalMenu unitedMenu) {
            return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(unitedMenu, recipe);
        }
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap((ETTerminalMenu) menu, recipe.get());
    }

    private Recipe<?> createFakeRecipe(EmiRecipe recipe, boolean preserveShape) {
        var ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

        for (int i = 0; i < Math.min(recipe.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(recipe.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        if (preserveShape && recipe instanceof EmiCraftingRecipe craftingRecipe) {
            if (craftingRecipe.shapeless) {
                var shapelessIngredients = NonNullList.<Ingredient>create();
                ingredients.stream()
                        .filter(ingredient -> !ingredient.isEmpty())
                        .forEach(shapelessIngredients::add);
                return new ShapelessRecipe("", CraftingBookCategory.MISC, ItemStack.EMPTY, shapelessIngredients);
            }

            int minX = 3;
            int minY = 3;
            int maxX = -1;
            int maxY = -1;
            for (int i = 0; i < ingredients.size(); i++) {
                if (!ingredients.get(i).isEmpty()) {
                    int x = i % 3;
                    int y = i / 3;
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }

            if (maxX >= minX && maxY >= minY) {
                int width = maxX - minX + 1;
                int height = maxY - minY + 1;
                var shapedIngredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        shapedIngredients.set(y * width + x, ingredients.get((y + minY) * 3 + x + minX));
                    }
                }
                var pattern = new ShapedRecipePattern(width, height, shapedIngredients, Optional.empty());
                return new ShapedRecipe("", CraftingBookCategory.MISC, pattern, ItemStack.EMPTY);
            }
        }

        var pattern = new ShapedRecipePattern(3, 3, ingredients, Optional.empty());
        return new ShapedRecipe("", CraftingBookCategory.MISC, pattern, ItemStack.EMPTY);
    }
}
