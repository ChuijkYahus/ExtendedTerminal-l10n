package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.AppEngCraftingSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.FakeAnvilMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ETAnvilSlot extends AppEngCraftingSlot {
    private final FakeAnvilMenu anvilDelegate;

    public ETAnvilSlot(Player player, InternalInventory anvilInv, FakeAnvilMenu anvilDelegate, ETTerminalMenu menu) {
        super(player, anvilInv);
        this.anvilDelegate = anvilDelegate;
        this.setMenu(menu);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        ETTerminalMenu terminalMenu = this.getMenu() instanceof ETTerminalMenu menu ? menu : null;
        if (terminalMenu != null && !terminalMenu.consumeAnvilExperience(player)) {
            return;
        }
        anvilDelegate.et$onTakeWithoutExperience(player, stack);

        ItemStack newLeft = anvilDelegate.slots.get(0).getItem().copy();
        ItemStack newRight = anvilDelegate.slots.get(1).getItem().copy();

        if (terminalMenu != null) {
            terminalMenu.onAnvilTake(newLeft, newRight);
        }
    }

    @Override
    public boolean mayPickup(Player player) {
        if (this.getMenu() instanceof ETTerminalMenu terminalMenu) {
            return super.mayPickup(player) && terminalMenu.canPayAnvilCost(player);
        }
        return anvilDelegate.et$mayPickup(player, super.mayPickup(player));
    }
}
