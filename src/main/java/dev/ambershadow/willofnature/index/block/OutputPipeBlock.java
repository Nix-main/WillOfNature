package dev.ambershadow.willofnature.index.block;

import dev.ambershadow.willofnature.index.block.entities.OutputPipeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OutputPipeBlock extends PipeBlock {

    public static final MapCodec<OutputPipeBlock> CODEC = simpleCodec(OutputPipeBlock::new);

    public OutputPipeBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<OutputPipeBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OutputPipeBlockEntity(pos, state);
    }
}

