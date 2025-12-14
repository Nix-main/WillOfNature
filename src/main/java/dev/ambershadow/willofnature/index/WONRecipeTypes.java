package dev.ambershadow.willofnature.index;

import dev.ambershadow.willofnature.index.recipe.CrushingRecipe;
import dev.ambershadow.willofnature.index.recipe.WONBlastingRecipe;
import dev.ambershadow.willofnature.index.recipe.WONSmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class WONRecipeTypes {
    static void init() {}

    public static final RecipeType<WONSmeltingRecipe> WON_SMELTING = WONRegistrar.register("won_smelting");
    public static final RecipeType<WONBlastingRecipe> WON_BLASTING = WONRegistrar.register("won_blasting");
    public static final RecipeType<CrushingRecipe> CRUSHING = WONRegistrar.register("crushing");

}