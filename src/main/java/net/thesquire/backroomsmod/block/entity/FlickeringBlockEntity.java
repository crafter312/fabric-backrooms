package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.custom.FluorescentLightBlock;

public class FlickeringBlockEntity extends BlockEntity {

    public static void tick(World world, BlockPos pos, BlockState state, FlickeringBlockEntity blockEntity) {
        if(world.isClient() || !state.get(ModBlockProperties.FLICKERING)) return;

        double d = blockEntity.getRandom();
        int lum = blockEntity.getDefaultLuminance();

        if(d < 0.1)
            lum = blockEntity.getDimmedLuminance();
        else if(state.get(FluorescentLightBlock.LUMINANCE) == blockEntity.getDefaultLuminance())
            return;

        world.setBlockState(pos, state.with(ModBlockProperties.LUMINANCE, lum), Block.NOTIFY_LISTENERS);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private final Random rand = Random.create();
    private final int defaultLuminance;
    private final BiasedToBottomIntProvider dimAmount;

    public FlickeringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int lum) {
        super(type, pos, state);
        defaultLuminance = lum;
        dimAmount = BiasedToBottomIntProvider.create(2, Math.max(lum - 2, 2));
    }

    public double getRandom() { return rand.nextDouble(); }
    public int getDefaultLuminance() { return defaultLuminance; }
    public int getDimmedLuminance() { return defaultLuminance - dimAmount.get(rand); }

}
