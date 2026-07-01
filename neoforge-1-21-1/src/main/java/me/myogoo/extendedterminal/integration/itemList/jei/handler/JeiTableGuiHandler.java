package me.myogoo.extendedterminal.integration.itemList.jei.handler;

import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JeiTableGuiHandler<R extends Recipe<?>, T extends ETTerminalBaseMenu<R>, S extends ETTerminalBaseScreen<R,T>> implements IGuiContainerHandler<S> {
    private final RecipeType<R>[] recipeType;

    @SafeVarargs
    public JeiTableGuiHandler(RecipeType<R>... recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(S screen) {
        return screen.getExclusionZones();
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(S screen, double guiMouseX, double guiMouseY) {
        var outputSlot = screen.getMenu().getSlots(screen.getMenu().getOutputSlotSemantic()).getFirst();
        int x = outputSlot.x - 50;
        int y = outputSlot.y;
        return List.of(IGuiClickableArea.createBasic(x, y, 40, 24, recipeType));
    }
}
