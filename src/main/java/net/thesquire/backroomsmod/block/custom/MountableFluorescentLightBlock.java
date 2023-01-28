package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.entity.flickering.FluorescentLightBlockEntity;
import net.thesquire.backroomsmod.block.entity.flickering.MountableFluorescentLightBlockEntity;
import org.jetbrains.annotations.Nullable;

public class MountableFluorescentLightBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final EnumProperty<WallMountLocation> WALL_MOUNT_LOCATION = Properties.WALL_MOUNT_LOCATION;
    public static final BooleanProperty FLICKERING = ModBlockProperties.FLICKERING;
    public static final IntProperty LUMINANCE = ModBlockProperties.LUMINANCE;

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

    public static int getLuminance(BlockState state) {
        return state.get(LUMINANCE);
    }

    /////////////////////////////////////////////////////////////////////////////////

    public MountableFluorescentLightBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(WALL_MOUNT_LOCATION, WallMountLocation.WALL)
                .with(FACING, Direction.NORTH).with(FLICKERING, false)
                .with(LUMINANCE, FluorescentLightBlockEntity.defaultLuminance));
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
        builder.add(FACING, WALL_MOUNT_LOCATION, FLICKERING, LUMINANCE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MountableFluorescentLightBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ModBlockWithEntity.checkType(type, ModBlockEntities.MOUNTABLE_FLUORESCENT_LIGHT, MountableFluorescentLightBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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

    @SuppressWarnings("deprecation")
    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
    }

    /*@Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
        if(blockEntity instanceof MountableFluorescentLightBlockEntity)
            return ((MountableFluorescentLightBlockEntity)blockEntity).getEventListener();
        return null;
    }*/
}
