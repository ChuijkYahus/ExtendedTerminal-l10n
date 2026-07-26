package me.myogoo.extendedterminal.api.translation;

import me.myogoo.myotus.client.MyoTranslateKey;

public enum ETTranslationKey implements MyoTranslateKey {
    ITEM_GROUP_EXTENDED_TERMINAL("itemGroup.extendedterminal");

    private final String key;

    ETTranslationKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

    public enum GUI implements MyoTranslateKey {
        GUI_CONFIG_TITLE("gui.extendedterminal.config.title"),
        GUI_CONFIG_UNITED_TITLE("gui.extendedterminal.config.united.title"),
        GUI_CONFIG_REMEMBER_UNITED_RECIPE_TYPE("gui.extendedterminal.config.remember_united_recipe_type"),
        GUI_BASIC_TERMINAL("gui.extendedterminal.basicTerminal"),
        GUI_ADVANCED_TERMINAL("gui.extendedterminal.advancedTerminal"),
        GUI_ELITE_TERMINAL("gui.extendedterminal.eliteTerminal"),
        GUI_ULTIMATE_TERMINAL("gui.extendedterminal.ultimateTerminal"),
        GUI_SCULK_TERMINAL("gui.extendedterminal.sculkTerminal"),
        GUI_NETHER_TERMINAL("gui.extendedterminal.netherTerminal"),
        GUI_END_TERMINAL("gui.extendedterminal.endTerminal"),
        GUI_EXTREME_TERMINAL("gui.extendedterminal.extremeTerminal"),
        GUI_EPIC_TERMINAL("gui.extendedterminal.epicTerminal"),
        GUI_ET_TERMINAL("gui.extendedterminal.et_terminal"),
        GUI_UNITED_TERMINAL("gui.extendedterminal.unitedTerminal"),
        GUI_CRAFTING_PANEL("gui.extendedterminal.craftingpanel"),
        GUI_SMITHING_TABLE_PANEL("gui.extendedterminal.smithingtablepanel"),
        GUI_STONECUTTER_PANEL("gui.extendedterminal.stonecutterpanel"),
        GUI_ANVIL_PANEL("gui.extendedterminal.anvilpanel"),
        GUI_ANVIL_PANEL_XP_COST("gui.extendedterminal.anvilpanel.xpcost"),
        GUI_AE2WTLIB_PICK_BLOCK_TEXT("gui.ae2wtlib.pick_block.text"),
        ANVIL_EXPERIENCE_SOURCE_PRIORITY("gui.extendedterminal.anvil_experience_source.priority"),
        ANVIL_EXPERIENCE_SOURCE_PLAYER("gui.extendedterminal.anvil_experience_source.player"),
        ANVIL_EXPERIENCE_SOURCE_FLUID("gui.extendedterminal.anvil_experience_source.fluid"),
        ANVIL_EXPERIENCE_SOURCE_CELL("gui.extendedterminal.anvil_experience_source.cell");

        private final String key;

        GUI(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum KEY implements MyoTranslateKey {
        KEY_WIRELESS_ET_TERMINAL("key.ae2.wireless_et_terminal");

        private final String key;

        KEY(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum ITEM implements MyoTranslateKey {
        ITEM_ET_TERMINAL("item.extendedterminal.et_terminal"),
        ITEM_WIRELESS_ET_TERMINAL("item.extendedterminal.wireless_et_terminal"),
        ITEM_BASIC_TERMINAL("item.extendedterminal.basic_terminal"),
        ITEM_ADVANCED_TERMINAL("item.extendedterminal.advanced_terminal"),
        ITEM_ELITE_TERMINAL("item.extendedterminal.elite_terminal"),
        ITEM_ULTIMATE_TERMINAL("item.extendedterminal.ultimate_terminal"),
        ITEM_UNITED_TERMINAL("item.extendedterminal.united_terminal"),
        ITEM_WIRELESS_UNITED_TERMINAL("item.extendedterminal.wireless_united_terminal"),
        ITEM_SCULK_TERMINAL("item.extendedterminal.sculk_terminal"),
        ITEM_NETHER_TERMINAL("item.extendedterminal.nether_terminal"),
        ITEM_END_TERMINAL("item.extendedterminal.end_terminal"),
        ITEM_EXTREME_TERMINAL("item.extendedterminal.extreme_terminal"),
        ITEM_NEO_EXTREME_TERMINAL("item.extendedterminal.neo_extreme_terminal"),
        ITEM_EPIC_TERMINAL("item.extendedterminal.epic_terminal"),
        ITEM_COMPAT_PROCESSOR("item.extendedterminal.compat_processor"),
        ITEM_PRINTED_COMPAT_PROCESSOR("item.extendedterminal.printed_compat_processor"),
        ITEM_COMPAT_PRESS("item.extendedterminal.compat_press"),
        ITEM_CHARGED_ENDER_PEARL("item.extendedterminal.charged_ender_pearl"),
        ITEM_MATERIAL_CONVERTER("item.extendedterminal.material_converter"),
        ITEM_MATERIAL_CONVERTER_TOOLTIP("item.extendedterminal.material_converter.tooltip"),
        ITEM_MATERIAL_CONVERTER_REMOVAL_WARNING("item.extendedterminal.material_converter.removal_warning"),
        ITEM_DEPRECATED_MATERIAL_TOOLTIP("item.extendedterminal.item.tooltip.deprecated_material"),
        ITEM_UNREGISTERED_TERMINAL_TOOLTIP("item.extendedterminal.item.tooltip.unregistered_terminal");

        private final String key;

        ITEM(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum BLOCK implements MyoTranslateKey {
        BLOCK_MATERIAL_CONVERTER("block.extendedterminal.material_converter"),
        MINECRAFT_CRAFTING_TABLE("block.minecraft.crafting_table"),
        EXTENDED_CRAFTING_BASIC("block.extendedcrafting.basic_table"),
        EXTENDED_CRAFTING_ADVANCED("block.extendedcrafting.advanced_table"),
        EXTENDED_CRAFTING_ELITE("block.extendedcrafting.elite_table"),
        EXTENDED_CRAFTING_ULTIMATE("block.extendedcrafting.ultimate_table"),
        AVARITIA_NEO_EXTREME("block.avaritia.extreme_crafting_table"),
        RE_AVARITIA_SCULK("block.avaritia.sculk_crafting_table"),
        RE_AVARITIA_NETHER("block.avaritia.nether_crafting_table"),
        RE_AVARITIA_END("block.avaritia.end_crafting_table"),
        RE_AVARITIA_EXTREME("block.avaritia.extreme_crafting_table");

        private final String key;

        BLOCK(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum MESSAGE implements MyoTranslateKey {
        MESSAGE_MATERIAL_CONVERTER_CONVERTED("message.extendedterminal.material_converter.converted"),
        MESSAGE_MATERIAL_CONVERTER_EMPTY("message.extendedterminal.material_converter.empty"),
        MESSAGE_MATERIAL_CONVERTER_MISSING_TARGETS("message.extendedterminal.material_converter.missing_targets"),
        MESSAGE_MATERIAL_CONVERTER_OFFLINE("message.extendedterminal.material_converter.offline");

        private final String key;

        MESSAGE(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public enum JEI implements MyoTranslateKey {
        JEI_NOT_SUPPORTED_ERROR("extendedterminal.jei.notsupportederror");

        private final String key;

        JEI(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }
}
