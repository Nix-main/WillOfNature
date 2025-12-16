package dev.ambershadow.willofnature.index.block.entities;

import dev.ambershadow.willofnature.registration.WONBlockEntities;
import dev.ambershadow.willofnature.index.recipe.CrushingRecipe;
import dev.ambershadow.willofnature.index.screens.CrusherScreenHandler;
import dev.ambershadow.willofnature.util.Byproduct;
import dev.ambershadow.willofnature.util.CrushingRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;
import java.util.Random;

public class CrusherBlockEntity extends BaseContainerBlockEntity implements MenuProvider, Nameable, WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {

    protected final ContainerData containerData;
    protected NonNullList<ItemStack> inventory;

    private RecipeHolder<CrushingRecipe> lastRecipe;

    int cookTime;
    int cookTimeTotal;

    int capacity;

    private int energyUsed;
    private int energyRemainder;

    private final SimpleEnergyStorage energy = new SimpleEnergyStorage(10000, 1000000, 1000000){
        @Override
        protected void onFinalCommit(){
            setChanged();
        }
    };

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(WONBlockEntities.CRUSHER, pos, state);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        capacity = (int)this.energy.getCapacity();
        this.containerData = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0 -> {
                        return CrusherBlockEntity.this.cookTime;
                    }
                    case 1 -> {
                        return CrusherBlockEntity.this.cookTimeTotal;
                    }
                    case 2 -> {
                        return (int) CrusherBlockEntity.this.energy.amount;
                    }
                    case 3 -> {
                        return (int)CrusherBlockEntity.this.energy.getCapacity();
                    }
                    default -> {
                        return 0;
                    }
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CrusherBlockEntity.this.cookTime = value;
                    case 1 -> CrusherBlockEntity.this.cookTimeTotal = value;
                    case 2 -> CrusherBlockEntity.this.energy.amount = value;
                    case 3 -> capacity = value;
                }
            }

            public int getCount() {
                return 4;
            }
        };
    }

    private List<ItemStack> getByproducts(){
        return List.of(inventory.get(2), inventory.get(3), inventory.get(4));
    }

    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            if (recipe.value() instanceof CrushingRecipe)
                //noinspection unchecked
                this.lastRecipe = (RecipeHolder<CrushingRecipe>) recipe;
        }
    }

    public void setEnergy(int val){
        energy.amount = val;
    }

    public int getEnergy(){
        return (int)energy.amount;
    }

    @Override
    public @Nullable RecipeHolder<CrushingRecipe> getRecipeUsed() {
        return lastRecipe;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        ContainerHelper.saveAllItems(nbt, this.inventory, registryLookup);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.crusher");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new CrusherScreenHandler(syncId, playerInventory, this, containerData);
    }

    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory, registryLookup);
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public void fillStackedContents(StackedContents finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.accountStack(itemStack);
        }
    }

    private static int getTime(Level world, CrusherBlockEntity crusher) {
        var opt = CrushingRecipe.findAnyMatch(crusher.inventory, world);
        return opt.map(crushingRecipeRecipeEntry -> crushingRecipeRecipeEntry.value().getTime()).orElse(200);
    }

    private static boolean canAcceptRecipeOutput(RegistryAccess registryManager, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> slots, int count) {
        if (slots.getFirst().isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.value().getResultItem(registryManager);
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(1);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(itemStack2, itemStack)) {
            return false;
        }
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxStackSize()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxStackSize();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CrusherBlockEntity blockEntity) {
        if (world.isClientSide) return;
        var match = CrushingRecipe.findAnyMatch(blockEntity.inventory, world);
        if (match.isPresent()
                && match.get() instanceof RecipeHolder<CrushingRecipe> recipe
                && recipe.value().matches(
                new CrushingRecipeInput(blockEntity.inventory.getFirst(), blockEntity.getByproducts()), world)
                && canAcceptRecipeOutput(world.registryAccess(),
                recipe, blockEntity.inventory, recipe.value().getResultItem(world.registryAccess())
                        .getCount())) {


            int totalEnergy = recipe.value().getEnergy();
            int totalTime = getTime(world, blockEntity);

            int energyPerTick = totalEnergy / totalTime;
            int remainder = totalEnergy % totalTime;

            var lastRecipe = blockEntity.getRecipeUsed();

            if (lastRecipe != null && !lastRecipe.value().equals(recipe.value()) && (blockEntity.cookTime > 0 || blockEntity.energy.amount <= 0)) {
                blockEntity.cookTime = Mth.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
                if (blockEntity.cookTime == 0) {
                    blockEntity.cookTimeTotal = 0;
                }
                blockEntity.energyUsed = 0;
                blockEntity.energyRemainder = 0;
                return;
            }

            if (blockEntity.energy.amount >= energyPerTick) {
                blockEntity.energy.amount -= energyPerTick;
                blockEntity.energyUsed += energyPerTick;
            } else {
                blockEntity.cookTime = Mth.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
                if (blockEntity.cookTime == 0) {
                    blockEntity.cookTimeTotal = 0;
                    blockEntity.energyUsed = 0;
                    blockEntity.energyRemainder = 0;
                }
                return;
            }

            blockEntity.energyRemainder += totalEnergy % totalTime;
            if (blockEntity.energyRemainder >= totalTime) {
                blockEntity.energyRemainder -= totalTime;
                blockEntity.energy.amount -= 1;
                blockEntity.energyUsed += 1;
            }

            blockEntity.cookTime = Mth.clamp(blockEntity.cookTime + 1, 0, totalTime);
            blockEntity.cookTimeTotal = totalTime;
            blockEntity.setRecipeUsed(recipe);

            if (blockEntity.cookTime >= blockEntity.cookTimeTotal && blockEntity.energyUsed >= totalEnergy) {
                blockEntity.cookTime = 0;
                blockEntity.energyUsed = 0;
                blockEntity.energyRemainder = 0;

                blockEntity.inventory.getFirst().shrink(1);

                var currentOutput = blockEntity.inventory.get(1);
                if (currentOutput.isEmpty()) {
                    blockEntity.inventory.set(1, recipe.value().getResultItem(world.registryAccess()));
                } else {
                    blockEntity.inventory.get(1).grow(recipe.value().getResultItem(world.registryAccess()).getCount());
                }

                int slot = 2;
                for (Byproduct bp : recipe.value().getByproducts()) {
                    if (new Random().nextDouble() <= bp.chance()) {
                        var stack = blockEntity.inventory.get(slot);
                        if (stack.isEmpty()) {
                            blockEntity.inventory.set(slot, bp.item().copy());
                        } else {
                            stack.grow(bp.item().getCount());
                        }
                        slot++;
                    }
                }
            }
        } else {
            blockEntity.cookTime = Mth.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
            if (blockEntity.cookTime == 0) {
                blockEntity.cookTimeTotal = 0;
            }
            blockEntity.energyUsed = 0;
            blockEntity.energyRemainder = 0;
        }
    }
}