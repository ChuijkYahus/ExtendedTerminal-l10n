package me.myogoo.extendedterminal.network.serverbound;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedRecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FillUnitedGridPacket extends FillTableCraftingGridFromRecipePacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, FillUnitedGridPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    FillUnitedGridPacket::write,
                    FillUnitedGridPacket::decode);

    public static final CustomPacketPayload.Type<FillUnitedGridPacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_united_grid_from_recipe"));

    private final UnitedRecipeType recipeType;

    public FillUnitedGridPacket(@Nullable ResourceLocation recipeId, List<ItemStack> ingredientTemplates, boolean craftMissing, UnitedRecipeType recipeType) {
        super(recipeId, ingredientTemplates, craftMissing, 9, 9);
        this.recipeType = recipeType;
    }

    @Override
    public void write(RegistryFriendlyByteBuf stream) {
        if (recipeId != null) {
            stream.writeBoolean(true);
            stream.writeResourceLocation(recipeId);
        } else {
            stream.writeBoolean(false);
        }

        stream.writeInt(ingredientTemplates.size());
        for (var ingredientTemplate : ingredientTemplates) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(stream, ingredientTemplate);
        }
        stream.writeBoolean(craftMissing);
        stream.writeEnum(recipeType);
    }

    public static FillUnitedGridPacket decode(RegistryFriendlyByteBuf stream) {
        ResourceLocation recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceLocation();
        }
        var ingredientTemplates = NonNullList.withSize(stream.readInt(), ItemStack.EMPTY);
        ingredientTemplates.replaceAll(ignored -> ItemStack.OPTIONAL_STREAM_CODEC.decode(stream));
        var craftMissing = stream.readBoolean();
        UnitedRecipeType recipeType = stream.readEnum(UnitedRecipeType.class);
        return new FillUnitedGridPacket(recipeId, ingredientTemplates, craftMissing, recipeType);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        var menu = player.containerMenu;
        if(menu instanceof UnitedTerminalMenu unitedMenu) {
            unitedMenu.setSelectedRecipeType(this.recipeType);
        }

        super.handleOnServer(player);
    }
}
