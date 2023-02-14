package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockProperties;

import java.util.Map;

public class PaintedWarehouseConcreteBlock extends Block {

    private static final BooleanProperty DRIPPING = ModBlockProperties.DRIPPING;
    private static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    private static final BooleanProperty EAST = ConnectingBlock.EAST;
    private static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    private static final BooleanProperty WEST = ConnectingBlock.WEST;
    private static final BooleanProperty UP = ConnectingBlock.UP;
    private static final BooleanProperty DOWN = ConnectingBlock.DOWN;

    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

    public PaintedWarehouseConcreteBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(DRIPPING, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DRIPPING, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), state.get(NORTH))
                .with(FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), state.get(SOUTH))
                .with(FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), state.get(EAST))
                .with(FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), state.get(WEST))
                .with(FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), state.get(UP))
                .with(FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), state.get(DOWN));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), state.get(NORTH))
                .with(FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), state.get(SOUTH))
                .with(FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), state.get(EAST))
                .with(FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), state.get(WEST))
                .with(FACING_PROPERTIES.get(mirror.apply(Direction.UP)), state.get(UP))
                .with(FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), state.get(DOWN));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(!state.contains(DRIPPING) || !state.get(DRIPPING) || random.nextDouble() > 0.1) return;

        BlockPos blockPos = pos.offset(Direction.DOWN);
        if(world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP)) return;

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - 0.05;
        double z = pos.getZ() + random.nextDouble();
        world.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0, 0.0, 0.0);
    }

}
