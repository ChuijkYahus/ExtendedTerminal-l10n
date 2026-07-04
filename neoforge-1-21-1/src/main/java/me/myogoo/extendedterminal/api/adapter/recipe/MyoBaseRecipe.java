package me.myogoo.extendedterminal.api.adapter.recipe;

import net.minecraft.world.item.crafting.Recipe;

public interface MyoBaseRecipe {
    <R extends Recipe<?>> R get();
}
