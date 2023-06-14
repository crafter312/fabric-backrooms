package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public record ModLootChestFeatureConfig(Identifier lootTableKey) implements FeatureConfig {
    public static final Codec<ModLootChestFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("lootTableKey").forGetter(ModLootChestFeatureConfig::lootTableKey)
    ).apply(instance, instance.stable(ModLootChestFeatureConfig::new)));
}
