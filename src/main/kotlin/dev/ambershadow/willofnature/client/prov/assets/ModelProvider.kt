package dev.ambershadow.willofnature.client.prov.assets


import dev.ambershadow.willofnature.index.WONRegistrar.getValuesOfType
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates

class ModelProvider(o: FabricDataOutput) : FabricModelProvider(o) {
    override fun generateBlockStateModels(gen: BlockModelGenerators) {
//        gen.createFurnace(WONBlocks.WON_BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
//        gen.createFurnace(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
    }

    override fun generateItemModels(gen: ItemModelGenerators) {
        for (item in getValuesOfType(BuiltInRegistries.ITEM)) {
            gen.generateFlatItem(item, ModelTemplates.FLAT_ITEM)
        }
    }
}
