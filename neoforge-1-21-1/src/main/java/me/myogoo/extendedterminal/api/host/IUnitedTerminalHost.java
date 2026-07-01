package me.myogoo.extendedterminal.api.host;

import appeng.api.storage.ITerminalHost;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedRecipeType;
import org.jetbrains.annotations.Nullable;

public interface IUnitedTerminalHost extends ITerminalHost {
    boolean getRememberRecipeType();

    void setRememberRecipeType(boolean remember);

    @Nullable
    UnitedRecipeType getUnitedRecipeType();

    void setUnitedRecipeType(@Nullable UnitedRecipeType recipeKind);
}
