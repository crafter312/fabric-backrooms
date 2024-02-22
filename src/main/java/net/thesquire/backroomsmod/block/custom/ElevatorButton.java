package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.entity.ElevatorButtonBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElevatorButton extends ButtonBlock implements BlockEntityProvider {

    public ElevatorButton(Settings settings, BlockSetType blockSetType, int pressTicks) {
        super(blockSetType, pressTicks, settings);
        this.setDefaultState(this.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.WEST));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElevatorButtonBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient() ? ModBlockWithEntity.checkType(type, ModBlockEntities.ELEVATOR_BUTTON,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, state1)) : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<ElevatorButtonBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.ELEVATOR_BUTTON);
        if(world.isClient() || hand == Hand.OFF_HAND || optional.isEmpty()) return super.onUse(state, world, pos, player, hand, hit);

        ElevatorButtonBlockEntity elevatorButton = optional.get();
        if(!player.isSneaking() && !elevatorButton.hasPortal()) return super.onUse(state, world, pos, player, hand, hit);

        if(!elevatorButton.hasPortal()) {
            elevatorButton.updatePortalOrigin(state);
            elevatorButton.initPortal((ServerWorld) world, state);
            return super.onUse(state, world, pos, player, hand, hit);
        }

        if(!state.get(POWERED))
            elevatorButton.toggleElevatorState();
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Optional<ElevatorButtonBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.ELEVATOR_BUTTON);
        optional.ifPresent(ElevatorButtonBlockEntity::onBreak);

        return super.onBreak(world, pos, state, player);
    }
}
