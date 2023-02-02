package net.thesquire.backroomsmod.world.biome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModBiomeKeys {

    public static final RegistryKey<Biome> LEVEL_0 = register("level_0");
    public static final RegistryKey<Biome> LEVEL_0_DARK = register("level_0_dark");

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(BackroomsMod.MOD_ID, name));
    }

}
