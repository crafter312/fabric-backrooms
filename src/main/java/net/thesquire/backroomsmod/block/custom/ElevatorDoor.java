package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.entity.ElevatorDoorBlockEntity;
import net.thesquire.backroomsmod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

public class ElevatorDoor extends DoorBlock implements BlockEntityProvider {

    private static final VoxelShape NORTH_BOTTOM_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 11, 16, 16, 15),
            Block.createCuboidShape(0, 0, 10, 2, 16, 16),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_BOTTOM_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 10, 2, 16, 16),
            Block.createCuboidShape(2, 0, 11, 3, 16, 15),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_BOTTOM_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 11, 14, 16, 15),
            Block.createCuboidShape(14, 0, 10, 16, 16, 16),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_BOTTOM_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(14, 0, 10, 16, 16, 16),
            Block.createCuboidShape(13, 0, 11, 14, 16, 15),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_TOP_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 11, 16, 14, 15),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 10, 2, 16, 16),
                    Block.createCuboidShape(2, 14, 10, 16, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_TOP_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 11, 3, 14, 15),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 10, 2, 16, 16),
                    Block.createCuboidShape(2, 14, 10, 16, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_TOP_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 11, 14, 14, 15),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(14, 0, 10, 16, 16, 16),
                    Block.createCuboidShape(0, 14, 10, 14, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape NORTH_TOP_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(13, 0, 11, 14, 14, 15),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(14, 0, 10, 16, 16, 16),
                    Block.createCuboidShape(0, 14, 10, 14, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);

    private static final VoxelShape EAST_BOTTOM_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 0, 6, 16, 2),
            Block.createCuboidShape(1, 0, 2, 5, 16, 16),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_BOTTOM_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(1, 0, 2, 5, 16, 3),
            Block.createCuboidShape(0, 0, 0, 6, 16, 2),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_BOTTOM_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 14, 6, 16, 16),
            Block.createCuboidShape(1, 0, 0, 5, 16, 14),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_BOTTOM_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(1, 0, 13, 5, 16, 14),
            Block.createCuboidShape(0, 0, 14, 6, 16, 16),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_TOP_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(1, 0, 2, 5, 14, 16),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 0, 6, 16, 2),
                    Block.createCuboidShape(0, 14, 2, 6, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_TOP_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(1, 0, 2, 5, 14, 3),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 0, 6, 16, 2),
                    Block.createCuboidShape(0, 14, 2, 6, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_TOP_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(1, 0, 0, 5, 14, 14),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 14, 6, 16, 16),
                    Block.createCuboidShape(0, 14, 0, 6, 16, 14),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape EAST_TOP_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(1, 0, 13, 5, 14, 14),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 14, 6, 16, 16),
                    Block.createCuboidShape(0, 14, 0, 6, 16, 14),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);

    private static final VoxelShape SOUTH_BOTTOM_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 1, 14, 16, 5),
            Block.createCuboidShape(14, 0, 0, 16, 16, 6),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_BOTTOM_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(14, 0, 0, 16, 16, 6),
            Block.createCuboidShape(13, 0, 1, 14, 16, 5),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_BOTTOM_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 1, 16, 16, 5),
            Block.createCuboidShape(0, 0, 0, 2, 16, 6),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_BOTTOM_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 0, 2, 16, 6),
            Block.createCuboidShape(2, 0, 1, 3, 16, 5),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_TOP_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(0, 0, 1, 14, 14, 5),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(14, 0, 0, 16, 16, 6),
                    Block.createCuboidShape(0, 14, 0, 14, 16, 6),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_TOP_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(13, 0, 1, 14, 14, 5),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(14, 0, 0, 16, 16, 6),
                    Block.createCuboidShape(0, 14, 0, 14, 16, 6),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_TOP_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 1, 16, 14, 5),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 0, 2, 16, 6),
                    Block.createCuboidShape(2, 14, 0, 16, 16, 6),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_TOP_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(2, 0, 1, 3, 14, 5),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(0, 0, 0, 2, 16, 6),
                    Block.createCuboidShape(2, 14, 0, 16, 16, 6),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);

    private static final VoxelShape WEST_BOTTOM_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(11, 0, 0, 15, 16, 14),
            Block.createCuboidShape(10, 0, 14, 16, 16, 16),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_BOTTOM_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(10, 0, 14, 16, 16, 16),
            Block.createCuboidShape(11, 0, 13, 15, 16, 14),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_BOTTOM_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(11, 0, 2, 15, 16, 16),
            Block.createCuboidShape(10, 0, 0, 16, 16, 2),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_BOTTOM_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(10, 0, 0, 16, 16, 2),
            Block.createCuboidShape(11, 0, 2, 15, 16, 3),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_TOP_LEFT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(11, 0, 0, 15, 14, 14),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(10, 0, 14, 16, 16, 16),
                    Block.createCuboidShape(10, 14, 0, 16, 16, 14),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_TOP_LEFT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(11, 0, 13, 15, 14, 14),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(10, 0, 14, 16, 16, 16),
                    Block.createCuboidShape(10, 14, 0, 16, 16, 14),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_TOP_RIGHT = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(11, 0, 2, 15, 14, 16),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(10, 0, 0, 16, 16, 2),
                    Block.createCuboidShape(10, 14, 2, 16, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);
    private static final VoxelShape WEST_TOP_RIGHT_OPEN = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(11, 0, 2, 15, 14, 3),
            VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(10, 0, 0, 16, 16, 2),
                    Block.createCuboidShape(10, 14, 2, 16, 16, 16),
                    BooleanBiFunction.OR),
            BooleanBiFunction.OR);

    private final VoxelShape[] connectionsToShape;

    public ElevatorDoor(Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
        this.connectionsToShape = generateStateToShapeMap();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElevatorDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ModBlockWithEntity.checkType(type, ModBlockEntities.ELEVATOR_DOOR, ElevatorDoorBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.connectionsToShape[this.getConnectionMask(state)];
    }

    private VoxelShape[] generateStateToShapeMap() {
        BlockState defaultState = this.getDefaultState();
        BlockState northState = defaultState.with(FACING, Direction.NORTH);
        BlockState eastState = defaultState.with(FACING, Direction.EAST);
        BlockState southState = defaultState.with(FACING, Direction.SOUTH);
        BlockState westState = defaultState.with(FACING, Direction.WEST);

        VoxelShape[] voxelShapes = new VoxelShape[32];
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = NORTH_BOTTOM_LEFT;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = NORTH_BOTTOM_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = NORTH_BOTTOM_RIGHT;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = NORTH_BOTTOM_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = NORTH_TOP_LEFT;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = NORTH_TOP_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = NORTH_TOP_RIGHT;
        voxelShapes[this.getConnectionMask(northState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = NORTH_TOP_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = EAST_BOTTOM_LEFT;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = EAST_BOTTOM_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = EAST_BOTTOM_RIGHT;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = EAST_BOTTOM_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = EAST_TOP_LEFT;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = EAST_TOP_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = EAST_TOP_RIGHT;
        voxelShapes[this.getConnectionMask(eastState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = EAST_TOP_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = SOUTH_BOTTOM_LEFT;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = SOUTH_BOTTOM_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = SOUTH_BOTTOM_RIGHT;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = SOUTH_BOTTOM_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = SOUTH_TOP_LEFT;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = SOUTH_TOP_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = SOUTH_TOP_RIGHT;
        voxelShapes[this.getConnectionMask(southState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = SOUTH_TOP_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = WEST_BOTTOM_LEFT;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = WEST_BOTTOM_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = WEST_BOTTOM_RIGHT;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.LOWER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = WEST_BOTTOM_RIGHT_OPEN;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, false))] = WEST_TOP_LEFT;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.LEFT).with(OPEN, true))] = WEST_TOP_LEFT_OPEN;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, false))] = WEST_TOP_RIGHT;
        voxelShapes[this.getConnectionMask(westState.with(HALF, DoubleBlockHalf.UPPER).with(HINGE, DoorHinge.RIGHT).with(OPEN, true))] = WEST_TOP_RIGHT_OPEN;
        return voxelShapes;
    }

    private int getConnectionMask(BlockState state) {
        int i = 0;

        // horizontal facing property
        i |= state.get(DoorBlock.FACING).getHorizontal();
        int facingSize = (int) Math.ceil(ModUtils.log(2.0, (double) Direction.Type.HORIZONTAL.stream().count()));

        // door open property
        if (state.get(DoorBlock.OPEN))
            i |= 1 << facingSize;

        // door hinge property
        if (state.get(DoorBlock.HINGE).equals(DoorHinge.LEFT))
            i |= 1 << facingSize + 1;

        // door half property
        if (state.get(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER))
            i |= 1 << facingSize + 2;

        return i;
    }

}
