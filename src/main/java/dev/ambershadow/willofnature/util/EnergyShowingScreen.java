package dev.ambershadow.willofnature.util;

import dev.ambershadow.willofnature.WillOfNature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public interface EnergyShowingScreen {

    ResourceLocation ENERGY = WillOfNature.id("textures/gui/sprites/container/global/energy.png");

    default void drawEnergyBar(GuiGraphics context, int energy, int maxEnergy, Rectangle bar, int backgroundWidth, int backgroundHeight, boolean tooltip){
        drawEnergyBar(context, energy, maxEnergy, bar, backgroundWidth, backgroundHeight, false, 0, 0);
    }

    default void drawEnergyBar(GuiGraphics context, int energy, int maxEnergy, Rectangle bar, int backgroundWidth, int backgroundHeight, boolean tooltip, int mouseX, int mouseY){
        int height = (int)Math.floor(((double)energy/maxEnergy) * 50);
        int barX = ((Minecraft.getInstance().getWindow().getGuiScaledWidth() - backgroundWidth) / 2) + bar.x;
        int barY = ((Minecraft.getInstance().getWindow().getGuiScaledHeight() - backgroundHeight) / 2) + bar.y;
        context.blit(ENERGY, barX, barY, 0, 0, 0, 15, 50, 30, 50);
        context.blit(ENERGY, barX, barY + (bar.height - height), 0, 15, 0, 15, height, 30, 50);
        if (tooltip){
            if ((mouseX >= barX && mouseX <= barX + bar.width) && (
                    mouseY >= barY && mouseY <= barY + bar.height))
                context.renderTooltip(Minecraft.getInstance().font, Component.literal(energy + "/" + maxEnergy + " E"), mouseX, mouseY);
        }
    }
}
