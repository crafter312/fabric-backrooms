package net.thesquire.backroomsmod.world.feature;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.thesquire.backroomsmod.BackroomsMod;

import java.util.List;

public class ModPlacedFeatures {

    public static RegistryEntry<PlacedFeature> BISMUTHINITE_ORE_PLACED;
    public static RegistryEntry<PlacedFeature> FLUORESCENT_LIGHT_PLACED;
    public static RegistryEntry<PlacedFeature> FLUORESCENT_LIGHT_FLICKERING_PLACED;

    public static RegistryEntry<PlacedFeature> LEVEL_0_WALL_PLACED;
    public static RegistryEntry<PlacedFeature> LEVEL_0_THIN_STRAIGHT_WALL_PLACED;
    public static RegistryEntry<PlacedFeature> LEVEL_0_THIN_CROOKED_WALL_PLACED;

    public static RegistryEntry<PlacedFeature> LEVEL_1_WALL_LIGHTS_PLACED;
    public static RegistryEntry<PlacedFeature> LEVEL_1_PUDDLE_PLACED;

    private static final PlacementModifier LIGHT_BLOCK_MODIFIER = BlockFilterPlacementModifier.of(
            BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), List.of(Blocks.AIR)));
    private static final PlacementModifier LEVEL_0_WALL_BLOCK_MODIFIER = makeFloorPlacedFeatureBlockModifier(Blocks.LIGHT_GRAY_CONCRETE);

    private static final PlacementModifier LEVEL_0_FLOOR = HeightRangePlacementModifier.uniform(YOffset.fixed(18), YOffset.fixed(22));
    private static final PlacementModifier LEVEL_0_CEILING = HeightRangePlacementModifier.uniform(YOffset.fixed(23), YOffset.fixed(25));

    private static final PlacementModifier LEVEL_1_LIGHT_PLACEMENT = HeightRangePlacementModifier.uniform(YOffset.fixed(23), YOffset.fixed(23));
    private static final PlacementModifier LEVEL_1_FLOOR = HeightRangePlacementModifier.uniform(YOffset.fixed(20), YOffset.fixed(20));

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void registerPlacedFeatures() {
        System.out.println("Registering placed features for " + BackroomsMod.MOD_ID);

        BISMUTHINITE_ORE_PLACED = PlacedFeatures.register("bismuthinite_ore_placed",
                ModConfiguredFeatures.BISMUTHINITE_ORE,
                modifiersWithCount(8, HeightRangePlacementModifier.trapezoid(YOffset.fixed(70), YOffset.fixed(160))));

        FLUORESCENT_LIGHT_PLACED = PlacedFeatures.register("fluorescent_light_placed",
                ModConfiguredFeatures.FLUORESCENT_LIGHT,
                modifiersWithCount(12, LEVEL_0_CEILING, LIGHT_BLOCK_MODIFIER));

        FLUORESCENT_LIGHT_FLICKERING_PLACED = PlacedFeatures.register("fluorescent_light_flickering_placed",
                ModConfiguredFeatures.FLUORESCENT_LIGHT_FLICKERING,
                modifiersWithRarity(48, LEVEL_0_CEILING, LIGHT_BLOCK_MODIFIER));

        LEVEL_0_WALL_PLACED = PlacedFeatures.register("level_0_wall_placed",
                ModConfiguredFeatures.LEVEL_0_WALL,
                modifiersWithCount(6, LEVEL_0_FLOOR, LEVEL_0_WALL_BLOCK_MODIFIER));

        LEVEL_0_THIN_STRAIGHT_WALL_PLACED = PlacedFeatures.register("level_0_thin_straight_wall_placed",
                ModConfiguredFeatures.LEVEL_0_THIN_STRAIGHT_WALL,
                modifiersWithCount(4, LEVEL_0_FLOOR, LEVEL_0_WALL_BLOCK_MODIFIER));

        LEVEL_0_THIN_CROOKED_WALL_PLACED = PlacedFeatures.register("level_0_thin_crooked_wall_placed",
                ModConfiguredFeatures.LEVEL_0_THIN_CROOKED_WALL,
                modifiersWithCount(7, LEVEL_0_FLOOR, LEVEL_0_WALL_BLOCK_MODIFIER));

        LEVEL_1_WALL_LIGHTS_PLACED = PlacedFeatures.register("level_1_wall_lights_placed",
                ModConfiguredFeatures.LEVEL_1_WALL_LIGHTS,
                modifiersWithCount(16, LEVEL_1_LIGHT_PLACEMENT));

        LEVEL_1_PUDDLE_PLACED = PlacedFeatures.register("level_1_puddle_placed",
                ModConfiguredFeatures.LEVEL_1_PUDDLE,
                modifiersWithCount(12, LEVEL_1_FLOOR));

    }

    private static PlacementModifier makeFloorPlacedFeatureBlockModifier(Block block) {
        return BlockFilterPlacementModifier.of(BlockPredicate.bothOf(
                BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), List.of(block)),
                BlockPredicate.not(BlockPredicate.matchingBlocks(BlockPos.ORIGIN, List.of(block)))));
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
