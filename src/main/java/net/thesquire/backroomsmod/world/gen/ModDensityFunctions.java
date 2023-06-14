package net.thesquire.backroomsmod.world.gen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.gen.densityfunction.GridWalls;
import net.thesquire.backroomsmod.world.gen.densityfunction.SquareColumns;

public class ModDensityFunctions {

    public static void registerModDensityFunctions() {
        BackroomsMod.LOGGER.info("Registering density functions for " + BackroomsMod.MOD_ID);

        registerDensityFunctionType("square_columns", SquareColumns.CODEC_HOLDER);
        registerDensityFunctionType("grid_walls", GridWalls.CODEC_HOLDER);
    }

    private static void registerDensityFunctionType(String name, CodecHolder<? extends DensityFunction> codecHolder) {
        Registry.register(Registries.DENSITY_FUNCTION_TYPE, BackroomsMod.makeId(name), codecHolder.codec());
    }

}
