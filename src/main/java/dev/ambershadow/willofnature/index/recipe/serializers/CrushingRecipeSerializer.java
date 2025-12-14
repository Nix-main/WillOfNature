package dev.ambershadow.willofnature.index.recipe.serializers;

import dev.ambershadow.willofnature.index.recipe.CrushingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrushingRecipeSerializer implements RecipeSerializer<CrushingRecipe> {
    private final MapCodec<CrushingRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> packetCodec;

    public CrushingRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(CrushingRecipe::getGroup),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CrushingRecipe::getIngredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.getResultItem(null)),
                Codec.INT.fieldOf("time").orElse(200).forGetter(CrushingRecipe::getTime),
                ItemStack.STRICT_CODEC.listOf(0, 3)
                    .optionalFieldOf("byproducts", List.of())
                    .forGetter(CrushingRecipe::getByproducts),
                Codec.INT.optionalFieldOf("energy", 500).forGetter(CrushingRecipe::getEnergy)
            ).apply(instance, CrushingRecipe::new)
        );

        this.packetCodec = StreamCodec.of(
                this::write,
                this::read
        );
    }

    protected void write(RegistryFriendlyByteBuf buf, CrushingRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeVarInt(recipe.getTime());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
        ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
        List<ItemStack> byproducts = recipe.getByproducts();
        buf.writeVarInt(byproducts.size());
        for (ItemStack byproduct : byproducts) {
            ItemStack.STREAM_CODEC.encode(buf, byproduct);
        }
        buf.writeVarInt(recipe.getEnergy());
    }

    protected CrushingRecipe read(RegistryFriendlyByteBuf buf) {
        var group = buf.readUtf();
        var time = buf.readVarInt();
        var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        var result = ItemStack.STREAM_CODEC.decode(buf);
        var bpSize = buf.readVarInt();
        var byproducts = new ArrayList<ItemStack>(bpSize);
        for (int i = 0; i < bpSize; i++) {
            byproducts.add(ItemStack.STREAM_CODEC.decode(buf));
        }
        var energy = buf.readVarInt();
        return new CrushingRecipe(group, ingredient, result, time, byproducts, energy);
    }

    @Override
    public @NotNull MapCodec<CrushingRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> streamCodec() {
        return packetCodec;
    }
}