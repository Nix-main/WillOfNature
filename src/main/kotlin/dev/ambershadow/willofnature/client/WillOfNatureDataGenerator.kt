package dev.ambershadow.willofnature.client

import dev.ambershadow.willofnature.WillOfNature.MOD_ID
import dev.ambershadow.willofnature.client.prov.assets.EnLangProvider
import dev.ambershadow.willofnature.client.prov.assets.ModelProvider
import dev.ambershadow.willofnature.client.prov.data.DamageTypeCreator
import dev.ambershadow.willofnature.client.prov.data.RecipesProvider
import dev.ambershadow.willofnature.client.prov.tags.BlockTagsProvider
import dev.ambershadow.willofnature.client.prov.tags.ItemTagsProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries

object WillOfNatureDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()

        // Asset
        pack.addProvider(::EnLangProvider)
        pack.addProvider(::ModelProvider)
        // Data
        pack.addProvider(::DynamicRegistryProvider)
        pack.addProvider(::RecipesProvider)
        // Tags
        val blockTags = pack.addProvider(::BlockTagsProvider)
        pack.addProvider { o, r -> ItemTagsProvider(o, r, blockTags) }
    }

    override fun buildRegistry(gen: RegistrySetBuilder) {
        gen.add(Registries.DAMAGE_TYPE, DamageTypeCreator::bootstrap)
//        gen.add(Registries.PAINTING_VARIANT, PaintingCreator::bootstrap)
    }

    class DynamicRegistryProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
        FabricDynamicRegistryProvider(o, r) {

        override fun getName(): String = "$MOD_ID/dyn_data"

        override fun configure(reg: HolderLookup.Provider, e: Entries) {
            e.addAll(reg.lookupOrThrow(Registries.DAMAGE_TYPE))
//            e.addAll(reg.lookupOrThrow(Registries.ENCHANTMENT))
        }
    }
}