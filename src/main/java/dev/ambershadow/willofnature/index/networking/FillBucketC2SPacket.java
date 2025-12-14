package dev.ambershadow.willofnature.index.networking;

import dev.ambershadow.willofnature.WillOfNature;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record FillBucketC2SPacket(ItemStack bucketItem, int amount) implements CustomPacketPayload {

    public static final Type<FillBucketC2SPacket> ID = new Type<>(WillOfNature.id("fill_bucket"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FillBucketC2SPacket> CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, FillBucketC2SPacket::bucketItem, ByteBufCodecs.INT, FillBucketC2SPacket::amount, FillBucketC2SPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<FillBucketC2SPacket> {
        @Override
        public void receive(@NotNull FillBucketC2SPacket payload, ServerPlayNetworking.Context context) {
            ServerPlayer player = context.player();
            player.containerMenu.setCarried(payload.bucketItem);
            player.getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET, payload.amount));
        }
    }
}
