package net.thesquire.backroomsmod.world.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.feature.custom.*;

public class ModFeatures {

    public static final Feature<ModSimpleWallFeatureConfig> WALL = register("wall",
            new ModSimpleWallFeature(ModSimpleWallFeatureConfig.CODEC));
    public static final Feature<ModThinWallFeatureConfig> THIN_WALL = register("thin_wall",
            new ModThinWallFeature(ModThinWallFeatureConfig.CODEC));
    public static final Feature<ModBlockGridFeatureConfig> BLOCK_GRID = register("lighting_feature",
            new ModBlockGridFeature(ModBlockGridFeatureConfig.CODEC));
    public static final Feature<ModWallMountableFeatureConfig> WALL_MOUNTABLE = register("wall_mountable",
            new ModWallMountableFeature(ModWallMountableFeatureConfig.CODEC));

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registry.FEATURE, new Identifier(BackroomsMod.MOD_ID, name), feature);
    }

}
