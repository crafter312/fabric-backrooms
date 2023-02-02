package net.thesquire.backroomsmod.world.feature;

import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.feature.custom.*;

public class ModFeatures {

    public static final RegistryKey<Feature<?>> WALL_KEY = registerKey("wall");
    public static final RegistryKey<Feature<?>> THIN_WALL_KEY = registerKey("thin_wall");
    public static final RegistryKey<Feature<?>> BLOCK_GRID_KEY = registerKey("lighting_feature");
    public static final RegistryKey<Feature<?>> WALL_MOUNTABLE_KEY = registerKey("wall_mountable");

    public static Feature<ModSimpleWallFeatureConfig> WALL;
    public static Feature<ModThinWallFeatureConfig> THIN_WALL;
    public static Feature<ModBlockGridFeatureConfig> BLOCK_GRID;
    public static Feature<ModWallMountableFeatureConfig> WALL_MOUNTABLE;

    public static void registerModFeatures() {
        BackroomsMod.LOGGER.info("Registering mod features for " + BackroomsMod.MOD_ID);

        WALL = register("wall", new ModSimpleWallFeature(ModSimpleWallFeatureConfig.CODEC));
        THIN_WALL = register("thin_wall", new ModThinWallFeature(ModThinWallFeatureConfig.CODEC));
        BLOCK_GRID  = register("lighting_feature", new ModBlockGridFeature(ModBlockGridFeatureConfig.CODEC));
        WALL_MOUNTABLE = register("wall_mountable", new ModWallMountableFeature(ModWallMountableFeatureConfig.CODEC));
    }

    public static void bootstrap(Registerable<Feature<?>> context) {
        register(context, WALL_KEY, new ModSimpleWallFeature(ModSimpleWallFeatureConfig.CODEC));
        register(context, THIN_WALL_KEY, new ModThinWallFeature(ModThinWallFeatureConfig.CODEC));
        register(context, BLOCK_GRID_KEY, new ModBlockGridFeature(ModBlockGridFeatureConfig.CODEC));
        register(context, WALL_MOUNTABLE_KEY, new ModWallMountableFeature(ModWallMountableFeatureConfig.CODEC));
    }

    public static RegistryKey<Feature<?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.FEATURE, new Identifier(BackroomsMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<Feature<?>> context,
                                                                                 RegistryKey<Feature<?>> key, F feature) {
        context.register(key, feature);
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, new Identifier(BackroomsMod.MOD_ID, name), feature);
    }

}
