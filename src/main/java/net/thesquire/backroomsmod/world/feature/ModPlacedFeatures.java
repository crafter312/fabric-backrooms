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
import net.thesquire.backroomsmod.world.feature.placement.WallAdjacentPlacementModifier;

import java.util.List;

public class ModPlacedFeatures {

    // generic features
    public static RegistryKey<PlacedFeature> BISMUTHINITE_ORE_PLACED_KEY = registerKey("bismuthinite_ore_placed");
    public static RegistryKey<PlacedFeature> FLUORESCENT_LIGHT_PLACED_KEY = registerKey("fluorescent_light_placed");
    public static RegistryKey<PlacedFeature> FLUORESCENT_LIGHT_FLICKERING_PLACED_KEY = registerKey("fluorescent_light_flickering_placed");
    public static RegistryKey<PlacedFeature> CONCRETE_PUDDLE_INDIVIDUAL_PLACED_KEY = registerKey("level_1_puddle_individual_placed");

    // level 0 features
    public static RegistryKey<PlacedFeature> LEVEL_0_WALL_PLACED_KEY = registerKey("level_0_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_0_THIN_STRAIGHT_WALL_PLACED_KEY = registerKey("level_0_thin_straight_wall_placed");
    public static RegistryKey<PlacedFeature> LEVEL_0_THIN_CROOKED_WALL_PLACED_KEY = registerKey("level_0_thin_crooked_wall_placed");

    // level 1 features
    public static RegistryKey<PlacedFeature> LEVEL_1_WALL_LIGHTS_PLACED_KEY = registerKey("level_1_wall_lights_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_PUDDLE_PLACED_KEY = registerKey("level_1_puddle_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_DRIPPING_CONCRETE_PLACED_KEY = registerKey("level_1_dripping_concrete_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_REBAR_CONCRETE_SINGLE_PLACED_KEY = registerKey("level_1_rebar_concrete_single_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_DRIPPING_REBAR_PLACED_KEY = registerKey("level_1_dripping_rebar_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_PUDDLE_DRIP_PLACED_KEY = registerKey("level_1_puddle_drip_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_REBAR_CONCRETE_PLACED_KEY = registerKey("level_1_rebar_concrete_placed");
    public static RegistryKey<PlacedFeature> LEVEL_1_LOOT_CHEST_PLACED_KEY = registerKey("level_1_loot_chest_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        PlacementModifier lightBlockModifier = BlockFilterPlacementModifier.of(
                BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), List.of(Blocks.AIR)));
        PlacementModifier level0WallBlockModifier = makeFloorPlacedFeatureBlockModifier(Blocks.LIGHT_GRAY_CONCRETE, Blocks.LIGHT_GRAY_CARPET);
        PlacementModifier findAdjacentWall = WallAdjacentPlacementModifier.of(makeAdjacentWallBlockPredicate(ModBlocks.PAINTED_WAREHOUSE_CONCRETE), 16);

        PlacementModifier level0Floor = HeightRangePlacementModifier.uniform(YOffset.fixed(18), YOffset.fixed(22));
        PlacementModifier level0Ceiling = HeightRangePlacementModifier.uniform(YOffset.fixed(23), YOffset.fixed(25));

        PlacementModifier level1Floor = HeightRangePlacementModifier.uniform(YOffset.fixed(20), YOffset.fixed(20));
        PlacementModifier level1OnFloor = HeightRangePlacementModifier.uniform(YOffset.fixed(21), YOffset.fixed(21));
        PlacementModifier level1LightPlacement = HeightRangePlacementModifier.uniform(YOffset.fixed(23), YOffset.fixed(23));
        PlacementModifier level1Ceiling = HeightRangePlacementModifier.uniform(YOffset.fixed(25), YOffset.fixed(25));

        register(context, BISMUTHINITE_ORE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BISMUTHINITE_ORE_KEY),
                modifiersWithCount(8, HeightRangePlacementModifier.trapezoid(YOffset.fixed(70), YOffset.fixed(160))));
        register(context, FLUORESCENT_LIGHT_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FLUORESCENT_LIGHT_KEY),
                modifiersWithCount(12, level0Ceiling, lightBlockModifier));
        register(context, FLUORESCENT_LIGHT_FLICKERING_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FLUORESCENT_LIGHT_FLICKERING_KEY),
                modifiersWithRarity(48, level0Ceiling, lightBlockModifier));
        register(context, CONCRETE_PUDDLE_INDIVIDUAL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.CONCRETE_PUDDLE_KEY),
                modifiersWithCount(8, level1Floor));

        register(context, LEVEL_0_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_0_WALL_KEY),
                modifiersWithCount(8, level0Floor, level0WallBlockModifier));
        register(context, LEVEL_0_THIN_STRAIGHT_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_0_THIN_STRAIGHT_WALL_KEY),
                modifiersWithCount(14, level0Floor, level0WallBlockModifier));
        register(context, LEVEL_0_THIN_CROOKED_WALL_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_0_THIN_CROOKED_WALL_KEY),
                modifiersWithCount(12, level0Floor, level0WallBlockModifier));

        register(context, LEVEL_1_WALL_LIGHTS_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_WALL_LIGHTS_KEY),
                modifiersWithCount(14, level1LightPlacement));
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
                modifiersWithCount(3, level1Ceiling));
        register(context, LEVEL_1_REBAR_CONCRETE_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_REBAR_CONCRETE_KEY),
                modifiersWithCount(2, level1Ceiling));
        register(context, LEVEL_1_LOOT_CHEST_PLACED_KEY,
                configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.LEVEL_1_LOOT_CHEST_KEY),
                List.of(RarityFilterPlacementModifier.of(8), SquarePlacementModifier.of(), level1OnFloor, findAdjacentWall, BiomePlacementModifier.of()));
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

    private static PlacementModifier makeFloorPlacedFeatureBlockModifier(Block block1, Block block2) {
        return BlockFilterPlacementModifier.of(BlockPredicate.bothOf(
                BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), block1),
                BlockPredicate.matchingBlocks(block2)));
    }

    private static PlacementModifier makeFloorPlacedFeatureBlockModifier(Block block) {
        return makeFloorPlacedFeatureBlockModifier(block, Blocks.AIR);
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

    private static List<PlacementModifier> modifiersNotSquare(PlacementModifier countModifier, PlacementModifier heightModifier, PlacementModifier blockPlacementModifier) {
        return List.of(countModifier, heightModifier, blockPlacementModifier, BiomePlacementModifier.of());
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
