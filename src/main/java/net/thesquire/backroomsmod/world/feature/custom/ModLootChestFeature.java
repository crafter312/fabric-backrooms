package net.thesquire.backroomsmod.world.feature.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.thesquire.backroomsmod.block.entity.AbandonedSuppliesBlockEntity;

public class ModLootChestFeature extends Feature<ModLootChestFeatureConfig> {

    public static void setupAndFillInventory(StructureWorldAccess world, Random random, BlockPos origin, Identifier lootTableKey) {
        BlockEntity blockEntity = world.getBlockEntity(origin);
        if (blockEntity == null) return;
        NbtCompound nbt = blockEntity.createNbt();

        // Block entity can store loot table, fill normally
        if (nbt.contains("LootTable", NbtElement.STRING_TYPE))
            LootableInventory.setLootTable(world, random, origin, lootTableKey);

        // Block entity cannot store loot table, fill inventory immediately from loot table
        else {
            if (blockEntity instanceof Inventory inventory) {
                MinecraftServer server = world.getServer();
                if (server != null) {
                    LootTable lootTable = server.getLootManager().getLootTable(lootTableKey);
                    LootContextParameterSet parameterSet = new LootContextParameterSet.Builder(world.toServerWorld())
                            .add(LootContextParameters.ORIGIN, origin.toCenterPos())
                            .build(LootContextTypes.CHEST);
                    inventory.clear();
                    lootTable.supplyInventory(inventory, parameterSet, random.nextLong());

                    // My special AbandonedSuppliesBlockEntity class requires that the block state be
                    // updated here to reflect the inventory contents. Otherwise, the rest of the surrounding
                    // code block should work for everything else (not that this is a common case to begin
                    // with).
                    if (blockEntity instanceof AbandonedSuppliesBlockEntity abandonedSuppliesBlockEntity)
                        abandonedSuppliesBlockEntity.updateBlockState(world);

                    inventory.markDirty();
                }
            }
        }
    }

    ////////////////////////////////////////////

    public ModLootChestFeature(Codec<ModLootChestFeatureConfig> configCodec) { super(configCodec); }

    @Override
    public boolean generate(FeatureContext<ModLootChestFeatureConfig> context) {
        Random random = context.getRandom();
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        Identifier lootTableKey = context.getConfig().lootTableKey();
        BlockState lootContainer = context.getConfig().lootableContainer();
        boolean preferFacingOutwards = context.getConfig().preferFacingOutwards();

        DirectionProperty property = lootContainer.contains(Properties.FACING) ? Properties.FACING : null;
        property = lootContainer.contains(Properties.HORIZONTAL_FACING) ? Properties.HORIZONTAL_FACING : property;

        // If preferFacingOutwards == true, then identify which direction is facing away from wall (if next to wall)
        // This also requires that the block have either FACING or HORIZONTAL_FACING properties
        if (property != null && preferFacingOutwards) {
            BlockState testBlock1 = world.getBlockState(origin.offset(Direction.NORTH));
            BlockState testBlock2 = world.getBlockState(origin.offset(Direction.EAST));
            BlockState testBlock3 = world.getBlockState(origin.offset(Direction.SOUTH));
            BlockState testBlock4 = world.getBlockState(origin.offset(Direction.WEST));
            int result = (testBlock1.isSideSolidFullSquare(world, origin.offset(Direction.NORTH), Direction.SOUTH) ? 1 : 0) |
                    ((testBlock2.isSideSolidFullSquare(world, origin.offset(Direction.EAST), Direction.WEST) ? 1 : 0) << 1) |
                    ((testBlock3.isSideSolidFullSquare(world, origin.offset(Direction.SOUTH), Direction.NORTH) ? 1 : 0) << 2) |
                    ((testBlock4.isSideSolidFullSquare(world, origin.offset(Direction.WEST), Direction.EAST) ? 1 : 0) << 3);
            if ((result & 0b1010) == 0b1000)
                lootContainer = lootContainer.with(property, Direction.SOUTH);
            else if ((result & 0b1010) == 0b0010)
                lootContainer = lootContainer.with(property, Direction.NORTH);
            else if ((result & 0b0101) == 0b0100)
                lootContainer = lootContainer.with(property, Direction.WEST);
            else if ((result & 0b0101) == 0b0001)
                lootContainer = lootContainer.with(property, Direction.EAST);
        }

        // If lootContainer has facing property but no facing direction preference, make random
        else if (property != null) {
            UniformIntProvider dirProvider = UniformIntProvider.create(0, 3);
            Direction direction = Direction.fromHorizontal(dirProvider.get(random));
            lootContainer = lootContainer.with(property, direction);
        }

        // Place in world and set loot table
        boolean b1 = world.setBlockState(origin, lootContainer, Block.NOTIFY_ALL);
        ModLootChestFeature.setupAndFillInventory(world, random, origin, lootTableKey);

        return b1;
    }



}
