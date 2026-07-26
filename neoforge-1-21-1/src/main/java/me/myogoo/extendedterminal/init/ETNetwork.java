package me.myogoo.extendedterminal.init;

import appeng.core.network.ServerboundPacket;
import com.blakebr0.extendedcrafting.ExtendedCrafting;
import me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.FillUnitedGridPacket;
import me.myogoo.extendedterminal.network.serverbound.ae2helpers.FillPendingCraftingSlotPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ETNetwork {
    public static void init(RegisterPayloadHandlersEvent event) {
        var register = event.registrar(ExtendedCrafting.MOD_ID);

        register.playToServer(FillTableCraftingGridFromRecipePacket.TYPE, FillTableCraftingGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
        register.playToServer(FillUnitedGridPacket.TYPE, FillUnitedGridPacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
        register.playToServer(FillSmithingGridFromRecipePacket.TYPE, FillSmithingGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
        register.playToServer(FillStonecutterGridFromRecipePacket.TYPE, FillStonecutterGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
        register.playToServer(FillPendingCraftingSlotPacket.TYPE, FillPendingCraftingSlotPacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
    }
}
