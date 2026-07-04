package me.myogoo.extendedterminal.api.host;

import appeng.api.storage.ITerminalHost;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import org.jetbrains.annotations.Nullable;

public interface IUnitedTerminalHost extends ITerminalHost {
    boolean shouldRememberRecipeType();

    void setRememberRecipeType(boolean remember);

    @Nullable
    MyoRecipeType getLastRecipeType();

    void setLastRecipeType(@Nullable MyoRecipeType recipeType);
}
