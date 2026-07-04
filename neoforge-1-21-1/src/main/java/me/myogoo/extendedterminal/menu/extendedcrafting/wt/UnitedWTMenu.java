package me.myogoo.extendedterminal.menu.extendedcrafting.wt;

import appeng.menu.MenuOpener;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMenu;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class UnitedWTMenu extends UnitedTerminalMenu {
    public static final MenuType<UnitedWTMenu> TYPE = MenuTypeBuilder
            .create(UnitedWTMenu::new, UnitedWTHost.class)
            .buildUnregistered(ExtendedTerminal.makeId("united_terminal_wt"));
    public static final String MAGNET_MODE = "magnetMode";
    public static final String MAGNET_MENU = "magnetMenu";

    public UnitedWTMenu(int id, Inventory ip, UnitedWTHost host) {
        super(TYPE, id, ip, host);

        var singularitySlot = new RestrictedInputSlot(
                RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                host.getSubInventory(WTMenuHost.INV_SINGULARITY),
                0);
        addSlot(singularitySlot, AE2wtlibSlotSemantics.SINGULARITY);

        registerClientAction(MAGNET_MODE, MagnetMode.class, this::setMagnetMode);
        registerClientAction(MAGNET_MENU, this::openMagnetMenu);
    }

    public boolean isWUT() {
        return ((WTMenuHost) getHost()).getItemStack().getItem() instanceof ItemWUT;
    }

    public MagnetMode getMagnetMode() {
        return MagnetHandler.getMagnetMode(((WTMenuHost) getHost()).getItemStack());
    }

    public void setMagnetMode(MagnetMode mode) {
        if (isClientSide()) {
            sendClientAction(MAGNET_MODE, mode);
        }
        MagnetHandler.saveMagnetMode(((WTMenuHost) getHost()).getItemStack(), mode);
    }

    public void openMagnetMenu() {
        if (isClientSide()) {
            sendClientAction(MAGNET_MENU);
            return;
        }
        MenuOpener.open(MagnetMenu.TYPE, getPlayer(), getLocator());
    }
}
