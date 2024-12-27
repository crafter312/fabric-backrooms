package net.thesquire.backroomsmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

//TODO: add dedicated item texture to remove dark tint issue
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
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape NORTH_CLOSED = VoxelShapes.combineAndSimplify(Stream.of(
            Block.createCuboidShape(0, 0, 11, 16, 2, 13),
            Block.createCuboidShape(0, 14, 11, 16, 16, 13),
            Block.createCuboidShape(14, 2, 11, 16, 14, 13),
            Block.createCuboidShape(0, 2, 11, 2, 14, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), Block.createCuboidShape(2, 2, 11, 14, 14, 13), BooleanBiFunction.OR);

    private static final VoxelShape EAST_CLOSED = VoxelShapes.combineAndSimplify(Stream.of(
            Block.createCuboidShape(3, 0, 0, 5, 2, 16),
            Block.createCuboidShape(3, 14, 0, 5, 16, 16),
            Block.createCuboidShape(3, 2, 14, 5, 14, 16),
            Block.createCuboidShape(3, 2, 0, 5, 14, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), Block.createCuboidShape(3, 2, 2, 5, 14, 14), BooleanBiFunction.OR);

    private static final VoxelShape SOUTH_CLOSED = VoxelShapes.combineAndSimplify(Stream.of(
            Block.createCuboidShape(0, 0, 3, 16, 2, 5),
            Block.createCuboidShape(0, 14, 3, 16, 16, 5),
            Block.createCuboidShape(0, 2, 3, 2, 14, 5),
            Block.createCuboidShape(14, 2, 3, 16, 14, 5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), Block.createCuboidShape(2, 2, 3, 14, 14, 5), BooleanBiFunction.OR);

    private static final VoxelShape WEST_CLOSED = VoxelShapes.combineAndSimplify(Stream.of(
            Block.createCuboidShape(11, 0, 0, 13, 2, 16),
            Block.createCuboidShape(11, 14, 0, 13, 16, 16),
            Block.createCuboidShape(11, 2, 0, 13, 14, 2),
            Block.createCuboidShape(11, 2, 14, 13, 14, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), Block.createCuboidShape(11, 2, 2, 13, 14, 14), BooleanBiFunction.OR);

    private static final VoxelShape NORTH_OPEN = Stream.of(
            Stream.of(
                    Block.createCuboidShape(0, 0, 11, 16, 1, 13),
                    Block.createCuboidShape(0, 15, 11, 16, 16, 13),
                    Block.createCuboidShape(15, 1, 11, 16, 15, 13),
                    Block.createCuboidShape(0, 1, 11, 1, 15, 13)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(12, 1, 1, 14, 2, 15),
                    Block.createCuboidShape(12, 14, 1, 14, 15, 15),
                    Block.createCuboidShape(12, 2, 14, 14, 14, 15),
                    Block.createCuboidShape(12, 2, 1, 14, 14, 2)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Block.createCuboidShape(12, 2, 2, 14, 14, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape WEST_OPEN = Stream.of(
            Stream.of(
                    Block.createCuboidShape(11, 0, 0, 13, 1, 16),
                    Block.createCuboidShape(11, 15, 0, 13, 16, 16),
                    Block.createCuboidShape(11, 1, 0, 13, 15, 1),
                    Block.createCuboidShape(11, 1, 15, 13, 15, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(1, 1, 2, 15, 2, 4),
                    Block.createCuboidShape(1, 14, 2, 15, 15, 4),
                    Block.createCuboidShape(14, 2, 2, 15, 14, 4),
                    Block.createCuboidShape(1, 2, 2, 2, 14, 4)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Block.createCuboidShape(2, 2, 2, 14, 14, 4)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape EAST_OPEN = Stream.of(
            Stream.of(
                    Block.createCuboidShape(3, 0, 0, 5, 1, 16),
                    Block.createCuboidShape(3, 15, 0, 5, 16, 16),
                    Block.createCuboidShape(3, 1, 15, 5, 15, 16),
                    Block.createCuboidShape(3, 1, 0, 5, 15, 1)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(1, 1, 12, 15, 2, 14),
                    Block.createCuboidShape(1, 14, 12, 15, 15, 14),
                    Block.createCuboidShape(1, 2, 12, 2, 14, 14),
                    Block.createCuboidShape(14, 2, 12, 15, 14, 14)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Block.createCuboidShape(2, 2, 12, 14, 14, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SOUTH_OPEN = Stream.of(
            Stream.of(
                    Block.createCuboidShape(0, 0, 3, 16, 1, 5),
                    Block.createCuboidShape(0, 15, 3, 16, 16, 5),
                    Block.createCuboidShape(0, 1, 3, 1, 15, 5),
                    Block.createCuboidShape(15, 1, 3, 16, 15, 5)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Stream.of(
                    Block.createCuboidShape(2, 1, 1, 4, 2, 15),
                    Block.createCuboidShape(2, 14, 1, 4, 15, 15),
                    Block.createCuboidShape(2, 2, 1, 4, 14, 2),
                    Block.createCuboidShape(2, 2, 14, 4, 14, 15)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(),
            Block.createCuboidShape(2, 2, 2, 4, 14, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    /////////////////////////////////////////////////////////////////////////////////////

    private final BlockSoundGroup glassSoundGroup = BlockSoundGroup.GLASS;

    private final BlockSetType blockSetType;
    private final VoxelShape[] connectionsToShape;

    public FramedWindowBlock(BlockSetType type, Settings settings) {
        super(settings.sounds(type.soundType()));
        this.blockSetType = type;
        this.connectionsToShape = generateStateToShapeMap();
        this.setDefaultState(this.getDefaultState()
                .with(TrapdoorBlock.FACING, Direction.NORTH)
                .with(TrapdoorBlock.OPEN, false)
                .with(TrapdoorBlock.POWERED, false)
                .with(TrapdoorBlock.WATERLOGGED, false));
    }

    public MapCodec<? extends FramedWindowBlock> getCodec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.connectionsToShape[this.getConnectionMask(state)];
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
        BlockState blockState = state.cycle(TrapdoorBlock.OPEN);
        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        if (blockState.get(TrapdoorBlock.WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        this.playToggleSound(player, world, pos, blockState.get(TrapdoorBlock.OPEN));
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
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        Direction direction = ctx.getSide();
        blockState = ctx.canReplaceExisting() || !direction.getAxis().isHorizontal() ? blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()) : blockState.with(FACING, direction);
        if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()))
            blockState = blockState.with(OPEN, true).with(POWERED, true);
        return blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, WATERLOGGED);
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

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(Properties.OPEN) ? VoxelShapes.empty() : this.connectionsToShape[this.getConnectionMask(state)];
    }

    private VoxelShape[] generateStateToShapeMap() {
        BlockState defaultState = this.getDefaultState();
        BlockState northState = defaultState.with(FACING, Direction.NORTH);
        BlockState eastState = defaultState.with(FACING, Direction.EAST);
        BlockState southState = defaultState.with(FACING, Direction.SOUTH);
        BlockState westState = defaultState.with(FACING, Direction.WEST);

        VoxelShape[] voxelShapes = new VoxelShape[8];
        voxelShapes[this.getConnectionMask(northState.with(TrapdoorBlock.OPEN, false))] = NORTH_CLOSED;
        voxelShapes[this.getConnectionMask(eastState.with(TrapdoorBlock.OPEN, false))] = EAST_CLOSED;
        voxelShapes[this.getConnectionMask(southState.with(TrapdoorBlock.OPEN, false))] = SOUTH_CLOSED;
        voxelShapes[this.getConnectionMask(westState.with(TrapdoorBlock.OPEN, false))] = WEST_CLOSED;
        voxelShapes[this.getConnectionMask(northState.with(TrapdoorBlock.OPEN, true))] = NORTH_OPEN;
        voxelShapes[this.getConnectionMask(eastState.with(TrapdoorBlock.OPEN, true))] = EAST_OPEN;
        voxelShapes[this.getConnectionMask(southState.with(TrapdoorBlock.OPEN, true))] = SOUTH_OPEN;
        voxelShapes[this.getConnectionMask(westState.with(TrapdoorBlock.OPEN, true))] = WEST_OPEN;
        return voxelShapes;
    }

    private int getConnectionMask(BlockState state) {
        int i = 0;

        // horizontal facing property
        i |= state.get(FACING).getHorizontal();
        int facingSize = (int) Math.ceil(ModUtils.log(2.0, (double) Direction.Type.HORIZONTAL.stream().count()));

        // door open property
        if (state.get(DoorBlock.OPEN))
            i |= 1 << facingSize;

        return i;
    }

}
