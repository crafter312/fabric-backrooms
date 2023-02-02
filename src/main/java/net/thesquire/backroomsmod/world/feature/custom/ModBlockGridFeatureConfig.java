package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

/**
 * This feature's parameters:
 * @param targets A list of replaceable blocks and what blocks they should be replaced with
 * @param width The number of blocks wide the grid is along the x-axis (blocks in the grid, not total blocks)
 * @param depth The number of blocks deep the grid is along the z-axis (blocks in the grid, not total blocks)
 * @param spacing The number of blocks in-between each grid block, not counting the grid block itself
 * @param chance An extra chance modifier used to introduce random variance to the block grid
 */

public record ModBlockGridFeatureConfig(List<OreFeatureConfig.Target> targets, IntProvider width, IntProvider depth,
                                        IntProvider spacing, float chance) implements FeatureConfig {
    public static final Codec<ModBlockGridFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(OreFeatureConfig.Target.CODEC).fieldOf("targets").forGetter(ModBlockGridFeatureConfig::targets),
                    IntProvider.VALUE_CODEC.fieldOf("width").forGetter(ModBlockGridFeatureConfig::width),
                    IntProvider.VALUE_CODEC.fieldOf("depth").forGetter(ModBlockGridFeatureConfig::depth),
                    IntProvider.VALUE_CODEC.fieldOf("spacing").forGetter(ModBlockGridFeatureConfig::spacing),
                    Codec.FLOAT.fieldOf("chance").forGetter(ModBlockGridFeatureConfig::chance))
            .apply(instance, instance.stable(ModBlockGridFeatureConfig::new)));
}
