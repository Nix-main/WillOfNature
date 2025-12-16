package dev.ambershadow.willofnature.client.prov.tags

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import java.util.concurrent.CompletableFuture

class ItemTagsProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>,bt: BlockTagsProvider) :
    FabricTagProvider.ItemTagProvider(o, r, bt) {
    override fun addTags(arg: HolderLookup.Provider) {
//        getOrCreateTagBuilder(NameItemTags.TEST)
//            .add(Items.HEAVY_CORE, Items.TRIDENT)


        conventionTags()
        // copy(BlockTags.STAIRS, ItemTags.STAIRS)
    }

    fun conventionTags() {
//        getOrCreateTagBuilder(ConventionalItemTags.EGGS)
//            .add(Items.HEAVY_CORE)
    }
}