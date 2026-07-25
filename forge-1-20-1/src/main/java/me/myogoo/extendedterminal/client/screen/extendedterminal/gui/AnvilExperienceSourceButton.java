package me.myogoo.extendedterminal.client.screen.extendedterminal.gui;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.myotus.client.gui.widgets.button.MyoCycleButton;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnvilExperienceSourceButton extends MyoCycleButton {
    private static final ResourceLocation APPLIED_EXPERIENCED_CELL_ID =
            new ResourceLocation("appex", "experience_storage_cell_1k");
    private final ETTerminalMenu menu;

    public AnvilExperienceSourceButton(ETTerminalMenu menu) {
        super(
                menu::cycleAnvilExperienceSourcePriority,
                menu::cycleAnvilExperienceSourcePriority,
                () -> getSourceIcon(menu),
                () -> List.of(createSourceTooltip(menu)));
        this.menu = menu;
    }

    private static Component createSourceTooltip(ETTerminalMenu menu) {
        return Component.translatable(
                ETTranslationKey.GUI.ANVIL_EXPERIENCE_SOURCE_PRIORITY.key(),
                createSourcePriorityLabel(menu));
    }

    private static Component createSourcePriorityLabel(ETTerminalMenu menu) {
        return Component.translatable(menu.getSelectedAnvilExperienceSourceLabelKey().key());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partial);
        if (isPlayerSourceSelected()) {
            renderPlayerHead(guiGraphics);
        }
    }

    @Nullable
    private static Item getSourceIcon(ETTerminalMenu menu) {
        return switch (menu.getSelectedAnvilExperienceSource()) {
            case FLUID_XP -> Items.EXPERIENCE_BOTTLE;
            case APPLIED_EXPERIENCED_AMOUNT -> getAppliedExperiencedCellIcon();
            case PLAYER -> null;
        };
    }

    private boolean isPlayerSourceSelected() {
        return this.menu.getSelectedAnvilExperienceSource() == ExperienceMath.ExperienceSource.PLAYER;
    }

    private void renderPlayerHead(GuiGraphics guiGraphics) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        var stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), player.getGameProfile()));
        guiGraphics.renderItem(stack, getX(), getY());
    }

    private static Item getAppliedExperiencedCellIcon() {
        return BuiltInRegistries.ITEM.getOptional(APPLIED_EXPERIENCED_CELL_ID)
                .orElse(Items.EXPERIENCE_BOTTLE);
    }
}
