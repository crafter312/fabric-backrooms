package net.thesquire.backroomsmod.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.thesquire.backroomsmod.util.mixin.callback.IOnGameStateChangeCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameStateChange(Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket;)V", at = @At("HEAD"))
    public void onOnGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        IOnGameStateChangeCallback.EVENT.invoker().interact(packet);
    }

}
