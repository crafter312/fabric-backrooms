package net.thesquire.backroomsmod.world.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.structure.Structure;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModStructureKeys {

    // structure keys
    public static final RegistryKey<Structure> THRESHOLD_CHAMBER = register("threshold_chamber");
    public static final RegistryKey<Structure> LEVEL_1_PORTAL_8W = register("level_1_portal_8w");

    private static RegistryKey<Structure> register(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, BackroomsMod.makeId(name));
    }

}
