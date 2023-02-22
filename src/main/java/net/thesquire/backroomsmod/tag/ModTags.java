package net.thesquire.backroomsmod.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModTags {

    public static final TagKey<GameEvent> BLACKOUT_TRIGGERS = of("blackout_triggers");

    private static TagKey<GameEvent> of(String name) {
        return TagKey.of(RegistryKeys.GAME_EVENT, new Identifier(BackroomsMod.MOD_ID, name));
    }

}
