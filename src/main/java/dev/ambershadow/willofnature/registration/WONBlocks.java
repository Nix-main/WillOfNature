package dev.ambershadow.willofnature.registration;

import dev.ambershadow.willofnature.index.block.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WONBlocks {
    static void init(){}
    public static final Block WON_FURNACE = WONRegistrar.register(
            "won_furnace",
            WONFurnaceBlock::new,
            Blocks.FURNACE.properties(),
            true,
            CreativeModeTabs.FUNCTIONAL_BLOCKS,
            CreativeModeTabs.REDSTONE_BLOCKS
    );

    public static final Block WON_BLAST_FURNACE = WONRegistrar.register(
            "won_blast_furnace",
            WONBlastFurnaceBlock::new,
            Blocks.BLAST_FURNACE.properties(),
            true,
            CreativeModeTabs.FUNCTIONAL_BLOCKS
    );

    public static final Block COPPER_PIPE = WONRegistrar.register(
            "copper_pipe",
            CopperPipeBlock::new,
            Blocks.IRON_BARS.properties(),
            true,
            WONRegistrar.BLOCKS_REGISTRY_KEY
    );

    public static final Block INPUT_PIPE = WONRegistrar.register(
            "input_pipe",
            InputPipeBlock::new,
            Blocks.IRON_BARS.properties(),
            true,
            WONRegistrar.BLOCKS_REGISTRY_KEY
    );

    public static final Block OUTPUT_PIPE = WONRegistrar.register(
            "output_pipe",
            OutputPipeBlock::new,
            Blocks.IRON_BARS.properties(),
            true,
            WONRegistrar.BLOCKS_REGISTRY_KEY
    );

    public static final Block CRUSHER = WONRegistrar.CRUSHER;
}
