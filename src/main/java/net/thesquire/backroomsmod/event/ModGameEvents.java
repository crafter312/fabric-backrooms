package net.thesquire.backroomsmod.event;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.config.ModConfig;

public class ModGameEvents {

    public static GameEvent BLACKOUT;

    public static void registerModGameEvents() {
        BackroomsMod.LOGGER.info("Registering game events for " + BackroomsMod.MOD_ID);

        BLACKOUT = register("blackout", ModConfig.maxBlackoutRange);
    }

    private static GameEvent register(String name, int range) {
        return Registry.register(Registries.GAME_EVENT, new Identifier(BackroomsMod.MOD_ID, name), new GameEvent(name, range));
    }

}
