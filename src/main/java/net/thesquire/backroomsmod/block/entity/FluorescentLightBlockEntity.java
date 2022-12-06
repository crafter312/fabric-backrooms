package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.custom.FluorescentLightBlock;

public class FluorescentLightBlockEntity extends BlockEntity {

    private static final Random rand = Random.create();
    private static final BiasedToBottomIntProvider dim1 = BiasedToBottomIntProvider.create(0, 15);
    private static final BiasedToBottomIntProvider dim2 = BiasedToBottomIntProvider.create(0, 8);
    private static final UniformIntProvider dim3 = UniformIntProvider.create(0,2);

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FluorescentLightBlockEntity blockEntity) {
        if(world.isClient()) return;
        if(!state.get(FluorescentLightBlock.FLICKERING)) return;

        double d = rand.nextDouble();
        if(d>=0.5) return;

        int lum = 15;
        if(d>=0.1) { lum -= dim3.get(rand); }
        else if(d>=0.02) { lum -= dim2.get(rand); }
        else { lum -= dim1.get(rand); }


        world.setBlockState(pos, state.with(FluorescentLightBlock.LUMINANCE, lum), Block.NOTIFY_LISTENERS);
    }

}
