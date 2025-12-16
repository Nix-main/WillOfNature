package dev.ambershadow.willofnature.index.block.entities;

import dev.ambershadow.willofnature.client.networking.UpdateFluidS2CPacket;
import dev.ambershadow.willofnature.registration.WONBlockEntities;
import dev.ambershadow.willofnature.index.recipe.WONBlastingRecipe;
import dev.ambershadow.willofnature.index.screens.WONBlastFurnaceScreenHandler;
import dev.ambershadow.willofnature.util.FluidUpdateSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WONBlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity implements StackedContentsCompatible,
        ExtendedScreenHandlerFactory<WONBlastFurnaceScreenHandler.PosData>, SidedStorageBlockEntity,
        FluidUpdateSender {
    private final SingleFluidStorage fluidStorage  =
            new SingleFluidStorage() {
                @Override
                protected long getCapacity(FluidVariant variant) {
                    return 1000;
                }
            };

    private RecipeHolder<WONBlastingRecipe> recipe;


    @Override
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction side){
        return fluidStorage;
    }

    public WONBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(WONBlockEntities.WON_BLAST_FURNACE, pos, state, RecipeType.BLASTING);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);
    }


    @Override
    public void sendFluidUpdatePacket() {
        if (level != null && !level.isClientSide) {
            var id = fluidStorage.amount > 0 ? BuiltInRegistries.FLUID.getId(fluidStorage.variant.getFluid()) : 0;
            for (var player : PlayerLookup.tracking(this)) {
                ServerPlayNetworking.send(player,
                            new UpdateFluidS2CPacket(worldPosition, id, fluidStorage.amount));
            }
        }
        setChanged();
    }

    public SingleFluidStorage getFluidStorage(){
        return fluidStorage;
    }


    public static void tick(Level world, BlockPos pos, BlockState state, WONBlastFurnaceBlockEntity blockEntity) {
        if (world.isClientSide) return;
        int cookTimeBefore = blockEntity.dataAccess.get(2);
        int amountBefore = blockEntity.items.getFirst().getCount();
        AbstractFurnaceBlockEntity.serverTick(world, pos, state, blockEntity);
        int cookTimeAfter = blockEntity.dataAccess.get(2);
        int amountAfter = blockEntity.items.getFirst().getCount();
        if (cookTimeBefore > 0 && cookTimeAfter == 0 && amountBefore > 0 && amountAfter == (amountBefore - 1)) {
            blockEntity.onRecipeComplete();
        } else {
            Optional<RecipeHolder<WONBlastingRecipe>> recipe = WONBlastingRecipe.findAnyMatch(blockEntity.items, world);
            blockEntity.recipe = recipe.orElse(null);
        }
    }

    private void onRecipeComplete() {
        if (recipe != null) {
            WONBlastingRecipe wonRecipe = recipe.value();
            if (!wonRecipe.getFluid().isSame(Fluids.EMPTY)) {
                setFluid(wonRecipe.getFluid(), wonRecipe.getFluidAmount() + fluidStorage.amount);
                sendFluidUpdatePacket();
            }
        }
    }

    public Fluid getFluidType() {
        return fluidStorage.variant.getFluid();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.blast_furnace");
    }


    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new WONBlastFurnaceScreenHandler(syncId, playerInventory, this, this.dataAccess);
    }


    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        CompoundTag fluid = new CompoundTag();
        this.fluidStorage.writeNbt(fluid, registryLookup);
        nbt.put("Fluid", fluid);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        fluidStorage.readNbt(nbt.getCompound("Fluid"), registryLookup);
        sendFluidUpdatePacket();
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{2, 3, 4, 5};
        } else if (side == Direction.UP) {
            return new int[]{0};
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

    public void setFluid(Fluid fluid, long amount) {
        this.fluidStorage.variant = FluidVariant.of(fluid);
        fluidStorage.amount = amount;
        if (amount == 0)
            fluidStorage.variant = FluidVariant.blank();
        setChanged();
    }


    public long getFluidAmount(){ return fluidStorage.getAmount();}

    @Override
    public WONBlastFurnaceScreenHandler.PosData getScreenOpeningData(ServerPlayer player) {
        return new WONBlastFurnaceScreenHandler.PosData(getBlockPos());
    }
}