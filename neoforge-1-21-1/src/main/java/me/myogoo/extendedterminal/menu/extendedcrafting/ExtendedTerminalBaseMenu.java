package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.SlotSemantic;
import appeng.menu.slot.CraftingMatrixSlot;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;


public class ExtendedTerminalBaseMenu extends ETTerminalBaseMenu<ITableRecipe> {
    private final ETCraftingBaseSlot outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    private final CraftingMatrixSlot[] craftingSlots;
    @Nullable
    private TableCraftingInput lastTestedInput;

    public ExtendedTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host,etMenuType, config);
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());
        for(int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this,craftingGridInv,i), this.menuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new ExCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this,this.menuType),
                this.menuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);
    }

    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if(checkCraftingOnlyActive()) return;

        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for(var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        var testInput = TableCraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(),testItems,this.menuType.getTier());

        if (!forceUpdate && Objects.equals(this.lastTestedInput,testInput)) {
            return;
        }

        var level = getPlayer().level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), testInput, level)
                .orElse(null);
        this.lastTestedInput = testInput;

        if(this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(testInput,registryAccess()));
        }
    }

    @Override public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(menuType.getCraftingInventory());
    }

}
