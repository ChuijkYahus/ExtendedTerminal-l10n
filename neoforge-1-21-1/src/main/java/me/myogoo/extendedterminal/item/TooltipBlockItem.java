package me.myogoo.extendedterminal.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TooltipBlockItem extends BlockItem {
    private final String tooltipKey;
    private final String warningKey;

    public TooltipBlockItem(Block block, Properties properties, String tooltipKey, String warningKey) {
        super(block, properties);
        this.tooltipKey = tooltipKey;
        this.warningKey = warningKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(warningKey).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
    }
}
