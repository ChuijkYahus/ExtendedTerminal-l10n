package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels;

import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.AETextField;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.AnvilExperienceSourceButton;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.XPLabelWidget;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class AnvilPanel extends ETTerminalModePanel {
    private static final Blitter BG = Blitter.texture("guis/et_terminal_panel.png", 512, 512).src(0, 201, 159, 65);

    private final AETextField textField;
    private final XPLabelWidget enchantCostLabel;
    private final AnvilExperienceSourceButton priorityButton;

    public AnvilPanel(ETTerminalScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets, ETTranslationKey.GUI.GUI_ANVIL_PANEL);

        this.textField = widgets.addTextField("ET_ANVIL_NAME_FIELD");
        this.textField.setMaxLength(50);
        this.textField.setResponder(this::onNameChanged);
        this.enchantCostLabel = new XPLabelWidget();
        widgets.add("ET_ANVIL_COST_LABEL", this.enchantCostLabel);

        this.priorityButton = new AnvilExperienceSourceButton(this.menu);
        widgets.add("ET_ANVIL_XP_PRIORITY_BUTTON", this.priorityButton);

        setVisible(false);
    }

    @Override
    public ItemStack getIcon() {
        return Blocks.ANVIL.asItem().getDefaultInstance();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        screen.setSlotsHidden(ETSlotSemantics.ANVIL_RIGHT_INPUT, !visible);
        screen.setSlotsHidden(ETSlotSemantics.ANVIL_LEFT_INPUT, !visible);
        screen.setSlotsHidden(ETSlotSemantics.ANVIL_RESULT, !visible);

        enchantCostLabel.setVisible(visible);
        this.textField.setVisible(visible);
        this.priorityButton.setVisibility(visible);
    }

    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);
    }


    private void onNameChanged(String input) {
        var slot = menu.getSlots(ETSlotSemantics.ANVIL_LEFT_INPUT).getFirst();
        if (slot.hasItem()) {
            String name = input;
            if (!slot.getItem().has(DataComponents.CUSTOM_NAME)
                    && input.equals(slot.getItem().getHoverName().getString())) {
                name = "";
            }
            this.menu.setAnvilItemName(name);
        }
    }

    @Override
    public void updateBeforeRender() {
        enchantCostLabel.setCost(menu.getAnvilCost());
    }
}
