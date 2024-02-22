package net.thesquire.backroomsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.stream.Stream;

public class PipeBlock extends ConnectingBlock {

    public static final MapCodec<PipeBlock> CODEC = createCodec(PipeBlock::new);

    private static final VoxelShape RING_SHAPE_NS = Stream.of(
            Block.createCuboidShape(4, 0, 7, 12, 1, 9),
            Block.createCuboidShape(0, 4, 7, 1, 12, 9),
            Block.createCuboidShape(4, 15, 7, 12, 16, 9),
            Block.createCuboidShape(15, 4, 7, 16, 12, 9),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 2, 7, 2, 4, 9), Block.createCuboidShape(2, 1, 7, 4, 2, 9), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(2, 14, 7, 4, 15, 9), Block.createCuboidShape(1, 12, 7, 2, 14, 9), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(14, 12, 7, 15, 14, 9), Block.createCuboidShape(12, 14, 7, 14, 15, 9), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(12, 1, 7, 14, 2, 9), Block.createCuboidShape(14, 2, 7, 15, 4, 9), BooleanBiFunction.OR)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape RING_SHAPE_EW = Stream.of(
            Block.createCuboidShape(7, 0, 4, 9, 1, 12),
            Block.createCuboidShape(7, 4, 0, 9, 12, 1),
            Block.createCuboidShape(7, 15, 4, 9, 16, 12),
            Block.createCuboidShape(7, 4, 15, 9, 12, 16),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 2, 1, 9, 4, 2), Block.createCuboidShape(7, 1, 2, 9, 2, 4), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 14, 2, 9, 15, 4), Block.createCuboidShape(7, 12, 1, 9, 14, 2), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 12, 14, 9, 14, 15), Block.createCuboidShape(7, 14, 12, 9, 15, 14), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(7, 1, 12, 9, 2, 14), Block.createCuboidShape(7, 2, 14, 9, 4, 15), BooleanBiFunction.OR)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape RING_SHAPE_UD = Stream.of(
            Block.createCuboidShape(4, 7, 15, 12, 9, 16),
            Block.createCuboidShape(15, 7, 4, 16, 9, 12),
            Block.createCuboidShape(4, 7, 0, 12, 9, 1),
            Block.createCuboidShape(0, 7, 4, 1, 9, 12),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(14, 7, 12, 15, 9, 14), Block.createCuboidShape(12, 7, 14, 14, 9, 15), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(12, 7, 1, 14, 9, 2), Block.createCuboidShape(14, 7, 2, 15, 9, 4), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 7, 2, 2, 9, 4), Block.createCuboidShape(2, 7, 1, 4, 9, 2), BooleanBiFunction.OR),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(2, 7, 14, 4, 9, 15), Block.createCuboidShape(1, 7, 12, 2, 9, 14), BooleanBiFunction.OR)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    ////////////////////////////////////////////
    public PipeBlock(Settings settings) {
        super(0.3125f, settings);
        setDefaultState(getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));

        int mask_ns = this.getConnectionMask(getDefaultState().with(NORTH, true).with(SOUTH, true));
        int mask_ew = this.getConnectionMask(getDefaultState().with(EAST, true).with(WEST, true));
        int mask_ud = this.getConnectionMask(getDefaultState().with(UP, true).with(DOWN, true));
        this.facingsToShape[mask_ns] = VoxelShapes.combineAndSimplify(RING_SHAPE_NS, this.facingsToShape[mask_ns], BooleanBiFunction.OR);
        this.facingsToShape[mask_ew] = VoxelShapes.combineAndSimplify(RING_SHAPE_EW, this.facingsToShape[mask_ew], BooleanBiFunction.OR);
        this.facingsToShape[mask_ud] = VoxelShapes.combineAndSimplify(RING_SHAPE_UD, this.facingsToShape[mask_ud], BooleanBiFunction.OR);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
    }

    public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
        BlockState blockDown = world.getBlockState(pos.down());
        BlockState blockUp = world.getBlockState(pos.up());
        BlockState blockNorth = world.getBlockState(pos.north());
        BlockState blockEast = world.getBlockState(pos.east());
        BlockState blockSouth = world.getBlockState(pos.south());
        BlockState blockWest = world.getBlockState(pos.west());
        return this.getDefaultState()
                .with(DOWN, blockDown.isOf(this))
                .with(UP, blockUp.isOf(this))
                .with(NORTH, blockNorth.isOf(this))
                .with(EAST, blockEast.isOf(this))
                .with(SOUTH, blockSouth.isOf(this))
                .with(WEST, blockWest.isOf(this));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(FACING_PROPERTIES.get(direction), neighborState.isOf(this));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    protected MapCodec<? extends ConnectingBlock> getCodec() {
        return CODEC;
    }

}
