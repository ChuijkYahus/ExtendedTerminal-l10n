package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.InternalInventory;
import appeng.helpers.InventoryAction;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedRecipeType;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import appeng.api.inventories.ISegmentedInventory;
import appeng.menu.slot.CraftingMatrixSlot;
import com.google.common.base.Preconditions;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.me.storage.LinkStatusRespectingInventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class UnitedTerminalMenu extends ETTerminalBaseMenu<Recipe<RecipeInput>> {
    protected final ETCraftingBaseSlot<?, ?> outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    protected final CraftingMatrixSlot[] craftingSlots;

    public static final String ACTION_REMEMBER_RECIPE_KIND = "rememberRecipeKind";
    private static final String ACTION_SELECT_NEXT_RECIPE_KIND = "selectNextRecipeKind";
    private static final String ACTION_SELECT_PREVIOUS_RECIPE_KIND = "selectPreviousRecipeKind";
    public static final MenuType<UnitedTerminalMenu> TYPE = MenuTypeBuilder
            .create(UnitedTerminalMenu::new, IUnitedTerminalHost.class)
            .buildUnregistered(ETMenuType.UNITED_TERMINAL.getId());

    @Nullable
    private UnitedRecipe currentUnitedRecipe;
    @GuiSync(0)
    private UnitedRecipeType selectedRecipeType = UnitedRecipeType.VANILLA;
    @Nullable
    private List<ItemStack> lastUnitedItems;

    public UnitedTerminalMenu(MenuType<?> menuType, int id, Inventory ip, IUnitedTerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.UNITED_TERMINAL, ExtendedCraftingConfig.INSTANCE.getUltimateConfig());
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];

        var craftingGridInv = this.craftingInventoryHost.getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    this.menuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new UnitedCraftingTerminalSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, storage, craftingGridInv, craftingGridInv, this, this.menuType),
                this.menuType.getSlotSemanticResult());

        registerClientAction(ACTION_REMEMBER_RECIPE_KIND, Boolean.class, this::setRememberRecipeType);
        registerClientAction(ACTION_SELECT_NEXT_RECIPE_KIND, this::selectNextRecipeKind);
        registerClientAction(ACTION_SELECT_PREVIOUS_RECIPE_KIND, this::selectPreviousRecipeKind);
        loadSavedRecipeType();
        updateCurrentRecipeAndOutput(true);
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

        if (!this.selectedRecipeType.isActive()) {
            this.selectedRecipeType = UnitedRecipeType.VANILLA;
            if (getHost() instanceof IUnitedTerminalHost unhost) {
                unhost.setUnitedRecipeType(UnitedRecipeType.VANILLA);
            }
        }

        var testItems = getCraftingSlotItems();
        if (!forceUpdate && sameItems(this.lastUnitedItems, testItems)) {
            return;
        }

        var recipe = findUnitedRecipe(testItems);

        this.currentUnitedRecipe = recipe;
        this.currentRecipe = recipe == null ? null : recipe.castRecipeHolder();
        this.lastUnitedItems = testItems;

        if (this.currentUnitedRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentUnitedRecipe.assemble(getPlayer().level()));
        }
    }

    @Nullable
    public UnitedRecipe getCurrentUnitedRecipe() {
        return currentUnitedRecipe;
    }

    private static boolean sameItems(@Nullable List<ItemStack> previous, List<ItemStack> current) {
        if (previous == null || previous.size() != current.size()) {
            return false;
        }
        for (int i = 0; i < previous.size(); i++) {
            if (!ItemStack.matches(previous.get(i), current.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public UnitedRecipe findUnitedRecipe(List<ItemStack> items) {
        return findUnitedRecipe(items, this.selectedRecipeType);
    }

    public UnitedRecipeType getSelectedRecipeType() {
        return selectedRecipeType;
    }


    public boolean hasMultipleRecipeKinds() {
        return getActiveRecipeKinds().size() > 1;
    }

    public void setSelectedRecipeType(UnitedRecipeType selectedRecipeKind) {
        if (selectedRecipeKind == null
                || !selectedRecipeKind.isActive()
                || this.selectedRecipeType == selectedRecipeKind) {
            return;
        }
        this.selectedRecipeType = selectedRecipeKind;
        saveSavedRecipeType(selectedRecipeKind);
        this.currentUnitedRecipe = null;
        this.lastUnitedItems = null;
        updateCurrentRecipeAndOutput(true);
    }

    private void loadSavedRecipeType() {
        if (!(getHost() instanceof IUnitedTerminalHost host) || !host.getRememberRecipeType()) {
            return;
        }

        var remembered = host.getUnitedRecipeType();
        if (remembered != null && remembered.isActive()) {
            this.selectedRecipeType = remembered;
        }
    }

    private void saveSavedRecipeType(UnitedRecipeType recipeType) {
        if (!isClientSide() && getHost() instanceof IUnitedTerminalHost host && host.getRememberRecipeType()) {
            host.setUnitedRecipeType(recipeType);
        }
    }

    public boolean rememberRecipeKind() {
        return !(getHost() instanceof IUnitedTerminalHost host) || host.getRememberRecipeType();
    }

    public void setRememberRecipeType(boolean remember) {
        if (isClientSide()) {
            sendClientAction(ACTION_REMEMBER_RECIPE_KIND, remember);
            return;
        }

        if (getHost() instanceof IUnitedTerminalHost host) {
            host.setRememberRecipeType(remember);
            if (remember) {
                host.setUnitedRecipeType(getSelectedRecipeType());
            }
        }
    }

    public void selectNextRecipeKind() {
        selectRecipeKindOffset(1, ACTION_SELECT_NEXT_RECIPE_KIND);
    }

    public void selectPreviousRecipeKind() {
        selectRecipeKindOffset(-1, ACTION_SELECT_PREVIOUS_RECIPE_KIND);
    }

    private void selectRecipeKindOffset(int offset, String clientAction) {
        if (isClientSide()) {
            sendClientAction(clientAction);
            return;
        }
        var values = getActiveRecipeKinds();
        if (values.size() <= 1) {
            return;
        }
        var currentIndex = values.indexOf(this.selectedRecipeType);
        setSelectedRecipeType(values.get(Math.floorMod(currentIndex + offset, values.size())));
    }

    public static List<UnitedRecipeType> getActiveRecipeKinds() {
        var kinds = new ArrayList<UnitedRecipeType>(UnitedRecipeType.values().length);
        for (var kind : UnitedRecipeType.values()) {
            if (kind.isActive()) {
                kinds.add(kind);
            }
        }
        return kinds;
    }

    @Nullable
    public UnitedRecipe findUnitedRecipe(List<ItemStack> items, UnitedRecipeType recipeType) {
        if (!recipeType.isActive()) {
            return null;
        }
        var level = getPlayer().level();
        if (recipeType.getMyomodAnnotation().equals(ExtendedCrafting.class)) {
            return findExtendedCraftingRecipe(level, items, recipeType);
        } else if (recipeType.getMyomodAnnotation().equals(AvaritiaNeo.class)) {
            return findAvaritiaNeoRecipe(level, items, recipeType);
        } else if (recipeType.getMyomodAnnotation().equals(ReAvaritia.class)) {
            return findReAvaritiaRecipe(level, items, recipeType);
        }
        return findVanillaRecipe(level, items, recipeType);
    }

    public TableCraftingInput createTableInput(List<ItemStack> items, @Nullable ITableRecipe recipe) {
        return TableCraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items, getInputTier(recipe));
    }

    protected int getInputTier(@Nullable ITableRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        var tier = recipe.getTier();
        return Math.max(tier, 0);
    }

    @Nullable
    private UnitedRecipe findExtendedCraftingRecipe(Level level, List<ItemStack> items, UnitedRecipeType kind) {
        for (var recipe : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())) {
            if (!canCraftExtendedCraftingRecipeInKind(recipe.value(), kind)) {
                continue;
            }
            var input = createTableInput(items, recipe.value());
            if (recipe.value().matches(input, level)) {
                return new UnitedRecipe(this, kind, recipe, input);
            }
        }
        return null;
    }

    private UnitedRecipe findVanillaRecipe(Level level, List<ItemStack> items, UnitedRecipeType typo) {
        return null;
    }

    private static boolean canCraftExtendedCraftingRecipeInKind(ITableRecipe recipe, UnitedRecipeType kind) {
        int recipeTier = recipe.getTier();
        if (recipe.hasRequiredTier()) {
            return recipeTier == kind.tier();
        }
        return recipeTier <= kind.tier();
    }

    @Nullable
    private UnitedRecipe findAvaritiaNeoRecipe(Level level, List<ItemStack> items, UnitedRecipeType kind) {
        try {
            var input = CraftingInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items);
            return level.getRecipeManager().getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), input, level)
                    .map(recipe -> new UnitedRecipe(this, kind, recipe, input))
                    .orElse(null);
        } catch (LinkageError ignored) {
            return null;
        }
    }

    @Nullable
    private UnitedRecipe findReAvaritiaRecipe(Level level, List<ItemStack> items, UnitedRecipeType kind) {
        try {
            var input = TierInput.of(menuType.getGridSideLength(), menuType.getGridSideLength(), items, kind.tier());
            return level.getRecipeManager().getRecipeFor(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), input, level)
                    .map(recipe -> new UnitedRecipe(this, kind, recipe, input))
                    .orElse(null);
        } catch (LinkageError ignored) {
            return null;
        }
    }

    public record UnitedRecipe(UnitedTerminalMenu menu, UnitedRecipeType recipeType,
                               RecipeHolder<? extends Recipe<? extends RecipeInput>> recipe,
                               RecipeInput input) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        public RecipeHolder<Recipe<RecipeInput>> castRecipeHolder() {
            return (RecipeHolder) recipe;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public RecipeHolder<ITableRecipe> castTableRecipeHolder() {
            return (RecipeHolder) recipe;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public boolean matches(Level level) {
            return ((Recipe) recipe.value()).matches(input, level);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public ItemStack assemble(Level level) {
            return ((Recipe) recipe.value()).assemble(input, level.registryAccess());
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public NonNullList<ItemStack> getRemainingItems() {
            return ((Recipe) recipe.value()).getRemainingItems(input);
        }

        public int top() {
            if (input instanceof TableCraftingInput tableInput) {
                return tableInput.top();
            }
            if (input instanceof TierInput tierInput) {
                return tierInput.top();
            }
            return 0;
        }

        public int left() {
            if (input instanceof TableCraftingInput tableInput) {
                return tableInput.left();
            }
            if (input instanceof TierInput tierInput) {
                return tierInput.left();
            }
            return 0;
        }

        public int width() {
            if (input instanceof TableCraftingInput tableInput) {
                return tableInput.width();
            }
            if (input instanceof TierInput tierInput) {
                return tierInput.width();
            }
            if (input instanceof CraftingInput craftingInput) {
                return craftingInput.width();
            }
            return menu.menuType.getGridSideLength();
        }
    }
}
