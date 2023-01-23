package net.thesquire.backroomsmod.dimension;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModDimensionKeys {

    public static final RegistryKey<World> LEVEL_0 = RegistryKey.of(Registry.WORLD_KEY, new Identifier(BackroomsMod.MOD_ID, "level_0"));
    public static final RegistryKey<World> LEVEL_1 = RegistryKey.of(Registry.WORLD_KEY, new Identifier(BackroomsMod.MOD_ID, "level_1"));

}
