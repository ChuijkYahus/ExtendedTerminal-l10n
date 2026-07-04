package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public enum ETTerminalMode {
    CRAFTING(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableCraftingPanel()),
    STONECUTTING(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableStonecutterPanel()),
    SMITHING(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableSmithingPanel()),
    ANVIL(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableAnvilPanel());

    private final boolean enabled;

    ETTerminalMode(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean canLoad() {
        try {
            Field field = ETTerminalMode.class.getField(this.name());
            if (field.getDeclaredAnnotations().length == 0) {
                return this.enabled;
            }
            return Arrays.stream(field.getDeclaredAnnotations())
                    .map(Annotation::annotationType)
                    .allMatch(a -> MyotusAPI.get().integrations().isLoaded(a)) && this.enabled;

        } catch (NoSuchFieldException e) {
            ExtendedTerminal.LOGGER.error("Terminal Mode {} is not loaded due to missing field in ETTerminalMode",
                    this.name());
        }
        return false;
    }

    public ResourceLocation getInventoryId() {
        return switch (this) {
            case CRAFTING -> ETMenuType.ET_TERMINAL.getCraftingInventory();
            case STONECUTTING -> ETTerminalPart.StoneCutterInventory;
            case SMITHING -> ETTerminalPart.SmithingInventory;
            case ANVIL -> ETTerminalPart.AnvilInventory;
        };
    }

    public List<SlotSemantic> getInputSlotSemantics() {
        return switch (this) {
            case CRAFTING -> List.of(ETMenuType.ET_TERMINAL.getSlotSemanticGrid());
            case STONECUTTING -> List.of(ETSlotSemantics.STONECUTTING_INPUT);
            case SMITHING ->
                List.of(ETSlotSemantics.SMITHING_TABLE_BASE, ETSlotSemantics.SMITHING_TABLE_TEMPLATE,
                        ETSlotSemantics.SMITHING_TABLE_ADDITION);
            case ANVIL -> List.of(ETSlotSemantics.ANVIL_LEFT_INPUT, ETSlotSemantics.ANVIL_RIGHT_INPUT);
        };
    }
    
    public SlotSemantic getOutputSlotSemantics() {
        return switch (this) {
            case CRAFTING -> ETMenuType.ET_TERMINAL.getSlotSemanticResult();
            case STONECUTTING -> ETSlotSemantics.STONECUTTING_RESULT;
            case SMITHING -> SlotSemantics.SMITHING_TABLE_RESULT;
            case ANVIL -> ETSlotSemantics.ANVIL_RESULT;
        };
    }

    public static List<ETTerminalMode> loadableValues() {
        var list = Arrays.stream(values()).filter(ETTerminalMode::canLoad).toList();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No ETTerminalModes are loadable!");
        } else {
            return list;
        }
    }
}
