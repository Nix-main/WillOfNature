package dev.ambershadow.willofnature.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record Byproduct(ItemStack item, float chance) {
    public static final Codec<Byproduct> CODEC
            = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ItemStack.CODEC.fieldOf("item").forGetter(Byproduct::item),
                            Codec.FLOAT.fieldOf("chance").forGetter(Byproduct::chance)
                    ).apply(instance, Byproduct::new))
    );

    public Byproduct copy(){
        return new Byproduct(item.copy(), chance);
    }
}
