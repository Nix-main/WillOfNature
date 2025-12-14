package dev.ambershadow.willofnature.client.screens;

import dev.ambershadow.willofnature.WillOfNature;
import dev.ambershadow.willofnature.index.screens.CrusherScreenHandler;
import dev.ambershadow.willofnature.util.EnergyShowingScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CrusherScreen extends AbstractContainerScreen<CrusherScreenHandler> implements EnergyShowingScreen {
    public CrusherScreen(CrusherScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    private static final ResourceLocation BACKGROUND = WillOfNature.id("textures/gui/container/crusher.png");
    private static final ResourceLocation PROGRESS = WillOfNature.id("container/crusher/progress");

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        context.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int l = (int) Math.ceil((double) menu.getTime() / menu.getTimeTotal() * 29);
        context.blitSprite(PROGRESS, 29, 8, 0, 0, i + 77, j + 39, l, 8);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta){
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
        drawEnergyBar(context, menu.getEnergy(), menu.getMaxEnergy(), menu.getEnergyBarArea(), imageWidth, imageHeight, true, mouseX, mouseY);
    }
}
