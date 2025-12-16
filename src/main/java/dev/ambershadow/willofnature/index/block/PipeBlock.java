package dev.ambershadow.willofnature.index.block;

import dev.ambershadow.willofnature.registration.WONBlockEntities;
import dev.ambershadow.willofnature.index.block.entities.CopperPipeBlockEntity;
import dev.ambershadow.willofnature.index.block.entities.InputPipeBlockEntity;
import dev.ambershadow.willofnature.index.block.entities.OutputPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

public abstract class PipeBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public PipeBlock(Properties settings) {
        super(settings.noOcclusion());
        registerDefaultState(getStateDefinition().any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int first = 5;
        int second = 16;
        double div = (double) first /second;
        double sec = (double) 11/second;
        VoxelShape shape = Shapes.box(div, div, div, sec, sec, sec); // center cube

        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, Shapes.box(div, div, 0.0, sec, sec, div));
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, Shapes.box(div, div, sec, sec, sec, 1.0));
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, Shapes.box(sec, div, div, 1.0, sec, sec));
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, Shapes.box(0.0, div, div, div, sec, sec));
        }
        if (state.getValue(UP)) {
            shape = Shapes.or(shape, Shapes.box(div, sec, div, sec, 1.0, sec));
        }
        if (state.getValue(DOWN)) {
            shape = Shapes.or(shape, Shapes.box(div, 0.0, div, sec, div, sec));
        }

        return shape;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir,
                                           BlockState neighborState,
                                           LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        boolean connect = false;

        BlockEntity be = world.getBlockEntity(neighborPos);
        BlockEntity thiz = world.getBlockEntity(pos);
        if (be != null && FluidStorage.SIDED.find(be.getLevel(), neighborPos, dir.getOpposite()) != null && !(thiz instanceof CopperPipeBlockEntity)) {
            connect = true;
        } else if (neighborState.getBlock() instanceof PipeBlock) {
            connect = true;
        }

        return state.setValue(getPropertyForDirection(dir), connect);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            state = updateShape(state, dir, neighborState, world, pos, neighborPos);
        }
        world.setBlock(pos, state, Block.UPDATE_ALL);
    }

    private static BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (state.getBlock() instanceof InputPipeBlock) {
            return createTickerHelper(type, WONBlockEntities.INPUT_PIPE, InputPipeBlockEntity::tick);
        } else if (state.getBlock() instanceof CopperPipeBlock) {
            return createTickerHelper(type, WONBlockEntities.COPPER_PIPE, CopperPipeBlockEntity::tick);
        } else if (state.getBlock() instanceof OutputPipeBlock) {
            return createTickerHelper(type, WONBlockEntities.OUTPUT_PIPE, OutputPipeBlockEntity::tick);
        }
        return null;
    }
}
