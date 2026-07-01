package me.myogoo.extendedterminal.menu.extendedcrafting.slot;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExCraftingTerminalSlot extends ETCraftingBaseSlot<ITableRecipe, TableCraftingInput> {
    public ExCraftingTerminalSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix, ICraftingGridMenu ccp, ETMenuType menuType) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp, menuType);
    }

    @Override
    protected RecipeHolder<ITableRecipe> findRecipe(TableCraftingInput ic, Level level) {
        if (this.menu instanceof ExtendedTerminalBaseMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentRecipe();

            if (recipe != null && recipe.value().matches(ic, level)) {
                return terminalMenu.getCurrentRecipe();
            }
        }

        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), ic, level).orElse(null);
    }

    @Override
    protected NonNullList<ItemStack> getETRemainingItems(TableCraftingInput ic, Level level) {
        if (this.menu instanceof ExtendedTerminalBaseMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentRecipe();

            if (recipe != null && recipe.value().matches(ic, level)) {
                return terminalMenu.getCurrentRecipe().value().getRemainingItems(ic);
            }
        }
        return super.getRemainingItems(ic,level);
    }

    @Override
    protected void makeItem(Player player, ItemStack stack) {
        //this.amountCrafted += stack.getCount();
        this.checkTakeAchievements(stack);

        var items = NonNullList.withSize(this.craftInv.size(), ItemStack.EMPTY);
        for(int i = 0; i < this.craftInv.size(); i++) {
            items.set(i, this.craftInv.getStackInSlot(i));
        }
        TableCraftingInput positioned = TableCraftingInput.of(
                this.menuType.getGridSideLength(), this.menuType.getGridSideLength(), items, this.menuType.getTier());

        CommonHooks.setCraftingPlayer(player);
        var remainingItems = this.getETRemainingItems(positioned,player.level());
        CommonHooks.setCraftingPlayer(null);

        for(int y = 0; y < menuType.getGridSideLength(); y++) {
            for(int x = 0; x < menuType.getGridSideLength(); x++) {
                var slotIdx = y * menuType.getGridSideLength() + x;
                var remainderIdx = (y - positioned.top()) * 3 + (x - positioned.left());

                // Consumes the item from the grid
                this.craftInv.extractItem(slotIdx, 1, false);

                if (remainderIdx >= 0 && remainderIdx < remainingItems.size()) {
                    var remainingInSlot = remainingItems.get(remainderIdx);
                    if (!remainingInSlot.isEmpty()) {
                        if (this.craftInv.getStackInSlot(slotIdx).isEmpty()) {
                            this.craftInv.setItemDirect(slotIdx, remainingInSlot);
                        } else if (!player.getInventory().add(remainingInSlot)) {
                            player.drop(remainingInSlot, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected ItemStack craftItem(Player p, MEStorage inv, KeyCounter all) {
        // update crafting matrix...
        var is = this.getItem().copy();
        if (is.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Make sure the item in the slot is still the same item as before
        final var set = new ItemStack[this.getPattern().size()];
        // Safeguard for empty slots in the inventory for now
        Arrays.fill(set, ItemStack.EMPTY);

        // add one of each item to the items on the board...
        var level = p.level();
        if (!level.isClientSide()) {
            final var ic = NonNullList.withSize(menuType.getGridSize(), ItemStack.EMPTY);
            for (var x = 0; x < menuType.getGridSize(); x++) {
                ic.set(x, this.getPattern().getStackInSlot(x));
            }
            var recipeInput = TableCraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), ic, menuType.getTier());

            final var r = this.findRecipe(recipeInput, level);
            setRecipeUsed(r);

            if (r == null) {
                return ItemStack.EMPTY;
            }

            is = r.value().assemble(recipeInput, level.registryAccess());

            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var x = 0; x < this.getPattern().size(); x++) {
                    if (!this.getPattern().getStackInSlot(x).isEmpty()) {
                        set[x] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, r.value(), is,
                                recipeInput.width(), recipeInput.height(),
                                ic,
                                this.getPattern().getStackInSlot(x), x, all,
                                filter,
                                menuType);
                        ic.set(x, set[x]);
                    }
                }
            }
        }

        this.makeItem(p, is);
        this.postCraft(p, inv, set, is);

        p.containerMenu.slotsChanged(this.craftInv.toContainer());
        return is;
    }


    private static ItemStack extractItemsByRecipe(IEnergySource energySrc,
                                                  IActionSource mySrc,
                                                  MEStorage src,
                                                  Level level,
                                                  ITableRecipe r,
                                                  ItemStack output,
                                                  int gridWidth, int gridHeight,
                                                  List<ItemStack> craftingItems,
                                                  ItemStack providedTemplate,
                                                  int slot,
                                                  KeyCounter items,
                                                  IPartitionList filter,
                                                  ETMenuType menuType) {
        if (energySrc.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.9) {
            if (providedTemplate == null) {
                return ItemStack.EMPTY;
            }

            var ae_req = AEItemKey.of(providedTemplate);

            if (filter == null || filter.isListed(ae_req)) {
                var extracted = src.extract(ae_req, 1, Actionable.MODULATE, mySrc);
                if (extracted > 0) {
                    energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    return ae_req.toStack();
                }
            }

            var checkFuzzy = !providedTemplate.getComponents().isEmpty() || providedTemplate.isDamageableItem();

            if (items != null && checkFuzzy) {
                var craftingInputItems = new ArrayList<>(craftingItems);

                for (var x : items) {
                    if (x.getKey() instanceof AEItemKey itemKey) {
                        if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {

                            craftingInputItems.set(slot, itemKey.toStack());
                            var adjustedCraftingInput = TableCraftingInput.of(gridWidth, gridHeight, craftingInputItems,menuType.getTier());
                            if (r.matches(adjustedCraftingInput, level)
                                    && ItemStack.matches(r.assemble(adjustedCraftingInput, level.registryAccess()),
                                    output)) {
                                if (filter == null || filter.isListed(itemKey)) {
                                    var ex = src.extract(itemKey, 1, Actionable.MODULATE, mySrc);
                                    if (ex > 0) {
                                        energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                                        return itemKey.toStack();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
