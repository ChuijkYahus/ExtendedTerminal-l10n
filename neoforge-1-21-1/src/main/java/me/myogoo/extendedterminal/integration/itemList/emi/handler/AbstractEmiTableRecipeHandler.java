package me.myogoo.extendedterminal.integration.itemList.emi.handler;

import appeng.api.stacks.GenericStack;
import appeng.core.AEConfig;
import appeng.core.localization.ItemModText;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTableRecipeTransferHandler;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractEmiTableRecipeHandler<T extends ETTerminalBaseMenu<?>>
        extends ItemListTableRecipeTransferHandler<T>
        implements StandardRecipeHandler<T>, EmiTransferResult {
    private final Class<T> containerClass;

    public AbstractEmiTableRecipeHandler(Class<T> containerClass) {
        this.containerClass = containerClass;
    }

    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_HOTBAR));
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_INVENTORY));
        slots.addAll(menu.getSlots(menu.getCraftingGridSlotSemantic()));
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        return menu.getSlots(menu.getCraftingGridSlotSemantic());
    }

    @Override
    public @Nullable Slot getOutputSlot(T menu) {
        for (var slot : menu.getSlots(menu.getOutputSlotSemantic())) {
            return slot;
        }
        return null;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
        if (!AEConfig.instance().isExposeNetworkInventoryToEmi()) {
            return StandardRecipeHandler.super.getInventory(screen);
        }

        var list = new ArrayList<EmiStack>();

        for (Slot slot : getInputSources(screen.getMenu())) {
            list.add(EmiStack.of(slot.getItem()));
        }
        var menu = screen.getMenu();
        var repo = menu.getClientRepo();

        if (repo != null) {
            for (var entry : repo.getAllEntries()) {
                if (entry.getStoredAmount() <= 0) {
                    continue; // Skip items that are only craftable
                }
                var emiStack = EmiStackHelper
                        .toEmiStack(new GenericStack(entry.getWhat(), entry.getStoredAmount()));
                if (emiStack != null) {
                    list.add(emiStack);
                }
            }
        }


        return new EmiPlayerInventory(list);
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
        if (context.getType() == EmiCraftContext.Type.FILL_BUTTON) {
            return transferRecipe(recipe, context, false).canCraft();
        }
        return StandardRecipeHandler.super.canCraft(recipe, context);
    }

    protected abstract Result transferRecipe(T menu,
                                             @Nullable RecipeHolder<?> holder,
                                             EmiRecipe emiRecipe,
                                             boolean doTransfer);

    protected final Result transferRecipe(EmiRecipe emiRecipe, EmiCraftContext<T> context, boolean doTransfer) {
        if (!containerClass.isInstance(context.getScreenHandler())) {
            return Result.createNotApplicable();
        }

        T menu = containerClass.cast(context.getScreenHandler());
        var holder = getRecipeHolder(context.getScreenHandler().getPlayer().level(), emiRecipe);

        var result = transferRecipe(menu, holder, emiRecipe, doTransfer);
        if (result instanceof Result.Success && doTransfer) {
            Minecraft.getInstance().setScreen(context.getScreen());
        }
        return result;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return false;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return transferRecipe(recipe, context, true).canCraft();
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<T> context) {
        var tooltip = transferRecipe(recipe, context, false).getTooltip(recipe, context);
        if (tooltip != null) {
            return tooltip.stream()
                    .map(Component::getVisualOrderText)
                    .map(ClientTooltipComponent::create)
                    .toList();
        } else {
            return StandardRecipeHandler.super.getTooltip(recipe, context);
        }
    }

    @Override
    public void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
        transferRecipe(recipe, context, false).render(recipe, context, widgets, draw);
    }

    @Nullable
    private RecipeHolder<?> getRecipeHolder(Level level, EmiRecipe recipe) {
        if (recipe.getBackingRecipe() != null) {
            return recipe.getBackingRecipe();
        }
        if (recipe.getId() != null) {
            // TODO: This can produce false positives...
            return level.getRecipeManager().byKey(recipe.getId()).orElse(null);
        }
        return null;
    }


    @Nullable
    protected Result transferSetup(@Nullable RecipeHolder<?> holder, EmiRecipe emiRecipe, int gridSize) {
        if (holder == null) {
            return Result.createNotApplicable();
        }

        var recipe = holder.value();
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }

        if (!fitsInNxNGrid(recipe, gridSize)) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        return null;
    }

    protected Result doTransfer(T menu, ITableRecipeAdapter recipe, ResourceLocation id, boolean doTransfer) {
        // Find missing ingredient
        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, recipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots(),
                    slotToIngredientMap.keySet());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                // Highlight the slots with missing ingredients
                return new Result.PartiallyCraftable(missingSlots, slotToIngredientMap.keySet());
            }
        } else {
            // Thank you RS for pioneering this amazing feature! :)
            boolean craftMissing = AbstractContainerScreen.hasControlDown();
            performTransfer(menu, recipe, craftMissing, id);
        }
        return Result.createSuccessful();
    }

    private boolean fitsInNxNGrid(Recipe<?> recipe, int gridSize) {
        return recipe == null || recipe.canCraftInDimensions(gridSize, gridSize);
    }

    protected abstract boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe);

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe);
}
