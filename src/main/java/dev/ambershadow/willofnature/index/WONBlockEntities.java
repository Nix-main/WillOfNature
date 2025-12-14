package dev.ambershadow.willofnature.index;

import dev.ambershadow.willofnature.index.block.entities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class WONBlockEntities {
    static void init(){}

    public static final BlockEntityType<WONFurnaceBlockEntity> WON_FURNACE =
            WONRegistrar.register("won_furnace", WONFurnaceBlockEntity::new, WONBlocks.WON_FURNACE);
    public static final BlockEntityType<WONBlastFurnaceBlockEntity> WON_BLAST_FURNACE =
            WONRegistrar.register("won_blast_furnace", WONBlastFurnaceBlockEntity::new, WONBlocks.WON_BLAST_FURNACE);

    public static final BlockEntityType<CopperPipeBlockEntity> COPPER_PIPE =
            WONRegistrar.register("copper_pipe", CopperPipeBlockEntity::new, WONBlocks.COPPER_PIPE);

    public static final BlockEntityType<InputPipeBlockEntity> INPUT_PIPE =
            WONRegistrar.register("input_pipe", InputPipeBlockEntity::new, WONBlocks.INPUT_PIPE);

    public static final BlockEntityType<OutputPipeBlockEntity> OUTPUT_PIPE =
            WONRegistrar.register("output_pipe", OutputPipeBlockEntity::new, WONBlocks.OUTPUT_PIPE);

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER =
            WONRegistrar.register("crusher", CrusherBlockEntity::new, WONBlocks.CRUSHER);
}
