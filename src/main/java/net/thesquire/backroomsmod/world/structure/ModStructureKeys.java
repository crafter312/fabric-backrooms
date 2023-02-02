package net.thesquire.backroomsmod.world.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModStructureKeys {

    public static final RegistryKey<Structure> THRESHOLD_CHAMBER = register("threshold_chamber");

    private static RegistryKey<Structure> register(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier(BackroomsMod.MOD_ID, name));
    }

}
