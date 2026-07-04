package me.myogoo.extendedterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.client.screen.avaritiaNeo.NeoExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.SculkTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.wt.UnitedWTScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.wt.ETWTScreen;
import me.myogoo.extendedterminal.init.ETConfigTab;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.integration.polymorph.ETPolymorph;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import me.myogoo.extendedterminal.api.annotation.Polymorph;

@Mod(value = ExtendedTerminal.MODID, dist = Dist.CLIENT)
public class ETClient {
    public ETClient(IEventBus eventBus) {
        eventBus.addListener(ETClient::clientSetup);
        eventBus.addListener(RegisterColorHandlersEvent.Item.class, ETClient::initColorParts);
        eventBus.addListener(ETClient::initScreens);
        if(MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            eventBus.addListener(ETClient::InitWTScreen);
        }
        if(MyotusAPI.integrations().isLoaded(Polymorph.class)) {
            ETPolymorph.init();
        }
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ETConfigTab::initialize);
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        // extended terminal
        InitScreens.register(event, ETTerminalMenu.TYPE, ETTerminalScreen<ETTerminalMenu>::new, "/screens/et_terminal.json");

        // extended crafting terminals
        InitScreens.register(event, BasicTerminalMenu.TYPE, BasicTerminalScreen::new, "/screens/extended_terminal/basic_terminal.json");
        InitScreens.register(event, AdvancedTerminalMenu.TYPE, AdvancedTerminalScreen::new, "/screens/extended_terminal/advanced_terminal.json");
        InitScreens.register(event, EliteTerminalMenu.TYPE, EliteTerminalScreen::new, "/screens/extended_terminal/elite_terminal.json");
        InitScreens.register(event, UltimateTerminalMenu.TYPE, UltimateTerminalScreen::new, "/screens/extended_terminal/ultimate_terminal.json");
        InitScreens.register(event, UnitedTerminalMenu.TYPE, UnitedTerminalScreen<UnitedTerminalMenu>::new, "/screens/extended_terminal/united_terminal.json");

        // avaritia terminals
        InitScreens.register(event, SculkTerminalMenu.TYPE, SculkTerminalScreen::new, "/screens/avaritia/sculk_terminal.json");
        InitScreens.register(event, NetherTerminalMenu.TYPE, NetherTerminalScreen::new, "/screens/avaritia/nether_terminal.json");
        InitScreens.register(event, EndTerminalMenu.TYPE, EndTerminalScreen::new, "/screens/avaritia/end_terminal.json");
        InitScreens.register(event, ExtremeTerminalMenu.TYPE, ExtremeTerminalScreen::new, "/screens/avaritia/extreme_terminal.json");

        InitScreens.register(event, NeoExtremeTerminalMenu.TYPE, NeoExtremeTerminalScreen::new, "/screens/avaritia/extreme_terminal.json");
    }

    public static void InitWTScreen(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ETWTMenu.TYPE, ETWTScreen::new, "/screens/wireless_et_terminal.json");
        InitScreens.register(event, UnitedWTMenu.TYPE, UnitedWTScreen::new, "/screens/wireless_united_terminal.json");
    }

    public static void initColorParts(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex)
                        -> (new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000),
                            ETParts.TERMINAL_PARTS.stream().map(x -> (ItemLike)x).toArray(ItemLike[]::new));
    }
}
