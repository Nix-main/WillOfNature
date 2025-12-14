package dev.ambershadow.willofnature.index.block;

import dev.ambershadow.willofnature.index.block.entities.InputPipeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputPipeBlock extends PipeBlock {

    public static final MapCodec<InputPipeBlock> CODEC = simpleCodec(InputPipeBlock::new);

    public InputPipeBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<InputPipeBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InputPipeBlockEntity(pos, state);
    }
}

