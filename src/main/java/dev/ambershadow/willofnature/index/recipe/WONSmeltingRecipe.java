package dev.ambershadow.willofnature.index.recipe;

import dev.ambershadow.willofnature.registration.WONRecipeSerializers;
import dev.ambershadow.willofnature.registration.WONRecipeTypes;
import dev.ambershadow.willofnature.util.Byproduct;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WONSmeltingRecipe implements Recipe<RecipeInput> {
    private final String group;
    private final CookingBookCategory category;
    private final List<Ingredient> ingredients;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;
    private final List<Byproduct> byproducts;

    public WONSmeltingRecipe(String group, CookingBookCategory category,
                             List<Ingredient> ingredients, ItemStack result,
                             float experience, int cookingTime, List<Byproduct> byproducts) {
        this.group = group;
        this.category = category;
        this.ingredients = List.copyOf(ingredients);
        this.result = result.copy();
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.byproducts = List.copyOf(byproducts);
    }
    public List<Byproduct> getByproducts() {
        List<Byproduct> copies = new ArrayList<>();
        for (Byproduct byproduct : byproducts) {
            copies.add(byproduct.copy());
        }
        return copies;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredients;
    }

    public boolean hasSecondIngredient() {
        return ingredients.size() > 1 && !ingredients.get(1).isEmpty();
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
        return WONRecipeSerializers.WON_SMELTING;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return WONRecipeTypes.WON_SMELTING;
    }

    @Override
    public boolean matches(RecipeInput inv, Level world) {
        ItemStack a = inv.getItem(0);
        ItemStack b = inv.size() > 1 ? inv.getItem(1) : ItemStack.EMPTY;
        if (ingredients.size() == 1) {
            return (ingredients.getFirst().test(a) || ingredients.getFirst().test(b)) && canFitByproducts(byproducts, List.of(inv.getItem(2), inv.getItem(3), inv.getItem(4)));
        }
        if (ingredients.size() == 2) {
            if (a.isEmpty() || b.isEmpty()) return false;
            boolean orderA = ingredients.get(0).test(a) && ingredients.get(1).test(b);
            boolean orderB = ingredients.get(0).test(b) && ingredients.get(1).test(a);
            return (orderA || orderB) && canFitByproducts(byproducts, List.of(inv.getItem(2), inv.getItem(3), inv.getItem(4)));
        }
        return false;
    }

    public static boolean canFitByproducts(List<Byproduct> byproducts, List<ItemStack> slots) {
        if (slots.size() != 3) return false;

        int placed = 0;

        for (Byproduct byproduct : byproducts) {
            boolean fit = false;

            for (ItemStack slot : slots) {
                if (slot.isEmpty()) {
                    fit = true;
                    break;
                }
                if (ItemStack.isSameItemSameComponents(slot, byproduct.item())) {
                    int max = slot.getMaxStackSize();
                    int current = slot.getCount();
                    int reserved = byproduct.item().getCount();

                    if (current <= max - reserved) {
                        fit = true;
                        break;
                    }
                }
            }

            if (fit) {
                placed++;
            } else {
                return false;
            }
        }

        return placed == byproducts.size();
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
        return new ItemStack(Blocks.FURNACE);
    }
}