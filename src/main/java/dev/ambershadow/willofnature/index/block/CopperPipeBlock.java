package dev.ambershadow.willofnature.index.block;

import dev.ambershadow.willofnature.index.block.entities.CopperPipeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopperPipeBlock extends PipeBlock {

    public static final MapCodec<CopperPipeBlock> CODEC = simpleCodec(CopperPipeBlock::new);

    public CopperPipeBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<CopperPipeBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperPipeBlockEntity(pos, state);
    }
}
