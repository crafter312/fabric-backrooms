package net.thesquire.backroomsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.entity.flickering.FluorescentLightBlockEntity;
import org.jetbrains.annotations.Nullable;

import static net.thesquire.backroomsmod.block.custom.ModBlockWithEntity.checkType;

public class FluorescentLightBlock extends BlockWithEntity {

    public static final MapCodec<FluorescentLightBlock> CODEC = createCodec(FluorescentLightBlock::new);
    public static final BooleanProperty FLICKERING = ModBlockProperties.FLICKERING;
    public static final IntProperty LUMINANCE = ModBlockProperties.LUMINANCE;

    //////////////////////////////////////////////////////////

    public FluorescentLightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FLICKERING, false).with(LUMINANCE, FluorescentLightBlockEntity.defaultLuminance));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
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
