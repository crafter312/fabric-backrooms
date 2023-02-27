package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ModDoubleFeature extends Feature<ModDoubleFeatureConfig> {

    public ModDoubleFeature(Codec<ModDoubleFeatureConfig> configCodec) { super(configCodec); }

    @Override
    public boolean generate(FeatureContext<ModDoubleFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        ChunkGenerator chunkGenerator = context.getGenerator();
        Random random = context.getRandom();
        BlockPos origin = context.getOrigin();

        ModDoubleFeatureConfig config = context.getConfig();

        return config.primaryFeature().value().generateUnregistered(world, chunkGenerator, random, origin)
            && config.secondaryFeature().value().generateUnregistered(world, chunkGenerator, random, origin.add(config.offset()));
    }

}
