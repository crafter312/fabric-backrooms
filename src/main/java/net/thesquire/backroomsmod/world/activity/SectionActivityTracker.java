package net.thesquire.backroomsmod.world.activity;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.PersistentState;
import net.thesquire.backroomsmod.BackroomsMod;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SectionActivityTracker extends PersistentState {

    public static final PersistentState.Type<SectionActivityTracker> TYPE = new PersistentState.Type<>(
            SectionActivityTracker::new,         // Creates a blank state if data file is missing
            SectionActivityTracker::readNbt,     // Clears map and reads existing NBT file
            DataFixTypes.LEVEL                   // Tracks what kind of data fixer to use (if any)
    );

    static public SectionActivityTracker readNbt(NbtCompound nbt) {
        if (!nbt.contains("chunkSectionActivity")) return new SectionActivityTracker();
        NbtList nbtList = nbt.getList("chunkSectionActivity", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : nbtList) {
            if (element instanceof NbtCompound sectionNbt) {
                ChunkSectionPos sectionPos = SectionActivityTracker.ChunkSectionPosFromNbt(sectionNbt);
                if (sectionPos == null) continue;
                SectionActivityTracker.SectionActivityData data = SectionActivityTracker.SectionActivityData.makeDefault();
                data.readNbt(sectionNbt);
                SectionActivityTracker.ACTIVITY_MAP.put(sectionPos, data);
            }
        }
        return new SectionActivityTracker();
    }

    public static class SectionActivityData {

        public static SectionActivityData makeDefault() {
            return new SectionActivityData(0, 0);
        }

        /////////////////////////////////////////////////////////////////////////////////////

        private int blocksPlaced;
        private int ticksSpent;

        public SectionActivityData(int blocksPlaced, int ticksSpent) {
            this.blocksPlaced = blocksPlaced;
            this.ticksSpent = ticksSpent;
        }

        public void incrementBlocks() { this.blocksPlaced++; }

        public void incrementTicks() { this.ticksSpent++; }

        public int getBlocksPlaced() { return this.blocksPlaced; }

        public int getTicksSpent() { return this.ticksSpent; }

        public void readNbt(NbtCompound nbt) {
            if (nbt.contains("blocksPlaced")) this.blocksPlaced = nbt.getInt("blocksPlaced");
            if (nbt.contains("ticksSpent")) this.ticksSpent = nbt.getInt("ticksSpent");
        }

        public void writeNbt(NbtCompound nbt) {
            nbt.putInt("blocksPlaced", this.blocksPlaced);
            nbt.putInt("ticksSpent", this.ticksSpent);
        }

    }

    public static final ConcurrentHashMap<ChunkSectionPos, SectionActivityData> ACTIVITY_MAP = new ConcurrentHashMap<>();

    public static void TrackSectionBlocksPlacedData(ItemPlacementContext context) {
        ChunkSectionPos sectionPos = ChunkSectionPos.from(context.getBlockPos());
        SectionActivityData data = ACTIVITY_MAP.computeIfAbsent(sectionPos, key -> SectionActivityData.makeDefault());
        data.incrementBlocks();
        BackroomsMod.activityTracker.markDirty();
        if ((data.getBlocksPlaced() % 5) == 0)
            BackroomsMod.LOGGER.info("[PersistentState]: {} blocks placed in {}", data.getBlocksPlaced(), sectionPos);
    }

    public static void TrackSectionPlayerTickData(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ChunkSectionPos sectionPos = ChunkSectionPos.from(player.getPos());
            SectionActivityData data = ACTIVITY_MAP.computeIfAbsent(sectionPos, key -> SectionActivityData.makeDefault());
            data.incrementTicks();
            BackroomsMod.activityTracker.markDirty();
            if ((data.getTicksSpent() % 100) == 0)
                BackroomsMod.LOGGER.info("[PersistentState]: {} ticks spent by all players in {}", data.getTicksSpent(), sectionPos);
        }
    }

    @Nullable
    public static ChunkSectionPos ChunkSectionPosFromNbt(NbtCompound nbt) {
        if (!nbt.contains("sectionPos")) return null;
        int[] arr = nbt.getIntArray("sectionPos");
        if (arr.length != 3) return null;
        return ChunkSectionPos.from(arr[0], arr[1], arr[2]);
    }

    public static void ChunkSectionPosToNbt(ChunkSectionPos sectionPos, NbtCompound nbt) {
        int[] arr = new int[]{sectionPos.getX(), sectionPos.getY(), sectionPos.getZ()};
        nbt.putIntArray("sectionPos", arr);
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (Map.Entry<ChunkSectionPos, SectionActivityData> set : ACTIVITY_MAP.entrySet()) {
            NbtCompound sectionNbt = new NbtCompound();
            ChunkSectionPosToNbt(set.getKey(), sectionNbt);
            set.getValue().writeNbt(sectionNbt);
            nbtList.add(sectionNbt);
        }
        nbt.put("chunkSectionActivity", nbtList);
        return nbt;
    }

}