package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.custom.AbandonedSuppliesBlock;
import net.thesquire.backroomsmod.inventory.ImplementedInventory;
import net.thesquire.backroomsmod.item.ModItems;

public class AbandonedSuppliesBlockEntity extends BlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public AbandonedSuppliesBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABANDONED_SUPPLIES, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void markDirty() {
        if (this.world == null || this.world.isClient()) return;

        // First, create default BlockState with the facing property of the previous state
        BlockState oldState = this.world.getBlockState(this.pos);
        if (!oldState.isOf(ModBlocks.ABANDONED_SUPPLIES)) return;
        BlockState newState = oldState.getBlock().getDefaultState().with(AbandonedSuppliesBlock.FACING, oldState.get(AbandonedSuppliesBlock.FACING));

        // Then, loop through the inventory and configure the block's state depending on the contents
        for (int i = 0; i < this.size(); i++) {
            ItemStack stack = this.getStack(i);
            if (stack.isEmpty()) continue;

            if (stack.isOf(ModItems.ALMOND_WATER))
                newState = newState.with(AbandonedSuppliesBlock.HAS_ALMOND_WATER, true);
            else if (stack.isOf(ModItems.SPRAY_PAINT))
                newState = newState.with(AbandonedSuppliesBlock.HAS_SPRAY_PAINT, true);
            else if (stack.isOf(Blocks.OAK_LOG.asItem()))
                newState = newState.with(AbandonedSuppliesBlock.HAS_OAK_LOGS, true);
            else if (stack.isOf(Items.STICK))
                newState = newState.with(AbandonedSuppliesBlock.HAS_STICKS, true);
            else if (stack.isOf(Items.FLINT_AND_STEEL))
                newState = newState.with(AbandonedSuppliesBlock.HAS_FLINT_AND_STEEL, true);
            else if (stack.isOf(Items.COAL))
                newState = newState.with(AbandonedSuppliesBlock.HAS_COAL, true);
        }

        // Finally, tell the world to set the new BlockState and mark the block entity as clean
        if (newState == oldState) return;
        this.world.setBlockState(this.pos, newState);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        items.clear();

        if (nbt.contains("LootTable", NbtElement.STRING_TYPE)) {
            Identifier lootTableId = new Identifier(nbt.getString("LootTable"));
            long seed = nbt.getLong("LootTableSeed");

            if (this.world instanceof ServerWorld serverWorld) {
                LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(lootTableId);
                LootContextParameterSet context = new LootContextParameterSet.Builder(serverWorld)
                        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos))
                        .build(LootContextTypes.CHEST);
                lootTable.supplyInventory(this, context, seed);
                this.markDirty();
            }
        }
        else {
            Inventories.readNbt(nbt, items);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
    }
}
