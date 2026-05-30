package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

/**
 * This feature's parameters:
 * @param blockState the BlockState to be placed
 * @param numWindows the max number of blocks that will it will attempt to place (in a straight line)
 * @param spacing the number of blocks between each placed block
 * @param inWall whether the feature should place each block on the wall surface or inside the wall
 */

public record ModWindowFeatureConfig(BlockState blockState, IntProvider numWindows, IntProvider spacing, boolean inWall) implements FeatureConfig {
    public static final Codec<ModWindowFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BlockState.CODEC.fieldOf("blockState").forGetter(ModWindowFeatureConfig::blockState),
                    IntProvider.VALUE_CODEC.fieldOf("numWindows").forGetter(ModWindowFeatureConfig::numWindows),
                    IntProvider.VALUE_CODEC.fieldOf("spacing").forGetter(ModWindowFeatureConfig::spacing),
                    Codec.BOOL.fieldOf("inWall").forGetter(ModWindowFeatureConfig::inWall))
            .apply(instance, instance.stable(ModWindowFeatureConfig::new)));
}
