package dev.ambershadow.willofnature.index.recipe;

import dev.ambershadow.willofnature.registration.WONRecipeSerializers;
import dev.ambershadow.willofnature.registration.WONRecipeTypes;
import dev.ambershadow.willofnature.mixin.RecipeManagerAccessor;
import dev.ambershadow.willofnature.util.Byproduct;
import dev.ambershadow.willofnature.util.CrushingRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CrushingRecipe implements Recipe<CrushingRecipeInput> {
    public static Optional<RecipeHolder<CrushingRecipe>> findAnyMatch(NonNullList<ItemStack> inventory, Level world){
        Optional<RecipeHolder<?>> entry = ((RecipeManagerAccessor)world.getRecipeManager()).getRecipeMap()
                .get(WONRecipeTypes.CRUSHING).stream().filter(Objects::nonNull)
                .filter(recipeEntry -> ((CrushingRecipe)recipeEntry.value()).ingredient.test(inventory.getFirst())).findFirst();
        //noinspection unchecked
        return entry.map(recipeEntry -> (RecipeHolder<CrushingRecipe>) recipeEntry);
    }


    private final String group;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int time;
    private final List<Byproduct> byproducts;
    private final int energy;

    public CrushingRecipe(String group, Ingredient ingredient, ItemStack result,
                          int cookingTime, List<Byproduct> byproducts, int energy) {
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.time = cookingTime;
        this.byproducts = byproducts;
        this.energy = energy;
    }


    @Override
    public boolean matches(CrushingRecipeInput input, Level world) {
        return ingredient.test(input.getItem(0)) &&
                WONSmeltingRecipe.canFitByproducts(byproducts, input.getByproducts());
    }

    @Override
    public @NotNull ItemStack assemble(CrushingRecipeInput input, HolderLookup.Provider lookup) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return result.copy() ;
    }

    public int getEnergy(){
        return energy;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return WONRecipeSerializers.CRUSHING;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return WONRecipeTypes.CRUSHING ;
    }

    @Override
    public @NotNull String getGroup(){
        return group;
    }

    public Ingredient getIngredient(){
        return ingredient;
    }

    public int getTime(){
        return time;
    }
    public List<Byproduct> getByproducts(){
        return byproducts;
    }
}
