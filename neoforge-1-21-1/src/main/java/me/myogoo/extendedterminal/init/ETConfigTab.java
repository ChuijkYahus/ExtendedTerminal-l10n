package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.gui.config.UnitedTerminalConfigScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config.ETTerminalConfigScreen;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.part.extendedterminal.UnitedTerminalPart;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import me.myogoo.myotus.api.config.MyoConfigTab;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ETConfigTab {
    public static void initialize() {
        var ae2wtlibLoaded = MyotusAPI.integrations().isLoaded(AE2WTLib.class);

        if (ae2wtlibLoaded) {
            MyotusAPI.configTabs()
                    .terminalConfigTab(new MyoConfigTab(
                            ResourceLocation.fromNamespaceAndPath(ExtendedTerminal.MODID, "wireless_et_terminal"),
                            Component.translatable(ETTranslationKey.GUI.GUI_CONFIG_TITLE.key()),
                            WTItems.WIRELESS_ET_TERMINAL.stack(),
                            "et_config.json",
                            new ETTerminalConfigScreen()
                    ).visibleWhen(context -> context.host() instanceof ETWTHost));
        }

        MyotusAPI.configTabs()
                .terminalConfigTab(new MyoConfigTab(
                        ResourceLocation.fromNamespaceAndPath(ExtendedTerminal.MODID, "united_terminal"),
                        Component.translatable(ETTranslationKey.GUI.GUI_CONFIG_UNITED_TITLE.key()),
                        ETParts.UNITED_TERMINAL_PART.stack(),
                        "united_config.json",
                        new UnitedTerminalConfigScreen()
                ).visibleWhen(context -> context.host() instanceof UnitedTerminalPart
                        || (ae2wtlibLoaded && context.host() instanceof UnitedWTHost)));
    }
}
