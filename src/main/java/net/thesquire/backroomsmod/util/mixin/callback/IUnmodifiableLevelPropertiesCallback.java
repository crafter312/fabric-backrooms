package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface IUnmodifiableLevelPropertiesCallback {

    Event<IUnmodifiableLevelPropertiesCallback> EVENT = EventFactory.createArrayBacked(IUnmodifiableLevelPropertiesCallback.class,
            (listeners) -> (ci) -> {
                for (IUnmodifiableLevelPropertiesCallback listener : listeners) {
                    listener.interact(ci);
                }
            });

    void interact(CallbackInfoReturnable<Boolean> ci);

}
