package dev.ambershadow.willofnature.index.networking;

import dev.ambershadow.willofnature.WillOfNature;
import dev.ambershadow.willofnature.index.block.entities.WONBlastFurnaceBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public record UpdateFluidC2SPacket(BlockPos pos, int fluidId, long amount) implements CustomPacketPayload {

    public static final Type<UpdateFluidC2SPacket> ID = new Type<>(WillOfNature.id("update_fluid_server"));
    public static final StreamCodec<FriendlyByteBuf, UpdateFluidC2SPacket> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, UpdateFluidC2SPacket::pos, ByteBufCodecs.INT, UpdateFluidC2SPacket::fluidId, ByteBufCodecs.VAR_LONG, UpdateFluidC2SPacket::amount, UpdateFluidC2SPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<UpdateFluidC2SPacket> {
        @Override
        public void receive(@NotNull UpdateFluidC2SPacket payload, @NotNull ServerPlayNetworking.Context context) {
            if (context.player() != null){
                BlockEntity be = context.player().level().getBlockEntity(payload.pos);
                if (be instanceof WONBlastFurnaceBlockEntity entity){
                    entity.setFluid(BuiltInRegistries.FLUID.byId(payload.fluidId), payload.amount);
                }
            }
        }
    }
}