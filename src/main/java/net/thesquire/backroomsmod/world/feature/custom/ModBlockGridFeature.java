package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.custom.FluorescentLightBlock;

public class ModBlockGridFeature extends Feature<ModBlockGridFeatureConfig> {

    public ModBlockGridFeature(Codec<ModBlockGridFeatureConfig> configCodec) { super(configCodec); }

    @Override
    public boolean generate(FeatureContext<ModBlockGridFeatureConfig> context) {
        BlockPos pos = context.getOrigin();
        ModBlockGridFeatureConfig config = context.getConfig();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();

        int width = config.width().get(random);
        int depth = config.depth().get(random);
        float chance = config.chance();
        int spacing = config.spacing().get(random) + 1;
        BlockPos startPos = new BlockPos(
                pos.getX() - width,
                pos.getY(),
                pos.getZ() - depth);

        // limit width and depth to chunk size
        int w = Math.min(((width - 1) * (spacing + 1)) + 1, 16);
        int d = Math.min(((depth - 1) * (spacing + 1)) + 1, 16);
        int total = w * d;
        for(int t = 0; t < total; t++) {
            int i = t % w;
            int j = t / w;
            BlockPos blockPos = startPos.add(i, 0, j);
            if ((i % spacing) != 0 || (j % spacing) != 0) {
                if (world.getBlockState(blockPos).contains(ModBlockProperties.HAS_LIGHT)) {
                    world.setBlockState(blockPos, ModBlocks.CEILING_TILE.getDefaultState().with(ModBlockProperties.HAS_LIGHT, true),
                            Block.NOTIFY_ALL);
                }
                continue;
            }
            for(OreFeatureConfig.Target target : config.targets()) {
                BlockState currentState = world.getBlockState(blockPos);
                if (!shouldPlace(currentState, target, random)) continue;
                if (target.state.contains(FluorescentLightBlock.FLICKERING)) {
                    world.setBlockState(blockPos, target.state.with(FluorescentLightBlock.FLICKERING, random.nextFloat() < chance),
                            Block.NOTIFY_LISTENERS);
                } else {
                    world.setBlockState(blockPos, target.state, Block.NOTIFY_LISTENERS);
                }
            }
        }

        return true;
    }

    private boolean shouldPlace(BlockState state, OreFeatureConfig.Target target, Random random) {
        boolean isCorrectBlock = target.target.test(state, random);
        boolean notHasLight = true;
        if (state.contains(ModBlockProperties.HAS_LIGHT)) {
            notHasLight = !state.get(ModBlockProperties.HAS_LIGHT);
        }
        return isCorrectBlock && notHasLight;
    }

}
