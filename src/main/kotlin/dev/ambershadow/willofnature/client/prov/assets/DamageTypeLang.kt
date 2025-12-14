package dev.ambershadow.willofnature.client.prov.assets

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object DamageTypeLang {
    fun translations(gen: FabricLanguageProvider.TranslationBuilder) {

        // i dont think we will need more then this v
//        gen.defaultDamage(DuskDamageTypes.ACID, "was dissolved")

//        gen.damage(
//            DuskDamageTypes.ELECTRICITY,
//            DuskDamageTypes.INDIRECT_ELECTRICITY,
//            "made a shocking discovery",
//            "was shocked to find"
//        )
    }

    private fun FabricLanguageProvider.TranslationBuilder.defaultDamage(
        key: ResourceKey<DamageType>,
        message: String,
        transition: String = "by",
        tryingToEscape: String = "whilst trying to escape",
        using: String = "using"
    ) {
        val id = key.location().path
        this.add("death.attack.$id", "%s $message")
        this.add("death.attack.$id.item", "%s $message $transition %s $using %s")
        this.add("death.attack.$id.player", "%s $message $tryingToEscape %s")
    }

    private fun FabricLanguageProvider.TranslationBuilder.damage(
        key: ResourceKey<DamageType>,
        message: String,
        messageItem: String,
        messageAttacker: String = messageItem
    ) {
        val id = key.location().path
        this.add("death.attack.$id", "%s $message")
        this.add("death.attack.$id.item", "%s $messageItem %s using %s")
        this.add("death.attack.$id.player", "%s $messageAttacker %s")
    }


    fun FabricLanguageProvider.TranslationBuilder.damage(
        direct: ResourceKey<DamageType>, indirect: ResourceKey<DamageType>,
        message: String, messageItem: String, messageAttacker: String = messageItem
    ) {
        this.directDamage(direct, message, messageAttacker)
        this.indirectDamage(indirect, message, messageItem)
    }


    fun FabricLanguageProvider.TranslationBuilder.directDamage(
        key: ResourceKey<DamageType>, message: String, messageAttacker: String
    ) {
        val id = key.location().path
        this.add("death.attack.$id", "%s $message")
        this.add("death.attack.$id.player", "%s $messageAttacker %s")
    }

    fun FabricLanguageProvider.TranslationBuilder.indirectDamage(
        key: ResourceKey<DamageType>, message: String, messageItem: String
    ) {
        val id = key.location().path
        this.add("death.attack.$id", "%s $message")
        this.add("death.attack.$id.item", "%s $messageItem %s using %s")
    }
}