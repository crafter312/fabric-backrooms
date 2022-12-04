package net.thesquire.backroomsmod.world.feature;

import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.world.feature.custom.ModBlockGridFeatureConfig;
import net.thesquire.backroomsmod.world.feature.custom.ModSimpleWallFeatureConfig;
import net.thesquire.backroomsmod.world.feature.custom.ModThinWallFeatureConfig;

import java.util.List;

public class ModConfiguredFeatures {

    public static final List<OreFeatureConfig.Target> OVERWORLD_BISMUTHINITE_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.BISMUTHINITE_ORE.getDefaultState()));
    public static final List<OreFeatureConfig.Target> FLUORESCENT_LIGHT_TARGET = List.of(
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(ModBlocks.CEILING_TILE), ModBlocks.FLUORESCENT_LIGHT.getDefaultState()));
    public static final List<OreFeatureConfig.Target> LEVEL_0_WALL_TARGET = List.of(
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(Blocks.LIGHT_GRAY_CARPET), ModBlocks.YELLOW_WALLPAPER.getDefaultState()),
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(Blocks.AIR), ModBlocks.YELLOW_WALLPAPER.getDefaultState()));

    public static RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> BISMUTHINITE_ORE;
    public static RegistryEntry<ConfiguredFeature<ModBlockGridFeatureConfig, ?>> FLUORESCENT_LIGHT;
    public static RegistryEntry<ConfiguredFeature<ModBlockGridFeatureConfig, ?>> FLUORESCENT_LIGHT_FLICKERING;
    public static RegistryEntry<ConfiguredFeature<ModSimpleWallFeatureConfig, ?>> LEVEL_0_WALL;
    public static RegistryEntry<ConfiguredFeature<ModThinWallFeatureConfig, ?>> LEVEL_0_THIN_STRAIGHT_WALL;
    public static RegistryEntry<ConfiguredFeature<ModThinWallFeatureConfig, ?>> LEVEL_0_THIN_CROOKED_WALL;

    /**********************************************************************************************/

    public static void registerConfiguredFeatures() {
        System.out.println("Registering configured features for " + BackroomsMod.MOD_ID);

        BISMUTHINITE_ORE = ConfiguredFeatures.register("bismuthinite_ore", Feature.ORE, new OreFeatureConfig(OVERWORLD_BISMUTHINITE_ORES, 8, 0.2f));
        FLUORESCENT_LIGHT = ConfiguredFeatures.register("fluorescent_light", ModFeatures.BLOCK_GRID, new ModBlockGridFeatureConfig(FLUORESCENT_LIGHT_TARGET,
                UniformIntProvider.create(4, 6), UniformIntProvider.create(4, 6), ConstantIntProvider.create(1), 0.01f));
        FLUORESCENT_LIGHT_FLICKERING = ConfiguredFeatures.register("fluorescent_light_flickering", ModFeatures.BLOCK_GRID, new ModBlockGridFeatureConfig(FLUORESCENT_LIGHT_TARGET,
                UniformIntProvider.create(2, 4), UniformIntProvider.create(2, 4), ConstantIntProvider.create(1), 0.5f));
        LEVEL_0_WALL = ConfiguredFeatures.register("level_0_wall", ModFeatures.WALL, new ModSimpleWallFeatureConfig(LEVEL_0_WALL_TARGET,
                ConstantIntProvider.create(4), UniformIntProvider.create(4, 8), UniformIntProvider.create(4, 8)));
        LEVEL_0_THIN_STRAIGHT_WALL = ConfiguredFeatures.register("level_0_thin_straight_wall", ModFeatures.THIN_WALL, new ModThinWallFeatureConfig(LEVEL_0_WALL_TARGET,
                UniformIntProvider.create(1, 3), UniformIntProvider.create(8, 12), ConstantIntProvider.create(4), true));
        LEVEL_0_THIN_CROOKED_WALL = ConfiguredFeatures.register("level_0_thin_crooked_wall", ModFeatures.THIN_WALL, new ModThinWallFeatureConfig(LEVEL_0_WALL_TARGET,
                UniformIntProvider.create(2, 4), UniformIntProvider.create(8, 12), ConstantIntProvider.create(4), false));
    }

}
