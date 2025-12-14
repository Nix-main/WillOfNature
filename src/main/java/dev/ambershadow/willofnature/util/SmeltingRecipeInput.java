package dev.ambershadow.willofnature.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SmeltingRecipeInput implements RecipeInput {
    private final ItemStack a; // maps to furnace slot 0
    private final ItemStack b; // maps to furnace slot 6
    private final ItemStack c; // biproduct 1
    private final ItemStack d;
    private final ItemStack e;

    public SmeltingRecipeInput(ItemStack a, ItemStack b, List<ItemStack> currentBpSlots) {
        this.a = a;
        this.b = b;
        c = !currentBpSlots.isEmpty() ? currentBpSlots.getFirst() : ItemStack.EMPTY;
        d = currentBpSlots.size() > 1 ? currentBpSlots.get(1) : ItemStack.EMPTY;
        e = currentBpSlots.size() > 2 ? currentBpSlots.get(2) : ItemStack.EMPTY;

    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index){
            case 0 -> a.copy();
            case 1 -> b.copy();
            case 2 -> c.copy();
            case 3 -> d.copy();
            case 4 -> e.copy();
            default -> throw new IllegalArgumentException("SmeltingRecipeInput index out of bounds: " + index);
        };
    }
}
