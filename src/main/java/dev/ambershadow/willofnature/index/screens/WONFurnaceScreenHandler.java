package dev.ambershadow.willofnature.index.screens;

import dev.ambershadow.willofnature.registration.WONScreenHandlers;
import dev.ambershadow.willofnature.mixin.AbstractContainerMenuAccessor;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class WONFurnaceScreenHandler extends AbstractFurnaceMenu {

    private final Container inventory;

    public WONFurnaceScreenHandler(int syncId, Inventory playerInventory) {
         this(syncId, playerInventory, new SimpleContainer(7), new SimpleContainerData(7));
    }

    public WONFurnaceScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData delegate) {
        super(
                WONScreenHandlers.WON_FURNACE,
                RecipeType.SMELTING,
                RecipeBookType.FURNACE,
                syncId,
                playerInventory,
                inventory,
                delegate
        );
        this.inventory = inventory;
        this.slots.clear();
        ((AbstractContainerMenuAccessor) this).getLastSlots().clear();
        ((AbstractContainerMenuAccessor) this).getPreviousStacks().clear();

        this.addSlot(new Slot(inventory, 0, 47, 17));
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
        this.addSlot(new Slot(inventory, 6, 65, 17));


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

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents finder) {
        if (this.inventory instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible) this.inventory).fillStackedContents(finder);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(0).setByPlayer(ItemStack.EMPTY);
        this.getSlot(6).setByPlayer(ItemStack.EMPTY);
        this.getSlot(2).setByPlayer(ItemStack.EMPTY);
    }

    @Override
    public int getResultSlotIndex() {
        return 2;
    }
    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }
    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.FURNACE;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);

        if (slotObj.hasItem()) {
            ItemStack stackInSlot = slotObj.getItem();
            newStack = stackInSlot.copy();

            if (slot >= 0 && slot <= 6) {
                if (!this.moveItemStackTo(stackInSlot, 7, 43, false)) {
                    return ItemStack.EMPTY;
                }
                slotObj.onQuickCraft(stackInSlot, newStack);
            }
            else if (slot >= 7 && slot < 45) {
                boolean insertedAsFuel = isFuel(stackInSlot) && this.moveItemStackTo(stackInSlot, 1, 2, false);

                if (!insertedAsFuel) {
                    boolean insertedInSlot0 = this.moveItemStackTo(stackInSlot, 0, 1, false);

                    if (!insertedInSlot0) {
                        boolean insertedInSlot6 = this.moveItemStackTo(stackInSlot, 6, 7, false);

                        if (!insertedInSlot6) {
                            if (slot < 36) {
                                if (!this.moveItemStackTo(stackInSlot, 36, 43, false)) {
                                    return ItemStack.EMPTY;
                                }
                            } else if (!this.moveItemStackTo(stackInSlot, 9, 36, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
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