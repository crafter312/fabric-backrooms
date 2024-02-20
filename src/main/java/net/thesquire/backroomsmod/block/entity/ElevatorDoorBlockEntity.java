package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;

public class ElevatorDoorBlockEntity extends BlockEntity {

    private final ElevatorDoorAnimator doorAnimator = new ElevatorDoorAnimator(0.04f);

    public ElevatorDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEVATOR_DOOR, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ElevatorDoorBlockEntity blockEntity) {
        blockEntity.doorAnimator.step();
    }

    public float getAnimationProgress(float tickDelta) {
        return this.doorAnimator.getProgress(tickDelta);
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.doorAnimator.setOpen(data > 0);
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

}
