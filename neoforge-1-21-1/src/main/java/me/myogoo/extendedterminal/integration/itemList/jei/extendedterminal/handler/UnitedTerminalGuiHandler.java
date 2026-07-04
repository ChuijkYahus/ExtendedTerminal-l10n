package me.myogoo.extendedterminal.integration.itemList.jei.extendedterminal.handler;

import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class UnitedTerminalGuiHandler implements IGuiContainerHandler<UnitedTerminalScreen<UnitedTerminalMenu>> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(UnitedTerminalScreen<UnitedTerminalMenu> screen) {
        return screen.getExclusionZones();
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(UnitedTerminalScreen<UnitedTerminalMenu> screen, double guiMouseX, double guiMouseY) {
        var menu = screen.getMenu();
        var recipeType = menu.getSelectedRecipeType();
        var outputSlot = menu.getSlots(menu.getOutputSlotSemantic()).getFirst();

        return List.of(IGuiClickableArea.createBasic(outputSlot.x - 50, outputSlot.y, 40, 24, RecipeTypes.CRAFTING));
    }
}