package dev.ambershadow.willofnature.mixin;

import dev.ambershadow.willofnature.registration.WONBlocks;
import dev.ambershadow.willofnature.index.block.entities.WONBlastFurnaceBlockEntity;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlock.class)
public class AbstractFurnaceBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    public void onUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        if (!world.isClientSide) {
            if (state.getBlock() != Blocks.FURNACE && state.getBlock() != Blocks.BLAST_FURNACE) return;
            BlockState newState = state.getBlock() == Blocks.FURNACE ? WONBlocks.WON_FURNACE.defaultBlockState() : WONBlocks.WON_BLAST_FURNACE.defaultBlockState();
            world.setBlockAndUpdate(pos, newState
                    .setValue(FurnaceBlock.FACING, state.getValue(FurnaceBlock.FACING))
                    .setValue(FurnaceBlock.LIT, state.getValue(FurnaceBlock.LIT)));
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof WONBlastFurnaceBlockEntity factory)
                player.openMenu(factory);
            else if (entity instanceof MenuProvider factory2)
                player.openMenu(factory2);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}

