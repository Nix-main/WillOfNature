package dev.ambershadow.willofnature.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @Inject(method = "buildContents", at = @At("RETURN"))
    private void removeVanillaFurnace(CreativeModeTab.ItemDisplayParameters displayContext, CallbackInfo ci) {
        CreativeModeTab group = (CreativeModeTab) (Object) this;
        group.getDisplayItems().removeIf(stack -> stack.getItem() == Items.FURNACE);
        group.getSearchTabDisplayItems().removeIf(stack -> stack.getItem() == Items.FURNACE);
        group.getDisplayItems().removeIf(stack -> stack.getItem() == Items.BLAST_FURNACE);
        group.getSearchTabDisplayItems().removeIf(stack -> stack.getItem() == Items.BLAST_FURNACE);

    }
}