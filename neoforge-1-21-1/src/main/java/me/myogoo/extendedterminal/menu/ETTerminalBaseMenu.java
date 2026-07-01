package me.myogoo.extendedterminal.menu;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.ITerminalHost;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.util.inv.PlayerInternalInventory;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;
import me.myogoo.extendedterminal.api.annotation.Polymorph;

public abstract class ETTerminalBaseMenu<R extends Recipe<?>> extends MEStorageMenu implements ICraftingGridMenu {
    protected RecipeHolder<R> currentRecipe;
    private final IETTerminalConfig config;
    protected final ETMenuType menuType;
    private static final String ACTION_CLEAR_TO_PLAYER = "clearToPlayer";

    public ETTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host);
        this.menuType = etMenuType;
        this.config = config;
        registerClientAction(ACTION_CLEAR_TO_PLAYER, ResourceLocation.class, this::clearToPlayerInventory);
        if (MyotusAPI.integrations().isLoaded(Polymorph.class)) {
            registerClientAction("polyeng$selectRecipe", () -> updateCurrentRecipeAndOutput(true));
        }

    }

    public RecipeHolder<R> getCurrentRecipe() {
        return this.currentRecipe;
    }

    //Abstract Methods
    public abstract void clearCraftingGrid();

    public SlotSemantic getCraftingGridSlotSemantic() {
        return this.menuType.getSlotSemanticGrid();
    }

    public SlotSemantic getOutputSlotSemantic() {
        return this.menuType.getSlotSemanticResult();
    }

    public int getCraftingGridSize() {
        return this.menuType.getGridSize();
    }

    public int getCraftingGridWidth() {
        return this.menuType.getGridSideLength();
    }

    public int getCraftingGridHeight() {
        return this.menuType.getGridSideLength();
    }

    protected abstract void updateCurrentRecipeAndOutput(boolean forceUpdate);

    public ETMenuType getETMenuType() {
        return this.menuType;
    }
    //Override Methods
    @Override
    public void slotsChanged(Container container) {
        updateCurrentRecipeAndOutput(false);
    }

    @Override
    public boolean hasIngredient(Ingredient ingredient, Object2IntOpenHashMap<Object> reservedAmounts) {
        for (var slot : getSlots(this.getCraftingGridSlotSemantic())) {
            var stackInSlot = slot.getItem();
            if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                var reservedAmount = reservedAmounts.getOrDefault(slot, 0);
                if (stackInSlot.getCount() > reservedAmount) {
                    reservedAmounts.merge(slot, 1, Integer::sum);
                    return true;
                }
            }
        }
        return super.hasIngredient(ingredient, reservedAmounts);
    }

    @Override
    public void startAutoCrafting(List<AutoCraftEntry> toCraft) {
        CraftConfirmMenu.openWithCraftingList(getActionHost(), (ServerPlayer) getPlayer(), getLocator(), toCraft);
    }

    @Override
    public IEnergySource getEnergySource() {
        return this.energySource;
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        if (this.getSlot(slot) instanceof ETCraftingBaseSlot<?,?> craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    craftingSlot.doClick(action, player);
            }
            return;
        }
        super.doAction(player, action, slot, id);
    }

    protected boolean isCraftable(ItemStack itemStack) {
        var clientRepo = getClientRepo();

        if (clientRepo != null) {
            for (var stack : clientRepo.getAllEntries()) {
                if (AEItemKey.matches(stack.getWhat(), itemStack) && stack.isCraftable()) {
                    return true;
                }
            }
        }
        return false;
    }

    public CraftingTermMenu.MissingIngredientSlots findMissingIngredients(Map<Integer, Ingredient> ingredients) {

        // Try to figure out if any slots have missing ingredients
        // Find every "slot" (in JEI parlance) that has no equivalent item in the item repo or player inventory
        Set<Integer> missingSlots = new HashSet<>(); // missing but not craftable
        Set<Integer> craftableSlots = new HashSet<>(); // missing but craftable

        // We need to track how many of a given item stack we've already used for other slots in the recipe.
        // Otherwise recipes that need 4x<item> will not correctly show missing items if at least 1 of <item> is in
        // the grid.
        var reservedGridAmounts = new Object2IntOpenHashMap<>();
        var playerItems = getPlayerInventory().items;
        var reservedPlayerItems = new int[playerItems.size()];

        for (var entry : ingredients.entrySet()) {
            var ingredient = entry.getValue();

            boolean found = false;
            // Player inventory is cheaper to check
            for (int i = 0; i < playerItems.size(); i++) {
                // Do not consider locked slots
                if (isPlayerInventorySlotLocked(i)) {
                    continue;
                }

                var stack = playerItems.get(i);
                if (stack.getCount() - reservedPlayerItems[i] > 0 && ingredient.test(stack)) {
                    reservedPlayerItems[i]++;
                    found = true;
                    break;
                }
            }

            // Then check the terminal screen's repository of network items
            if (!found) {
                // We use AE stacks to get an easily comparable item type key that ignores stack size
                if (hasIngredient(ingredient, reservedGridAmounts)) {
                    reservedGridAmounts.merge(ingredient, 1, Integer::sum);
                    found = true;
                }
            }

            // Check the terminal once again, but this time for craftable items
            if (!found) {
                for (var stack : ingredient.getItems()) {
                    if (isCraftable(stack)) {
                        craftableSlots.add(entry.getKey());
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                missingSlots.add(entry.getKey());
            }
        }

        return new CraftingTermMenu.MissingIngredientSlots(missingSlots, craftableSlots);
    }

    public void clearToPlayerInventory() {
        clearToPlayerInventory(getETMenuType().getCraftingInventory());
    }

    public void clearToPlayerInventory(ResourceLocation invId) {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR_TO_PLAYER, invId);
            return;
        }

        var playerInv = new PlayerInternalInventory(getPlayerInventory());
        var inv = ((ISegmentedInventory) getHost()).getSubInventory(invId);
        for (int i = 0; i < inv.size(); ++i) {
            for (int emptyLoop = 0; emptyLoop < 2; ++emptyLoop) {
                boolean allowEmpty = emptyLoop == 1;

                // Hotbar first
                final int HOTBAR_SIZE = 9;
                for (int j = HOTBAR_SIZE; j-- > 0; ) {
                    if (playerInv.getStackInSlot(j).isEmpty() == allowEmpty) {
                        inv.setItemDirect(i, playerInv.getSlotInv(j).addItems(inv.getStackInSlot(i)));
                    }
                }
                // Rest of inventory
                for (int j = HOTBAR_SIZE; j < Inventory.INVENTORY_SIZE; ++j) {
                    if (playerInv.getStackInSlot(j).isEmpty() == allowEmpty) {
                        inv.setItemDirect(i, playerInv.getSlotInv(j).addItems(inv.getStackInSlot(i)));
                    }
                }
            }
        }
    }

    protected boolean checkCraftingOnlyActive() {
        return config.enableCraftOnlyPowered() && (this.getGridNode() == null || (this.getGridNode() != null && !this.getGridNode().isActive()));
    }
}
