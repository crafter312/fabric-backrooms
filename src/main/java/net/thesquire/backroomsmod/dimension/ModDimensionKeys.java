package net.thesquire.backroomsmod.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModDimensionKeys {

    public static final RegistryKey<World> LEVEL_0 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_0"));
    public static final RegistryKey<World> LEVEL_1 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_1"));
    public static final RegistryKey<World> LEVEL_2 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_2"));
    public static final RegistryKey<World> LEVEL_4 = RegistryKey.of(RegistryKeys.WORLD, BackroomsMod.makeId("level_4"));

}
