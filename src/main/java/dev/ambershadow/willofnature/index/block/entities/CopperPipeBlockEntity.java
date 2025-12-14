package dev.ambershadow.willofnature.index.block.entities;

import dev.ambershadow.willofnature.index.WONBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CopperPipeBlockEntity extends PipeBlockEntity {

    public CopperPipeBlockEntity(BlockPos pos, BlockState state) {
        super(WONBlockEntities.COPPER_PIPE, pos, state);
    }

    private static final int MAX_FLUID_PER_TICK = 50;

    public static void tick(Level world, BlockPos pos, BlockState state, PipeBlockEntity blockEntity) {
        if (world == null || world.isClientSide) return;
        List<PipeBlockEntity> pipes = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            var next = pos.relative(dir);
            if (next.equals(blockEntity.previous)) continue;
            var entity = world.getBlockEntity(next);
            if (entity instanceof PipeBlockEntity e){
                pipes.add(e);
            }
        }

        transfer(pipes, pos, blockEntity.storage, MAX_FLUID_PER_TICK);
    }
}