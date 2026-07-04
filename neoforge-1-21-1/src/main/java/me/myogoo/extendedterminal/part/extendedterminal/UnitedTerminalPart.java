package me.myogoo.extendedterminal.part.extendedterminal;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class UnitedTerminalPart extends ETTerminalBasePart implements IUnitedTerminalHost {
    private static final String REMEMBER_RECIPE_TYPE = "rememberUnitedRecipeType";
    private static final String SELECTED_RECIPE_TYPE = "selectedUnitedRecipeType";

    @PartModels
    public static final ResourceLocation UNITED_MODEL_BASE = ExtendedTerminal.makeId("part/extendedcrafting/united_terminal_base");
    @PartModels
    public static final ResourceLocation MODEL_OFF = ExtendedTerminal.makeId("part/extendedcrafting/united_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = ExtendedTerminal.makeId("part/extendedcrafting/united_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, UNITED_MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, UNITED_MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, UNITED_MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private boolean rememberUnitedRecipeType = true;
    @Nullable
    private MyoRecipeType rememberedUnitedRecipeType;

    public UnitedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.UNITED_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ExtendedCraftingConfig.INSTANCE.getUltimateConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return UnitedTerminalMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.rememberUnitedRecipeType = !data.contains(REMEMBER_RECIPE_TYPE, Tag.TAG_BYTE)
                || data.getBoolean(REMEMBER_RECIPE_TYPE);
        if (data.contains(SELECTED_RECIPE_TYPE, Tag.TAG_STRING)) {
            this.rememberedUnitedRecipeType = MyoRecipeType.valueOf(data.getString(SELECTED_RECIPE_TYPE));
        } else {
            this.rememberedUnitedRecipeType = null;
        }
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        data.putBoolean(REMEMBER_RECIPE_TYPE, this.rememberUnitedRecipeType);
        if (this.rememberedUnitedRecipeType != null) {
            data.putString(SELECTED_RECIPE_TYPE, this.rememberedUnitedRecipeType.toString());
        } else {
            data.remove(SELECTED_RECIPE_TYPE);
        }
    }

    @Override
    public boolean getRememberRecipeType() {
        return rememberUnitedRecipeType;
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        this.rememberUnitedRecipeType = remember;
        if (!remember) {
            this.rememberedUnitedRecipeType = null;
        }
        getHost().markForSave();
    }

    @Override
    public @Nullable MyoRecipeType getUnitedRecipeType() {
        return null;
    }

    @Override
    public void setUnitedRecipeType(@Nullable MyoRecipeType recipeKind) {

    }

    public @Nullable MyoRecipeType getRememberedUnitedRecipeType() {
        return rememberedUnitedRecipeType;
    }

    public void setRememberedUnitedRecipeType(@Nullable MyoRecipeType recipeKind) {
        this.rememberedUnitedRecipeType = recipeKind;
        getHost().markForSave();
    }
}
