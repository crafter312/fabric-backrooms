package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

/**
 * This feature's parameters:
 * @param blockState the BlockState to be placed next to a solid block
 * @param range the size of square over which to search for valid block placements
 * @param placeChance the chance that, should a valid block placement be found, a block will generate there
 * @param flickeringChance the chance that a placed block has its FLICKERING state set to true
 */

public record ModWallMountableFeatureConfig(BlockState blockState, IntProvider range, float placeChance, float flickeringChance) implements FeatureConfig {
    public static final Codec<ModWallMountableFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("blockState").forGetter(ModWallMountableFeatureConfig::blockState),
                IntProvider.VALUE_CODEC.fieldOf("range").forGetter(ModWallMountableFeatureConfig::range),
                Codec.FLOAT.fieldOf("placeChance").forGetter(ModWallMountableFeatureConfig::placeChance),
                Codec.FLOAT.fieldOf("flickeringChance").forGetter(ModWallMountableFeatureConfig::flickeringChance))
            .apply(instance, instance.stable(ModWallMountableFeatureConfig::new)));
}
