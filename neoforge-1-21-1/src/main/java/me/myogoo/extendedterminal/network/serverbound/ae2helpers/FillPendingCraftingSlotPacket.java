package me.myogoo.extendedterminal.network.serverbound.ae2helpers;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.core.network.ServerboundPacket;
import com.google.common.primitives.Ints;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class FillPendingCraftingSlotPacket implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, FillPendingCraftingSlotPacket> STREAM_CODEC = StreamCodec
            .ofMember(FillPendingCraftingSlotPacket::write, FillPendingCraftingSlotPacket::decode);

    public static final CustomPacketPayload.Type<FillPendingCraftingSlotPacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_pending_crafting_slot"));

    private final int slotIndex;
    private final AEKey what;

    public FillPendingCraftingSlotPacket(int slotIndex, AEKey what) {
        this.slotIndex = slotIndex;
        this.what = what;
    }

    private void write(RegistryFriendlyByteBuf stream) {
        stream.writeVarInt(slotIndex);
        AEKey.writeKey(stream, what);
    }

    private static FillPendingCraftingSlotPacket decode(RegistryFriendlyByteBuf stream) {
        return new FillPendingCraftingSlotPacket(stream.readVarInt(), AEKey.readKey(stream));
    }

    @Override
    public CustomPacketPayload.@NotNull Type<FillPendingCraftingSlotPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (!(player.containerMenu instanceof ETTerminalBaseMenu<?> menu) || !(what instanceof AEItemKey itemKey)) {
            return;
        }

        var slots = menu.getSlots(menu.getCraftingGridSlotSemantic());
        if (slotIndex < 0 || slotIndex >= slots.size() || slots.get(slotIndex).hasItem()) {
            return;
        }

        var node = menu.getGridNode();
        if (node == null || !menu.getLinkStatus().connected()) {
            return;
        }

        var extracted = StorageHelper.poweredExtraction(
                menu.getEnergySource(),
                node.getGrid().getStorageService().getInventory(),
                itemKey,
                1,
                menu.getActionSource());
        if (extracted <= 0) {
            return;
        }

        menu.getCraftingMatrix().setItemDirect(slotIndex, itemKey.toStack(Ints.saturatedCast(extracted)));
        menu.slotsChanged(menu.getCraftingMatrix().toContainer());
        menu.broadcastChanges();
    }
}
