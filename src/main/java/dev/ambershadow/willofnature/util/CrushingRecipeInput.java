package dev.ambershadow.willofnature.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CrushingRecipeInput implements RecipeInput {

    private final ItemStack input;
    private final List<ItemStack> byproducts;


    public CrushingRecipeInput(ItemStack input, List<ItemStack> currentBps) {
        this.input = input;
        this.byproducts = currentBps;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        var x = (slot == 0 ? input : byproducts.size() >= slot ? byproducts.get(slot - 1) : null);
        if (x != null) return x.copy();
        else throw new IllegalArgumentException("CrushingRecipeInput index out of bounds: " + slot);
    }

    public List<ItemStack> getByproducts() {
        return byproducts;
    }


    @Override
    public int size() {
        return 4;
    }
}
