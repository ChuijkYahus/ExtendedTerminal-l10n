package me.myogoo.extendedterminal.adapter.recipe.table;

import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableInputAdapter;
import net.minecraft.world.item.crafting.CraftingInput;

public class TableInputAdapter implements ITableInputAdapter {
    private final int top;
    private final int tier;
    private final int left;
    private final int width;
    private final int height;

    public TableInputAdapter(TierInput input) {
        this.left = input.left();
        this.tier = input.tier();
        this.top = input.top();
        this.width = input.width();
        this.height = input.height();
    }

    public TableInputAdapter(TableCraftingInput input) {
        this.left = input.left();
        this.tier = input.tier();
        this.top = input.top();
        this.width = input.width();
        this.height = input.height();
    }

    public TableInputAdapter(CraftingInput input) {
        CraftingInput.Positioned positioned = CraftingInput.ofPositioned(input.width(), input.height(), input.items());
        this.left = positioned.left();
        this.top = positioned.top();
        this.width = input.width();
        this.height = input.height();
        this.tier = 4;
    }

    @Override
    public int top() {
        return top;
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public int left() {
        return left;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }
}
