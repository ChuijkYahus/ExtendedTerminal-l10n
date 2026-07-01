package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.AppEngCraftingSlot;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.FakeAnvilMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


public class ETAnvilSlot extends AppEngCraftingSlot {
    private final FakeAnvilMenu anvilDelegate;
    private final Slot LEFT_INPUT;
    private final Slot RIGHT_INPUT;

    public ETAnvilSlot(Player player, InternalInventory anvilInv, FakeAnvilMenu anvilDelegate, ETTerminalMenu menu) {
        super(player, anvilInv);
        this.anvilDelegate = anvilDelegate;
        this.setMenu(menu);
        LEFT_INPUT = getMenu().getSlots(ETSlotSemantics.ANVIL_LEFT_INPUT).getFirst();
        RIGHT_INPUT = getMenu().getSlots(ETSlotSemantics.ANVIL_RIGHT_INPUT).getFirst();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        ETTerminalMenu terminalMenu = this.getMenu() instanceof ETTerminalMenu menu ? menu : null;
        if (terminalMenu != null && !terminalMenu.prepareAnvilExperienceForTake(player)) {
            return;
        }
        try {
            anvilDelegate.ET$OnTakeWithoutExperience(player, stack);

            ItemStack newLeft = anvilDelegate.slots.get(0).getItem().copy();
            ItemStack newRight = anvilDelegate.slots.get(1).getItem().copy();

            if (terminalMenu != null) {
                terminalMenu.onAnvilTake(() -> {
                    LEFT_INPUT.set(newLeft);
                    RIGHT_INPUT.set(newRight);
                });
            }
        } finally {
            if (terminalMenu != null) {
                terminalMenu.clearPreparedAnvilExperienceForTake();
            }
        }
    }

    @Override
    public boolean mayPickup(Player player) {
        if (this.getMenu() instanceof ETTerminalMenu terminalMenu) {
            return super.mayPickup(player) && terminalMenu.prepareAnvilExperienceForTake(player);
        }
        return anvilDelegate.ET$MayPickup(player, super.mayPickup(player));
    }
}
