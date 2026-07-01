package me.myogoo.extendedterminal.part.extendedterminal;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ETTerminalPart extends ETTerminalBasePart implements IETTerminalHost {
    @PartModels
    public static final ResourceLocation MODEL_OFF = ExtendedTerminal.makeId("part/extendedterminal/extended_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = ExtendedTerminal.makeId("part/extendedterminal/extended_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public static final ResourceLocation SMITHING_INVENTORY = ExtendedTerminal.makeId("smithing_crafting_inventory");
    public static final ResourceLocation STONECUTTER_INVENTORY = ExtendedTerminal.makeId("stonecutter_crafting_inventory");
    public static final ResourceLocation ANVIL_INVENTORY = ExtendedTerminal.makeId("anvil_inventory");

    private final AppEngInternalInventory smithingGrid = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory stoneCutterGrid = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory anvilInv = new AppEngInternalInventory(this, 2);

    private ETTerminalMode mode;
    @Nullable
    private ResourceLocation stonecuttingRecipeId;
    @Nullable
    private ExperienceMath.ExperienceSource rememberedAnvilExperienceSource;
    private boolean isLoading;

    public ETTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ET_TERMINAL, ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig());
        this.mode = ETTerminalMode.loadableValues().get(0);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.smithingGrid.clear();
        this.stoneCutterGrid.clear();
        this.anvilInv.clear();
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
        if (id.equals(SMITHING_INVENTORY)) {
            return this.smithingGrid;
        } else if (id.equals(STONECUTTER_INVENTORY)) {
            return this.stoneCutterGrid;
        } else if (id.equals(ANVIL_INVENTORY)) {
            return this.anvilInv;
        }
        return super.getSubInventory(id);
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putString("mode", this.mode.name());
        if (this.stonecuttingRecipeId != null) {
            data.putString("stonecuttingRecipeId", this.stonecuttingRecipeId.toString());
        }
        if (this.rememberedAnvilExperienceSource != null) {
            data.putString("anvilExperienceSource", this.rememberedAnvilExperienceSource.name());
        }
        this.smithingGrid.writeToNBT(data, "smithingGrid");
        this.stoneCutterGrid.writeToNBT(data, "stoneCutterGrid");
        this.anvilInv.writeToNBT(data, "anvilInv");
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        this.isLoading = true;
        try {
            super.readFromNBT(data);
            try {
                this.mode = ETTerminalMode.valueOf(data.getString("mode"));
            } catch (IllegalArgumentException ignored) {
                this.mode = ETTerminalMode.loadableValues().get(0);
            }

            if (data.contains("stonecuttingRecipeId", Tag.TAG_STRING)) {
                this.stonecuttingRecipeId = new ResourceLocation(data.getString("stonecuttingRecipeId"));
            } else {
                this.stonecuttingRecipeId = null;
            }

            this.rememberedAnvilExperienceSource = readAnvilExperienceSource(data);

            this.smithingGrid.readFromNBT(data, "smithingGrid");
            this.stoneCutterGrid.readFromNBT(data, "stoneCutterGrid");
            this.anvilInv.readFromNBT(data, "anvilInv");
        } finally {
            this.isLoading = false;
        }
    }

    @Override
    public void saveChanges() {
        if (!this.isLoading) {
            super.saveChanges();
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

    @Override
    public ETTerminalMode getMode() {
        return this.mode;
    }

    @Override
    public void setMode(ETTerminalMode mode) {
        this.mode = mode;
        this.saveChanges();
    }

    @Override
    public @Nullable ResourceLocation getStoneCutterRecipeId() {
        return this.stonecuttingRecipeId;
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
