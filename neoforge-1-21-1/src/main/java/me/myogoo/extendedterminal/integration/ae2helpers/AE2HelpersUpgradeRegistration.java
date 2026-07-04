package me.myogoo.extendedterminal.integration.ae2helpers;

import appeng.api.upgrades.Upgrades;
import me.myogoo.extendedterminal.init.ETParts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

public final class AE2HelpersUpgradeRegistration {
    private AE2HelpersUpgradeRegistration() {
    }

    public static void registerTerminalPartUpgrades(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM)) {
            return;
        }

        for (var terminalPart : ETParts.TERMINAL_PARTS) {
            registerSupportedCards(terminalPart.asItem());
        }
    }

    public static void registerSupportedCards(ItemLike terminal) {
        registerImportExportCards(terminal);
        registerResultImportCard(terminal);
    }

    private static void registerImportExportCards(ItemLike terminal) {
        findImportExportCard("import_card").ifPresent(card -> Upgrades.add(card, terminal, 1));
        findImportExportCard("export_card").ifPresent(card -> Upgrades.add(card, terminal, 1));
    }

    public static void registerResultImportCard(ItemLike terminal) {
        findResultImportCard().ifPresent(card -> Upgrades.add(card, terminal, 1));
    }

    private static Optional<Item> findImportExportCard(String path) {
        var modernId = ResourceLocation.fromNamespaceAndPath("ae2importexportcard", path);
        var legacyId = ResourceLocation.fromNamespaceAndPath("ae2insertexportcard", path);
        return BuiltInRegistries.ITEM.getOptional(modernId)
                .or(() -> BuiltInRegistries.ITEM.getOptional(legacyId));
    }

    private static Optional<Item> findResultImportCard() {
        var canonicalId = ResourceLocation.fromNamespaceAndPath("ae2helpers", "result_import_card");
        return BuiltInRegistries.ITEM.getOptional(canonicalId);
    }
}
