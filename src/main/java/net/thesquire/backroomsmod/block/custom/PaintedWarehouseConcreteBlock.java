package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import org.jetbrains.annotations.Nullable;

public class PaintedWarehouseConcreteBlock extends HorizontalFacingBlock {

    private static final BooleanProperty DRIPPING = ModBlockProperties.DRIPPING;
    private static final IntProperty ALT_SIDES = ModBlockProperties.ALT_SIDES;
    private static final Random rand = Random.create();
    private static final BiasedToBottomIntProvider sidesProvider = BiasedToBottomIntProvider.create(0, 2);

    public PaintedWarehouseConcreteBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(DRIPPING, false)
                .with(FACING, Direction.NORTH)
                .with(ALT_SIDES, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DRIPPING, FACING, ALT_SIDES);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getPlayerFacing().getOpposite())
                .with(ALT_SIDES, sidesProvider.get(rand));
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
