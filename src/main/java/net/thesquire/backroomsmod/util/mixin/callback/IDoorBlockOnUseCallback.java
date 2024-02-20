package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface IDoorBlockOnUseCallback {

    Event<IDoorBlockOnUseCallback> EVENT = EventFactory.createArrayBacked(IDoorBlockOnUseCallback.class,
            (listeners) -> (state, world, pos, player, hand, hit, ci) -> {
                for (IDoorBlockOnUseCallback listener : listeners) {
                    listener.interact(state, world, pos, player, hand, hit, ci);
                }
            });

    void interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ci);

}
