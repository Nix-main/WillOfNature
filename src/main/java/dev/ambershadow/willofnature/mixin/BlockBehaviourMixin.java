package dev.ambershadow.willofnature.mixin;

import dev.ambershadow.willofnature.registration.WONRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "onRemove", at = @At("TAIL"))
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, CallbackInfo ci) {
        validate(blockPos, level);
    }

    @Inject(method = "onPlace", at = @At("TAIL"))
    protected void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, CallbackInfo ci) {
        validate(blockPos, level);
    }

    @Unique
    private static void validate(BlockPos pos, Level level){
        WONRegistrar.getMultiblocks().forEach(multiblock -> {
            if (multiblock.validate(pos, level)) {
                // do something idk
            }
        });
    }
}
