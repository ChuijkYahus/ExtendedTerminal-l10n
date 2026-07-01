package me.myogoo.extendedterminal.api.adapter.recipe.table;

import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.adapter.recipe.table.TableInputAdapter;

public interface ITableInputAdapter {
    int top();
    int tier();
    int left();
    int width();
    int height();
    static ITableInputAdapter of(TierInput input) {
        return new TableInputAdapter(input);
    }

    static ITableInputAdapter of(TableCraftingInput input) {
        return new TableInputAdapter(input);
    }
}
