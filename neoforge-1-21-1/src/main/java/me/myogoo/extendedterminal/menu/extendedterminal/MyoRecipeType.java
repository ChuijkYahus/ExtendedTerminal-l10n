package me.myogoo.extendedterminal.menu.extendedterminal;

import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.api.annotation.VANILLA;
import me.myogoo.extendedterminal.api.integration.IntegrationConstant;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.myotus.api.MyotusAPI;

import java.lang.annotation.Annotation;


public enum MyoRecipeType {
    VANILLA(VANILLA.class, ETTranslationKey.BLOCK.MINECRAFT_CRAFTING_TABLE.key(), -1, IntegrationConstant.MINECRAFT, "crafting_table", "crafting"),

    BASIC(ExtendedCrafting.class, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_BASIC.key(), 1, IntegrationConstant.EXTENDED_CRAFTING_MODID, "basic_table","basic_crafting"),
    ADVANCED(ExtendedCrafting.class, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_ADVANCED.key(), 2, IntegrationConstant.EXTENDED_CRAFTING_MODID, "advanced_table","advanced_crafting"),
    ELITE(ExtendedCrafting.class, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_ELITE.key(), 3, IntegrationConstant.EXTENDED_CRAFTING_MODID, "elite_table","elite_crafting"),
    ULTIMATE(ExtendedCrafting.class, ETTranslationKey.BLOCK.EXTENDED_CRAFTING_ULTIMATE.key(), 4, IntegrationConstant.EXTENDED_CRAFTING_MODID, "ultimate_table","ultimate_crafting"),

    EXTREME_MEO(AvaritiaNeo.class, ETTranslationKey.BLOCK.AVARITIA_NEO_EXTREME.key(), 4, IntegrationConstant.AVARITIA_MODID, "extreme_crafting_table","extreme_crafting"),

    SCULK(ReAvaritia.class, ETTranslationKey.BLOCK.RE_AVARITIA_SCULK.key(), 1, IntegrationConstant.AVARITIA_MODID, "sculk_crafting_table","sculk_craft"),
    Nether(ReAvaritia.class, ETTranslationKey.BLOCK.RE_AVARITIA_NETHER.key(), 2, IntegrationConstant.AVARITIA_MODID, "nether_crafting_table","nether_craft"),
    END(ReAvaritia.class, ETTranslationKey.BLOCK.RE_AVARITIA_END.key(), 3, IntegrationConstant.AVARITIA_MODID, "end_crafting_table","end_craft"),
    EXTREME(ReAvaritia.class, ETTranslationKey.BLOCK.RE_AVARITIA_EXTREME.key(), 4, IntegrationConstant.AVARITIA_MODID, "extreme_crafting_table","extreme_craft"),;

    private final String translateKey;
    private final int tier;
    private final String blockId;
    private final String modid;
    private final String jeiUid;
    private final Class<? extends Annotation> myomodAnnotation;

    MyoRecipeType(Class<? extends Annotation> annotationClass, String translateKey, int tier, String modid, String blockId, String JeiRecipeId) {
        this.translateKey = translateKey;
        this.tier = tier;
        this.modid = modid;
        this.blockId = blockId;
        this.jeiUid = JeiRecipeId;
        this.myomodAnnotation = annotationClass;
    }

    public boolean isActive() {
        return myomodAnnotation.equals(VANILLA.class) || MyotusAPI.integrations().isLoaded(myomodAnnotation);
    }

    public Class<?> getMyomodAnnotation() {
        return this.myomodAnnotation;
    }

    public int tier() {
        return tier;
    }

    public String blockId() {
        return blockId;
    }

    public String modid() {
        return modid;
    }

    public String jeiUid() {
        return jeiUid;
    }

    public String getTranslateKey() {
        return translateKey;
    }
}
