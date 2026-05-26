package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.OFFICE_WINDOW, OfficeWindowBlockEntity::staticTick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>)ticker : null;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Optional<OfficeWindowBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.OFFICE_WINDOW);
        optional.ifPresent(OfficeWindowBlockEntity::onBreak);

        return super.onBreak(world, pos, state, player);
    }

}