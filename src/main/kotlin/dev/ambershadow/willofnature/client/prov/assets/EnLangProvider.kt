package dev.ambershadow.willofnature.client.prov.assets

import dev.ambershadow.willofnature.index.WONRegistrar.getValuesOfType
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

class EnLangProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(o, r) {


    override fun generateTranslations(lookup: HolderLookup.Provider, gen: TranslationBuilder) {
        getValuesOfType(ITEM).forEach { gen.add(it.descriptionId, genLang(it.id)) }
        getValuesOfType(BLOCK).forEach {
            try {
                gen.add(it.descriptionId, genLang(it.id))
            } catch (_: Exception) {
            }
        }
        getValuesOfType(ENTITY_TYPE).forEach { gen.add(it.descriptionId, genLang(it.id)) }

        gen.add("container.crusher", "Crusher")
        gen.add("item_group.willofnature.items", "Will Of Nature Items")
        gen.add("item_group.willofnature.blocks", "WIll Of Nature Blocks")

        DamageTypeLang.translations(gen)

//        NameTabs.MOD_TAB.let { gen.add(it.key(), "modid") }
//        NameItemTags.ITEM_TAGS.forEach { gen.add(it.translationKey, genLang(it.location)) }
    }

    private fun genLang(id: ResourceLocation): String =
        id.path.split("_").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

    val Item.id get() = ITEM.getKey(this)
    val Block.id get() = BLOCK.getKey(this)
    val EntityType<*>.id get() = ENTITY_TYPE.getKey(this)
}