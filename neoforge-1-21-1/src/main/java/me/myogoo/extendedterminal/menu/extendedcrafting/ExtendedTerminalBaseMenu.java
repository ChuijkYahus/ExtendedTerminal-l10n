package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import appeng.api.inventories.ISegmentedInventory;
import appeng.menu.slot.CraftingMatrixSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import java.util.ArrayList;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.me.storage.LinkStatusRespectingInventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ExtendedTerminalBaseMenu extends ETTerminalBaseMenu<ITableRecipe> {
    protected final ETCraftingBaseSlot<?, ?> outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    protected final CraftingMatrixSlot[] craftingSlots;

    @Nullable
    protected TableCraftingInput lastTestedInput;

    public ExtendedTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host, etMenuType, config);
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];

        var craftingGridInv = this.craftingInventoryHost.getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    this.menuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = createOutputSlot(linkStatusInventory, craftingGridInv),
                this.menuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);
    }

    protected ETCraftingBaseSlot<?, ?> createOutputSlot(MEStorage storage, InternalInventory craftingGridInv) {
        return new ExCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                this.energySource, storage, craftingGridInv, craftingGridInv, this, this.menuType);
    }


    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    protected List<ItemStack> getCraftingSlotItems() {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        return testItems;
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(menuType.getCraftingInventory());
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) return;

        var testItems = getCraftingSlotItems();
        var testInput = createTableInput(testItems, null);

        if (!forceUpdate && Objects.equals(this.lastTestedInput, testInput)) {
            return;
        }

        var level = getPlayer().level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), testInput, level)
                .orElse(null);
        this.lastTestedInput = testInput;

        if (this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(testInput, registryAccess()));
        }
    }

    public TableCraftingInput createTableInput(List<ItemStack> items, @Nullable ITableRecipe recipe) {
        return TableCraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items, getInputTier(recipe));
    }

    protected int getInputTier(@Nullable ITableRecipe recipe) {
        return this.menuType.getTier();
    }


}
