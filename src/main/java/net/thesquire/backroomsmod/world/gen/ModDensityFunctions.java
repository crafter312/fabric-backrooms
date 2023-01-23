package net.thesquire.backroomsmod.world.gen;

import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.gen.densityfunction.SquareColumns;

public class ModDensityFunctions {

    public static void registerModDensityFunctions() {
        BackroomsMod.LOGGER.info("Registering density functions for " + BackroomsMod.MOD_ID);

        registerDensityFunctionType("square_columns", SquareColumns.CODEC_HOLDER);
    }

    private static void registerDensityFunctionType(String name, CodecHolder<? extends DensityFunction> codecHolder) {
        Registry.register(Registry.DENSITY_FUNCTION_TYPE, new Identifier(BackroomsMod.MOD_ID, name), codecHolder.codec());
    }

}
