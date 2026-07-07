package me.myogoo.extendedterminal.integration.itemList.jei.handler;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;

public abstract class MyoBaseClickableArea implements IGuiClickableArea {
    protected final static Rect2i DummyRect2i = new Rect2i(0,0,0,0);

    protected enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public Rect2i getArea(Slot slot) {
            return switch (this) {
                case UP -> new Rect2i(slot.x, slot.y + 24, 24, 24);
                case DOWN -> new Rect2i(slot.x, slot.y - 24, 24, 24);
                case LEFT -> new Rect2i(slot.x - 40, slot.y, 24, 24);
                case RIGHT -> new Rect2i(slot.x + 40, slot.y, 24, 24);
            };
        }
    }
}
