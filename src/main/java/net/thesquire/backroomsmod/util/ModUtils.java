package net.thesquire.backroomsmod.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.config.ModConfig;
import net.thesquire.backroomsmod.world.structure.ModStructureKeys;

import java.util.Optional;

public class ModUtils {

    public static BlockPos findSuitableTeleportDestination(World world, BlockPos pos) {
        for (int i = 0; i < ModConfig.MAX_TELEPORT_DEST_SEARCH_DISTANCE; i++) {
            BlockPos destPos = pos.add(i, 0, i);
            BlockPos destPosUp = destPos.add(Direction.UP.getVector());
            BlockPos destPosDown = destPos.add(Direction.DOWN.getVector());
            BlockState block = world.getBlockState(destPos);
            BlockState blockAbove = world.getBlockState(destPosUp);
            BlockState blockBelow = world.getBlockState(destPosDown);
            if (!block.isSolidBlock(world, destPos) && !blockAbove.isSolidBlock(world, destPosUp) && blockBelow.isSolidBlock(world, destPosDown)) {
                return destPos;
            }
        }
        return pos;
    }

    public static int boolToInt(boolean b, int first, int second) {
        return b ? first : second;
    }

    // this is a slightly modified version of Minecraft's NbtHelper::toBlockState method
    public static BlockState blockStateFromNbt(NbtCompound nbt){
        if (!nbt.contains("Name", NbtElement.STRING_TYPE)) {
            return Blocks.AIR.getDefaultState();
        }
        Identifier identifier = new Identifier(nbt.getString("Name"));
        Optional<RegistryEntry.Reference<Block>> optional = Registries.BLOCK.getEntry(RegistryKey.of(RegistryKeys.BLOCK, identifier));
        if (optional.isEmpty()) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = (Block)((RegistryEntry<?>)optional.get()).value();
        BlockState blockState = block.getDefaultState();
        if (nbt.contains("Properties", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("Properties");
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            for (String string : nbtCompound.getKeys()) {
                net.minecraft.state.property.Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                blockState = withProperty(blockState, property, string, nbtCompound, nbt);
            }
        }
        return blockState;
    }

    private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(S state, net.minecraft.state.property.Property<T> property, String key, NbtCompound properties, NbtCompound root) {
        Optional<T> optional = property.parse(properties.getString(key));
        if (optional.isPresent()) {
            return state.with(property, optional.get());
        }
        BackroomsMod.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", key, properties.getString(key), root.toString());
        return state;
    }

    public static Optional<BlockPos> findStructure(ServerWorld world, BlockPos pos, RegistryKey<Structure> structureKey) {
        Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
        Optional<RegistryEntry.Reference<Structure>> registryEntry = registry.getEntry(structureKey);
        if(registryEntry.isEmpty()) return Optional.empty();

        RegistryEntryList<Structure> registryEntryList = RegistryEntryList.of(registryEntry.get());
        Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator()
                .locateStructure(world, registryEntryList, pos, 100, false);
        if (pair == null) return Optional.empty();

        return Optional.of(pair.getFirst());
    }

    public static void placeStructure(ServerWorld world, String name, BlockPos pos, int angle) {
        Optional<StructureTemplate> optional;
        StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
        Identifier templateName = StringHelper.isEmpty(name) ? null : Identifier.tryParse(name);
        if(templateName == null) {
            BackroomsMod.LOGGER.warn("Invalid structure name: " + name);
            return;
        }

        // get StructureTemplate from supplied name
        try {
            optional = structureTemplateManager.getTemplate(templateName);
        }
        catch (InvalidIdentifierException invalidIdentifierException) {
            return;
        }
        if (optional.isEmpty()) {
            return;
        }
        StructureTemplate template = optional.get();

        // get BlockRotation from supplied angle
        BlockRotation rotation;
        switch (angle) {
            case 90 -> rotation = BlockRotation.CLOCKWISE_90;
            case 180 -> rotation = BlockRotation.CLOCKWISE_180;
            case 270 -> rotation = BlockRotation.COUNTERCLOCKWISE_90;
            default -> rotation = BlockRotation.NONE;
        }

        // for now, no mirroring and doesn't ignore entities
        StructurePlacementData structurePlacementData = new StructurePlacementData()
                .setRotation(rotation)
                .setMirror(BlockMirror.NONE)
                .setIgnoreEntities(false);

        template.place(world, pos, pos, structurePlacementData, StructureBlockBlockEntity.createRandom(0L), Block.NOTIFY_LISTENERS);
    }

}
