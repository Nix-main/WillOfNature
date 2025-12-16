package dev.ambershadow.willofnature.index.recipe.serializers;

import dev.ambershadow.willofnature.index.recipe.WONSmeltingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ambershadow.willofnature.util.Byproduct;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CookingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WONSmeltingRecipeSerializer implements RecipeSerializer<WONSmeltingRecipe> {

    private final MapCodec<WONSmeltingRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, WONSmeltingRecipe> packetCodec;

    public WONSmeltingRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(WONSmeltingRecipe::getGroup),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(WONSmeltingRecipe::getCategory),
                        Ingredient.CODEC_NONEMPTY.listOf(1, 2)
                                .fieldOf("ingredients")
                                .forGetter(WONSmeltingRecipe::getAllIngredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.getResultItem(null)),
                        Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(WONSmeltingRecipe::getExperience),
                        Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(WONSmeltingRecipe::getCookingTime),
                        Byproduct.CODEC.listOf()
                                .optionalFieldOf("byproducts", List.of())
                                .forGetter(WONSmeltingRecipe::getByproducts)
                ).apply(instance, WONSmeltingRecipe::new)
        );
        this.packetCodec = StreamCodec.of(
                this::write,
                this::read
        );
    }

    private void write(RegistryFriendlyByteBuf buf, WONSmeltingRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeEnum(recipe.getCategory());

        List<Ingredient> ingredients = recipe.getAllIngredients();
        buf.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }

        ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
        buf.writeFloat(recipe.getExperience());
        buf.writeVarInt(recipe.getCookingTime());

        List<Byproduct> byproducts = recipe.getByproducts();
        buf.writeVarInt(byproducts.size());
        for (Byproduct byproduct : byproducts) {
            ItemStack.STREAM_CODEC.encode(buf, byproduct.item());
            ByteBufCodecs.FLOAT.encode(buf, byproduct.chance());
        }
    }

    private WONSmeltingRecipe read(RegistryFriendlyByteBuf buf) {
        String group = buf.readUtf();
        CookingBookCategory category = buf.readEnum(CookingBookCategory.class);

        int ingredientCount = buf.readVarInt();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientCount; i++) {
            ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        }

        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        float experience = buf.readFloat();
        int cookingTime = buf.readVarInt();

        int byproductCount = buf.readVarInt();
        List<Byproduct> byproducts = new ArrayList<>(byproductCount);
        for (int i = 0; i < byproductCount; i++) {
            byproducts.add(new Byproduct(ItemStack.STREAM_CODEC.decode(buf), buf.readFloat()));
        }

        return new WONSmeltingRecipe(group, category, ingredients, result, experience, cookingTime, byproducts);
    }

    @Override
    public @NotNull MapCodec<WONSmeltingRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, WONSmeltingRecipe> streamCodec() {
        return packetCodec;
    }
}