package net.thesquire.backroomsmod.world.feature;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.world.feature.placement.NoiseThresholdPlacementModifier;
import net.thesquire.backroomsmod.world.feature.placement.WallAdjacentPlacementModifier;
import net.thesquire.backroomsmod.world.gen.ModDensityFunctions;

import java.util.List;

public class ModPlacedFeatures {

    // generic features
    public static RegistryKey<PlacedFeature> BISMUTHINITE_ORE_PLACED_KEY = registerKey("bismuthinite_ore_placed");
    public static RegistryKey<PlacedFeature> CONCRETE_PUDDLE_INDIVIDUAL_PLACED_KEY = registerKey("level_1_puddle_individual_placed");

    // level 0 features
    public static RegistryKey<PlacedFeature> LEVEL_0_WALL_PLACED_KEY = registerKey("level_0_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_0_THIN_STRAIGHT_WALL_PLACED_KEY = registerKey("level_0_thin_straight_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_0_THIN_CROOKED_WALL_PLACED_KEY = registerKey("level_0_thin_crooked_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_0_FLUORESCENT_LIGHT_PLACED_KEY = registerKey("level_0_fluorescent_light_placed");
    public static RegistryKey<PlacedFeature> LEVEL_0_FLUORESCENT_LIGHT_FLICKERING_PLACED_KEY = registerKey("level_0_fluorescent_light_flickering_placed");

    // level 1 features
    public static RegistryKey<PlacedFeature> LEVEL_1_WALL_LIGHTS_PLACED_KEY = registerKey("level_1_wall_lights_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_PUDDLE_PLACED_KEY = registerKey("level_1_puddle_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_DRIPPING_CONCRETE_PLACED_KEY = registerKey("level_1_dripping_concrete_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_REBAR_CONCRETE_SINGLE_PLACED_KEY = registerKey("level_1_rebar_concrete_single_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_DRIPPING_REBAR_PLACED_KEY = registerKey("level_1_dripping_rebar_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_PUDDLE_DRIP_PLACED_KEY = registerKey("level_1_puddle_drip_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_REBAR_CONCRETE_PLACED_KEY = registerKey("level_1_rebar_concrete_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_LOOT_CHEST_PLACED_KEY = registerKey("level_1_loot_chest_placed");

    // level 2 features
    public static RegistryKey<PlacedFeature> LEVEL_2_PIPE_NETWORK_PLACED_KEY = registerKey("level_2_pipe_network_placed");
    public static RegistryKey<PlacedFeature> LEVEL_2_WALL_LIGHTS_PLACED_KEY = registerKey("level_2_wall_lights_placed");

    // level 4 features
    public static RegistryKey<PlacedFeature> LEVEL_4_THIN_STRAIGHT_WALL_PLACED_KEY = registerKey("level_4_thin_straight_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_4_THIN_CROOKED_WALL_PLACED_KEY = registerKey("level_4_thin_crooked_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_4_FLUORESCENT_LIGHT_PLACED_KEY = registerKey("level_4_fluorescent_light_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        var densityFunctionRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.DENSITY_FUNCTION);

        PlacementModifier lightBlockModifier = BlockFilterPlacementModifier.of(
                BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), List.of(Blocks.AIR)));
        PlacementModifier findAdjacentWall = WallAdjacentPlacementModifier.of(makeAdjacentWallBlockPredicate(ModBlocks.PAINTED_WAREHOUSE_CONCRETE), 16);
        PlacementModifier findAdjacentWall2 = WallAdjacentPlacementModifier.of(Direction.NORTH, makeAdjacentWallBlockPredicate(ModBlocks.WAREHOUSE_CONCRETE),
                BlockPredicate.not(BlockPredicate.matchingBlocks(ModBlocks.PIPE_BLOCK)),16);
        PlacementModifier level4NoiseThreshold = new NoiseThresholdPlacementModifier(
                densityFunctionRegistryEntryLookup.getOrThrow(ModDensityFunctions.BASE_3D_NOISE_LEVEL_4_KEY),
                densityFunctionRegistryEntryLookup.getOrThrow(ModDensityFunctions.GRID_WALLS_LEVEL_4_KEY));

        // fixed y values
        PlacementModifier y20 = HeightRangePlacementModifier.uniform(YOffset.fixed(20), YOffset.fixed(20));
        PlacementModifier y21 = HeightRangePlacementModifier.uniform(YOffset.fixed(21), YOffset.fixed(21));
        PlacementModifier y22 = HeightRangePlacementModifier.uniform(YOffset.fixed(22), YOffset.fixed(22));
        PlacementModifier y23 = HeightRangePlacementModifier.uniform(YOffset.fixed(23), YOffset.fixed(23));
        PlacementModifier y24 = HeightRangePlacementModifier.uniform(YOffset.fixed(24), YOffset.fixed(24));
        PlacementModifier y25 = HeightRangePlacementModifier.uniform(YOffset.fixed(25), YOffset.fixed(25));

        register(context, BISMUTHINITE_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BISMUTHINITE_ORE_KEY),
                modifiersWithCount(8, HeightRangePlacementModifier.trapezoid(YOffset.fixed(70), YOffset.fixed(160))));
        register(context, CONCRETE_PUDDLE_INDIVIDUAL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.CONCRETE_PUDDLE_KEY),
                modifiersWithCount(8, y20));

        register(context, LEVEL_0_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_0_WALL_KEY),
                modifiersWithCount(2, y20));
        register(context, LEVEL_0_THIN_STRAIGHT_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_0_THIN_STRAIGHT_WALL_KEY),
                modifiersWithCount(3, y20));
        register(context, LEVEL_0_THIN_CROOKED_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_0_THIN_CROOKED_WALL_KEY),
                modifiersWithCount(3, y20));
        register(context, LEVEL_0_FLUORESCENT_LIGHT_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FLUORESCENT_LIGHT_KEY),
                modifiersWithCount(4, y24, lightBlockModifier));
        register(context, LEVEL_0_FLUORESCENT_LIGHT_FLICKERING_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FLUORESCENT_LIGHT_FLICKERING_KEY),
                modifiersWithRarity(100, y24, lightBlockModifier));

        register(context, LEVEL_1_WALL_LIGHTS_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.WALL_LIGHTS_KEY),
                modifiersWithCount(14, y23));
        register(context, LEVEL_1_PUDDLE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.CONCRETE_PUDDLE_KEY),
                BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(ModBlocks.WAREHOUSE_CONCRETE)));
        register(context, LEVEL_1_DRIPPING_CONCRETE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_DRIPPING_CONCRETE_KEY),
                BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(ModBlocks.PAINTED_WAREHOUSE_CONCRETE)));
        register(context, LEVEL_1_REBAR_CONCRETE_SINGLE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_REBAR_CONCRETE_KEY),
                BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(ModBlocks.PAINTED_WAREHOUSE_CONCRETE)));
        register(context, LEVEL_1_DRIPPING_REBAR_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_DRIPPING_REBAR_KEY),
                BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(ModBlocks.PAINTED_WAREHOUSE_CONCRETE)));
        register(context, LEVEL_1_PUDDLE_DRIP_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_PUDDLE_DRIP_KEY),
                modifiersWithCount(3, y25));
        register(context, LEVEL_1_REBAR_CONCRETE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_REBAR_CONCRETE_KEY),
                modifiersWithCount(2, y25));
        register(context, LEVEL_1_LOOT_CHEST_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_LOOT_CHEST_KEY),
                modifiersWithRarity(8, y21, findAdjacentWall));

        register(context, LEVEL_2_PIPE_NETWORK_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_2_PIPE_NETWORK_KEY),
                modifiersWithCount(14, y23, findAdjacentWall2));
        register(context, LEVEL_2_WALL_LIGHTS_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.WALL_LIGHTS_KEY),
                modifiersWithCount(14, y22));

        register(context, LEVEL_4_THIN_STRAIGHT_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_4_THIN_STRAIGHT_WALL_KEY),
                modifiersWithCount(3, y21, level4NoiseThreshold));
        register(context, LEVEL_4_THIN_CROOKED_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_4_THIN_CROOKED_WALL_KEY),
                modifiersWithCount(3, y21, level4NoiseThreshold));
        register(context, LEVEL_4_FLUORESCENT_LIGHT_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FLUORESCENT_LIGHT_KEY),
                modifiersWithCount(4, y25, lightBlockModifier));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, BackroomsMod.makeId(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        context.register(key, new PlacedFeature(configuration, List.of(modifiers)));
    }

    private static BlockPredicate makeAdjacentWallBlockPredicate(Block block) {
        return BlockPredicate.anyOf(
                BlockPredicate.matchingBlocks(Direction.NORTH.getVector(), block),
                BlockPredicate.matchingBlocks(Direction.EAST.getVector(), block),
                BlockPredicate.matchingBlocks(Direction.SOUTH.getVector(), block),
                BlockPredicate.matchingBlocks(Direction.WEST.getVector(), block));
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier, PlacementModifier blockPlacementModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, blockPlacementModifier, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier, PlacementModifier blockPlacementModifier) {
        return ModPlacedFeatures.modifiers(CountPlacementModifier.of(count), heightModifier, blockPlacementModifier);
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return ModPlacedFeatures.modifiers(CountPlacementModifier.of(count), heightModifier);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier, PlacementModifier blockPlacementModifier) {
        return ModPlacedFeatures.modifiers(RarityFilterPlacementModifier.of(chance), heightModifier, blockPlacementModifier);
    }

}
