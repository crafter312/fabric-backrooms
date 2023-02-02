package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ModThinWallFeature extends Feature<ModThinWallFeatureConfig> {

    private static final Direction[] directions = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

    public ModThinWallFeature(Codec<ModThinWallFeatureConfig> configCodec) { super(configCodec); }

    /**
     * This feature generates a one block wide wall whose length and height can be specified.
     * The wall can either be straight, or can have any number of segments which
     * could be at 90 degree angles to each other (not straight).
     */

    @Override
    public boolean generate(FeatureContext<ModThinWallFeatureConfig> context) {
        ModThinWallFeatureConfig config = context.getConfig();
        Random random = context.getRandom();
        if (config.straight()) return generateStraightWall(context.getOrigin(), context.getWorld(), config, random,
                config.segments().get(random) * config.lengths().get(random), config.height().get(random), getRandomDirection(random));
        else return generateCrookedWall(context.getOrigin(), context.getWorld(), config, random);
    }

    private boolean generateStraightWall(BlockPos origin, StructureWorldAccess world, ModThinWallFeatureConfig config, Random random,
                                         int length, int height, Vec3i dir) {
        boolean setBlocks = false;

        // limit wall length to chunk size
        length = Math.min(length, 16);
        int total = length * height;

        for (int t = 0; t < total; t++) {
            int h = t % height;
            int l = t / height;

            BlockPos pos = origin.add(dir.multiply(l)).add(0,h,0);

            boolean notTarget = true;
            for (OreFeatureConfig.Target target : config.targets()) {
                if (!shouldPlace(world.getBlockState(pos), target, random)) continue;
                world.setBlockState(pos, target.state, Block.NOTIFY_ALL);
                notTarget = false;
                setBlocks = true;
            }
            if (notTarget) return setBlocks;
        }
        return setBlocks;
    }

    private boolean generateCrookedWall(BlockPos origin, StructureWorldAccess world, ModThinWallFeatureConfig config, Random random) {
        int segments = config.segments().get(random);
        Vec3i dir = getRandomDirection(random);

        boolean setBlocks = false;
        BlockPos pos = origin;
        Vec3i oldDir = dir;
        int[] dirLengths = {0, 0, 0, 0};
        for (int i = 0; i < segments; i++) {
            int length = config.lengths().get(random);
            int height = config.height().get(random);

            int index = 0;
            while (dir.equals(oldDir)) {
                index = (int) (random.nextFloat() * directions.length);
                dir = directions[index].getVector();
            }
            dirLengths[index] += length;

            // limit wall length in any direction to chunk size
            if(dirLengths[index] > 16) return setBlocks;

            setBlocks = generateStraightWall(pos, world, config, random, length, height, dir);
            pos = pos.add(dir.multiply(length));
            oldDir = dir;
        }

        return setBlocks;
    }

    private Vec3i getRandomDirection(Random random) {
        int index = (int) (random.nextFloat() * directions.length);
        return directions[index].getVector();
    }

    private boolean shouldPlace(BlockState state, OreFeatureConfig.Target target, Random random) {
        return target.target.test(state, random);
    }

}
