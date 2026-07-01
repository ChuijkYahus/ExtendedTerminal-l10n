package me.myogoo.extendedterminal.client.ae2helpers;

import appeng.api.stacks.AEItemKey;
import appeng.menu.me.common.GridInventoryEntry;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.network.serverbound.ae2helpers.FillPendingCraftingSlotPacket;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ETAutoCraftingWatcher {
    public static final ETAutoCraftingWatcher INSTANCE = new ETAutoCraftingWatcher();

    private final Map<Integer, Ingredient> pendingSlots = new LinkedHashMap<>();
    private ETMenuType pendingMenuType;
    private boolean active;
    private int startDelay;

    private ETAutoCraftingWatcher() {
    }

    public void preparePending(ETTerminalBaseMenu<?> menu, List<Ingredient> ingredients, boolean craftMissing) {
        var map = new LinkedHashMap<Integer, Ingredient>();
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                map.put(i, ingredient);
            }
        }
        preparePending(menu, map, craftMissing);
    }

    public void preparePending(ETTerminalBaseMenu<?> menu, Map<Integer, Ingredient> ingredients, boolean craftMissing) {
        if (!craftMissing || ingredients.isEmpty()) {
            clear();
            return;
        }

        var missing = menu.findMissingIngredients(ingredients);
        setPending(menu, ingredients, missing.craftableSlots());
    }

    private void setPending(ETTerminalBaseMenu<?> menu, Map<Integer, Ingredient> ingredients, Set<Integer> craftableSlots) {
        pendingSlots.clear();
        for (var slot : craftableSlots) {
            var ingredient = ingredients.get(slot);
            if (ingredient != null && !ingredient.isEmpty()) {
                pendingSlots.put(slot, ingredient);
            }
        }
        pendingMenuType = menu.getETMenuType();
        active = !pendingSlots.isEmpty();
        startDelay = 5;
    }

    public void clear() {
        pendingSlots.clear();
        pendingMenuType = null;
        active = false;
        startDelay = 0;
    }

    public void onTick(ETTerminalBaseScreen<?, ?> screen) {
        if (!active) {
            return;
        }
        if (startDelay > 0) {
            startDelay--;
            return;
        }

        var menu = screen.getMenu();
        if (pendingMenuType != null && menu.getETMenuType() != pendingMenuType) {
            clear();
            return;
        }
        var clientRepo = menu.getClientRepo();
        if (clientRepo == null) {
            return;
        }

        var craftingSlots = menu.getSlots(menu.getCraftingGridSlotSemantic());
        var iterator = pendingSlots.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            int slotIndex = entry.getKey();
            if (slotIndex < 0 || slotIndex >= craftingSlots.size() || craftingSlots.get(slotIndex).hasItem()) {
                iterator.remove();
                continue;
            }

            var key = findAvailableItem(clientRepo.getByIngredient(entry.getValue()));
            if (key != null) {
                PacketDistributor.sendToServer(new FillPendingCraftingSlotPacket(slotIndex, key));
                iterator.remove();
                startDelay = 5;
                break;
            }
        }

        if (pendingSlots.isEmpty()) {
            clear();
        }
    }

    private AEItemKey findAvailableItem(Collection<GridInventoryEntry> entries) {
        for (var entry : entries) {
            if (entry.getStoredAmount() > 0 && entry.getWhat() instanceof AEItemKey itemKey) {
                return itemKey;
            }
        }
        return null;
    }
}
