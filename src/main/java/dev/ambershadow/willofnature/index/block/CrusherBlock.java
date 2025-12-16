package dev.ambershadow.willofnature.index.block;

import com.mojang.serialization.MapCodec;
import dev.ambershadow.willofnature.registration.WONBlockEntities;
import dev.ambershadow.willofnature.index.block.entities.CrusherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrusherBlock extends BaseEntityBlock {
    public CrusherBlock(Properties settings) {
        super(settings);
    }

    public static final MapCodec<CrusherBlock> CODEC = simpleCodec(CrusherBlock::new);

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof CrusherBlockEntity crusher) {
                crusher.setEnergy(1000);
                crusher.setChanged();
            }
            player.openMenu(state.getMenuProvider(world, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, WONBlockEntities.CRUSHER, CrusherBlockEntity::tick);
    }
}
