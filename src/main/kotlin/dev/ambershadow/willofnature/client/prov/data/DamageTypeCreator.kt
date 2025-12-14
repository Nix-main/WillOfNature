package dev.ambershadow.willofnature.client.prov.data

import dev.ambershadow.willofnature.data.WONDamageTypes
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.world.damagesource.DamageScaling
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes

object DamageTypeCreator {
    fun bootstrap(c: BootstrapContext<DamageType>) {
        // Steal from DamageTypes.class
        c.register(
            WONDamageTypes.GAY,
            DamageType(WONDamageTypes.GAY.location().path, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 1f)
        )
    }

}