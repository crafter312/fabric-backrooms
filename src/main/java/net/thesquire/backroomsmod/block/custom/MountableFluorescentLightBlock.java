package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class MountableFluorescentLightBlock extends HorizontalFacingBlock {

    public static final EnumProperty<WallMountLocation> WALL_MOUNT_LOCATION = Properties.WALL_MOUNT_LOCATION;

    private static final VoxelShape SHAPE_FLOOR_NS = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 6, 16, 1, 10),
            Block.createCuboidShape(0, 1, 7, 16, 2, 9),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_FLOOR_EW = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(6, 0, 0, 10, 1, 16),
            Block.createCuboidShape(7, 1, 0, 9, 2, 16),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_WALL_N = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(6, 0, 15, 10, 16, 16),
            Block.createCuboidShape(7, 0, 14, 9, 16, 15),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_WALL_E = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 6, 1, 16, 10),
            Block.createCuboidShape(1, 0, 7, 2, 16, 9),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_WALL_S = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(6, 0, 0, 10, 16, 1),
            Block.createCuboidShape(7, 0, 1, 9, 16, 2),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_WALL_W = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(15, 0, 6, 16, 16, 10),
            Block.createCuboidShape(14, 0, 7, 15, 16, 9),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_CEILING_NS = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 15, 6, 16, 16, 10),
            Block.createCuboidShape(0, 14, 7, 16, 15, 9),
            BooleanBiFunction.OR
    );

    private static final VoxelShape SHAPE_CEILING_EW = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(6, 15, 0, 10, 16, 16),
            Block.createCuboidShape(7, 14, 0, 9, 15, 16),
            BooleanBiFunction.OR
    );

    public MountableFluorescentLightBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState facingState = getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
        Direction blockFace = ctx.getSide();
        return switch (blockFace) {
            case DOWN -> facingState.with(WALL_MOUNT_LOCATION, WallMountLocation.CEILING);
            case UP -> facingState.with(WALL_MOUNT_LOCATION, WallMountLocation.FLOOR);
            default -> getDefaultState().with(FACING, blockFace).with(WALL_MOUNT_LOCATION, WallMountLocation.WALL);
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        String stateName = state.get(WALL_MOUNT_LOCATION).name() + state.get(FACING).name();
        return switch (stateName) {
            case "WALLNORTH" -> SHAPE_WALL_N;
            case "WALLEAST" -> SHAPE_WALL_E;
            case "WALLSOUTH" -> SHAPE_WALL_S;
            case "WALLWEST" -> SHAPE_WALL_W;
            case "CEILINGNORTH", "CEILINGSOUTH" -> SHAPE_CEILING_NS;
            case "CEILINGEAST", "CEILINGWEST" -> SHAPE_CEILING_EW;
            case "FLOOREAST", "FLOORWEST" -> SHAPE_FLOOR_EW;
            default -> SHAPE_FLOOR_NS;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WALL_MOUNT_LOCATION);
    }

}
