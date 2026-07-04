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
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Arrays;
import java.util.List;

public class UnitedCraftingTerminalSlot extends ETCraftingBaseSlot<Recipe<RecipeInput>, RecipeInput> {
    public UnitedCraftingTerminalSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage,
                                      InternalInventory cMatrix, InternalInventory secondMatrix, ICraftingGridMenu ccp,
                                      ETMenuType menuType) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp, menuType);
    }

    @Override
    protected RecipeHolder<Recipe<RecipeInput>> findRecipe(RecipeInput ic, Level level) {
        return null;
    }

    @Override
    protected NonNullList<ItemStack> getETRemainingItems(RecipeInput ic, Level level) {
        return NonNullList.create();
    }

    private NonNullList<ItemStack> getMyoRemainingItems(MyoTableInput ic, Level level) {
        if (this.menu instanceof UnitedTerminalMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentMyoRecipe();

            if (recipe != null && recipe.matches(ic, level, terminalMenu.getSelectedRecipeType())) {
                return terminalMenu.getCurrentMyoRecipe().getRemainingItems(ic, terminalMenu.getSelectedRecipeType());
            }
        }
        return NonNullList.create();
    }

    private MyoTableRecipe findRecipe(MyoTableInput ic, Level level) {
        if (this.menu instanceof UnitedTerminalMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentMyoRecipe();
            if (recipe != null && recipe.matches(ic, level, terminalMenu.getSelectedRecipeType())) {
                return terminalMenu.getCurrentMyoRecipe();
            }
        }
        return null;
    }

    @Override
    protected void makeItem(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        if (!(this.menu instanceof UnitedTerminalMenu termMenu)) {
            return;
        }

        var items = NonNullList.withSize(this.craftInv.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.craftInv.size(); i++) {
            items.set(i, this.craftInv.getStackInSlot(i));
        }

        var input = MyoTableInput.create(this.menuType.getGridSideLength(), this.menuType.getGridSideLength(), items, termMenu.getSelectedRecipeType().tier());

        CommonHooks.setCraftingPlayer(player);
        var remainingItems = this.getMyoRemainingItems(input, player.level());
        CommonHooks.setCraftingPlayer(null);

        for (int y = 0; y < menuType.getGridSideLength(); y++) {
            for (int x = 0; x < menuType.getGridSideLength(); x++) {
                var slotIdx = y * menuType.getGridSideLength() + x;
                var remainderIdx = (y - input.top()) * input.width() + (x - input.left());

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
    protected ItemStack craftItem(Player player, MEStorage inv, KeyCounter all) {
        var result = this.getItem().copy();
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!(this.menu instanceof UnitedTerminalMenu termMenu)) {
            return ItemStack.EMPTY;
        }

        final var set = new ItemStack[this.getPattern().size()];
        Arrays.fill(set, ItemStack.EMPTY);

        var level = player.level();
        if (!level.isClientSide()) {
            final var ic = NonNullList.withSize(menuType.getGridSize(), ItemStack.EMPTY);
            for (var x = 0; x < menuType.getGridSize(); x++) {
                ic.set(x, this.getPattern().getStackInSlot(x));
            }
            var recipeInput = MyoTableInput.create(menuType.getGridSideLength(), menuType.getGridSideLength(), ic, termMenu.getSelectedRecipeType().tier());

            final var recipe = this.findRecipe(recipeInput, level);
            setRecipeUsed(recipe.castHolderRecipe());
            if (recipe == null) {
                return ItemStack.EMPTY;
            }

            result = recipe.assemble(recipeInput, level, termMenu.getSelectedRecipeType());

            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var x = 0; x < this.getPattern().size(); x++) {
                    if (!this.getPattern().getStackInSlot(x).isEmpty()) {
                        set[x] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, recipe, termMenu.getSelectedRecipeType(), result,
                                ic, this.getPattern().getStackInSlot(x), x, all, filter);
                        ic.set(x, set[x]);
                    }
                }
            }
        }

        this.makeItem(player, result);
        this.postCraft(player, inv, set, result);
        player.containerMenu.slotsChanged(this.craftInv.toContainer());
        return result;
    }

    private static ItemStack extractItemsByRecipe(IEnergySource energySrc, IActionSource mySrc, MEStorage src,
                                                  Level level, MyoTableRecipe recipe, MyoRecipeType recipeType, ItemStack output,
                                                  List<ItemStack> craftingItems, ItemStack providedTemplate, int slot,
                                                  KeyCounter items, IPartitionList filter) {
        if (energySrc.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) <= 0.9 || providedTemplate == null) {
            return ItemStack.EMPTY;
        }

        var aeReq = AEItemKey.of(providedTemplate);
        if (filter == null || filter.isListed(aeReq)) {
            var extracted = src.extract(aeReq, 1, Actionable.MODULATE, mySrc);
            if (extracted > 0) {
                energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                return aeReq.toStack();
            }
        }

        var checkFuzzy = !providedTemplate.getComponents().isEmpty() || providedTemplate.isDamageableItem();
        if (items != null && checkFuzzy) {
            var craftingInputItems = NonNullList.withSize(craftingItems.size(), ItemStack.EMPTY);
            for (int i = 0; i < craftingItems.size(); i++) {
                craftingInputItems.set(i, craftingItems.get(i).copy());
            }

            for (var entry : items) {
                if (entry.getKey() instanceof AEItemKey itemKey) {
                    if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {
                        craftingInputItems.set(slot, itemKey.toStack());
                        var adjustedCraftingInput = MyoTableInput.create(9, 9, craftingItems, recipeType.tier());
                        if (recipe.matches(adjustedCraftingInput, level, recipeType) && ItemStack.matches(recipe.assemble(adjustedCraftingInput, level, recipeType), output)) {
                            if (filter == null || filter.isListed(itemKey)) {
                                var extracted = src.extract(itemKey, 1, Actionable.MODULATE, mySrc);
                                if (extracted > 0) {
                                    energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                                    return itemKey.toStack();
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
