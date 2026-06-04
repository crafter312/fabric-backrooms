package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public record ModLootChestFeatureConfig(Identifier lootTableKey, BlockState lootableContainer, boolean preferFacingOutwards) implements FeatureConfig {
    public static final Codec<ModLootChestFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("lootTableKey").forGetter(ModLootChestFeatureConfig::lootTableKey),
            BlockState.CODEC.fieldOf("lootableContainer").forGetter(ModLootChestFeatureConfig::lootableContainer),
            PrimitiveCodec.BOOL.fieldOf("preferFacingOutwards").forGetter(ModLootChestFeatureConfig::preferFacingOutwards)
    ).apply(instance, instance.stable(ModLootChestFeatureConfig::new)));
}
