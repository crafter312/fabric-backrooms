package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.thesquire.backroomsmod.block.ModBlockProperties;

public class ModWallMountableFeature extends Feature<ModWallMountableFeatureConfig> {

    public ModWallMountableFeature(Codec<ModWallMountableFeatureConfig> configCodec) { super(configCodec); }

    @Override
    public boolean generate(FeatureContext<ModWallMountableFeatureConfig> context) {
        BlockPos pos = context.getOrigin();
        ModWallMountableFeatureConfig config = context.getConfig();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        float placeChance = config.placeChance();
        float flickeringChance = config.flickeringChance();
        BlockState state = config.blockState();
        boolean isFacing = state.contains(Properties.FACING);
        boolean isHorizontalFacing = state.contains(Properties.HORIZONTAL_FACING);

        // limit search range to chunk size
        int range = Math.min(config.range().get(random), 16);

        int total = range * range;
        int i, j;
        Direction testDir;
        BlockPos blockPos, testPos;
        BlockState setState;
        boolean shouldPlace, isFlickering;
        for(int t = 0; t < total; t++) {
            i = t % range;
            j = t / range;
            blockPos = pos.add(i, 0, j);
            shouldPlace = random.nextFloat() < placeChance;
            if(!world.isAir(blockPos) || !shouldPlace) continue;

            isFlickering = random.nextFloat() < flickeringChance;
            setState = state.contains(ModBlockProperties.FLICKERING) ? state.with(ModBlockProperties.FLICKERING, isFlickering) : state;
            for(int u = 0; u < 4; u++) {
                testDir = Direction.fromHorizontal(u);
                testPos = blockPos.offset(testDir.getOpposite());

                if(world.getBlockState(testPos).isOf(state.getBlock())) break;
                else if(!world.getBlockState(testPos).isSideSolidFullSquare(world, testPos, testDir)) continue;
                else if(isHorizontalFacing)
                    setState = setState.with(Properties.HORIZONTAL_FACING, testDir);
                else if(isFacing)
                    setState = setState.with(Properties.FACING, testDir);
                world.setBlockState(blockPos, setState, Block.NOTIFY_ALL);
            }
        }

        return false;
    }

}
