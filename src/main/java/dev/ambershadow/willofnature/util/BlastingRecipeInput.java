package dev.ambershadow.willofnature.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BlastingRecipeInput implements RecipeInput {
    private final ItemStack slot;
    private final ItemStack byproduct1;
    private final ItemStack byproduct2;
    private final ItemStack byproduct3;

    private final Fluid fluid;
    private final long amount;

    private final BlockEntity entity;
    public BlastingRecipeInput(ItemStack a, List<ItemStack> currentBpSlots, Fluid fluid, long amount, BlockEntity entity) {
        this.slot = a;
        byproduct1 = !currentBpSlots.isEmpty() ? currentBpSlots.getFirst() : ItemStack.EMPTY;
        byproduct2 = currentBpSlots.size() > 1 ? currentBpSlots.get(1) : ItemStack.EMPTY;
        byproduct3 = currentBpSlots.size() > 2 ? currentBpSlots.get(2) : ItemStack.EMPTY;
        this.fluid = fluid;
        this.amount = amount;
        this.entity = entity;
    }

    public BlockEntity getBlockEntity(){ return entity;}
    public Fluid getFluid(){ return fluid;}
    public long getFluidAmount(){ return amount;}

    @Override
    public int size() {
        return 4;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return switch (index){
            case 0 -> slot.copy();
            case 1 -> byproduct1.copy();
            case 2 -> byproduct2.copy();
            case 3 -> byproduct3.copy();
            default -> throw new IllegalArgumentException("BlastingRecipeInput index out of bounds: " + index);
        };
    }
}
