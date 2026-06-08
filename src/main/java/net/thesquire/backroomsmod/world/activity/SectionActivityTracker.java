package net.thesquire.backroomsmod.world.activity;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class SectionActivityTracker {

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
    }

    public static void TrackSectionPlayerTickData(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ChunkSectionPos sectionPos = ChunkSectionPos.from(player.getPos());
            SectionActivityData data = ACTIVITY_MAP.computeIfAbsent(sectionPos, key -> SectionActivityData.makeDefault());
            data.incrementTicks();
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

}