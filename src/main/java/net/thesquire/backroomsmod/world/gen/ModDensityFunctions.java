package net.thesquire.backroomsmod.world.gen;

import net.minecraft.registry.*;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.gen.densityfunction.GridWalls;
import net.thesquire.backroomsmod.world.gen.densityfunction.SquareColumns;

public class ModDensityFunctions {

    public static final RegistryKey<DensityFunction> BASE_3D_NOISE_LEVEL_1 = ModDensityFunctions.of("level_1/base_3d_noise");
    public static final RegistryKey<DensityFunction> GRID_WALLS_LEVEL_1 = ModDensityFunctions.of("level_1/grid_walls");
    public static final RegistryKey<DensityFunction> GRID_WALLS_DOORS_LEVEL_1 = ModDensityFunctions.of("level_1/grid_walls_doors");

    public static final RegistryKey<DensityFunction> BASE_3D_NOISE_LEVEL_2 = ModDensityFunctions.of("level_2/base_3d_noise");
    public static final RegistryKey<DensityFunction> GRID_WALLS_LEVEL_2 = ModDensityFunctions.of("level_2/grid_walls");

    public static final RegistryKey<DensityFunction> BASE_3D_NOISE_LEVEL_4 = ModDensityFunctions.of("level_4/base_3d_noise");
    public static final RegistryKey<DensityFunction> GRID_WALLS_LEVEL_4 = ModDensityFunctions.of("level_4/grid_walls");

    public static void bootstrap(Registerable<DensityFunction> context) {
        var densityFunctionRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.DENSITY_FUNCTION);

        context.register(BASE_3D_NOISE_LEVEL_1, InterpolatedNoiseSampler.createBase3dNoiseFunction(
                30, 0.125, 80.0, 160.0, 8.0));
        context.register(GRID_WALLS_LEVEL_1, new GridWalls(
                densityFunctionRegistryEntryLookup.getOrThrow(BASE_3D_NOISE_LEVEL_1),
                new GridWalls.GridWallsData(26, 2, 26, 2, false)));
        context.register(GRID_WALLS_DOORS_LEVEL_1, new GridWalls(
                densityFunctionRegistryEntryLookup.getOrThrow(BASE_3D_NOISE_LEVEL_1),
                new GridWalls.GridWallsData(26, 2, 26, 2, true,3)));

        context.register(BASE_3D_NOISE_LEVEL_2, InterpolatedNoiseSampler.createBase3dNoiseFunction(
                60, 0.125, 80.0, 160.0, 8.0));
        context.register(GRID_WALLS_LEVEL_2, new GridWalls(
                densityFunctionRegistryEntryLookup.getOrThrow(BASE_3D_NOISE_LEVEL_2),
                new GridWalls.GridWallsData(42, 4, 17, 2, false)));

        context.register(BASE_3D_NOISE_LEVEL_4, InterpolatedNoiseSampler.createBase3dNoiseFunction(
                120, 0.125, 80.0, 160.0, 8.0));
        context.register(GRID_WALLS_LEVEL_4, new GridWalls(
                densityFunctionRegistryEntryLookup.getOrThrow(BASE_3D_NOISE_LEVEL_4),
                new GridWalls.GridWallsData(15, 1, 15, 1, true, 2)));
    }

    public static void registerModDensityFunctions() {
        BackroomsMod.LOGGER.info("Registering density functions for " + BackroomsMod.MOD_ID);

        registerDensityFunctionType("square_columns", SquareColumns.CODEC_HOLDER);
        registerDensityFunctionType("grid_walls", GridWalls.CODEC_HOLDER);
    }

    private static void registerDensityFunctionType(String name, CodecHolder<? extends DensityFunction> codecHolder) {
        Registry.register(Registries.DENSITY_FUNCTION_TYPE, BackroomsMod.makeId(name), codecHolder.codec());
    }

    public static RegistryKey<DensityFunction> of(String name) {
        return RegistryKey.of(RegistryKeys.DENSITY_FUNCTION, BackroomsMod.makeId(name));
    }

}
