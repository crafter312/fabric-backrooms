package net.thesquire.backroomsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AbandonedSuppliesBlock extends HorizontalFacingBlock {

    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
        VoxelShapes.cuboid(0.024999999999999856, 0, -0.024999999999999967, 1.025, 0.0625, 0.9750000000000001),
        VoxelShapes.cuboid(0.14999999999999986, 0.0625, 0.3062499999999999, 1, 0.8187500000000001, 0.925)
    );

    private static final VoxelShape EAST_SHAPE = VoxelShapes.union(
        VoxelShapes.cuboid(0.024999999999999856, 0, -1.1102230246251565e-16, 1.025, 0.0625, 1),
        VoxelShapes.cuboid(0.0749999999999999, 0.0625, 0.12499999999999989, 0.6937500000000001, 0.8187500000000001, 0.9750000000000001)
    );

    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
        VoxelShapes.cuboid(0, 0, 0, 1, 0.0625, 1),
        VoxelShapes.cuboid(0.02499999999999991, 0.0625, 0.050000000000000044, 0.875, 0.8187500000000001, 0.6687500000000001)
    );

    private static final VoxelShape WEST_SHAPE = VoxelShapes.union(
        VoxelShapes.cuboid(0, 0, -0.024999999999999967, 1, 0.0625, 0.9750000000000001),
        VoxelShapes.cuboid(0.3312499999999999, 0.0625, -5.551115123125783e-17, 0.95, 0.8187500000000001, 0.8500000000000001)
    );

    /////////////////////////////////////////////////////////////////////////////////////

    public AbandonedSuppliesBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case EAST -> {
                return EAST_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_SHAPE;
            }
            case WEST -> {
                return WEST_SHAPE;
            }
            default -> {
                return NORTH_SHAPE;
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
