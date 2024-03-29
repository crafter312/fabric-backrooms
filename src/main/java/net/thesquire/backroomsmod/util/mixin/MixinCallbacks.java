package net.thesquire.backroomsmod.util.mixin;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.custom.ElevatorDoor;
import net.thesquire.backroomsmod.block.custom.PaintedWarehouseConcreteBlock;
import net.thesquire.backroomsmod.block.entity.ElevatorButtonBlockEntity;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.sound.ModSounds;
import net.thesquire.backroomsmod.util.ModUtils;
import net.thesquire.backroomsmod.util.mixin.callback.*;

import java.util.Optional;

/**
 * For information on how to properly set up a mixin callback,
 * see <a href="https://fabricmc.net/wiki/tutorial:events">this link</a>.
 */
public class MixinCallbacks {

    public static void registerCallbacks() {
        BackroomsMod.LOGGER.info("Registering mixin callbacks for " + BackroomsMod.MOD_ID);

        // teleports player from overworld to backrooms level 0 upon taking suffocation or void damage
        IPlayerDamageCallback.EVENT.register((source, player) -> {
            if (player.getWorld() instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) player.getWorld();
                if (serverWorld.getRegistryKey() != World.OVERWORLD) return;
                if (source.isOf(DamageTypes.IN_WALL) || source.isOf(DamageTypes.OUT_OF_WORLD)) {
                    if (Random.create().nextFloat() > 0.01) return;
                    ServerWorld destServerWorld = serverWorld.getServer().getWorld(ModDimensionKeys.LEVEL_0);
                    BlockPos destPos = ModUtils.findSuitableTeleportDestination(destServerWorld, player.getBlockPos());
                    TeleportTarget targ = new TeleportTarget(new Vec3d(destPos.getX(), 20, destPos.getZ()), player.getVelocity(), player.getYaw(), player.getPitch());
                    FabricDimensions.teleport(player, destServerWorld, targ);
                }
            }
        });

        // turns painted warehouse concrete to rebar when right click with pickaxe
        IPickaxeItemCallback.EVENT.register((context, ci) -> {
            World world = context.getWorld();
            BlockPos blockPos = context.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            ItemStack itemStack = context.getStack();
            if (!blockState.isOf(ModBlocks.PAINTED_WAREHOUSE_CONCRETE)) return;

            PlayerEntity playerEntity = context.getPlayer();
            BooleanProperty face = PaintedWarehouseConcreteBlock.FACING_PROPERTIES.get(context.getSide());
            boolean chiseled = blockState.get(face);
            if (!chiseled) {
                world.playSound(context.getPlayer(), blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);

                if (!world.isClient()) {
                    BlockState blockState2 = blockState.with(face, true);
                    world.setBlockState(blockPos, blockState2);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
                    if (playerEntity != null)
                        itemStack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
                }
            }
            ci.setReturnValue(ActionResult.success(world.isClient()));
        });

        // triggers synced block event when ElevatorDoor block opens or closes
        // this in turn triggers the door animation to open or close accordingly
        IDoorBlockOnUseCallback.EVENT.register((state, world, pos, player, hand, hit, ci) -> {
            world.addSyncedBlockEvent(pos, state.getBlock(), 1, ModUtils.boolToInt(state.get(DoorBlock.OPEN), 1, 0));
            Direction facing = state.get(DoorBlock.FACING);
            BlockPos neighborPos = pos.offset(
                    state.get(DoorBlock.HINGE).equals(DoorHinge.LEFT)
                            ? facing.rotateYClockwise()
                            : facing.rotateYCounterclockwise());
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(state.getBlock())
                    && neighborState.get(DoorBlock.FACING).equals(state.get(DoorBlock.FACING))
                    && neighborState.get(DoorBlock.HALF).equals(state.get(DoorBlock.HALF))
                    && !neighborState.get(DoorBlock.HINGE).equals(state.get(DoorBlock.HINGE))
                    && !neighborState.get(DoorBlock.OPEN).equals(state.get(DoorBlock.OPEN))
                    && !neighborState.get(DoorBlock.POWERED))
                ((ElevatorDoor) neighborState.getBlock()).setOpen(player, world, neighborState, neighborPos, !neighborState.get(DoorBlock.OPEN));
        });
        IDoorBlockGetStateForNeighborUpdateCallback.EVENT.register((state, direction, neighborState, world, pos, neighborPos, ci) -> {
            if (!world.isClient()) {
                ServerWorld serverWorld = (ServerWorld) world;
                serverWorld.addSyncedBlockEvent(pos, state.getBlock(), 1, ModUtils.boolToInt(neighborState.get(DoorBlock.OPEN), 1, 0));
            }
        });

        // this removes any relevant portals if the block an elevator button is placed on is broken
        IWallMountedBlockGetStateForNeighborUpdateCallback.EVENT.register((state, direction, neighborState, world, pos, neighborPos, ci) -> {
            if (!world.isClient()) {
                Optional<ElevatorButtonBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.ELEVATOR_BUTTON);
                optional.ifPresent(ElevatorButtonBlockEntity::onBreak);
            }
        });
    }

    public static void registerClientCallbacks() {
        BackroomsMod.LOGGER.info("Registering client mixin callbacks for " + BackroomsMod.MOD_ID);

        // plays water drip sound for water splash particles in level 1 dimension
        IAddParticleCallback.EVENT.register((world, particle, x, y, z) -> {
            if (!world.getRegistryKey().equals(ModDimensionKeys.LEVEL_1) || !particle.equals(ParticleTypes.SPLASH)) return;
            world.playSound(x, y, z, ModSounds.LEVEL_1_DRIP, SoundCategory.AMBIENT, 0.8f, 1f, true);
        });

    }

}
