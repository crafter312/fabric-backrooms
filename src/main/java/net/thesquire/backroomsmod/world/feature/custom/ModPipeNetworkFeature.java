package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.*;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.thesquire.backroomsmod.block.custom.PipeBlock;

public class ModPipeNetworkFeature extends Feature<ModPipeNetworkFeatureConfig> {

    public ModPipeNetworkFeature(Codec<ModPipeNetworkFeatureConfig> configCodec) { super(configCodec); }

    @Override
    public boolean generate(FeatureContext<ModPipeNetworkFeatureConfig> context) {
        ModPipeNetworkFeatureConfig config = context.getConfig();
        BlockPredicate target = config.targetPredicate();
        BlockPredicate stopCondition = config.stopPlacementPredicate();
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        if (!target.test(world, origin) || stopCondition.test(world, origin)) return false;

        BlockState state = config.state();
        Random random = context.getRandom();
        float branchProbability = MathHelper.clamp(config.branchProbability(), 0.0f, 1.0f);
        IntProvider branchSpacing = config.branchSpacing();
        ChunkPos chunkPos = world.getChunk(origin).getPos();
        Direction dir = findAdjacentBlock(world, origin, random);
        Direction dir1 = dir.rotateYCounterclockwise();

        generateBranch(world, chunkPos, origin.offset(dir1), dir1, branchProbability, branchSpacing, state, target, stopCondition, random);
        generateBranch(world, chunkPos, origin, dir.rotateYClockwise(), branchProbability, branchSpacing, state, target, stopCondition, random);
        return true;
    }

    private void generateBranch(StructureWorldAccess world, ChunkPos chunkPos, BlockPos branchPos, Direction branchDirection, float branchProbability,
                                IntProvider branchSpacing, BlockState state, BlockPredicate target, BlockPredicate stopCondition, Random random) {
        int leftSpacing = branchSpacing.get(random);
        int rightSpacing = branchSpacing.get(random);
        int leftCount = 0, rightCount = 0;
        BlockPos.Mutable mutablePos = branchPos.mutableCopy();
        while (ChunkSectionPos.getSectionCoord(mutablePos.getX()) == chunkPos.x &&
                ChunkSectionPos.getSectionCoord(mutablePos.getZ()) == chunkPos.z &&
                target.test(world, mutablePos) && !stopCondition.test(world, mutablePos)) {

            Direction left = branchDirection.rotateYCounterclockwise();
            Direction right = branchDirection.rotateYClockwise();
            BlockPos leftPos = mutablePos.offset(left);
            BlockPos rightPos = mutablePos.offset(right);
            BlockPos nextPos = mutablePos.offset(branchDirection);
            if (state.getBlock() instanceof PipeBlock) {
                PipeBlock pipeBlock = (PipeBlock)state.getBlock();
                BlockPos previousPos = mutablePos.offset(branchDirection.getOpposite());
                world.setBlockState(mutablePos, pipeBlock.withConnectionProperties(world, mutablePos), Block.NOTIFY_ALL);
                world.setBlockState(previousPos, pipeBlock.withConnectionProperties(world, previousPos), Block.NOTIFY_ALL);

                if(world.getBlockState(leftPos).getBlock() instanceof PipeBlock)
                    world.setBlockState(leftPos, pipeBlock.withConnectionProperties(world, leftPos), Block.NOTIFY_ALL);
                if(world.getBlockState(rightPos).getBlock() instanceof PipeBlock)
                    world.setBlockState(rightPos, pipeBlock.withConnectionProperties(world, rightPos), Block.NOTIFY_ALL);
                if(world.getBlockState(nextPos).getBlock() instanceof PipeBlock)
                    world.setBlockState(nextPos, pipeBlock.withConnectionProperties(world, nextPos), Block.NOTIFY_ALL);
            }
            else world.setBlockState(mutablePos, state, Block.NOTIFY_ALL);

            if (canBranch(world, state, mutablePos, left, target, leftCount, leftSpacing) && random.nextFloat() < branchProbability) {
                generateBranch(world, chunkPos, leftPos, left, branchProbability, branchSpacing, state, target, stopCondition, random);
                leftCount = 0;
            }
            else ++leftCount;
            if (canBranch(world, state, mutablePos, right, target, rightCount, rightSpacing) && random.nextFloat() < branchProbability) {
                generateBranch(world, chunkPos, rightPos, right, branchProbability, branchSpacing, state, target, stopCondition, random);
                rightCount = 0;
            }
            else ++rightCount;

            mutablePos.move(branchDirection);
        }
    }

    private Direction findAdjacentBlock(StructureWorldAccess world, BlockPos pos, Random random) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos pos2 = pos.offset(dir);
            if (world.getBlockState(pos2).isSolidBlock(world, pos)) return dir;
        }
        return Direction.fromHorizontal(random.nextBetween(0, 3));
    }

    private boolean canBranch(StructureWorldAccess world, BlockState state, BlockPos pos, Direction dir, BlockPredicate target, int count, int spacing) {
        return target.test(world, pos.offset(dir, 3)) && count >= spacing && !hasAdjacentState(world, state, pos.offset(dir), dir);
    }

    private boolean hasAdjacentState(StructureWorldAccess world, BlockState state, BlockPos pos, Direction dir) {
        return world.getBlockState(pos.offset(dir.rotateYClockwise())).isOf(state.getBlock()) ||
                world.getBlockState(pos.offset(dir.rotateYCounterclockwise())).isOf(state.getBlock());
    }

}
