package dev.ambershadow.willofnature.client.prov.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.CompletableFuture

class BlockTagsProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(o, r) {
    override fun addTags(arg: HolderLookup.Provider) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(Blocks.HEAVY_CORE)


        conventionTags()
    }

    fun conventionTags() {
        getOrCreateTagBuilder(ConventionalBlockTags.SANDSTONE_BLOCKS)
            .add(Blocks.SANDSTONE)

    }
}