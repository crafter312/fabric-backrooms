package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public record ModSimpleWallFeatureConfig(List<OreFeatureConfig.Target> targets, IntProvider height, IntProvider width, IntProvider depth) implements FeatureConfig {

    public static final Codec<ModSimpleWallFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(OreFeatureConfig.Target.CODEC).fieldOf("targets").forGetter(ModSimpleWallFeatureConfig::targets),
                    IntProvider.VALUE_CODEC.fieldOf("height").forGetter(ModSimpleWallFeatureConfig::height),
                    IntProvider.VALUE_CODEC.fieldOf("width").forGetter(ModSimpleWallFeatureConfig::width),
                    IntProvider.VALUE_CODEC.fieldOf("depth").forGetter(ModSimpleWallFeatureConfig::depth))
            .apply(instance, instance.stable(ModSimpleWallFeatureConfig::new)));

}
