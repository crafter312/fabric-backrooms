package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ModLootChestFeature extends Feature<ModLootChestFeatureConfig> {

    public ModLootChestFeature(Codec<ModLootChestFeatureConfig> configCodec) { super(configCodec); }

    @Override
    public boolean generate(FeatureContext<ModLootChestFeatureConfig> context) {
        Random random = context.getRandom();
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        Identifier lootTableKey = context.getConfig().lootTableKey();
        BlockState lootContainer = Blocks.BARREL.getDefaultState().with(Properties.FACING, Direction.UP);
        boolean b1 = world.setBlockState(origin, lootContainer, Block.NOTIFY_ALL);
        LootableInventory.setLootTable(world, random, origin, lootTableKey);

        return b1;
    }

}
