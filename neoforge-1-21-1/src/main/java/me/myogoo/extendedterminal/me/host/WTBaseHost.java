package me.myogoo.extendedterminal.me.host;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.storage.ITerminalHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class WTBaseHost extends WTMenuHost implements IViewCellStorage, ITerminalHost {
    public WTBaseHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }
}
