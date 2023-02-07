package net.thesquire.backroomsmod.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModSounds {

    public static RegistryEntry.Reference<SoundEvent> NO_MUSIC;

    public static RegistryEntry.Reference<SoundEvent> LEVEL_0_DARK_MUSIC;
    public static RegistryEntry.Reference<SoundEvent> LEVEL_0_LOOP;

    public static SoundEvent LEVEL_1_DRIP;
    public static SoundEvent WAREHOUSE_CONCRETE_STEP;
    public static SoundEvent WAREHOUSE_CONCRETE_WATERLOGGED_STEP;

    public static BlockSoundGroup WAREHOUSE_CONCRETE;
    public static BlockSoundGroup WAREHOUSE_CONCRETE_WATERLOGGED;

    ////////////////////////////////////////////////////////////////////////////////////

    public static void registerModSounds() {
        BackroomsMod.LOGGER.info("Registering sounds for " + BackroomsMod.MOD_ID);

        registerSounds();
        makeSoundGroups();
    }

    private static void registerSounds() {
        NO_MUSIC = registerSoundEventReference("music.none");

        LEVEL_0_DARK_MUSIC = registerSoundEventReference("level_0.music.dark");
        LEVEL_0_LOOP = registerSoundEventReference("level_0.ambient.loop");

        LEVEL_1_DRIP = registerSoundEvent("level_1.ambient.drip");
        WAREHOUSE_CONCRETE_STEP = registerSoundEvent("level_1.warehouse_concrete.step");
        WAREHOUSE_CONCRETE_WATERLOGGED_STEP = registerSoundEvent("level_1.warehouse_concrete.waterlogged.step");
    }

    private static void makeSoundGroups() {
        WAREHOUSE_CONCRETE = new BlockSoundGroup(
                1.4f,
                1.0f,
                SoundEvents.BLOCK_STONE_BREAK,
                WAREHOUSE_CONCRETE_STEP,
                SoundEvents.BLOCK_STONE_PLACE,
                SoundEvents.BLOCK_STONE_HIT,
                SoundEvents.BLOCK_STONE_FALL);

        WAREHOUSE_CONCRETE_WATERLOGGED = new BlockSoundGroup(
                0.9f,
                1.0f,
                SoundEvents.BLOCK_STONE_BREAK,
                WAREHOUSE_CONCRETE_WATERLOGGED_STEP,
                SoundEvents.BLOCK_STONE_PLACE,
                SoundEvents.BLOCK_STONE_HIT,
                SoundEvents.BLOCK_STONE_FALL);
    }

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(BackroomsMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    private static RegistryEntry.Reference<SoundEvent> registerSoundEventReference(String name) {
        Identifier id = new Identifier(BackroomsMod.MOD_ID, name);
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

}
