package net.thesquire.backroomsmod.util.mixin.callback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;

@Environment(EnvType.CLIENT)
public interface IOnGameStateChangeCallback {

    Event<IOnGameStateChangeCallback> EVENT = EventFactory.createArrayBacked(IOnGameStateChangeCallback.class,
            (listeners) -> (packet) -> {
                for (IOnGameStateChangeCallback listener : listeners) {
                    listener.interact(packet);
                }
            });

    void interact(GameStateChangeS2CPacket packet);

}
