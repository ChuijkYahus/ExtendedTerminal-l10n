package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler;

import appeng.menu.SlotSemantics;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collection;
import java.util.List;

public class ETTerminalGuiHandler implements IGuiContainerHandler<ETTerminalScreen<?>> {

    @Override
    public List<Rect2i> getGuiExtraAreas(ETTerminalScreen screen) {
        return screen.getExclusionZones();
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(ETTerminalScreen<?> screen, double guiMouseX, double guiMouseY) {
        var menu = screen.getMenu();
        var mode = menu.getMode();

        switch (mode) {
            case CRAFTING -> {
                var outputSlot = menu.getSlots(menu.getOutputSlotSemantic()).getFirst();
                return List.of(IGuiClickableArea.createBasic(outputSlot.x - 50, outputSlot.y, 40, 24, RecipeTypes.CRAFTING, RecipeTypes.SMITHING, RecipeTypes.STONECUTTING, RecipeTypes.ANVIL));
            }
            case SMITHING -> {
                var outputSlot = menu.getSlots(SlotSemantics.SMITHING_TABLE_RESULT).getFirst();
                return List.of(IGuiClickableArea.createBasic(outputSlot.x - 50, outputSlot.y, 40, 24, RecipeTypes.SMITHING));
            }
            case STONECUTTING -> {
                var outputSlot = menu.getSlots(ETSlotSemantics.STONECUTTING_RESULT).getFirst();
                return List.of(IGuiClickableArea.createBasic(outputSlot.x - 24, outputSlot.y, 24, 24, RecipeTypes.STONECUTTING));
            }
            case ANVIL -> {
                var outputSlot = menu.getSlots(ETSlotSemantics.ANVIL_RESULT).getFirst();
                return List.of(IGuiClickableArea.createBasic(outputSlot.x - 24, outputSlot.y, 24, 24, RecipeTypes.ANVIL));
            }
        }
        return List.of();
    }
}
