package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.custom.FluorescentLightBlock;

import java.util.Random;
import java.util.UUID;

public class FluorescentLightBlockEntity extends BlockEntity {

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FluorescentLightBlockEntity blockEntity) {
        if(world.isClient()) return;
        if(!state.get(FluorescentLightBlock.FLICKERING)) return;

        Random rand = new Random();
        double d = rand.nextDouble();
        BiasedToBottomIntProvider dim1 = BiasedToBottomIntProvider.create(0, 15);
        BiasedToBottomIntProvider dim2 = BiasedToBottomIntProvider.create(0, 8);
        UniformIntProvider dim3 = UniformIntProvider.create(0,2);
        int lum = 15;
        if (d < 0.02) { lum -= dim1.get(rand); }
        else if (d < 0.1) { lum -= dim2.get(rand); }
        else if (d < 0.5) { lum -= dim3.get(rand); }
        else return;

        world.setBlockState(pos, state.with(FluorescentLightBlock.LUMINANCE, lum), Block.NOTIFY_LISTENERS);
    }

}
