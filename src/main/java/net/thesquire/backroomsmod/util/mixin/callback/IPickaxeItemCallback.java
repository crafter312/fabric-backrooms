package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface IPickaxeItemCallback {

    Event<IPickaxeItemCallback> EVENT = EventFactory.createArrayBacked(IPickaxeItemCallback.class,
            (listeners) -> (context, ci) -> {
                for (IPickaxeItemCallback listener : listeners) {
                    listener.interact(context, ci);
                }
            });

    void interact(ItemUsageContext context, CallbackInfoReturnable<ActionResult> ci);

}
