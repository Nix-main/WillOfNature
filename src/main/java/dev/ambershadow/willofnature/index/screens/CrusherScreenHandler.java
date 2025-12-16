package dev.ambershadow.willofnature.index.screens;

import dev.ambershadow.willofnature.registration.WONRecipeTypes;
import dev.ambershadow.willofnature.registration.WONScreenHandlers;
import dev.ambershadow.willofnature.index.recipe.CrushingRecipe;
import dev.ambershadow.willofnature.util.CrushingRecipeInput;
import dev.ambershadow.willofnature.util.EnergyHoldingScreenHandler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CrusherScreenHandler extends RecipeBookMenu<CrushingRecipeInput, CrushingRecipe> implements EnergyHoldingScreenHandler {

    private final Container inventory;
    private final ContainerData propertyDelegate;
    protected final Level world;
    private final RecipeType<? extends CrushingRecipe> recipeType;
    private final RecipeBookType category;

    public CrusherScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(5), new SimpleContainerData(4));
    }

    public CrusherScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(WONScreenHandlers.CRUSHER, syncId);
        int i;


        this.recipeType = WONRecipeTypes.CRUSHING;
        this.category = RecipeBookType.CRAFTING;
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.level();

        addSlot(new Slot(inventory, 0, 56, 35));
        addSlot(new Slot(inventory, 1, 116, 35){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(inventory, 2, 146, 17){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(inventory, 3, 146, 35){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addSlot(new Slot(inventory, 4, 146, 53){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        addDataSlots(propertyDelegate);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents finder) {
        if (this.inventory instanceof StackedContentsCompatible f) {
            f.fillStackedContents(finder);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(0).set(ItemStack.EMPTY);
        this.getSlot(1).set(ItemStack.EMPTY);
        this.getSlot(2).set(ItemStack.EMPTY);
        this.getSlot(3).set(ItemStack.EMPTY);
        this.getSlot(4).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CrushingRecipe> recipe) {
        return false;
    }

    @Override
    public int getResultSlotIndex() {
        return 1;
    }

    @Override
    public int getGridWidth() {
        return 0;
    }

    @Override
    public int getGridHeight() {
        return 0;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return false;
    }

    public int getTime(){
        return propertyDelegate.get(0);
    }

    public int getTimeTotal(){
        return propertyDelegate.get(1);
    }

    public int getEnergy(){
        return propertyDelegate.get(2);
    }
    public int getMaxEnergy(){
        return propertyDelegate.get(3);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);
        if (slotObj.hasItem()) {
            ItemStack stackInSlot = slotObj.getItem();
            newStack = stackInSlot.copy();
            if (slot >= 0 && slot <= 4) {
                if (!this.moveItemStackTo(stackInSlot, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
                slotObj.onQuickCraft(stackInSlot, newStack);
            } else if (slot >= 5 && slot < 43) {
                this.moveItemStackTo(stackInSlot, 0, 1, false);
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

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    private Rectangle barArea = new Rectangle(24, 18, 15, 50);

    @Override
    public Rectangle getEnergyBarArea() {
        return barArea;
    }
}
