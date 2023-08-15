package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;

public class ElevatorDoorBlockEntity extends BlockEntity {

    public ElevatorDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEVATOR_DOOR, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ElevatorDoorBlockEntity blockEntity) {}

}
