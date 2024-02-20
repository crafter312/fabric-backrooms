package net.thesquire.backroomsmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.thesquire.backroomsmod.block.custom.ElevatorDoor;
import net.thesquire.backroomsmod.util.mixin.callback.IDoorBlockGetStateForNeighborUpdateCallback;
import net.thesquire.backroomsmod.util.mixin.callback.IDoorBlockOnUseCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {

    @Inject(method = "onUse", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void onOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ci) {
        if(state.getBlock() instanceof ElevatorDoor)
            IDoorBlockOnUseCallback.EVENT.invoker().interact(state, world, pos, player, hand, hit, ci);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At(value = "RETURN", ordinal = 0))
    public void onGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
        if(state.getBlock() instanceof ElevatorDoor)
            IDoorBlockGetStateForNeighborUpdateCallback.EVENT.invoker().interact(state, direction, neighborState, world, pos, neighborPos, ci);
    }

}
