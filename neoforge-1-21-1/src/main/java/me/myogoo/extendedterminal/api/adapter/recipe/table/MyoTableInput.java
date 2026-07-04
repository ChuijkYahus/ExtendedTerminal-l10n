package me.myogoo.extendedterminal.api.adapter.recipe.table;

import me.myogoo.myotus.util.MyoLogger;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MyoTableInput implements RecipeInput {
    private final int width;
    private final int height;
    private final List<ItemStack> items;
    private final int tier;

    private final CraftingInput.Positioned positioned;

    private MyoTableInput(int width, int height, List<ItemStack> items, int tier) {
        positioned = CraftingInput.ofPositioned(width,height,items);
        this.width = width;
        this.height = height;
        this.items = items;
        this.tier = tier;
    }

    @SuppressWarnings("unchecked")
    public <I extends CraftingInput> I cast(Class<I> inputClass) {
        Method method;
        try {
            if (inputClass.equals(CraftingInput.class)) {
                method = inputClass.getDeclaredMethod("of", int.class, int.class, List.class);
                return (I) method.invoke(null, width(), height(), items());
            } else {
                method = inputClass.getDeclaredMethod("of", int.class, int.class, List.class, int.class);
                return (I) method.invoke(null, width(), height(), items(), tier());
            }
        } catch (NoSuchMethodException e) {
            MyoLogger.error("Failed to cast MyoTableInput to " + inputClass.getName(), e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            MyoLogger.error("Failed to invoke `of` MyoTableInput to" + inputClass.getName(), e);
            throw new RuntimeException(e);
        }
    }

    public CraftingInput cast() {
        return cast(CraftingInput.class);
    }

    public static MyoTableInput create(int width, int height, List<ItemStack> items, int tier) {
        return new MyoTableInput(width, height, items, tier);
    }

    public static MyoTableInput create(int width, int height, List<ItemStack> items) {
        return create(width, height, items, 0);
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public List<ItemStack> items() {
        return this.items;
    }

    public int tier() {
        return this.tier;
    }

    public int top() {
        return this.positioned.top();
    }

    public int left() {
        return this.positioned.left();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MyoTableInput that)) return false;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (tier != that.tier) return false;
        if (items.size() != that.items.size()) return false;
        return ItemStack.listMatches(items, that.items);
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return (ItemStack) this.items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }
}
