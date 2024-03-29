package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ModSimpleWallFeature extends Feature<ModSimpleWallFeatureConfig> {

    public ModSimpleWallFeature(Codec<ModSimpleWallFeatureConfig> configCodec) {
        super(configCodec);
    }

    /**
     * This feature generates a rectangular prism of the specified block(s)
     * with the feature origin somewhere in the middle at the bottom of
     * the shape.
     */
    @Override
    public boolean generate(FeatureContext<ModSimpleWallFeatureConfig> context) {
        BlockPos pos = context.getOrigin();
        ModSimpleWallFeatureConfig config = context.getConfig();
        StructureWorldAccess world = context.getWorld();
        ChunkPos chunkPos = world.getChunk(pos).getPos();
        Random random = context.getRandom();

        // limit width and depth to chunk size
        int width = Math.min(config.width().get(random), 16);
        int depth = Math.min(config.depth().get(random), 16);
        int height = config.height().get(random);
        BlockPos startPos = new BlockPos(
                pos.getX() - (width / 2),
                pos.getY(),
                pos.getZ() - (depth / 2));

        boolean setBlocks = false;
        int total = width * depth * height;
        for(int t = 0; t < total; t++) {
            int i = t % width;
            int j = t / (width * depth);
            int k = (t / width) % depth;

            BlockPos blockPos = startPos.add(i, j, k);
            int x = ChunkSectionPos.getSectionCoord(blockPos.getX());
            int z = ChunkSectionPos.getSectionCoord(blockPos.getZ());
            if(x != chunkPos.x || z != chunkPos.z)
                continue;

            for(OreFeatureConfig.Target target : config.targets()) {
                if (!shouldPlace(world.getBlockState(blockPos), target, context.getRandom())) continue;
                world.setBlockState(blockPos, target.state, Block.NOTIFY_ALL);
                setBlocks = true;
            }
        }

        return setBlocks;
    }

    private boolean shouldPlace(BlockState state, OreFeatureConfig.Target target, Random random) {
        return target.target.test(state, random);
    }

}
