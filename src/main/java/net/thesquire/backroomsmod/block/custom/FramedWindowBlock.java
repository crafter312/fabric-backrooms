package net.thesquire.backroomsmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.thesquire.backroomsmod.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

//TODO: add OfficeWindow class (extension of FramedWindowBlock) to contain portal functionality

/**
 * This class borrows extensively from {@link net.minecraft.block.TrapdoorBlock}. However, a few differences
 * make the direct extension of said class impractical.
 */
public class FramedWindowBlock extends HorizontalFacingBlock implements Waterloggable {

    public static final MapCodec<FramedWindowBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType),
            FramedWindowBlock.createSettingsCodec()
    ).apply(instance, FramedWindowBlock::new));

    // Block properties
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape NORTH_CLOSED = Block.createCuboidShape(0, 0, 11, 16, 16, 13);
    private static final VoxelShape EAST_CLOSED = Block.createCuboidShape(3, 0, 0, 5, 16, 16);
    private static final VoxelShape SOUTH_CLOSED = Block.createCuboidShape(0, 0, 3, 16, 16, 5);
    private static final VoxelShape WEST_CLOSED = Block.createCuboidShape(11, 0, 0, 13, 16, 16);

    private static final VoxelShape NORTH_OPEN_LEFT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(12, 1, 1, 14, 15, 15), Stream.of(
            Block.createCuboidShape(0, 0, 11, 16, 1, 13),
            Block.createCuboidShape(0, 15, 11, 16, 16, 13),
            Block.createCuboidShape(15, 1, 11, 16, 15, 13),
            Block.createCuboidShape(0, 1, 11, 1, 15, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape EAST_OPEN_LEFT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 1, 12, 15, 15, 14), Stream.of(
            Block.createCuboidShape(3, 0, 0, 5, 1, 16),
            Block.createCuboidShape(3, 15, 0, 5, 16, 16),
            Block.createCuboidShape(3, 1, 15, 5, 15, 16),
            Block.createCuboidShape(3, 1, 0, 5, 15, 1)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_OPEN_LEFT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(2, 1, 1, 4, 15, 15), Stream.of(
            Block.createCuboidShape(0, 0, 3, 16, 1, 5),
            Block.createCuboidShape(0, 15, 3, 16, 16, 5),
            Block.createCuboidShape(0, 1, 3, 1, 15, 5),
            Block.createCuboidShape(15, 1, 3, 16, 15, 5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape WEST_OPEN_LEFT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 1, 2, 15, 15, 4), Stream.of(
            Block.createCuboidShape(11, 0, 0, 13, 1, 16),
            Block.createCuboidShape(11, 15, 0, 13, 16, 16),
            Block.createCuboidShape(11, 1, 0, 13, 15, 1),
            Block.createCuboidShape(11, 1, 15, 13, 15, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);

    private static final VoxelShape NORTH_OPEN_RIGHT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(2, 1, 1, 4, 15, 15), Stream.of(
            Block.createCuboidShape(0, 0, 11, 16, 1, 13),
            Block.createCuboidShape(0, 15, 11, 16, 16, 13),
            Block.createCuboidShape(0, 1, 11, 1, 15, 13),
            Block.createCuboidShape(15, 1, 11, 16, 15, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape EAST_OPEN_RIGHT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 1, 2, 15, 15, 4), Stream.of(
            Block.createCuboidShape(3, 0, 0, 5, 1, 16),
            Block.createCuboidShape(3, 15, 0, 5, 16, 16),
            Block.createCuboidShape(3, 1, 0, 5, 15, 1),
            Block.createCuboidShape(3, 1, 15, 5, 15, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape SOUTH_OPEN_RIGHT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(12, 1, 1, 14, 15, 15), Stream.of(
            Block.createCuboidShape(0, 0, 3, 16, 1, 5),
            Block.createCuboidShape(0, 15, 3, 16, 16, 5),
            Block.createCuboidShape(15, 1, 3, 16, 15, 5),
            Block.createCuboidShape(0, 1, 3, 1, 15, 5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);
    private static final VoxelShape WEST_OPEN_RIGHT = VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 1, 12, 15, 15, 14), Stream.of(
            Block.createCuboidShape(11, 0, 0, 13, 1, 16),
            Block.createCuboidShape(11, 15, 0, 13, 16, 16),
            Block.createCuboidShape(11, 1, 15, 13, 15, 16),
            Block.createCuboidShape(11, 1, 0, 13, 15, 1)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);

    /////////////////////////////////////////////////////////////////////////////////////

    private final BlockSoundGroup glassSoundGroup = BlockSoundGroup.GLASS;

    private final BlockSetType blockSetType;
    private final VoxelShape[] connectionsToShape;

    public FramedWindowBlock(BlockSetType type, Settings settings) {
        super(settings.sounds(type.soundType()));
        this.blockSetType = type;
        this.connectionsToShape = generateStateToShapeMap();
        this.setDefaultState(this.getDefaultState()
                .with(FramedWindowBlock.FACING, Direction.NORTH)
                .with(FramedWindowBlock.OPEN, false)
                .with(FramedWindowBlock.HINGE, DoorHinge.LEFT)
                .with(FramedWindowBlock.POWERED, false)
                .with(FramedWindowBlock.WATERLOGGED, false));
    }

    public MapCodec<? extends FramedWindowBlock> getCodec() {
        return CODEC;
    }

    // The general shape of the window when closed is independent of the hinge position,
    // so the closed window case is evaluated separately from the open case in order to
    // reduce the size of connectionsToShape.
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(FramedWindowBlock.OPEN))
            return this.connectionsToShape[this.getConnectionMask(state)];
        switch (state.get(FramedWindowBlock.FACING)) {
            case EAST -> {
                return EAST_CLOSED;
            }
            case SOUTH -> {
                return SOUTH_CLOSED;
            }
            case WEST -> {
                return WEST_CLOSED;
            }
            default -> {
                return NORTH_CLOSED;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND, AIR -> {
                return state.get(OPEN);
            }
            case WATER -> {
                return state.get(WATERLOGGED);
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        this.flip(state, world, pos, player);
        return ActionResult.success(world.isClient);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK && !world.isClient() && this.blockSetType.canOpenByWindCharge() && !state.get(POWERED)) {
            this.flip(state, world, pos, null);
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        BlockState blockState = state.cycle(FramedWindowBlock.OPEN);
        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        if (blockState.get(FramedWindowBlock.WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        this.playToggleSound(player, world, pos, blockState.get(FramedWindowBlock.OPEN));
    }

    protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
        world.playSound(player, pos, open ? this.blockSetType.trapdoorOpen() : this.blockSetType.trapdoorClose(), SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
        world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient)
            return;
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(POWERED)) {
            if (state.get(OPEN) != bl) {
                state = state.with(OPEN, bl);
                this.playToggleSound(null, world, pos, bl);
            }
            world.setBlockState(pos, state.with(POWERED, bl), Block.NOTIFY_LISTENERS);
            if (state.get(WATERLOGGED)) {
                world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getSide();
        blockState = ctx.canReplaceExisting() || !direction.getAxis().isHorizontal() ? blockState.with(FramedWindowBlock.FACING, ctx.getHorizontalPlayerFacing().getOpposite()) : blockState.with(FramedWindowBlock.FACING, direction);
        if (world.isReceivingRedstonePower(blockPos))
            blockState = blockState.with(FramedWindowBlock.OPEN, true).with(FramedWindowBlock.POWERED, true);

        return blockState.with(FramedWindowBlock.HINGE, this.getHinge(ctx)).with(FramedWindowBlock.WATERLOGGED, world.getFluidState(blockPos).getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HINGE, POWERED, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED))
            return Fluids.WATER.getStill(false);
        return super.getFluidState(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    protected BlockSetType getBlockSetType() {
        return this.blockSetType;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, glassSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (glassSoundGroup.getVolume() + 1.0f) / 2.0f, glassSoundGroup.getPitch() * 0.8f, false);
        return super.onBreak(world, pos, state, player);
    }

    // Generates VoxelShape map for open states
    private VoxelShape[] generateStateToShapeMap() {
        BlockState defaultState = this.getDefaultState();
        BlockState northState = defaultState.with(FACING, Direction.NORTH);
        BlockState eastState = defaultState.with(FACING, Direction.EAST);
        BlockState southState = defaultState.with(FACING, Direction.SOUTH);
        BlockState westState = defaultState.with(FACING, Direction.WEST);

        VoxelShape[] voxelShapes = new VoxelShape[8];
        voxelShapes[this.getConnectionMask(northState.with(FramedWindowBlock.HINGE, DoorHinge.LEFT))] = NORTH_OPEN_LEFT;
        voxelShapes[this.getConnectionMask(eastState.with(FramedWindowBlock.HINGE, DoorHinge.LEFT))] = EAST_OPEN_LEFT;
        voxelShapes[this.getConnectionMask(southState.with(FramedWindowBlock.HINGE, DoorHinge.LEFT))] = SOUTH_OPEN_LEFT;
        voxelShapes[this.getConnectionMask(westState.with(FramedWindowBlock.HINGE, DoorHinge.LEFT))] = WEST_OPEN_LEFT;
        voxelShapes[this.getConnectionMask(northState.with(FramedWindowBlock.HINGE, DoorHinge.RIGHT))] = NORTH_OPEN_RIGHT;
        voxelShapes[this.getConnectionMask(eastState.with(FramedWindowBlock.HINGE, DoorHinge.RIGHT))] = EAST_OPEN_RIGHT;
        voxelShapes[this.getConnectionMask(southState.with(FramedWindowBlock.HINGE, DoorHinge.RIGHT))] = SOUTH_OPEN_RIGHT;
        voxelShapes[this.getConnectionMask(westState.with(FramedWindowBlock.HINGE, DoorHinge.RIGHT))] = WEST_OPEN_RIGHT;
        return voxelShapes;
    }

    private int getConnectionMask(BlockState state) {
        int i = 0;

        // horizontal facing property
        i |= state.get(FramedWindowBlock.FACING).getHorizontal();
        int facingSize = (int) Math.ceil(ModUtils.log(2.0, (double) Direction.Type.HORIZONTAL.stream().count()));

        // door hinge property
        if (state.get(FramedWindowBlock.HINGE).equals(DoorHinge.LEFT))
            i |= 1 << facingSize;

        return i;
    }

    /**
     * See {@link net.minecraft.block.DoorBlock}. This is similar, except all functionality related to double doors has
     * been removed.
     */
    private DoorHinge getHinge(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getHorizontalPlayerFacing();
        int j = direction.getOffsetX();
        int k = direction.getOffsetZ();
        Vec3d vec3d = ctx.getHitPos();
        double d = vec3d.x - (double)blockPos.getX();
        double e = vec3d.z - (double)blockPos.getZ();
        return j < 0 && e < 0.5 || j > 0 && e > 0.5 || k < 0 && d > 0.5 || k > 0 && d < 0.5 ? DoorHinge.RIGHT : DoorHinge.LEFT;
    }

}
