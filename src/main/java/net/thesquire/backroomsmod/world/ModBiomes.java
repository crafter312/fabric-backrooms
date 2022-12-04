package net.thesquire.backroomsmod.world;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.biome.ModBackroomsBiomes;
import net.thesquire.backroomsmod.world.biome.ModBiomeKeys;

public class ModBiomes {

    public static void registerBiomes() {
        BackroomsMod.LOGGER.info("Registering mod biomes for " + BackroomsMod.MOD_ID);

        register(ModBiomeKeys.LEVEL_0, ModBackroomsBiomes.level0());
        register(ModBiomeKeys.LEVEL_0_DARK, ModBackroomsBiomes.level0dark());
    }

    private static void register(RegistryKey<Biome> key, Biome biome) {
        BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
    }

}
