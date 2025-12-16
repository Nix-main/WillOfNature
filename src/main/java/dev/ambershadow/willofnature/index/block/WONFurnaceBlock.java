package dev.ambershadow.willofnature.index.block;

import dev.ambershadow.willofnature.registration.WONBlockEntities;
import dev.ambershadow.willofnature.index.block.entities.WONFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WONFurnaceBlock extends FurnaceBlock {
    public WONFurnaceBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void openContainer(Level world, BlockPos pos, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WONFurnaceBlockEntity) {
            player.openMenu((MenuProvider)blockEntity);
        }
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WONFurnaceBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, WONBlockEntities.WON_FURNACE,
                world.isClientSide ? null : WONFurnaceBlockEntity::tick);
    }
}