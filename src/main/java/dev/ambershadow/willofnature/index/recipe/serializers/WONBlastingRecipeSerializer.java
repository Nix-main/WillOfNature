package dev.ambershadow.willofnature.index.recipe.serializers;

import dev.ambershadow.willofnature.index.recipe.WONBlastingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ambershadow.willofnature.util.Byproduct;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WONBlastingRecipeSerializer implements RecipeSerializer<WONBlastingRecipe> {

    public static final Codec<Tuple<Fluid, Long>> FLUID_AMOUNT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(Tuple::getA),
                    Codec.LONG.fieldOf("amount").forGetter(Tuple::getB)
            ).apply(instance, Tuple::new));

    private final MapCodec<WONBlastingRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, WONBlastingRecipe> packetCodec;

    public WONBlastingRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(WONBlastingRecipe::getGroup),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(WONBlastingRecipe::getCategory),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(WONBlastingRecipe::getIngredient),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.getResultItem(null)),
                        Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(WONBlastingRecipe::getExperience),
                        Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(WONBlastingRecipe::getCookingTime),
                        Byproduct.CODEC.listOf()
                                .optionalFieldOf("byproducts", List.of())
                                .forGetter(WONBlastingRecipe::getByproducts),
                        FLUID_AMOUNT_CODEC.optionalFieldOf("fluid", new Tuple<>(Fluids.EMPTY, 0L))
                                .forGetter(r -> new Tuple<>(r.getFluid(), r.getFluidAmount()))
                ).apply(instance, WONBlastingRecipe::new)
        );
        this.packetCodec = StreamCodec.of(
                this::write,
                this::read
        );
    }

    protected void write(RegistryFriendlyByteBuf buf, WONBlastingRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeEnum(recipe.getCategory());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
        ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
        buf.writeFloat(recipe.getExperience());
        buf.writeVarInt(recipe.getCookingTime());

        List<Byproduct> byproducts = recipe.getByproducts();
        buf.writeVarInt(byproducts.size());
        for (Byproduct byproduct : byproducts) {
            ItemStack.STREAM_CODEC.encode(buf, byproduct.item());
            ByteBufCodecs.FLOAT.encode(buf, byproduct.chance());
        }
        buf.writeResourceLocation(BuiltInRegistries.FLUID.getKey(recipe.getFluid()));
        buf.writeLong(recipe.getFluidAmount());
    }

    protected WONBlastingRecipe read(RegistryFriendlyByteBuf buf) {
        String group = buf.readUtf();
        CookingBookCategory category = buf.readEnum(CookingBookCategory.class);
        Ingredient ing = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);


        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();

        int byproductCount = buf.readVarInt();
        var byproducts = new ArrayList<Byproduct>(byproductCount);
        for (int i = 0; i < byproductCount; i++) {
            byproducts.add(new Byproduct(ItemStack.STREAM_CODEC.decode(buf), buf.readFloat()));
        }
        ResourceLocation fluidId = buf.readResourceLocation();
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        long amount = buf.readLong();
        return new WONBlastingRecipe(group, category, ing, result, experience, cookingTime, byproducts, new Tuple<>(fluid, amount));
    }

    @Override
    public @NotNull MapCodec<WONBlastingRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, WONBlastingRecipe> streamCodec() {
        return packetCodec;
    }
}