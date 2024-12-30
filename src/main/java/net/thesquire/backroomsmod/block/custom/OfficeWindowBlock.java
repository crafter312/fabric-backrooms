package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.entity.OfficeWindowBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class OfficeWindowBlock extends FramedWindowBlock implements BlockEntityProvider {

    public OfficeWindowBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OfficeWindowBlockEntity(pos, state);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Optional<OfficeWindowBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.OFFICE_WINDOW);
        optional.ifPresent(OfficeWindowBlockEntity::onBreak);

        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient()) return;

        Optional<OfficeWindowBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.OFFICE_WINDOW);
        optional.ifPresent(blockEntity -> blockEntity.initPortal((ServerWorld) world, state));
    }
}
