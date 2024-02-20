package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface IWallMountedBlockGetStateForNeighborUpdateCallback {

    Event<IWallMountedBlockGetStateForNeighborUpdateCallback> EVENT = EventFactory.createArrayBacked(IWallMountedBlockGetStateForNeighborUpdateCallback.class,
            (listeners) -> (state, direction, neighborState, world, pos, neighborPos, ci) -> {
                for (IWallMountedBlockGetStateForNeighborUpdateCallback listener : listeners) {
                    listener.interact(state, direction, neighborState, world, pos, neighborPos, ci);
                }
            });

    void interact(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci);

}
