package dev.ambershadow.willofnature.index.recipe;

import dev.ambershadow.willofnature.index.WONRecipeSerializers;
import dev.ambershadow.willofnature.index.WONRecipeTypes;
import dev.ambershadow.willofnature.index.block.entities.WONBlastFurnaceBlockEntity;
import dev.ambershadow.willofnature.mixin.RecipeManagerAccessor;
import dev.ambershadow.willofnature.util.BlastingRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WONBlastingRecipe implements Recipe<RecipeInput> {

    public static Optional<RecipeHolder<WONBlastingRecipe>> findAnyMatch(NonNullList<ItemStack> inventory, Level world){
        Optional<RecipeHolder<?>> entry = ((RecipeManagerAccessor)world.getRecipeManager()).getRecipeMap()
                .get(WONRecipeTypes.WON_BLASTING).stream().filter(Objects::nonNull)
                .filter(recipeEntry -> ((WONBlastingRecipe)recipeEntry.value()).ingredient.test(inventory.getFirst())).findFirst();
        //noinspection unchecked
        return entry.map(recipeEntry -> (RecipeHolder<WONBlastingRecipe>) recipeEntry);
    }

    private final String group;
    private final CookingBookCategory category;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;
    private final Fluid fluid;
    private final long fluidAmount;
    private final List<ItemStack> byproducts;

    public WONBlastingRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result,
                             float experience, int cookingTime, List<ItemStack> byproducts, Tuple<Fluid, Long> fluidAmountPair) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.result = result.copy();
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.byproducts = List.copyOf(byproducts);
        this.fluid = fluidAmountPair.getA();
        this.fluidAmount = fluidAmountPair.getB();
    }
    public List<ItemStack> getByproducts(HolderLookup.Provider registries) {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack byproduct : byproducts) {
            copies.add(byproduct.copy());
        }
        return copies;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
    public Fluid getFluid(){
        return fluid;
    }
    public long getFluidAmount(){
        return fluidAmount;
    }

    public float getExperience(){
        return experience;
    }

    public int getCookingTime(){
        return cookingTime;
    }

    public CookingBookCategory getCategory(){
        return category;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return WONRecipeSerializers.WON_BLASTING;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return WONRecipeTypes.WON_BLASTING;
    }

    @Override
    public boolean matches(RecipeInput i, Level world) {
        if (i instanceof BlastingRecipeInput input) {
            ItemStack a = input.getItem(0);
            return (ingredient.test(a))
                    && WONSmeltingRecipe.canFitByproducts(byproducts, List.of(input.getItem(1), input.getItem(2), input.getItem(3)))
                    && canFitFluid(((WONBlastFurnaceBlockEntity)input.getBlockEntity()).getFluidType(), ((WONBlastFurnaceBlockEntity)input.getBlockEntity()).getFluidAmount(),
                    input.getFluid(), input.getFluidAmount());
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
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

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(Blocks.BLAST_FURNACE);
    }

    private static boolean canFitFluid(Fluid fluid, long fluidAmount, Fluid fluid2, long fluidAmount2){
        return (fluid.isSame(fluid2) || fluid.isSame(Fluids.EMPTY) || fluid2.isSame(Fluids.EMPTY)) && (fluidAmount + fluidAmount2 <= 1000);
    }
}