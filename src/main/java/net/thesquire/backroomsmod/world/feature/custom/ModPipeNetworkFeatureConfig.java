package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

/**
 * The arguments for this feature are as follows:
 * @param state The BlockState that is being placed.
 * @param branchProbability The probability that a branch will be created or not.
 * @param targetPredicate The condition that must be true for the target state to be placed. If false, this stops block placement
 * @param stopPlacementPredicate An additional optional argument which, when true, stops the block placement.
 */
public record ModPipeNetworkFeatureConfig(BlockState state, float branchProbability, IntProvider branchSpacing, BlockPredicate targetPredicate, BlockPredicate stopPlacementPredicate) implements FeatureConfig {

    public static final Codec<ModPipeNetworkFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(ModPipeNetworkFeatureConfig::state),
                    Codec.floatRange(0.0f, 1.0f).fieldOf("branch_probability").forGetter(ModPipeNetworkFeatureConfig::branchProbability),
                    IntProvider.POSITIVE_CODEC.fieldOf("branch_spacing").forGetter(ModPipeNetworkFeatureConfig::branchSpacing),
                    BlockPredicate.BASE_CODEC.fieldOf("target_predicate").forGetter(ModPipeNetworkFeatureConfig::targetPredicate),
                    BlockPredicate.BASE_CODEC.optionalFieldOf("stop_placement_predicate", BlockPredicate.not(BlockPredicate.alwaysTrue()))
                            .forGetter(ModPipeNetworkFeatureConfig::stopPlacementPredicate))
            .apply(instance, instance.stable(ModPipeNetworkFeatureConfig::new)));

    public static ModPipeNetworkFeatureConfig of(BlockState state, float branchProbability, IntProvider branchSpacing, BlockPredicate targetPredicate, BlockPredicate stopPlacementPredicate) {
        return new ModPipeNetworkFeatureConfig(state, branchProbability, branchSpacing, targetPredicate, stopPlacementPredicate);
    }

    public static ModPipeNetworkFeatureConfig of(BlockState state, float branchProbability, IntProvider branchSpacing, BlockPredicate targetPredicate) {
        return of(state, branchProbability, branchSpacing, targetPredicate, BlockPredicate.not(BlockPredicate.alwaysTrue()));
    }

}
