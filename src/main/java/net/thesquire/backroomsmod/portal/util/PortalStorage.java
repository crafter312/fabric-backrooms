package net.thesquire.backroomsmod.portal.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.util.ModUtils;
import org.apache.commons.lang3.Validate;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class PortalStorage extends PersistentState {

    public final WeakReference<ServerWorld> world;

    private final ConcurrentHashMap<Vec3d, Stack<BlockPos>> level0DestPortals = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BlockPos, MagneticDistortionSystemControlComputerBlockEntity> portalControlComputers = new ConcurrentHashMap<>();

    public static PortalStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                new PersistentState.Type<>(
                        () -> {
                            BackroomsMod.LOGGER.info("Level 0 portal storage initialized " + world.getRegistryKey().getValue());
                            return new PortalStorage(world);
                        },
                        (nbt) -> {
                            PortalStorage portalStorage = new PortalStorage(world);
                            portalStorage.fromNbt(nbt);
                            return portalStorage;
                        },
                        null
                ),
                "level_0_portals"
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PortalStorage(ServerWorld w) {
        world = new WeakReference<>(w);
    }

    public void fromNbt(NbtCompound nbt) {
        ServerWorld currWorld = world.get();
        Validate.notNull(currWorld);

        NbtList portals;
        try {
            portals = Objects.requireNonNull((NbtList) nbt.get("level0DestPortals"));
        }
        catch (NullPointerException err) {
            BackroomsMod.LOGGER.error("Error loading level 0 portals");
            return;
        }

        for(int i = 0; i < portals.size(); i++) {
            NbtCompound portal = portals.getCompound(i);
            this.initDestPortal(ModUtils.getVec3D(portal, "destPos"), BlockPos.fromLong(portal.getLong("computerPos")));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList portals = new NbtList();

        level0DestPortals.keys().asIterator().forEachRemaining(destPos ->
                level0DestPortals.get(destPos).forEach(computerPos -> {
            NbtCompound portal = new NbtCompound();
            ModUtils.putVec3D(portal, "destPos", destPos);
            portal.putLong("computerPos", computerPos.asLong());
            portals.add(portal);
        }));

        nbt.put("level0DestPortals", portals);
        return nbt;
    }

    // This method is only used when initializing a PortalStorage object from existing NBT data.
    // In this case, there is no need to kill the previous destination portal since we are not
    // adding any new portals.
    private void initDestPortal(Vec3d destPos, BlockPos computerPos) {
        if (!level0DestPortals.containsKey(destPos))
            level0DestPortals.put(destPos, new Stack<>());
        level0DestPortals.get(destPos).push(computerPos);
    }

    public void addDestPortal(Vec3d destPos, BlockPos computerPos) {
        if (!level0DestPortals.containsKey(destPos))
            level0DestPortals.put(destPos, new Stack<>());
        else
            portalControlComputers.get(level0DestPortals.get(destPos).peek()).killDestPortal();

        level0DestPortals.get(destPos).push(computerPos);
    }

    public void removeDestPortal(Vec3d destPos, BlockPos computerPos) {
        BlockPos oldHeadPos = level0DestPortals.get(destPos).peek();

        level0DestPortals.get(destPos).removeElement(computerPos);
        if(level0DestPortals.get(destPos).isEmpty())
            level0DestPortals.remove(destPos);
        else if(computerPos.equals(oldHeadPos))
            portalControlComputers.get(level0DestPortals.get(destPos).peek()).spawnDestPortal();
    }

    public void addPortalComputer(BlockPos computerPos, MagneticDistortionSystemControlComputerBlockEntity computer) {
        portalControlComputers.put(computerPos, computer);
    }

    public void removePortalComputer(BlockPos computerPos, MagneticDistortionSystemControlComputerBlockEntity computer) {
        portalControlComputers.remove(computerPos, computer);
    }

}
