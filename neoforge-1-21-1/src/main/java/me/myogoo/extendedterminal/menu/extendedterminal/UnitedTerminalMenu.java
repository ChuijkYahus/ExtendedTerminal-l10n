package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.CraftingTermMenu;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.TableTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UnitedTerminalMenu extends TableTerminalBaseMenu<Recipe<RecipeInput>> {
    public static final String ACTION_REMEMBER_RECIPE_TYPE = "rememberRecipeType";
    private static final String ACTION_SELECT_NEXT_RECIPE_TYPE = "selectNextRecipeType";
    private static final String ACTION_SELECT_PREVIOUS_RECIPE_TYPE = "selectPreviousRecipeType";
    public static final MenuType<UnitedTerminalMenu> TYPE = MenuTypeBuilder
            .create(UnitedTerminalMenu::new, IUnitedTerminalHost.class)
            .buildUnregistered(ETMenuType.UNITED_TERMINAL.getId());

    @Nullable
    private MyoTableRecipe currentRecipe;
    @GuiSync(0)
    private MyoRecipeType selectedRecipeType = MyoRecipeType.VANILLA;
    @Nullable
    protected MyoTableInput lastTestedInput;

    public UnitedTerminalMenu(MenuType<?> menuType, int id, Inventory ip, IUnitedTerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.UNITED_TERMINAL, ExtendedCraftingConfig.INSTANCE.getUltimateConfig()); // 이거 나중에 고쳐야함

        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new UnitedCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this,this.menuType),
                this.menuType.getSlotSemanticResult());


        registerClientAction(ACTION_REMEMBER_RECIPE_TYPE, Boolean.class, this::setRememberRecipeType);
        registerClientAction(ACTION_SELECT_NEXT_RECIPE_TYPE, this::selectNextRecipeType);
        registerClientAction(ACTION_SELECT_PREVIOUS_RECIPE_TYPE, this::selectPreviousRecipeType);
        loadSavedRecipeType();
        updateCurrentRecipeAndOutput(true);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        if (checkCraftingOnlyActive()) return;

        if (!this.selectedRecipeType.isActive()) {
            this.selectedRecipeType = MyoRecipeType.VANILLA;
            if (getHost() instanceof IUnitedTerminalHost unhost) {
                unhost.setLastRecipeType(MyoRecipeType.VANILLA);
            }
        }

        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : this.craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }

        var testInput = MyoTableInput.create(menuType.getGridSideLength(), menuType.getGridSideLength(), testItems, this.selectedRecipeType.tier());

        if (!forceUpdate && Objects.equals(this.lastTestedInput, testInput)) {
            return;
        }

        this.currentRecipe = findUnitedRecipe(testInput);
        this.lastTestedInput = testInput;

        if (this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.assemble(testInput, getPlayer().level(), this.selectedRecipeType));
        }
    }

    public MyoTableRecipe getCurrentMyoRecipe() {
        return currentRecipe;
    }

    @Override
    public RecipeHolder<Recipe<RecipeInput>> getCurrentRecipe() {
        return new RecipeHolder<>(currentRecipe.id(), currentRecipe.get());
    }

    public MyoRecipeType getSelectedRecipeType() {
        return selectedRecipeType;
    }

    public void setSelectedRecipeType(MyoRecipeType recipeType) {
        if (recipeType == null
                || !recipeType.isActive()
                || this.selectedRecipeType == recipeType) {
            return;
        }
        this.selectedRecipeType = recipeType;
        saveSavedRecipeType(recipeType);
        this.currentRecipe = null;
        this.lastTestedInput = null;
        updateCurrentRecipeAndOutput(true);
    }

    private void loadSavedRecipeType() {
        if (!(getHost() instanceof IUnitedTerminalHost host) || !host.shouldRememberRecipeType()) {
            return;
        }

        var remembered = host.getLastRecipeType();
        if (remembered != null && remembered.isActive()) {
            this.selectedRecipeType = remembered;
        }
    }

    private void saveSavedRecipeType(MyoRecipeType recipeType) {
        if (!isClientSide() && getHost() instanceof IUnitedTerminalHost host && host.shouldRememberRecipeType()) {
            host.setLastRecipeType(recipeType);
        }
    }

    public boolean rememberRecipeType() {
        return !(getHost() instanceof IUnitedTerminalHost host) || host.shouldRememberRecipeType();
    }

    public void setRememberRecipeType(boolean remember) {
        if (isClientSide()) {
            sendClientAction(ACTION_REMEMBER_RECIPE_TYPE, remember);
            return;
        }

        if (getHost() instanceof IUnitedTerminalHost host) {
            host.setRememberRecipeType(remember);
            if (remember) {
                host.setLastRecipeType(getSelectedRecipeType());
            }
        }
    }

    public void selectNextRecipeType() {
        selectRecipeTypeOffset(1, ACTION_SELECT_NEXT_RECIPE_TYPE);
    }

    public void selectPreviousRecipeType() {
        selectRecipeTypeOffset(-1, ACTION_SELECT_PREVIOUS_RECIPE_TYPE);
    }

    private void selectRecipeTypeOffset(int offset, String clientAction) {
        if (isClientSide()) {
            sendClientAction(clientAction);
            return;
        }
        var values = getActiveRecipeTypes();
        if (values.size() <= 1) {
            return;
        }
        var currentIndex = values.indexOf(this.selectedRecipeType);
        setSelectedRecipeType(values.get(Math.floorMod(currentIndex + offset, values.size())));
    }

    public static List<MyoRecipeType> getActiveRecipeTypes() {
        var recipeTypes = new ArrayList<MyoRecipeType>(MyoRecipeType.values().length);
        for (var recipeType : MyoRecipeType.values()) {
            if (recipeType.isActive()) {
                recipeTypes.add(recipeType);
            }
        }
        return recipeTypes;
    }

    @Nullable
    public MyoTableRecipe findUnitedRecipe(RecipeInput rawInput) {
        if (!selectedRecipeType.isActive()) {
            selectedRecipeType = MyoRecipeType.VANILLA;
            return findUnitedRecipe(rawInput);
        }

        if(!(rawInput instanceof MyoTableInput input)) {
            return null;
        }

        var level = getPlayer().level();
        if (selectedRecipeType.getMyomodAnnotation().equals(ExtendedCrafting.class)) {
            return findExtendedCraftingRecipe(level, input);
        } else if (selectedRecipeType.getMyomodAnnotation().equals(AvaritiaNeo.class)) {
            return findAvaritiaNeoRecipe(level, input);
        } else if (selectedRecipeType.getMyomodAnnotation().equals(ReAvaritia.class)) {
            return findReAvaritiaRecipe(level, input);
        } else {
            return findVanillaRecipe(level, input);
        }
    }

    @Override
    public CraftingTermMenu.MissingIngredientSlots findMissingIngredients(Map<Integer, Ingredient> ingredients) {
        return super.findMissingIngredients(ingredients);
    }

    @Nullable
    private static MyoTableRecipe findExtendedCraftingRecipe(Level level, MyoTableInput input) {
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), input.cast(TableCraftingInput.class), level)
                .map(recipe -> MyoTableRecipe.of(recipe.value(), recipe.id()))
                .orElse(null);
    }

    @Nullable
    private static MyoTableRecipe findVanillaRecipe(Level level, MyoTableInput input) {
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input.cast(), level)
                .map(recipe -> MyoTableRecipe.of(recipe.value(), recipe.id()))
                .orElse(null);
    }

    @Nullable
    private static MyoTableRecipe findAvaritiaNeoRecipe(Level level, MyoTableInput input) {
        return level.getRecipeManager().getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), input.cast(), level)
                .map(recipe -> MyoTableRecipe.of(recipe.value(), recipe.id()))
                .orElse(null);
    }

    @Nullable
    private static MyoTableRecipe findReAvaritiaRecipe(Level level, MyoTableInput input) {
        return level.getRecipeManager().getRecipeFor(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), input.cast(TierInput.class), level)
                .map(recipe -> MyoTableRecipe.of(recipe.value(), recipe.id()))
                .orElse(null);

    }
}
