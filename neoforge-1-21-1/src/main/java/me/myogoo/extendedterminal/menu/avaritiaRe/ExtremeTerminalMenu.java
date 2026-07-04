package me.myogoo.extendedterminal.menu.avaritiaRe;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ExtremeTerminalMenu extends AvaritiaTerminalBaseMenu {
    public static final MenuType<ExtremeTerminalMenu> TYPE = MenuTypeBuilder
            .create(ExtremeTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.EXTREME_TERMINAL.getId());

    public ExtremeTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.EXTREME_TERMINAL, AvaritiaReConfig.INSTANCE.getExtremeConfig());
    }
}
