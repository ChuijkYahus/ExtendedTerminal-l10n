package me.myogoo.extendedterminal.part.extendedterminal;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.MachineUpgradesChanged;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static appeng.parts.reporting.CraftingTerminalPart.*;

public class ETTerminalPart extends ETTerminalBasePart implements IETTerminalHost {
    @PartModels
    public static final ResourceLocation MODEL_OFF = ExtendedTerminal.makeId("part/extendedterminal/extended_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = ExtendedTerminal.makeId("part/extendedterminal/extended_terminal_on");


    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);


    public static final ResourceLocation SmithingInventory = ExtendedTerminal.makeId("smithing_crafting_inventory");
    public static final ResourceLocation StoneCutterInventory = ExtendedTerminal.makeId("stonecutter_crafting_inventory");
    public static final ResourceLocation AnvilInventory = ExtendedTerminal.makeId("anvil_inventory");
    public static final ResourceLocation UpgradeInventory = ExtendedTerminal.makeId("upgrade_inventory");

    private final AppEngInternalInventory smithingGrid = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory stoneCutterGrid = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory anvilInv = new AppEngInternalInventory(this, 2);

    private ETTerminalMode mode;
    @Nullable
    private ResourceLocation stonecuttingRecipeId;
    @Nullable
    private ExperienceMath.ExperienceSource rememberedAnvilExperienceSource;
    private boolean isLoading = false;

    public ETTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ET_TERMINAL);
        this.mode = ETTerminalMode.loadableValues().getFirst();
    }

    @Override
    public void clearContent() {
        super.clearContent();
        smithingGrid.clear();
        stoneCutterGrid.clear();
        anvilInv.clear();
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return ETTerminalMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_ON, MODELS_OFF, MODELS_HAS_CHANNEL);
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(SmithingInventory)) {
            return smithingGrid;
        } else if (id.equals(StoneCutterInventory)) {
            return stoneCutterGrid;
        } else if (id.equals(AnvilInventory)) {
            return anvilInv;
        } else if (id.equals(UpgradeInventory)) {
        }
        return super.getSubInventory(id);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        data.putString("mode", this.mode.name());
        if (this.stonecuttingRecipeId != null) {
            data.putString("stonecuttingRecipeId", this.stonecuttingRecipeId.toString());
        }
        if (this.rememberedAnvilExperienceSource != null) {
            data.putString("anvilExperienceSource", this.rememberedAnvilExperienceSource.name());
        }

        smithingGrid.writeToNBT(data, "smithingGrid", registries);
        stoneCutterGrid.writeToNBT(data, "stoneCutterGrid", registries);
        anvilInv.writeToNBT(data, "anvilInv", registries);

    }

    @Override
    public void saveChanges() {
        if(!isLoading) {
            super.saveChanges();
        }
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        isLoading = false;
        try {
            super.readFromNBT(data, registries);
            try {
                this.mode = ETTerminalMode.valueOf(data.getString("mode"));
            } catch (IllegalArgumentException ignored) {
                this.mode = ETTerminalMode.CRAFTING;
            }

            if (data.contains("stonecuttingRecipeId", Tag.TAG_STRING)) {
                this.stonecuttingRecipeId = ResourceLocation.parse(data.getString("stonecuttingRecipeId"));
            } else {
                this.stonecuttingRecipeId = null;
            }
            this.rememberedAnvilExperienceSource = readAnvilExperienceSource(data);
            smithingGrid.readFromNBT(data, "smithingGrid", registries);
            stoneCutterGrid.readFromNBT(data, "stoneCutterGrid", registries);
            anvilInv.readFromNBT(data, "anvilInv", registries);

        } finally {
            isLoading = false;
        }
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        for (var is : this.smithingGrid) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
        for (var is : this.stoneCutterGrid) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (var is : this.anvilInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    public ETTerminalMode getMode() {
        return mode;
    }

    @Override
    public void setMode(ETTerminalMode mode) {
        this.mode = mode;
        this.saveChanges();
    }

    @Override
    public @Nullable ResourceLocation getStoneCutterRecipeId() {
        return stonecuttingRecipeId;
    }

    @Override
    public void setStoneCutterRecipeId(@Nullable ResourceLocation stonecuttingRecipeId) {
        this.stonecuttingRecipeId = stonecuttingRecipeId;
        this.saveChanges();
    }

    @Override
    public @Nullable ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() {
        return this.rememberedAnvilExperienceSource;
    }

    @Override
    public void setRememberedAnvilExperienceSource(@Nullable ExperienceMath.ExperienceSource source) {
        this.rememberedAnvilExperienceSource = source;
        this.saveChanges();
    }

    private static @Nullable ExperienceMath.ExperienceSource readAnvilExperienceSource(CompoundTag data) {
        if (!data.contains("anvilExperienceSource", Tag.TAG_STRING)) {
            return null;
        }
        try {
            return ExperienceMath.ExperienceSource.valueOf(data.getString("anvilExperienceSource"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }


    @Override
    protected IPartModel selectModel(IPartModel offModels, IPartModel onModels, IPartModel hasChannelModels) {
        return super.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
