package dev.ambershadow.willofnature.index;

import dev.ambershadow.willofnature.index.recipe.CrushingRecipe;
import dev.ambershadow.willofnature.index.recipe.WONBlastingRecipe;
import dev.ambershadow.willofnature.index.recipe.serializers.CrushingRecipeSerializer;
import dev.ambershadow.willofnature.index.recipe.serializers.WONBlastingRecipeSerializer;
import dev.ambershadow.willofnature.index.recipe.WONSmeltingRecipe;
import dev.ambershadow.willofnature.index.recipe.serializers.WONSmeltingRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class WONRecipeSerializers {
    static void init(){}
    public static final RecipeSerializer<WONSmeltingRecipe> WON_SMELTING = WONRegistrar.register("won_smelting", new WONSmeltingRecipeSerializer());
    public static final RecipeSerializer<WONBlastingRecipe> WON_BLASTING = WONRegistrar.register("won_blasting", new WONBlastingRecipeSerializer());
    public static final RecipeSerializer<CrushingRecipe> CRUSHING = WONRegistrar.register("crushing", new CrushingRecipeSerializer());

}