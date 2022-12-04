package net.thesquire.backroomsmod.util.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public interface IPlayerDamageCallback {

    Event<IPlayerDamageCallback> EVENT = EventFactory.createArrayBacked(IPlayerDamageCallback.class,
            (listeners) -> (source, player) -> {
                for (IPlayerDamageCallback listener : listeners) {
                    listener.interact(source, player);
                }
            });

    void interact(DamageSource source, PlayerEntity player);

}
