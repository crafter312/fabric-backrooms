package net.thesquire.backroomsmod.world.activity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.PersistentState;

import java.util.Map;

public class SectionActivitySaveData extends PersistentState {

    static public void readNbt(NbtCompound nbt) {
        if (!nbt.contains("chunkSectionActivity")) return;
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
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (Map.Entry<ChunkSectionPos, SectionActivityTracker.SectionActivityData> set : SectionActivityTracker.ACTIVITY_MAP.entrySet()) {
            NbtCompound sectionNbt = new NbtCompound();
            SectionActivityTracker.ChunkSectionPosToNbt(set.getKey(), sectionNbt);
            set.getValue().writeNbt(sectionNbt);
            nbtList.add(sectionNbt);
        }
        nbt.put("chunkSectionActivity", nbtList);
        return nbt;
    }

}
