package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface IDoorBlockNeighborUpdateCallback {

    Event<IDoorBlockNeighborUpdateCallback> EVENT = EventFactory.createArrayBacked(IDoorBlockNeighborUpdateCallback.class,
            (listeners) -> (state, world, pos, sourceBlock, sourcePos, notify, ci) -> {
                for (IDoorBlockNeighborUpdateCallback listener : listeners) {
                    listener.interact(state, world, pos, sourceBlock, sourcePos, notify, ci);
                }
            });

    void interact(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci);

}
