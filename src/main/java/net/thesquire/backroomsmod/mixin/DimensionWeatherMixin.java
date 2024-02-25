package net.thesquire.backroomsmod.mixin;

import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.thesquire.backroomsmod.util.mixin.callback.IUnmodifiableLevelPropertiesCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UnmodifiableLevelProperties.class)
public abstract class DimensionWeatherMixin {

    @Inject(method = "isRaining()V", at = @At(value = "HEAD"), cancellable = true)
    public void onIsRaining(CallbackInfoReturnable<Boolean> ci) {
        IUnmodifiableLevelPropertiesCallback.EVENT.invoker().interact(ci);
    }

}
