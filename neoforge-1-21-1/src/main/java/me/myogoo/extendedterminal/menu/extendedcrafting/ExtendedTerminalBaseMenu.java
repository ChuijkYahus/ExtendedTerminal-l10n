package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.me.storage.LinkStatusRespectingInventory;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.TableTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class ExtendedTerminalBaseMenu extends TableTerminalBaseMenu<ITableRecipe> {
    public ExtendedTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host, etMenuType, config);

        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new ExCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this,this.menuType),
                this.menuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) return;

        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        var testInput = MyoTableInput.create(menuType.getGridSideLength(), menuType.getGridSideLength(), testItems, this.menuType.getTier());

        if (!forceUpdate && Objects.equals(this.lastTestedInput, testInput)) {
            return;
        }

        var level = getPlayer().level();
        var castedInput = testInput.cast(TableCraftingInput.class);
        this.currentRecipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), castedInput, level)
                .orElse(null);
        this.lastTestedInput = testInput;

        if (this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(castedInput, registryAccess()));
        }
    }
}
