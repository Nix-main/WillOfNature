package dev.ambershadow.willofnature.index.block.entities;

import dev.ambershadow.willofnature.index.WONBlockEntities;
import dev.ambershadow.willofnature.util.FluidUpdateSender;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class OutputPipeBlockEntity extends PipeBlockEntity{
    public OutputPipeBlockEntity(BlockPos pos, BlockState state) {
        super(WONBlockEntities.OUTPUT_PIPE, pos, state);
    }

    private static final int MAX_FLUID_PER_TICK = 50;

    public static void tick(Level world, BlockPos pos, BlockState state, PipeBlockEntity blockEntity) {
        if (world == null || world.isClientSide) return;
        for (Direction direction : Direction.values()) {
            var next = pos.relative(direction);
            var entity = world.getBlockEntity(next);
            if (!(entity instanceof PipeBlockEntity)){
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, next, direction.getOpposite());
                if (storage != null){
                    try(Transaction tx = Transaction.openOuter()){
                        FluidVariant resource = blockEntity.storage.variant;
                        if (resource.isBlank()) continue;
                        var inserted = storage.insert(resource, Math.min(MAX_FLUID_PER_TICK, blockEntity.storage.amount), tx);
                        blockEntity.storage.extract(resource, inserted, tx);
                        tx.commit();
                    }
                }
            }
            if (entity instanceof FluidUpdateSender sender)
                sender.sendFluidUpdatePacket();
        }
    }
}
