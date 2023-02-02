package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public record ModThinWallFeatureConfig(List<OreFeatureConfig.Target> targets, IntProvider segments, IntProvider lengths, IntProvider height, boolean straight) implements FeatureConfig {

    public static final Codec<ModThinWallFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(OreFeatureConfig.Target.CODEC).fieldOf("targets").forGetter(ModThinWallFeatureConfig::targets),
                    IntProvider.VALUE_CODEC.fieldOf("segments").forGetter(ModThinWallFeatureConfig::segments),
                    IntProvider.VALUE_CODEC.fieldOf("lengths").forGetter(ModThinWallFeatureConfig::lengths),
                    IntProvider.VALUE_CODEC.fieldOf("height").forGetter(ModThinWallFeatureConfig::height),
                    Codec.BOOL.fieldOf("straight").forGetter(ModThinWallFeatureConfig::straight))
            .apply(instance, instance.stable(ModThinWallFeatureConfig::new)));

}
