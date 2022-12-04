package net.thesquire.backroomsmod.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModSounds {

    public static final SoundEvent NO_MUSIC = registerSoundEvent("music.none");
    public static final SoundEvent LEVEL_0_DARK_MUSIC = registerSoundEvent("level_0_dark.music");
    public static final SoundEvent LEVEL_0_LOOP = registerSoundEvent("level_0.ambient.loop");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(BackroomsMod.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

}
