package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.api.networking.security.IActionSource;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import com.google.common.base.Preconditions;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.myotus.client.MyoTranslateKey;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.api.annotation.ApothicEnchanting;
import me.myogoo.extendedterminal.api.annotation.Apotheosis;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETAnvilSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETCraftingSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETSmithingSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETStoneCutterSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.*;
import me.myogoo.extendedterminal.api.annotation.Polymorph;

public class ETTerminalMenu extends ETTerminalBaseMenu<CraftingRecipe> {
    public static final MenuType<ETTerminalMenu> TYPE = MenuTypeBuilder
            .create(ETTerminalMenu::new, IETTerminalHost.class)
            .buildUnregistered(ETMenuType.ET_TERMINAL.getId());

    private static final String ACTION_SET_STONECUTTING_RECIPE_ID = "setStonecuttingRecipeId";
    private static final String ACTION_SET_MODE = "setMode";
    private static final String ACTION_UPDATE_STONECUTTER_RECIPES = "updateStoneCutterRecipes";
    private static final String ACTION_SET_ANVIL_ITEM_NAME = "setAnvilItemName";
    private static final String ACTION_CYCLE_ANVIL_EXPERIENCE_SOURCE_PRIORITY = "cycleAnvilExperienceSourcePriority";
    // ---------------------------------------------------------------------
    // Fields : 공용
    // ---------------------------------------------------------------------
    private final ISegmentedInventory craftingInventoryHost;
    @GuiSync(0)
    private ETTerminalMode currentMode = ETTerminalMode.CRAFTING;

    private final CraftingMatrixSlot[] craftingSlots;
    private final CraftingTermSlot outputSlot;
    private CraftingInput lastTestedCraftingInput;
    private RecipeHolder<CraftingRecipe> craftingRecipe;

    private final CraftingMatrixSlot smithingTemplateSlot;
    private final CraftingMatrixSlot smithingBaseSlot;
    private final CraftingMatrixSlot smithingAdditionSlot;
    private final ETSmithingSlot smithingOutputSlot;
    private SmithingRecipeInput lastTestedSmithingInput;
    private RecipeHolder<SmithingRecipe> smithingRecipe;

    private final CraftingMatrixSlot stonecuttingSlot;
    private final ETStoneCutterSlot stoneCutterOutputSlot;
    private List<RecipeHolder<StonecutterRecipe>> stoneCutterRecipes = new ArrayList<>();
    @GuiSync(1)
    private ResourceLocation stoneCutterRecipeId = null;

    private final CraftingMatrixSlot anvilLeftSlot;
    private final CraftingMatrixSlot anvilRightSlot;
    private final ETAnvilSlot anvilOutputSlot;
    private final FakeAnvilMenu anvilDelegate;
    @GuiSync(2)
    private int anvilCost = 0;
    @GuiSync(3)
    private int anvilExperienceSourcePriorityIndex = 0;
    @GuiSync(4)
    private boolean anvilFluidXpAvailable = false;
    @GuiSync(5)
    private boolean anvilAppliedExperiencedAvailable = false;

    private final IETTerminalHost host;

    public ETTerminalMenu(MenuType<?> menuType, int id, Inventory ip, IETTerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ET_TERMINAL,
                ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig());
        this.host = host;
        this.currentMode = host.getMode();
        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[this.menuType.getGridSize()];

        var player = getPlayerInventory().player;
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(this.menuType.getCraftingInventory());
        for (int i = 0; i < this.menuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    this.menuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new ETCraftingSlot(player, this.getActionSource(),
                this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this),
                this.menuType.getSlotSemanticResult());

        // Smithing Table
        var smithingInv = this.craftingInventoryHost.getSubInventory(SmithingInventory);
        this.addSlot(this.smithingTemplateSlot = new CraftingMatrixSlot(this, smithingInv, 0),
                ETSlotSemantics.SMITHING_TABLE_TEMPLATE);
        this.addSlot(this.smithingBaseSlot = new CraftingMatrixSlot(this, smithingInv, 1),
                ETSlotSemantics.SMITHING_TABLE_BASE);
        this.addSlot(this.smithingAdditionSlot = new CraftingMatrixSlot(this, smithingInv, 2),
                ETSlotSemantics.SMITHING_TABLE_ADDITION);
        this.addSlot(this.smithingOutputSlot = new ETSmithingSlot(player, this.getActionSource(),
                this.energySource, linkStatusInventory, smithingInv, smithingInv, this),
                SlotSemantics.SMITHING_TABLE_RESULT);

        // Stonecutting
        var stonecuttingInv = this.craftingInventoryHost.getSubInventory(StoneCutterInventory);
        this.addSlot(this.stonecuttingSlot = new CraftingMatrixSlot(this, stonecuttingInv, 0),
                ETSlotSemantics.STONECUTTING_INPUT);
        this.addSlot(this.stoneCutterOutputSlot = new ETStoneCutterSlot(player, this.getActionSource(),
                this.energySource, linkStatusInventory, stonecuttingInv, stonecuttingInv, this),
                ETSlotSemantics.STONECUTTING_RESULT);

        var anvilInv = this.craftingInventoryHost.getSubInventory(AnvilInventory);
        this.anvilDelegate = new FakeAnvilMenu(0, player.getInventory());
        this.addSlot(this.anvilLeftSlot = new CraftingMatrixSlot(this, anvilInv, 0), ETSlotSemantics.ANVIL_LEFT_INPUT);
        this.addSlot(this.anvilRightSlot = new CraftingMatrixSlot(this, anvilInv, 1),
                ETSlotSemantics.ANVIL_RIGHT_INPUT);
        this.addSlot(this.anvilOutputSlot = new ETAnvilSlot(player, anvilInv, anvilDelegate, this),
                ETSlotSemantics.ANVIL_RESULT);

        registerClientAction(ACTION_SET_STONECUTTING_RECIPE_ID, ResourceLocation.class, this::setStoneCutterRecipeId);
        registerClientAction(ACTION_SET_MODE, ETTerminalMode.class, this::setMode);
        registerClientAction(ACTION_SET_ANVIL_ITEM_NAME, String.class, this::setAnvilItemName);
        registerClientAction(ACTION_CYCLE_ANVIL_EXPERIENCE_SOURCE_PRIORITY, this::cycleAnvilExperienceSourcePriority);

        if (isServerSide()) {
            updateAnvilExperienceSourceAvailability();
        }
        updateCurrentRecipeAndOutput(true);
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> items, ItemStack carried) {
        super.initializeContents(stateId, items, carried);
        setMode(currentMode);
    }

    public void setMode(ETTerminalMode mode) {
        if (isClientSide()) {
            this.currentMode = mode;
            sendClientAction(ACTION_SET_MODE, mode);
        } else {
            this.host.setMode(mode);
            this.currentMode = host.getMode();
            updateCurrentRecipeAndOutput(true);
        }
    }

    public ETTerminalMode getMode() {
        return this.currentMode;
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        switch (currentMode) {
            case CRAFTING -> updateCraftingOutput(forceUpdate);
            case SMITHING -> updateSmithingOutput(forceUpdate);
            case STONECUTTING -> updateStonecuttingOutput(forceUpdate);
            case ANVIL -> updateAnvilOutput(forceUpdate);
            case null, default -> {
            }
        }
    }

    @Override
    public void clearCraftingGrid() {
        clearInventory(craftingSlots[0]);
    }

    public void clearSmithingGrid() {
        clearInventory(smithingTemplateSlot);
        clearInventory(smithingAdditionSlot);
        clearInventory(smithingBaseSlot);
    }

    public void clearInventory(AppEngSlot slot) {
        Preconditions.checkState(isClientSide());
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return getInventory(menuType.getCraftingInventory());
    }

    public InternalInventory getSmithingInventory() {
        return getInventory(SmithingInventory);
    }

    public InternalInventory getStoneCutterInventory() {
        return getInventory(StoneCutterInventory);
    }

    public InternalInventory getInventory(ResourceLocation id) {
        return this.craftingInventoryHost.getSubInventory(id);
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        if (this.getSlot(slot) instanceof ETCraftingBaseSlot<?, ?> craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT, CRAFT_ALL, CRAFT_ITEM, CRAFT_STACK -> craftingSlot.doClick(action, player);
            }
            return;
        }
        super.doAction(player, action, slot, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int idx) {
        if (isServerSide() && idx >= 0 && idx < this.slots.size() && this.getSlot(idx) == this.anvilOutputSlot) {
            ItemStack before = this.anvilOutputSlot.getItem().copy();
            if (!before.isEmpty() && this.anvilOutputSlot.mayPickup(player)) {
                int beforeCount = before.getCount();
                super.quickMoveStack(player, idx);

                ItemStack after = this.anvilOutputSlot.getItem();
                int afterCount = after.isEmpty() ? 0 : after.getCount();
                if (afterCount < beforeCount) {
                    ItemStack taken = before.copy();
                    taken.setCount(beforeCount - afterCount);
                    this.anvilOutputSlot.onTake(player, taken);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.quickMoveStack(player, idx);
    }

    // ---------------------------------------------------------------------
    // Crafting Section
    // ---------------------------------------------------------------------
    private void updateCraftingOutput(boolean forceUpdate) {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for (var craftingSlot : this.craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        var testInput = CraftingInput.of(3, 3, testItems);

        if (!forceUpdate && Objects.equals(lastTestedCraftingInput, testInput)) {
            return;
        }

        var level = getPlayer().level();
        this.currentRecipe = MyotusAPI.integrations().isLoaded(Polymorph.class)
                ? PolymorphApi.getInstance().getRecipeManager().getPlayerRecipe(this, RecipeType.CRAFTING, testInput, level, getPlayer()).orElse(null)
                : level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, testInput, level).orElse(null);
        this.lastTestedCraftingInput = testInput;

        if (this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(testInput, level.registryAccess()));
        }
    }

    public @Nullable RecipeHolder<CraftingRecipe> getCraftingRecipe() {
        return this.currentRecipe;
    }

    // ---------------------------------------------------------------------
    // Smithing Section
    // ---------------------------------------------------------------------
    private void updateSmithingOutput(boolean forceUpdate) {
        var smithingTestInput = new SmithingRecipeInput(smithingTemplateSlot.getItem().copy(),
                smithingBaseSlot.getItem().copy(), smithingAdditionSlot.getItem().copy());

        if (!forceUpdate && Objects.equals(lastTestedSmithingInput, smithingTestInput)) {
            return;
        }

        var level = getPlayer().level();
        var smithingRecipes = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, smithingTestInput, level);
        if (smithingRecipes.isEmpty()) {
            this.smithingOutputSlot.set(ItemStack.EMPTY);
        } else {
            RecipeHolder<SmithingRecipe> recipeHolder = smithingRecipes.getFirst();
            var result = recipeHolder.value().assemble(smithingTestInput, level.registryAccess());
            ;
            if (result.isItemEnabled(level.enabledFeatures())) {
                this.smithingRecipe = recipeHolder;
                this.smithingOutputSlot.set(result);
            }
        }
    }

    public @Nullable RecipeHolder<SmithingRecipe> getSmithingRecipe() {
        return this.smithingRecipe;
    }

    // ---------------------------------------------------------------------
    // Stonecutting Section
    // ---------------------------------------------------------------------
    private void updateStonecuttingOutput(boolean forceUpdate) {
        var input = stonecuttingSlot.getItem();
        stoneCutterRecipes.clear();

        if (input.isEmpty()) {
            stoneCutterRecipeId = null;
            stoneCutterOutputSlot.set(ItemStack.EMPTY);
            return;
        }

        updateStonecutterRecipes();
    }

    public void updateStonecutterRecipes() {
        var input = stonecuttingSlot.getItem();
        stoneCutterRecipes.clear();

        var level = getPlayer().level();
        var recipeManager = level.getRecipeManager();
        var recipeInput = new SingleRecipeInput(input);
        stoneCutterRecipes.addAll(
                recipeManager.getRecipesFor(RecipeType.STONECUTTING, recipeInput, level));

        if (stoneCutterRecipeId != null
                && stoneCutterRecipes.stream().noneMatch(r -> r.id().equals(stoneCutterRecipeId))) {
            stoneCutterRecipeId = null;
        }

    }

    public void setStoneCutterRecipeId(ResourceLocation stoneCutterRecipeId) {
        if (isClientSide()) {
            this.host.setStoneCutterRecipeId(stoneCutterRecipeId);
            sendClientAction(ACTION_SET_STONECUTTING_RECIPE_ID, host.getStoneCutterRecipeId());
        } else {
            this.stoneCutterRecipeId = stoneCutterRecipeId;
            var optionalRecipeHolder = getPlayer().level().getRecipeManager().byKey(stoneCutterRecipeId);
            if (optionalRecipeHolder.isPresent()) {
                var recipe = optionalRecipeHolder.get().value();
                stoneCutterOutputSlot.set(recipe.getResultItem(getPlayer().level().registryAccess()));
            } else {
                stoneCutterOutputSlot.set(ItemStack.EMPTY);
            }
        }
    }

    public ResourceLocation getStoneCutterRecipeId() {
        return this.stoneCutterRecipeId;
    }

    public List<RecipeHolder<StonecutterRecipe>> getStoneCutterRecipes() {
        return stoneCutterRecipes;
    }

    // ---------------------------------------------------------------------
    // Anvil Section
    // ---------------------------------------------------------------------
    public void updateAnvilOutput(boolean forceUpdate) {
        this.anvilDelegate.slots.get(0).set(this.anvilLeftSlot.getItem());
        this.anvilDelegate.slots.get(1).set(this.anvilRightSlot.getItem());
        anvilDelegate.createResult();
        this.anvilOutputSlot.set(anvilDelegate.getResultItem());
        this.anvilCost = anvilDelegate.getCost();
    }

    public int getAnvilCost() {
        return anvilCost;
    }

    public void cycleAnvilExperienceSourcePriority() {
        if (isClientSide()) {
            sendClientAction(ACTION_CYCLE_ANVIL_EXPERIENCE_SOURCE_PRIORITY);
        } else {
            updateAnvilExperienceSourceAvailability();
            int sourceCount = getAvailableAnvilExperienceSources().size();
            this.anvilExperienceSourcePriorityIndex = sourceCount <= 1
                    ? 0
                    : (this.anvilExperienceSourcePriorityIndex + 1) % sourceCount;
            rememberSelectedAnvilExperienceSource();
        }
    }

    public List<ExperienceMath.ExperienceSource> getAnvilExperienceSourcePriority() {
        var sources = getAvailableAnvilExperienceSources();
        var selected = sources.isEmpty()
                ? ExperienceMath.ExperienceSource.PLAYER
                : sources.get(Math.floorMod(this.anvilExperienceSourcePriorityIndex, sources.size()));
        return MyotusAPI.experience().availableAnvilSourcePriority(this.energySource, this.storage,
                getActionSourceFor(getPlayer()), selected);
    }

    public List<MyoTranslateKey> getAnvilExperienceSourcePriorityLabelKeys() {
        return getAnvilExperienceSourcePriority().stream()
                .<MyoTranslateKey>map(source -> switch (source) {
                    case PLAYER -> ETTranslationKey.GUI.ANVIL_EXPERIENCE_SOURCE_PLAYER;
                    case FLUID_XP -> ETTranslationKey.GUI.ANVIL_EXPERIENCE_SOURCE_FLUID;
                    case APPLIED_EXPERIENCED_AMOUNT -> ETTranslationKey.GUI.ANVIL_EXPERIENCE_SOURCE_CELL;
                })
                .toList();
    }

    private List<ExperienceMath.ExperienceSource> getAvailableAnvilExperienceSources() {
        var sources = new ArrayList<ExperienceMath.ExperienceSource>();
        sources.add(ExperienceMath.ExperienceSource.PLAYER);
        if (isAppliedExperiencedCellAvailable()) {
            sources.add(ExperienceMath.ExperienceSource.APPLIED_EXPERIENCED_AMOUNT);
        }
        if (isFluidXpAvailable()) {
            sources.add(ExperienceMath.ExperienceSource.FLUID_XP);
        }
        return sources;
    }

    private boolean isFluidXpAvailable() {
        return isServerSide()
                ? hasNetworkExperienceSource(ExperienceMath.ExperienceSource.FLUID_XP)
                : this.anvilFluidXpAvailable;
    }

    private boolean isAppliedExperiencedCellAvailable() {
        return isServerSide()
                ? hasNetworkExperienceSource(ExperienceMath.ExperienceSource.APPLIED_EXPERIENCED_AMOUNT)
                : this.anvilAppliedExperiencedAvailable;
    }

    private void updateAnvilExperienceSourceAvailability() {
        this.anvilFluidXpAvailable = hasNetworkExperienceSource(ExperienceMath.ExperienceSource.FLUID_XP);
        this.anvilAppliedExperiencedAvailable = hasNetworkExperienceSource(
                ExperienceMath.ExperienceSource.APPLIED_EXPERIENCED_AMOUNT);

        restoreRememberedAnvilExperienceSource();
    }

    private void restoreRememberedAnvilExperienceSource() {
        var sources = getAvailableAnvilExperienceSources();
        var remembered = this.host.getRememberedAnvilExperienceSource();
        var selected = remembered != null && sources.contains(remembered)
                ? remembered
                : ExperienceMath.ExperienceSource.PLAYER;
        int index = sources.indexOf(selected);
        this.anvilExperienceSourcePriorityIndex = index >= 0 ? index : 0;
        if (this.host.getRememberedAnvilExperienceSource() != selected) {
            this.host.setRememberedAnvilExperienceSource(selected);
        }
    }

    private void rememberSelectedAnvilExperienceSource() {
        var selected = getAnvilExperienceSourcePriority().get(0);
        if (this.host.getRememberedAnvilExperienceSource() != selected) {
            this.host.setRememberedAnvilExperienceSource(selected);
        }
    }

    private boolean hasNetworkExperienceSource(ExperienceMath.ExperienceSource source) {
        return getExtractableStorageExperience(getPlayer(), source) > 0;
    }

    public boolean canPayAnvilCost(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        return MyotusAPI.experience().canConsume(this.energySource, this.storage, getActionSourceFor(player),
                player, getRequiredAnvilExperience(player), getAnvilExperienceSourcePriority());
    }

    public boolean consumeAnvilExperience(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        return MyotusAPI.experience().consume(this.energySource, this.storage,
                getActionSourceFor(player), player, getRequiredAnvilExperience(player), getAnvilExperienceSourcePriority());
    }

    private long getRequiredAnvilExperience(Player player) {
        int cost = Math.max(0, this.anvilCost);
        if (usesApothicAnvilExperienceCost()) {
            return MyotusAPI.experience().totalForLevel(cost);
        }

        int targetLevel = Math.max(0, player.experienceLevel - cost);
        return MyotusAPI.experience().totalForLevel(player.experienceLevel)
                - MyotusAPI.experience().totalForLevel(targetLevel);
    }

    private boolean usesApothicAnvilExperienceCost() {
        return MyotusAPI.integrations().isLoaded(ApothicEnchanting.class)
                || MyotusAPI.integrations().isLoaded(Apotheosis.class);
    }

    private long getExtractableStorageExperience(Player player, ExperienceMath.ExperienceSource source) {
        return MyotusAPI.experience().extractable(this.energySource, this.storage,
                getActionSourceFor(player), source);
    }

    private IActionSource getActionSourceFor(Player player) {
        return IActionSource.ofPlayer(player, getActionHost());
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            updateAnvilExperienceSourceAvailability();
        }
        super.broadcastChanges();
    }

    public void setAnvilItemName(String name) {
        if (isServerSide()) {
            this.anvilDelegate.slots.get(0).set(this.anvilLeftSlot.getItem());
            this.anvilDelegate.slots.get(1).set(this.anvilRightSlot.getItem());
            if (this.anvilDelegate.setItemName(name)) {
                updateCurrentRecipeAndOutput(true);
            }
        } else {
            this.anvilDelegate.slots.get(0).set(this.anvilLeftSlot.getItem());
            this.anvilDelegate.slots.get(1).set(this.anvilRightSlot.getItem());
            if (this.anvilDelegate.setItemName(name)) {
                updateCurrentRecipeAndOutput(true);
            }
            sendClientAction(ACTION_SET_ANVIL_ITEM_NAME, name);
        }
    }

    public void onAnvilTake(ItemStack newLeft, ItemStack newRight) {
        var anvilInventory = getInventory(AnvilInventory);
        anvilInventory.setItemDirect(0, newLeft);
        anvilInventory.setItemDirect(1, newRight);
        updateAnvilOutput(true);
    }

    @Override
    public boolean hasIngredient(Ingredient ingredient, Object2IntOpenHashMap<Object> reservedAmounts) {
        List<Slot> slots = ETTerminalMode.loadableValues().stream()
                .map(ETTerminalMode::getInputSlotSemantics)
                .flatMap(Collection::stream)
                .map(this::getSlots)
                .flatMap(Collection::stream)
                .toList();

        for (var slot : slots) {
            var stackInSlot = slot.getItem();
            if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                var reservedAmount = reservedAmounts.getOrDefault(slot, 0);
                if (stackInSlot.getCount() > reservedAmount) {
                    reservedAmounts.merge(slot, 1, Integer::sum);
                    return true;
                }
            }
        }
        return super.hasIngredient(ingredient, reservedAmounts);
    }

    @Override
    public void onServerDataSync(ShortSet updatedFields) {
        super.onServerDataSync(updatedFields);
        updateCurrentRecipeAndOutput(true);
    }
}
