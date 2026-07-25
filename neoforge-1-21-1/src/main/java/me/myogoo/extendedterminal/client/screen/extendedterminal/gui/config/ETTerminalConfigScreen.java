package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import de.mari_023.ae2wtlib.AE2wtlibAdditionalComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.networking.TerminalSettingsPacket;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class ETTerminalConfigScreen implements MyoConfigTabScreen {
    private ETWTMenu menu;
    private AECheckbox pickBlock;
    private AECheckbox craftIfMissing;
    private AECheckbox restock;
    private AECheckbox magnet;
    private AECheckbox pickupToME;

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof ETWTMenu menu)) {
            return;
        }

        this.menu = menu;
        this.pickBlock = widget.addCheckbox("pickBlock", TextConstants.PICK_BLOCK, this::changeVisibility);
        this.craftIfMissing = widget.addCheckbox("craftIfMissing", TextConstants.CRAFT_IF_MISSING, this::save);
        this.restock = widget.addCheckbox("restock", TextConstants.RESTOCK, this::save);
        this.magnet = widget.addCheckbox("magnet", TextConstants.MAGNET, this::save);
        this.pickupToME = widget.addCheckbox("pickupToME", TextConstants.PICKUP_TO_ME, this::save);

        var stack = stack();
        pickBlock.setSelected(stack.getOrDefault(AE2wtlibComponents.PICK_BLOCK, false));
        craftIfMissing.setSelected(stack.getOrDefault(AE2wtlibComponents.CRAFT_IF_MISSING, false));
        craftIfMissing.active = pickBlock.isSelected();
        restock.setSelected(stack.getOrDefault(AE2wtlibComponents.RESTOCK, false));

        var magnetMode = stack.getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF);
        magnet.setSelected(magnetMode.magnet());
        pickupToME.setSelected(magnetMode.pickupToME());

        if (MagnetHandler.getMagnetMode(stack) == MagnetMode.NO_CARD) {
            magnet.active = false;
            pickupToME.active = false;
        }
    }

    private ItemStack stack() {
        if (menu == null || !(menu.getHost() instanceof WTMenuHost host)) {
            return ItemStack.EMPTY;
        }
        return host.getItemStack();
    }

    private void changeVisibility() {
        craftIfMissing.active = pickBlock.isSelected();
        save();
    }

    private void save() {
        if (menu == null || !(menu.getHost() instanceof WTMenuHost host)) {
            return;
        }

        var locator = host.getLocator();
        if (locator == null) {
            return;
        }

        PacketDistributor.sendToServer(new TerminalSettingsPacket(locator,
                pickBlock.isSelected(), restock.isSelected(), magnet.isSelected(), pickupToME.isSelected(),
                craftIfMissing.isSelected()));
    }
}
