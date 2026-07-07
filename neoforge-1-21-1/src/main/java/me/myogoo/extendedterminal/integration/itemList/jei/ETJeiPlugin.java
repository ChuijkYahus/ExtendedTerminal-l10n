package me.myogoo.extendedterminal.integration.itemList.jei;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.ExtendedTerminal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@JeiPlugin
public class ETJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ExtendedTerminal.makeId("jei_plugin");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static IJeiRuntime runtime;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        JeiRegisterHelper.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        JeiRegisterHelper.registerRecipeTransfer(registration);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        JeiRegisterHelper.registerGuiHandler(registration);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    public static RecipeType<?> recipeTypeFor(ResourceLocation uid) {
        var currentRuntime = runtime;
        if (currentRuntime == null) {
            return RecipeTypes.CRAFTING;
        }
        return currentRuntime.getRecipeManager()
                .getRecipeType(uid)
                .orElse(RecipeTypes.CRAFTING);
    }
}
