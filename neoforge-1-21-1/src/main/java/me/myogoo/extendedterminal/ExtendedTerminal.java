package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.integration.ae2helpers.AE2HelpersCompat;
import me.myogoo.extendedterminal.integration.ae2helpers.AE2HelpersUpgradeRegistration;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.init.wt.WTInit;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.init.wt.WTMenus;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import me.myogoo.extendedterminal.api.annotation.InvTweaks;

@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal(IEventBus modEventBus, ModContainer modContainer) {
        ETModIntegration.initialize();
        AE2HelpersCompat.logDetectedState(LOGGER);
        ETConfig.initialize(modContainer);

        ETItems.REGISTER.register(modEventBus);
        ETBlocks.REGISTER.register(modEventBus);
        ETBlockEntities.REGISTER.register(modEventBus);
        ETCreativeTab.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);
        ETDataComponent.REGISTER.register(modEventBus);
        modEventBus.addListener(EventPriority.LOWEST, AE2HelpersUpgradeRegistration::registerTerminalPartUpgrades);

        if(MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            WTItems.register();
            WTMenus.register();
            WTInit.registerTerminal();
            modEventBus.addListener(EventPriority.LOWEST, WTInit::registerUpgradeTooltips);
            modEventBus.addListener(WTInit::initCapabilities);
        }
        if(MyotusAPI.integrations().isLoaded(InvTweaks.class)) {
            InterModComms.sendTo("invtweaks", "blacklist-screen", () -> "me.myogoo.extendedterminal.client.screen.*");
            InterModComms.sendTo("invtweaks", "blacklist-screen", () -> "me.myogoo.extendedterminal.menu.*");
        }

        modEventBus.addListener(ETNetwork::init);

    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
