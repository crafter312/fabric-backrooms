package net.thesquire.backroomsmod.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.util.callback.IPlayerDamageCallback;

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
                    ServerWorld destServerWorld = serverWorld.getServer().getWorld(ModDimensionKeys.LEVEL_0);
                    BlockPos destPos = ModUtils.findSuitableTeleportDestination(destServerWorld, player.getBlockPos());
                    TeleportTarget targ = new TeleportTarget(new Vec3d(destPos.getX(), 20, destPos.getZ()), player.getVelocity(), player.getYaw(), player.getPitch());
                    FabricDimensions.teleport(player, destServerWorld, targ);
                }
            }
        });

    }

}
