package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.core.definitions.ItemDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.item.ChargedEnderPearlItem;
import me.myogoo.extendedterminal.item.TooltipBlockItem;
import me.myogoo.extendedterminal.item.TooltipItem;
import me.myogoo.extendedterminal.item.wtitem.ETWTItem;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ETItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static final ItemDefinition<Item> COMPAT_PROCESSOR = createItem("compat processor",
            ExtendedTerminal.makeId("compat_processor"),
            properties -> new TooltipItem(properties, ETTranslationKey.ITEM.ITEM_DEPRECATED_MATERIAL_TOOLTIP.key()));

    public static final ItemDefinition<Item> COMPAT_PRESS = createItem("compat press",
            ExtendedTerminal.makeId("compat_press"),
            properties -> new TooltipItem(properties, ETTranslationKey.ITEM.ITEM_DEPRECATED_MATERIAL_TOOLTIP.key()));

    public static final ItemDefinition<Item> PRINTED_COMPAT_PROCESSOR = createItem("printed compat circuit",
            ExtendedTerminal.makeId("printed_compat_processor"),
            properties -> new TooltipItem(properties, ETTranslationKey.ITEM.ITEM_DEPRECATED_MATERIAL_TOOLTIP.key()));

    public static final ItemDefinition<ChargedEnderPearlItem> CHARGED_ENDER_PEARL = createItem("charged ender pearl",
            ExtendedTerminal.makeId("charged_ender_pearl"),
            ChargedEnderPearlItem::new);

    public static final ItemDefinition<TooltipBlockItem> MATERIAL_CONVERTER = createItem("material_converter",
            ExtendedTerminal.makeId("material_converter"),
            properties -> new TooltipBlockItem(ETBlocks.MATERIAL_CONVERTER.get(), properties,
                    ETTranslationKey.ITEM.ITEM_MATERIAL_CONVERTER_TOOLTIP.key(),
                    ETTranslationKey.ITEM.ITEM_MATERIAL_CONVERTER_REMOVAL_WARNING.key()));

    public static <T extends Item> ItemDefinition<T> createItem(String name, ResourceLocation id,
            Function<Item.Properties, T> itemFactory) {
        var item = new ItemDefinition<>(name, REGISTER.registerItem(id.getPath(), itemFactory));
        ITEMS.add(item);
        return item;
    }

}
