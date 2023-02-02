package net.thesquire.backroomsmod.world;

import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.gen.ModOreGeneration;

public class ModWorldGen {

    public static void addModWorldGen() {
        BackroomsMod.LOGGER.info("Adding ore generation for " + BackroomsMod.MOD_ID);

        ModOreGeneration.generateOres();
    }

}
