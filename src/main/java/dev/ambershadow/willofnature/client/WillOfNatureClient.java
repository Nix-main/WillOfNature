package dev.ambershadow.willofnature.client;

import dev.ambershadow.willofnature.client.networking.UpdateFluidS2CPacket;
import dev.ambershadow.willofnature.client.screens.CrusherScreen;
import dev.ambershadow.willofnature.client.screens.WONBlastFurnaceScreen;
import dev.ambershadow.willofnature.client.screens.WONFurnaceScreen;
import dev.ambershadow.willofnature.registration.WONScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class WillOfNatureClient implements ClientModInitializer {

    public static class FluidData {
        public int fluidId;
        public long amount;

        public FluidData(int fluidId, long amount) {
            this.fluidId = fluidId;
            this.amount = amount;
        }
    }

    public static final Map<BlockPos, FluidData> fluidDataMap = new HashMap<>();


    @Override
    public void onInitializeClient() {
        MenuScreens.register(WONScreenHandlers.WON_FURNACE, WONFurnaceScreen::new);
        MenuScreens.register(WONScreenHandlers.WON_BLAST_FURNACE, WONBlastFurnaceScreen::new);
        MenuScreens.register(WONScreenHandlers.CRUSHER, CrusherScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(UpdateFluidS2CPacket.ID, new UpdateFluidS2CPacket.Receiver());
    }
}
