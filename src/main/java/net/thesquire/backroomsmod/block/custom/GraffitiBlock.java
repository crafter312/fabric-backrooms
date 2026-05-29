package net.thesquire.backroomsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class GraffitiBlock extends HorizontalFacingBlock implements Waterloggable {

    public static final MapCodec<GraffitiBlock> CODEC = createCodec(GraffitiBlock::new);

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0F, 0.0F, 0.0F, 1.0F, 16.0F, 16.0F);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 1.0F);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0F, 0.0F, 15.0F, 16.0F, 16.0F, 16.0F);

    public GraffitiBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends HorizontalFacingBlock> getCodec() { return CODEC; }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.contains(FACING)) return super.getOutlineShape(state, world, pos, context);
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
        builder.add(FACING, WATERLOGGED);
    }

}
