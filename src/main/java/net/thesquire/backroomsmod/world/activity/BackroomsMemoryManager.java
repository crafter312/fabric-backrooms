package net.thesquire.backroomsmod.world.activity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.mixin.StructureTemplateAccessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BackroomsMemoryManager extends PersistentState {

    public enum ChunkSectionStatus implements StringIdentifiable {
        CAPTURED_PENDING_MATERIALIZATION("captured_pending_materialization"),
        MATERIALIZED("materialized");

        // The codec generation utility
        public static final Codec<ChunkSectionStatus> CODEC = StringIdentifiable.createCodec(ChunkSectionStatus::values);

        private final String id;

        ChunkSectionStatus(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }
    }

    // Convert ChunkSectionPos to and from a parseable string (e.g., "x,y,z")
    private static final Codec<ChunkSectionPos> STRING_SECTION_CODEC = Codec.STRING.comapFlatMap(
            str -> {
                String[] split = str.split(",");
                try {
                    return DataResult.success(ChunkSectionPos.from(
                            Integer.parseInt(split[0]),
                            Integer.parseInt(split[1]),
                            Integer.parseInt(split[2])
                    ));
                } catch (Exception e) {
                    return DataResult.error(() -> "Invalid chunk section position string: " + str);
                }
            },
            pos -> pos.getX() + "," + pos.getY() + "," + pos.getZ()
    );

    // Codec for `BackroomsMemoryManager` using above ChunkSectionPos codec
    private static final Codec<BackroomsMemoryManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(STRING_SECTION_CODEC, ChunkSectionStatus.CODEC).fieldOf("section_statuses").forGetter(BackroomsMemoryManager::getSectionStatuses))
            .apply(instance, instance.stable(BackroomsMemoryManager::new)));

    public static final Type<BackroomsMemoryManager> TYPE = new Type<>(
            BackroomsMemoryManager::new,
            BackroomsMemoryManager::fromNbt,
            DataFixTypes.LEVEL
    );

    public static BackroomsMemoryManager fromNbt(NbtCompound nbt) {

        // Check if our specific key exists in the saved NBT
        if (!nbt.contains("backrooms_memory_manager"))
            return new BackroomsMemoryManager();

        NbtElement stateData = nbt.get("backrooms_memory_manager");

        // Decode the NbtElement back into our Java Map
        AtomicReference<BackroomsMemoryManager> manager = new AtomicReference<>(new BackroomsMemoryManager());
        CODEC.parse(NbtOps.INSTANCE, stateData)
                .resultOrPartial(error -> BackroomsMod.LOGGER.error("Failed to parse block memory states: {}", error))
                .ifPresent(manager::set);

        return manager.get();
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private final Map<ChunkSectionPos, ChunkSectionStatus> sectionStatuses = new HashMap<>();

    public BackroomsMemoryManager() {}

    public BackroomsMemoryManager(Map<ChunkSectionPos, ChunkSectionStatus> m) {
        this.sectionStatuses.putAll(m);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(error -> BackroomsMod.LOGGER.error("Failed to encode block memory states: {}", error))
                .ifPresent(encodedNbt -> nbt.put("backrooms_memory_manager", encodedNbt));

        return nbt;
    }

    public Map<ChunkSectionPos, ChunkSectionStatus> getSectionStatuses() {
        return this.sectionStatuses;
    }

    public void captureChunkSection(World world, ChunkSectionPos sectionPos) {
        if (world.isClient() || (world.getRegistryKey() != ServerWorld.OVERWORLD)) return;
        ServerWorld serverWorld = (ServerWorld) world;

        StructureTemplate structureTemplate = new StructureTemplate();
        List<StructureTemplate.StructureBlockInfo> blockInfos = new ArrayList<>();
        List<StructureTemplate.StructureEntityInfo> entityInfos = new ArrayList<>(); // Empty unless tracking entities
        BitSet mask = DatabaseManager.getBlockMask(sectionPos);
        BlockPos startPos = sectionPos.getMinPos();
        if (mask == null) return;

        // Loop through chunk section and get block states and entities for all player-placed blocks
        BlockPos minPos = new BlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        BlockPos maxPos = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (short i = 0; i < 4096; i++) {
            if (!mask.get(i)) continue;

            // Get block position and track total size of placed blocks
            BlockPos localPos = sectionPos.unpackBlockPos(i);
            BlockPos absolutePos = startPos.add(localPos);
            if ((absolutePos.getX() < minPos.getX()) || (absolutePos.getY() < minPos.getY()) || (absolutePos.getZ() < minPos.getZ()))
                minPos = absolutePos;
            if ((absolutePos.getX() > maxPos.getX()) || (absolutePos.getY() > maxPos.getY()) || (absolutePos.getZ() > maxPos.getZ()))
                maxPos = absolutePos;

            BlockState state = serverWorld.getBlockState(absolutePos);
            NbtCompound nbtData = null;
            BlockEntity blockEntity = serverWorld.getBlockEntity(absolutePos);
            if (blockEntity != null) {
                nbtData = blockEntity.createNbtWithId();
            }
            blockInfos.add(new StructureTemplate.StructureBlockInfo(localPos, state, nbtData));
        }

        // Return early if empty
        if (blockInfos.isEmpty()) return;

        // Set template information
        BlockPos size = maxPos.subtract(minPos);
        structureTemplate.setAuthor("backroomsmod");

        // Mimic Minecraft's internal sorting & serialization
        // Split blocks into lists so combineSorted can layer them correctly
        List<StructureTemplate.StructureBlockInfo> fullBlocks = new ArrayList<>();
        List<StructureTemplate.StructureBlockInfo> blocksWithNbt = new ArrayList<>();
        List<StructureTemplate.StructureBlockInfo> otherBlocks = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo info : blockInfos) {
            // Invoking the private static helper method from the decompiled class
            // It categorizes full cubes vs tile entities vs normal blocks
            StructureTemplateAccessor.invokeCategorize(info, fullBlocks, blocksWithNbt, otherBlocks);
        }

        // Combine and sort them by Y, X, and Z coordinates
        List<StructureTemplate.StructureBlockInfo> sortedBlocks = StructureTemplateAccessor.invokeCombineSorted(fullBlocks, blocksWithNbt, otherBlocks);

        // Inject our sorted data into the template using the Accessor
        StructureTemplateAccessor accessor = (StructureTemplateAccessor) structureTemplate;
        accessor.getBlockInfoLists().clear();
        accessor.getBlockInfoLists().add(new StructureTemplate.PalettedBlockInfoList(sortedBlocks));
        accessor.getEntities().clear();

        // 8. Serializing and Writing NBT to disk
        NbtCompound nbtData = template.writeNbt(new NbtCompound());
        File gameDir = world.getServer().getRunDirectory();
        File saveFile = new File(gameDir, "backrooms_cache/" + structureId.getNamespace() + "/" + structureId.getPath() + ".nbt");

        saveFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            NbtIo.writeCompressed(nbtData, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
