package net.thesquire.backroomsmod.portal.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.thesquire.backroomsmod.block.entity.MagneticDistortionSystemControlComputerBlockEntity;
import net.thesquire.backroomsmod.util.ModUtils;

import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class PortalStorage extends PersistentState {

    private final ConcurrentHashMap<Vec3d, Stack<BlockPos>> level0DestPortals = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BlockPos, MagneticDistortionSystemControlComputerBlockEntity> portalControlComputers = new ConcurrentHashMap<>();

    public static PortalStorage createFromNBT(NbtCompound nbt) {
        PortalStorage portalStorage = new PortalStorage();
        NbtList portals = Objects.requireNonNull((NbtList) nbt.get("level0DestPortals"));

        for(int i = 0; i < portals.size(); i++) {
            NbtCompound portal = portals.getCompound(i);
            portalStorage.initDestPortal(ModUtils.getVec3D(portal, "destPos"), BlockPos.fromLong(portal.getLong("computerPos")));
        }
        return portalStorage;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Override
    public boolean isDirty() {
        return true;
    }

}
