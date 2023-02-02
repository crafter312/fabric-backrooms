package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.entity.FluorescentLightBlockEntity;
import net.thesquire.backroomsmod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class FluorescentLightBlock extends BlockWithEntity {

    public static final BooleanProperty FLICKERING = BooleanProperty.of("flickering");
    public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);

    public FluorescentLightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FLICKERING, false).with(LUMINANCE, 15));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FLICKERING);
        builder.add(LUMINANCE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluorescentLightBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FLUORESCENT_LIGHT, FluorescentLightBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static int getLuminance(BlockState state) {
        return state.get(LUMINANCE);
    }

}
