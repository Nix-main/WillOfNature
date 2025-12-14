package dev.ambershadow.willofnature.index.block.entities;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;

import java.util.List;

public abstract class PipeBlockEntity extends BlockEntity {

    final SingleFluidStorage storage = new SingleFluidStorage(){
        @Override
        protected long getCapacity(FluidVariant variant) {
            return 1000;
        }
    };

    public PipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected BlockPos previous;

    protected static void transfer(List<PipeBlockEntity> pipes, BlockPos pos, SingleFluidStorage previous, int amount){
        long remaining = amount;
        int neighborsLeft = pipes.size();
        for (PipeBlockEntity pipe : pipes) {
            long share = remaining / neighborsLeft;
            try (Transaction tx = Transaction.openOuter()) {
                if (previous.isResourceBlank()) continue;
                FluidVariant resource = previous.getResource();
                long extracted = previous.extract(resource, share, tx);
                long accepted = pipe.storage.insert(resource, extracted, tx);
                tx.commit();
                pipe.previous = pos;
                remaining -= accepted;
            }

            neighborsLeft--;
        }
    }
}
