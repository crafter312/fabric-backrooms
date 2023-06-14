package net.thesquire.backroomsmod.world.feature;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.*;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.world.feature.custom.*;

import java.util.List;

public class ModConfiguredFeatures {

    // generic features
    public static final RegistryKey<ConfiguredFeature<?, ?>> BISMUTHINITE_ORE_KEY = registerKey("bismuthinite_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FLUORESCENT_LIGHT_KEY = registerKey("fluorescent_light");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FLUORESCENT_LIGHT_FLICKERING_KEY = registerKey("fluorescent_light_flickering");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CONCRETE_PUDDLE_KEY = registerKey("level_1_puddle");

    // level 0 features
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_0_WALL_KEY = registerKey("level_0_wall");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_0_THIN_STRAIGHT_WALL_KEY = registerKey("level_0_thin_straight_wall");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_0_THIN_CROOKED_WALL_KEY = registerKey("level_0_thin_crooked_wall");

    // level 1 features
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_1_WALL_LIGHTS_KEY = registerKey("level_1_wall_lights");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_1_DRIPPING_CONCRETE_KEY = registerKey("level_1_dripping_concrete");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_1_REBAR_CONCRETE_KEY = registerKey("level_1_rebar_concrete");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_1_DRIPPING_REBAR_KEY = registerKey("level_1_dripping_rebar");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_1_PUDDLE_DRIP_KEY = registerKey("level_1_puddle_drip");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LEVEL_1_LOOT_CHEST_KEY = registerKey("level_1_loot_chest");

    public static void bootstrap(Registerable<ConfiguredFeature<?,?>> context) {
        var placedFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldBismuthiniteOres = List.of(
                OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.BISMUTHINITE_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> fluorescentLightTarget = List.of(
                OreFeatureConfig.createTarget(new BlockMatchRuleTest(ModBlocks.CEILING_TILE), ModBlocks.FLUORESCENT_LIGHT.getDefaultState()));
        List<OreFeatureConfig.Target> Level0WallTarget = List.of(
                OreFeatureConfig.createTarget(new BlockMatchRuleTest(Blocks.LIGHT_GRAY_CARPET), ModBlocks.YELLOW_WALLPAPER.getDefaultState()),
                OreFeatureConfig.createTarget(new BlockMatchRuleTest(Blocks.AIR), ModBlocks.YELLOW_WALLPAPER.getDefaultState()));
        List<OreFeatureConfig.Target> Level1PuddleTarget = List.of(
                OreFeatureConfig.createTarget(new BlockStateMatchRuleTest(ModBlocks.WAREHOUSE_CONCRETE.getDefaultState()
                                .with(Properties.WATERLOGGED, false)),
                        ModBlocks.WAREHOUSE_CONCRETE.getDefaultState().with(Properties.WATERLOGGED, true)));
        List<OreFeatureConfig.Target> Level1DrippingConcreteTarget = List.of(
                OreFeatureConfig.createTarget(new BlockStateMatchRuleTest(ModBlocks.PAINTED_WAREHOUSE_CONCRETE.getDefaultState()
                                .with(Properties.DOWN, true)),
                        ModBlocks.PAINTED_WAREHOUSE_CONCRETE.getDefaultState().with(Properties.DOWN, true)
                                .with(ModBlockProperties.DRIPPING, true)));
        List<OreFeatureConfig.Target> Level1RebarConcreteTarget = List.of(
                OreFeatureConfig.createTarget(new BlockStateMatchRuleTest(ModBlocks.PAINTED_WAREHOUSE_CONCRETE.getDefaultState()
                                .with(ModBlockProperties.DRIPPING, false)),
                        ModBlocks.PAINTED_WAREHOUSE_CONCRETE.getDefaultState().with(Properties.DOWN, true)),
                OreFeatureConfig.createTarget(new BlockStateMatchRuleTest(ModBlocks.PAINTED_WAREHOUSE_CONCRETE.getDefaultState()
                                .with(ModBlockProperties.DRIPPING, true)),
                        ModBlocks.PAINTED_WAREHOUSE_CONCRETE.getDefaultState().with(ModBlockProperties.DRIPPING, true)
                                .with(Properties.DOWN, true)));

        register(context, BISMUTHINITE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldBismuthiniteOres, 8, 0.2f));
        register(context, FLUORESCENT_LIGHT_KEY, ModFeatures.BLOCK_GRID, new ModBlockGridFeatureConfig(fluorescentLightTarget,
                UniformIntProvider.create(4, 6), UniformIntProvider.create(4, 6), ConstantIntProvider.create(1), 0.01f));
        register(context, FLUORESCENT_LIGHT_FLICKERING_KEY, ModFeatures.BLOCK_GRID, new ModBlockGridFeatureConfig(fluorescentLightTarget,
                UniformIntProvider.create(2, 4), UniformIntProvider.create(2, 4), ConstantIntProvider.create(1), 0.5f));
        register(context, CONCRETE_PUDDLE_KEY, Feature.ORE, new OreFeatureConfig(Level1PuddleTarget, 30, 0f));

        register(context, LEVEL_0_WALL_KEY, ModFeatures.WALL, new ModSimpleWallFeatureConfig(Level0WallTarget,
                ConstantIntProvider.create(4), UniformIntProvider.create(4, 8), UniformIntProvider.create(4, 8)));
        register(context, LEVEL_0_THIN_STRAIGHT_WALL_KEY, ModFeatures.THIN_WALL, new ModThinWallFeatureConfig(Level0WallTarget,
                UniformIntProvider.create(1, 3), UniformIntProvider.create(8, 12), ConstantIntProvider.create(4), true));
        register(context, LEVEL_0_THIN_CROOKED_WALL_KEY, ModFeatures.THIN_WALL, new ModThinWallFeatureConfig(Level0WallTarget,
                UniformIntProvider.create(3, 6), UniformIntProvider.create(8, 14), ConstantIntProvider.create(4), false));

        register(context, LEVEL_1_WALL_LIGHTS_KEY, ModFeatures.WALL_MOUNTABLE, new ModWallMountableFeatureConfig(
                ModBlocks.MOUNTABLE_FLUORESCENT_LIGHT.getDefaultState(), ConstantIntProvider.create(18), 0.02f, 0.1f));
        register(context, LEVEL_1_DRIPPING_CONCRETE_KEY, Feature.REPLACE_SINGLE_BLOCK, new EmeraldOreFeatureConfig(Level1DrippingConcreteTarget));
        register(context, LEVEL_1_REBAR_CONCRETE_KEY, ModFeatures.FLAT_ORE_FEATURE,
                new OreFeatureConfig(Level1RebarConcreteTarget, 10, 0f));
        register(context, LEVEL_1_DRIPPING_REBAR_KEY, ModFeatures.DOUBLE_FEATURE, new ModDoubleFeatureConfig(
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_REBAR_CONCRETE_SINGLE_PLACED_KEY),
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_DRIPPING_CONCRETE_PLACED_KEY),
                new Vec3i(0,0,0)));
        register(context, LEVEL_1_PUDDLE_DRIP_KEY, ModFeatures.DOUBLE_FEATURE, new ModDoubleFeatureConfig(
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_DRIPPING_REBAR_PLACED_KEY),
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_PUDDLE_PLACED_KEY),
                new Vec3i(0, -5, 0)));
        register(context, LEVEL_1_LOOT_CHEST_KEY, ModFeatures.LOOT_CHEST,
                new ModLootChestFeatureConfig(BackroomsMod.makeId("chests/level_1_supplies")));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, BackroomsMod.makeId(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

}
