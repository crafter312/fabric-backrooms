package net.thesquire.backroomsmod.world.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.world.feature.custom.*;

public class ModFeatures {

    public static final RegistryKey<Feature<?>> WALL_KEY = registerKey("wall");
    public static final RegistryKey<Feature<?>> THIN_WALL_KEY = registerKey("thin_wall");
    public static final RegistryKey<Feature<?>> BLOCK_GRID_KEY = registerKey("lighting_feature");
    public static final RegistryKey<Feature<?>> WALL_MOUNTABLE_KEY = registerKey("wall_mountable");
    public static final RegistryKey<Feature<?>> DOUBLE_FEATURE_KEY = registerKey("double_feature");
    public static final RegistryKey<Feature<?>> FLAT_ORE_FEATURE_KEY = registerKey("flat_ore_feature");
    public static final RegistryKey<Feature<?>> LOOT_CHEST_KEY = registerKey("loot_chest");

    public static Feature<ModSimpleWallFeatureConfig> WALL;
    public static Feature<ModThinWallFeatureConfig> THIN_WALL;
    public static Feature<ModBlockGridFeatureConfig> BLOCK_GRID;
    public static Feature<ModWallMountableFeatureConfig> WALL_MOUNTABLE;
    public static Feature<ModDoubleFeatureConfig> DOUBLE_FEATURE;
    public static Feature<OreFeatureConfig> FLAT_ORE_FEATURE;
    public static Feature<ModLootChestFeatureConfig> LOOT_CHEST;

    public static void registerModFeatures() {
        BackroomsMod.LOGGER.info("Registering mod features for " + BackroomsMod.MOD_ID);

        WALL = register(WALL_KEY.getValue().getPath(), new ModSimpleWallFeature(ModSimpleWallFeatureConfig.CODEC));
        THIN_WALL = register(THIN_WALL_KEY.getValue().getPath(), new ModThinWallFeature(ModThinWallFeatureConfig.CODEC));
        BLOCK_GRID  = register(BLOCK_GRID_KEY.getValue().getPath(), new ModBlockGridFeature(ModBlockGridFeatureConfig.CODEC));
        WALL_MOUNTABLE = register(WALL_MOUNTABLE_KEY.getValue().getPath(), new ModWallMountableFeature(ModWallMountableFeatureConfig.CODEC));
        DOUBLE_FEATURE = register(DOUBLE_FEATURE_KEY.getValue().getPath(), new ModDoubleFeature(ModDoubleFeatureConfig.CODEC));
        FLAT_ORE_FEATURE = register(FLAT_ORE_FEATURE_KEY.getValue().getPath(), new ModFlatOreFeature(OreFeatureConfig.CODEC));
        LOOT_CHEST = register(LOOT_CHEST_KEY.getValue().getPath(), new ModLootChestFeature(ModLootChestFeatureConfig.CODEC));
    }

    public static RegistryKey<Feature<?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.FEATURE, BackroomsMod.makeId(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, BackroomsMod.makeId(name), feature);
    }

}
