package net.thesquire.backroomsmod.mixin;

import net.minecraft.server.integrated.IntegratedServerLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This mixin disables the experimental world settings warning.
 * Note that I did not write this mixin, I borrowed it from
 * <a href="https://github.com/rdvdev2/DisableCustomWorldsAdvice/blob/1.20.4/src/main/java/com/rdvdev2/disablecustomworldsadvice/mixin/MixinIntegratedServerLoader.java">DisableCustomWorldsAdvice</a> on GitHub.
 * */

@Mixin(IntegratedServerLoader.class)
public abstract class IntegratedServerLoaderMixin {

    // Set canShowBackupPrompt = false
    @ModifyVariable(
            method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lcom/mojang/serialization/Dynamic;ZZLjava/lang/Runnable;)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 4
    )
    private boolean removeAdviceOnLoad(boolean original) {
        return false;
    }

    // Set bypassWarnings = true
    @ModifyVariable(
            method = "tryLoad",
            at = @At("HEAD"),
            argsOnly = true,
            index = 4
    )
    private static boolean removeAdviceOnCreation(boolean original) {
        return true;
    }

}