package dev.ambershadow.willofnature.client.networking;

import dev.ambershadow.willofnature.WillOfNature;
import dev.ambershadow.willofnature.client.WillOfNatureClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public record UpdateFluidS2CPacket(BlockPos pos, int fluidId, long amount) implements CustomPacketPayload {

    public static final Type<UpdateFluidS2CPacket> ID = new Type<>(WillOfNature.id("update_fluid_client"));
    public static final StreamCodec<FriendlyByteBuf, UpdateFluidS2CPacket> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, UpdateFluidS2CPacket::pos, ByteBufCodecs.INT, UpdateFluidS2CPacket::fluidId, ByteBufCodecs.VAR_LONG, UpdateFluidS2CPacket::amount, UpdateFluidS2CPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<UpdateFluidS2CPacket> {
        @Override
        public void receive(@NotNull UpdateFluidS2CPacket payload, @NotNull ClientPlayNetworking.Context context) {
            WillOfNatureClient.fluidDataMap.put(
                    payload.pos,
                    new WillOfNatureClient.FluidData(payload.fluidId, payload.amount)
            );
        }
    }
}