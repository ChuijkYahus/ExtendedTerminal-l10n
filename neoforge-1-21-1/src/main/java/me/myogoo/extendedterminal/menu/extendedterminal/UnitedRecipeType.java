package me.myogoo.extendedterminal.menu.extendedterminal;

import me.myogoo.extendedterminal.api.annotation.AvaritiaNeo;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.api.annotation.Minecraft;
import me.myogoo.extendedterminal.api.integration.IntegrationConstant;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;


public enum UnitedRecipeType {
    VANILLA(Minecraft.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), -1, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.MINECRAFT, "crafting_table")),

    BASIC(ExtendedCrafting.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 1, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.EXTENDED_CRAFTING_MODID, "basic_table")),
    ADVANCED(ExtendedCrafting.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 2, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.EXTENDED_CRAFTING_MODID, "advanced_table")),
    ELITE(ExtendedCrafting.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 3, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.EXTENDED_CRAFTING_MODID, "elite_table")),
    ULTIMATE(ExtendedCrafting.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 4, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.EXTENDED_CRAFTING_MODID, "ultimate_table")),

    EXTREME_MEO(AvaritiaNeo.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 4, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.AVARITIA_MODID, "extreme_crafting_table")),

    SCULK(ReAvaritia.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 1, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.AVARITIA_MODID, "sculk_crafting_table")),
    Nether(ReAvaritia.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 2, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.AVARITIA_MODID, "nether_crafting_table")),
    END(ReAvaritia.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 3, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.AVARITIA_MODID, "end_crafting_table")),
    EXTREME(ReAvaritia.class, ETTranslationKey.BLOCK.BLOCK_MATERIAL_CONVERTER.key(), 4, ResourceLocation.fromNamespaceAndPath(IntegrationConstant.AVARITIA_MODID, "extreme_crafting_table"));

    private final String translateKey;
    private final int tier;
    private final ResourceLocation icon;
    private final Class<? extends Annotation> myomodAnnotation;

    UnitedRecipeType(@Nullable Class<? extends Annotation> annotationClass, String translateKey, int tier, ResourceLocation icon) {
        this.translateKey = translateKey;
        this.tier = tier;
        this.icon = icon;
        this.myomodAnnotation = annotationClass;
    }

    public boolean isActive() {
        return myomodAnnotation.equals(Minecraft.class) || MyotusAPI.integrations().isLoaded(myomodAnnotation);
    }

    public Class<?> getMyomodAnnotation() {
        return this.myomodAnnotation;
    }

    public int tier() {
        return tier;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public String getTranslateKey() {
        return translateKey;
    }
}
