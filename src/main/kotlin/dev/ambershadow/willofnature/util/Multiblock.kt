package dev.ambershadow.willofnature.util

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

abstract class Multiblock(loc: ResourceLocation) {
    val location: ResourceLocation = loc

    abstract fun validate(pos: BlockPos, level: Level) : Boolean


    fun interface MultiblockSupplier {
        fun apply(id: ResourceLocation): Multiblock
    }
}