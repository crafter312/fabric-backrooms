package net.thesquire.backroomsmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.thesquire.backroomsmod.block.custom.ElevatorButton;
import net.thesquire.backroomsmod.util.mixin.callback.IWallMountedBlockGetStateForNeighborUpdateCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallMountedBlock.class)
public abstract class WallMountedBlockMixin {

    @Inject(method = "getStateForNeighborUpdate", at = @At(value = "RETURN", ordinal = 0))
    public void onGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
        if (state.getBlock() instanceof ElevatorButton)
            IWallMountedBlockGetStateForNeighborUpdateCallback.EVENT.invoker().interact(state, direction, neighborState, world, pos, neighborPos, ci);
    }

}
