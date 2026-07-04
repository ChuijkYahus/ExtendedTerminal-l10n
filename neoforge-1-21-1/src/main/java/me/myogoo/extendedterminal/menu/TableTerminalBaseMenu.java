package me.myogoo.extendedterminal.menu;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.slot.CraftingMatrixSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class TableTerminalBaseMenu<R extends Recipe<?>> extends ETTerminalBaseMenu<R> {
    protected ETCraftingBaseSlot<?, ?> outputSlot;
    protected final CraftingMatrixSlot[] craftingSlots;
    protected final ISegmentedInventory craftingInventoryHost;

    @Nullable
    protected MyoTableInput lastTestedInput;

    public TableTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host, etMenuType, config);
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[etMenuType.getGridSize()];

        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());

        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i), this.menuType.getSlotSemanticGrid());
        }
    }

    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(menuType.getCraftingInventory());
    }
}
