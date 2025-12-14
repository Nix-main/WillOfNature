package dev.ambershadow.willofnature.index.screens;

import dev.ambershadow.willofnature.index.WONScreenHandlers;
import dev.ambershadow.willofnature.index.block.entities.WONBlastFurnaceBlockEntity;
import dev.ambershadow.willofnature.mixin.AbstractContainerMenuAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class WONBlastFurnaceScreenHandler extends AbstractFurnaceMenu {

    public record PosData(BlockPos pos) {
        public static final StreamCodec<RegistryFriendlyByteBuf, PosData> PACKET_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                PosData::pos,
                PosData::new
        );
    }

    private final Container inventory;
    public final Rectangle fluidRegion = new Rectangle(86, 58, 51, 18);
    private final BlockPos blockPos;
    public WONBlastFurnaceScreenHandler(int syncId, Inventory playerInventory, PosData posData) {
        this(syncId, playerInventory, new SimpleContainer(6), new SimpleContainerData(6), posData.pos);
    }
    public WONBlastFurnaceScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData delegate) {
        this(syncId, playerInventory, inventory, delegate, inventory instanceof WONBlastFurnaceBlockEntity entity ? entity.getBlockPos() : BlockPos.ZERO);
    }

    private WONBlastFurnaceScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData delegate, BlockPos blockPos) {
        super(
                WONScreenHandlers.WON_BLAST_FURNACE,
                RecipeType.BLASTING,
                RecipeBookType.BLAST_FURNACE,
                syncId,
                playerInventory,
                inventory,
                delegate
        );
        this.inventory = inventory;
        this.blockPos = blockPos;

        this.slots.clear();
        ((AbstractContainerMenuAccessor) this).getLastSlots().clear();
        ((AbstractContainerMenuAccessor) this).getPreviousStacks().clear();

        this.addSlot(new Slot(inventory, 0, 56, 17));
        this.addSlot(new Slot(inventory, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(playerInventory.player, inventory, 2, 116, 35));
        this.addSlot(new Slot(inventory, 3, 146, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(inventory, 4, 146, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(inventory, 5, 146, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

    }
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);

        if (slotObj.hasItem()) {
            ItemStack stackInSlot = slotObj.getItem();
            newStack = stackInSlot.copy();

            if (slot >= 0 && slot <= 5) {
                if (!this.moveItemStackTo(stackInSlot, 6, 42, false)) {
                    return ItemStack.EMPTY;
                }
                slotObj.onQuickCraft(stackInSlot, newStack);
            }
            else if (slot >= 6 && slot < 44) {
                boolean insertedAsFuel = isFuel(stackInSlot) && this.moveItemStackTo(stackInSlot, 1, 2, false);

                if (!insertedAsFuel) {
                    this.moveItemStackTo(stackInSlot, 0, 1, false);
                }
            }

            if (stackInSlot.isEmpty()) {
                slotObj.setByPlayer(ItemStack.EMPTY);
            } else {
                slotObj.setChanged();
            }

            if (stackInSlot.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slotObj.onTake(player, stackInSlot);
        }

        return newStack;
    }
}