package net.thesquire.backroomsmod.util.mixin;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.sound.ModSounds;
import net.thesquire.backroomsmod.util.ModUtils;
import net.thesquire.backroomsmod.util.mixin.callback.IAddParticleCallback;
import net.thesquire.backroomsmod.util.mixin.callback.IPlayerDamageCallback;

/**
 * For information on how to properly set up a mixin callback,
 * see <a href="https://fabricmc.net/wiki/tutorial:events">this link</a>.
 */
public class MixinCallbacks {

    public static void registerCallbacks() {
        registerPlayerEntityCallbacks();
    }

    /********************************************************/

    private static void registerPlayerEntityCallbacks() {

        // teleports player from overworld to backrooms level 0 upon taking suffocation or void damage
        IPlayerDamageCallback.EVENT.register((source, player) -> {
            if (player.world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) player.world;
                if (serverWorld.getRegistryKey() != World.OVERWORLD) return;
                if (source.equals(DamageSource.IN_WALL) || source.isOutOfWorld()) {
                    if (Random.create().nextFloat() > 0.01) return;
                    ServerWorld destServerWorld = serverWorld.getServer().getWorld(ModDimensionKeys.LEVEL_0);
                    BlockPos destPos = ModUtils.findSuitableTeleportDestination(destServerWorld, player.getBlockPos());
                    TeleportTarget targ = new TeleportTarget(new Vec3d(destPos.getX(), 20, destPos.getZ()), player.getVelocity(), player.getYaw(), player.getPitch());
                    FabricDimensions.teleport(player, destServerWorld, targ);
                }
            }
        });

        // plays water drip sound for water splash particles in level 1 dimension
        IAddParticleCallback.EVENT.register((world, particle, x, y, z) -> {
            if(!world.getRegistryKey().equals(ModDimensionKeys.LEVEL_1) || !particle.equals(ParticleTypes.SPLASH)) return;
            world.playSound(x, y, z, ModSounds.LEVEL_1_DRIP, SoundCategory.AMBIENT, 1f, 1f, true);
        });

    }

}
