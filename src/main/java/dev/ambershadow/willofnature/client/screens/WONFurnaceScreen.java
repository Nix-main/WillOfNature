package dev.ambershadow.willofnature.client.screens;

import dev.ambershadow.willofnature.WillOfNature;
import dev.ambershadow.willofnature.index.screens.WONFurnaceScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class WONFurnaceScreen extends AbstractFurnaceScreen<WONFurnaceScreenHandler> {
    private static final ResourceLocation TEXTURE = WillOfNature.id("textures/gui/container/furnace.png");
    private static final ResourceLocation BURN_PROGRESS = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    private static final ResourceLocation LIT_PROGRESS = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");

    public WONFurnaceScreen(WONFurnaceScreenHandler handler, Inventory inventory, Component title) {
        super(handler, new SmeltingRecipeBookComponent(), inventory, title, TEXTURE, LIT_PROGRESS, BURN_PROGRESS);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
}
