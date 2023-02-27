package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public record ModDoubleFeatureConfig(RegistryEntry<PlacedFeature> primaryFeature, RegistryEntry<PlacedFeature> secondaryFeature, Vec3i offset)
        implements FeatureConfig {
    public static final Codec<ModDoubleFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PlacedFeature.REGISTRY_CODEC.fieldOf("primaryFeature").forGetter(ModDoubleFeatureConfig::primaryFeature),
                PlacedFeature.REGISTRY_CODEC.fieldOf("secondaryFeature").forGetter(ModDoubleFeatureConfig::secondaryFeature),
                Vec3i.CODEC.fieldOf("offset").forGetter(ModDoubleFeatureConfig::offset))
            .apply(instance, instance.stable(ModDoubleFeatureConfig::new)));
}
