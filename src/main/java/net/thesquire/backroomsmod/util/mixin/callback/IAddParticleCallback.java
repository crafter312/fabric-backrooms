package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;

public interface IAddParticleCallback {

    Event<IAddParticleCallback> EVENT = EventFactory.createArrayBacked(IAddParticleCallback.class,
            (listeners) -> (world, particle, x, y, z) -> {
                for (IAddParticleCallback listener : listeners) {
                    listener.interact(world, particle, x, y, z);
                }
            });

    void interact(ClientWorld world, ParticleEffect particle, double x, double y, double z);

}
