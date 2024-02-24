package net.thesquire.backroomsmod.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModDimensionKeys {

    public static RegistryKey<World> LEVEL_0;
    public static RegistryKey<World> LEVEL_1;
    public static RegistryKey<World> LEVEL_2;
    public static RegistryKey<World> LEVEL_4;
    public static RegistryKey<World> LEVEL_11;

    public static void registerDimensionKeys() {
        BackroomsMod.LOGGER.info("Registering dimension keys for " + BackroomsMod.MOD_ID);

        LEVEL_0 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_0"));
        LEVEL_1 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_1"));
        LEVEL_2 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_2"));
        LEVEL_4 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_4"));
        LEVEL_11 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_11"));
    }

}
