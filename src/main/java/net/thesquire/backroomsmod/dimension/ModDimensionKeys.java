package net.thesquire.backroomsmod.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModDimensionKeys {

    public static final RegistryKey<World> LEVEL_0 = RegistryKey.of(RegistryKeys.WORLD, new Identifier(BackroomsMod.MOD_ID, "level_0"));
    public static final RegistryKey<World> LEVEL_1 = RegistryKey.of(RegistryKeys.WORLD, new Identifier(BackroomsMod.MOD_ID, "level_1"));

}
