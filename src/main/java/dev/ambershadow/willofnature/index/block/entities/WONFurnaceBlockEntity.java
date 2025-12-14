package dev.ambershadow.willofnature.index.block.entities;

import dev.ambershadow.willofnature.index.WONBlockEntities;
import dev.ambershadow.willofnature.index.screens.WONFurnaceScreenHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WONFurnaceBlockEntity extends AbstractFurnaceBlockEntity implements StackedContentsCompatible {
    protected WONFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType, pos, state, recipeType);
        this.items = NonNullList.withSize(7, ItemStack.EMPTY);
    }

    public WONFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(WONBlockEntities.WON_FURNACE, pos, state, RecipeType.SMELTING);
        this.items = NonNullList.withSize(7, ItemStack.EMPTY);
    }


    public static void tick(Level world, BlockPos pos, BlockState state, WONFurnaceBlockEntity blockEntity) {
        AbstractFurnaceBlockEntity.serverTick(world, pos, state, blockEntity);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.furnace");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new WONFurnaceScreenHandler(syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{2, 3, 4, 5};
        } else if (side == Direction.UP) {
            return new int[]{0, 6};
        } else {
            return new int[]{1};
        }
    }

    @Override
    public void fillStackedContents(StackedContents finder) {
        for (ItemStack stack : this.items) {
            finder.accountStack(stack);
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (slot >= 3 && slot <= 5) {
            return true;
        }
        return super.canTakeItemThroughFace(slot, stack, dir);
    }
}
