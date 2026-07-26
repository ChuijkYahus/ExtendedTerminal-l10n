package me.myogoo.extendedterminal.network.serverbound;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FillUnitedGridPacket extends FillTableCraftingGridFromRecipePacket {
    private static final int UNITED_GRID_SIZE = 81;
    private static final int MAX_RECIPE_TYPE_NAME_LENGTH = 32;

    public static final StreamCodec<RegistryFriendlyByteBuf, FillUnitedGridPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    FillUnitedGridPacket::write,
                    FillUnitedGridPacket::decode);

    public static final CustomPacketPayload.Type<FillUnitedGridPacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_united_grid_from_recipe"));

    private final MyoRecipeType recipeType;

    public FillUnitedGridPacket(@Nullable ResourceLocation recipeId, List<ItemStack> ingredientTemplates, boolean craftMissing,
                                int recipeWidth, int recipeHeight, MyoRecipeType recipeType) {
        super(recipeId, validateIngredientTemplates(ingredientTemplates), craftMissing,
                validateRecipeDimension(recipeWidth), validateRecipeDimension(recipeHeight));
        this.recipeType = recipeType;
    }

    @Override
    public CustomPacketPayload.@NotNull Type<FillUnitedGridPacket> type() {
        return TYPE;
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
        stream.writeInt(recipeWidth);
        stream.writeInt(recipeHeight);
        stream.writeUtf(recipeType.name(), MAX_RECIPE_TYPE_NAME_LENGTH);
    }

    public static FillUnitedGridPacket decode(RegistryFriendlyByteBuf stream) {
        ResourceLocation recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceLocation();
        }
        var ingredientTemplates = NonNullList.withSize(validateIngredientTemplateCount(stream.readInt()), ItemStack.EMPTY);
        ingredientTemplates.replaceAll(ignored -> ItemStack.OPTIONAL_STREAM_CODEC.decode(stream));
        var craftMissing = stream.readBoolean();
        int recipeWidth = stream.readInt();
        int recipeHeight = stream.readInt();
        MyoRecipeType recipeType = MyoRecipeType.valueOf(stream.readUtf(MAX_RECIPE_TYPE_NAME_LENGTH));
        return new FillUnitedGridPacket(recipeId, ingredientTemplates, craftMissing, recipeWidth, recipeHeight, recipeType);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        var menu = player.containerMenu;
        if (!(menu instanceof UnitedTerminalMenu unitedMenu) || !this.recipeType.isActive()) {
            return;
        }

        unitedMenu.setSelectedRecipeType(this.recipeType);
        if (unitedMenu.getSelectedRecipeType() != this.recipeType) {
            return;
        }

        super.handleOnServer(player);
    }

    private static List<ItemStack> validateIngredientTemplates(List<ItemStack> ingredientTemplates) {
        validateIngredientTemplateCount(ingredientTemplates.size());
        return ingredientTemplates;
    }

    private static int validateIngredientTemplateCount(int count) {
        if (count != UNITED_GRID_SIZE) {
            throw new IllegalArgumentException("United recipe transfer requires exactly 81 ingredient templates");
        }
        return count;
    }

    private static int validateRecipeDimension(int dimension) {
        if (dimension != NOT_SET_RECIPE_SIZE && (dimension < 1 || dimension > 9)) {
            throw new IllegalArgumentException("United recipe transfer dimensions must be between 1 and 9");
        }
        return dimension;
    }
}
