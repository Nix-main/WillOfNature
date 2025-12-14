package dev.ambershadow.willofnature.client.prov.data

import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.decoration.PaintingVariant

@Suppress("HasPlatformType")
object PaintingCreator {
    fun bootstrap(c: BootstrapContext<PaintingVariant>) {
//        c.painting(NamePaintings.CUSTOM, 6,6)
    }

    fun BootstrapContext<PaintingVariant>.painting(
        registryKey: ResourceKey<PaintingVariant>, width: Int, height: Int,
    ) = register(registryKey, PaintingVariant(width, height, registryKey.registry()))

}