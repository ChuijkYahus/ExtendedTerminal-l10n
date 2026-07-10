package me.myogoo.extendedterminal.client.screen.extendedcrafting.wt;

import appeng.client.gui.style.ScreenStyle;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.gui.IconButton;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnitedWTScreen extends UnitedTerminalScreen<UnitedWTMenu> implements IUniversalTerminalCapable {
    private final ScrollingUpgradesPanel upgradesPanel;
    private final IconButton magnetCardMenuButton;

    public UnitedWTScreen(UnitedWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if (menu.isWUT()) {
            addToLeftToolbar(cycleTerminalButton());
        }

        magnetCardMenuButton = addToLeftToolbar(IconButton.withAE2Background(
                btn -> getMenu().openMagnetMenu(),
                Icon.MAGNET).withTooltip(TextConstants.MAGNET_FILTER));

        this.upgradesPanel = addUpgradePanel(widgets, getMenu());
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        magnetCardMenuButton.setVisibility(switch (getMenu().getMagnetMode()) {
            case INVALID, NO_CARD -> false;
            case OFF, PICKUP_ME, PICKUP_INVENTORY, PICKUP_ME_NO_MAGNET -> true;
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int keyPressed) {
        boolean handled = super.keyPressed(keyCode, scanCode, keyPressed);
        if (!handled) {
            return checkForTerminalKeys(keyCode, scanCode);
        }
        return true;
    }

    @Override
    public @NotNull WTMenuHost getHost() {
        return (WTMenuHost) this.menu.getHost();
    }
}
