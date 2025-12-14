package dev.ambershadow.willofnature.client.screens;

import dev.ambershadow.willofnature.WillOfNature;
import dev.ambershadow.willofnature.client.WillOfNatureClient;
import dev.ambershadow.willofnature.index.networking.FillBucketC2SPacket;
import dev.ambershadow.willofnature.index.networking.UpdateFluidC2SPacket;
import dev.ambershadow.willofnature.index.screens.WONBlastFurnaceScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class WONBlastFurnaceScreen extends AbstractFurnaceScreen<WONBlastFurnaceScreenHandler> {
    private static final ResourceLocation TEXTURE = WillOfNature.id("textures/gui/container/blast_furnace.png");
    private static final ResourceLocation BURN_PROGRESS = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    private static final ResourceLocation LIT_PROGRESS = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");

    public WONBlastFurnaceScreen(WONBlastFurnaceScreenHandler handler, Inventory inventory, Component title) {
        super(handler, new SmeltingRecipeBookComponent(), inventory, title, TEXTURE, LIT_PROGRESS, BURN_PROGRESS);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        BlockPos pos = menu.getBlockPos();
        WillOfNatureClient.FluidData fluidData = WillOfNatureClient.fluidDataMap.getOrDefault(
                pos,
                new WillOfNatureClient.FluidData(0, 0)
        );
        int hoverX = ((Minecraft.getInstance().getWindow().getGuiScaledWidth() - imageWidth) / 2) + menu.fluidRegion.x;
        int hoverY = ((Minecraft.getInstance().getWindow().getGuiScaledHeight() - imageHeight) / 2) + menu.fluidRegion.y;
        Fluid fluid = BuiltInRegistries.FLUID.byId(fluidData.fluidId);
        if ((mouseX >= hoverX && mouseX <= hoverX + menu.fluidRegion.width) && (
                mouseY >= hoverY && mouseY <= hoverY + menu.fluidRegion.height)){
            MutableComponent name = fluid.defaultFluidState().createLegacyBlock().getBlock().getName();
            if (name.equals(Blocks.AIR.getName()) || fluidData.amount == 0)
                name = Component.literal("Empty");
            name = name.append(" " + fluidData.amount + "/1000 mB");
            context.renderTooltip(font, name, mouseX, mouseY);
        }
         FluidRenderHandler renderHandler =
                FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        if (renderHandler == null) return;
        TextureAtlasSprite[] textures = renderHandler.getFluidSprites(
                Minecraft.getInstance().level,
                BlockPos.ZERO,
                fluid.defaultFluidState()
        );

        if (textures == null || textures.length == 0 || textures[0] == null) {
            WillOfNature.LOGGER.warn("Missing fluid sprite for {}", BuiltInRegistries.FLUID.getKey(fluid));
            return;
        }
        TextureAtlasSprite sprite = textures[0];

        int maxAmount = 1000;
        int maxWidth = menu.fluidRegion.width - 2;
        long amount = fluidData.amount;
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        int scaledWidth = (int)((amount / (float) maxAmount) * maxWidth);
        int barX = (((Minecraft.getInstance().getWindow().getGuiScaledWidth() - imageWidth) / 2) + menu.fluidRegion.x) + 1;
        int barY = (((Minecraft.getInstance().getWindow().getGuiScaledHeight() - imageHeight) / 2) + menu.fluidRegion.y) + 1;
        int fluidColor = renderHandler.getFluidColor(Minecraft.getInstance().level, BlockPos.ZERO, fluid.defaultFluidState());
        float r = ((fluidColor >> 16) & 0xFF) / 255f;
        float g = ((fluidColor >> 8) & 0xFF) / 255f;
        float b = (fluidColor & 0xFF) / 255f;
        float a = ((fluidColor >> 24) & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
        context.blit(barX, barY, 0, scaledWidth, menu.fluidRegion.height - 2, sprite);
        context.pose().pushPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // reset
        for (int i = 9; i < 9*5; i+=10) {
            context.vLine(barX + i, barY - 1, barY + 5, Color.BLACK.getRGB());
            context.vLine(barX + i, barY + 10, barY + 16, Color.BLACK.getRGB());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        int hoverX = ((Minecraft.getInstance().getWindow().getGuiScaledWidth() - imageWidth) / 2) + menu.fluidRegion.x;
        int hoverY = ((Minecraft.getInstance().getWindow().getGuiScaledHeight() - imageHeight) / 2) + menu.fluidRegion.y;
        BlockPos pos = menu.getBlockPos();
        WillOfNatureClient.FluidData fluidData = WillOfNatureClient.fluidDataMap.getOrDefault(
                pos,
                new WillOfNatureClient.FluidData(0, 0)
        );
        if ((mouseX >= hoverX && mouseX <= hoverX + menu.fluidRegion.width) && (
                mouseY >= hoverY && mouseY <= hoverY + menu.fluidRegion.height)){
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return false;
            ItemStack cursorStack = player.containerMenu.getCarried();
            if (!cursorStack.is(Items.BUCKET) || fluidData.amount < 1000) return false;
            Fluid fluid = BuiltInRegistries.FLUID.byId(fluidData.fluidId);
            if (FluidRenderHandlerRegistry.INSTANCE.get(fluid) == null) return false;
            int amount = cursorStack.getCount() - 1;
            ClientPlayNetworking.send(new FillBucketC2SPacket(new ItemStack(fluid.getBucket()), amount));
            ClientPlayNetworking.send(new UpdateFluidC2SPacket(pos, fluidData.fluidId, fluidData.amount - 1000));
            fluidData.amount -= 1000;
            return true;
        }

        return false;
    }
}
