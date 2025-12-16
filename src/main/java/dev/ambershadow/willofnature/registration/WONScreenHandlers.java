package dev.ambershadow.willofnature.registration;

import dev.ambershadow.willofnature.index.screens.CrusherScreenHandler;
import dev.ambershadow.willofnature.index.screens.WONBlastFurnaceScreenHandler;
import dev.ambershadow.willofnature.index.screens.WONFurnaceScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class WONScreenHandlers {
    static void init(){}
    public static MenuType<WONFurnaceScreenHandler> WON_FURNACE = WONRegistrar.register("won_furnace", WONFurnaceScreenHandler::new, FeatureFlagSet.of());
    public static ExtendedScreenHandlerType<WONBlastFurnaceScreenHandler, WONBlastFurnaceScreenHandler.PosData> WON_BLAST_FURNACE = WONRegistrar.register("won_blast_furnace", WONBlastFurnaceScreenHandler::new, WONBlastFurnaceScreenHandler.PosData.PACKET_CODEC);
    public static MenuType<CrusherScreenHandler> CRUSHER = WONRegistrar.register("crusher", CrusherScreenHandler::new, FeatureFlagSet.of());
}
