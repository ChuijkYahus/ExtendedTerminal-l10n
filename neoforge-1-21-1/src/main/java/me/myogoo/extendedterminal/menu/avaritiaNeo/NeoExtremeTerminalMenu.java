package me.myogoo.extendedterminal.menu.avaritiaNeo;

import appeng.api.storage.ITerminalHost;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.config.avaritiaNeo.AvaritiaNeoConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.TableTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.avaritiaNeo.slot.AvaritiaNeoCraftingTerminalSlot;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class NeoExtremeTerminalMenu extends TableTerminalBaseMenu<RecipeExtremeCrafting> {
    public static final MenuType<NeoExtremeTerminalMenu> TYPE = MenuTypeBuilder
            .create(NeoExtremeTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.NEO_EXTREME_TERMINAL.getId());

    public NeoExtremeTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.NEO_EXTREME_TERMINAL, AvaritiaNeoConfig.INSTANCE.getExtremeConfig());
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new AvaritiaNeoCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this),
                this.menuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);
        updateCurrentRecipeAndOutput(true);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) return;

        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        var testInput = MyoTableInput.create(menuType.getGridSideLength(), menuType.getGridSideLength(), testItems);

        if (!forceUpdate && Objects.equals(this.lastTestedInput, testInput)) {
            return;
        }

        var level = getPlayer().level();
        var castedInput = testInput.cast();

        this.currentRecipe = level.getRecipeManager().getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), castedInput, level)
                .orElse(null);
        this.lastTestedInput = testInput;

        if (this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(castedInput, registryAccess()));
        }
    }
}
