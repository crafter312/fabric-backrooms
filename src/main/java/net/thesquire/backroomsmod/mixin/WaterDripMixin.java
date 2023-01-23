package net.thesquire.backroomsmod.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.thesquire.backroomsmod.util.mixin.callback.IAddParticleCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class WaterDripMixin {

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", at = @At("HEAD"))
    public void onAddParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
        IAddParticleCallback.EVENT.invoker().interact((ClientWorld) (Object) this, parameters, x, y, z);
    }

}
