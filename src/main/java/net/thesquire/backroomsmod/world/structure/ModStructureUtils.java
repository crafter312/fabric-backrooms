package net.thesquire.backroomsmod.world.structure;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;

public class ModStructureUtils {

    public static Optional<BlockPos> findStructure(ServerWorld world, BlockPos pos) {
        Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
        Optional<RegistryEntry.Reference<Structure>> registryEntry = registry.getEntry(ModStructureKeys.THRESHOLD_CHAMBER);
        if(registryEntry.isEmpty()) return Optional.empty();

        RegistryEntryList<Structure> registryEntryList = RegistryEntryList.of(registryEntry.get());
        Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator()
                .locateStructure(world, registryEntryList, pos, 100, false);
        if (pair == null) return Optional.empty();

        return Optional.of(pair.getFirst());
    }

}
