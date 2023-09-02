package net.thesquire.backroomsmod.world;

import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.sound.ModSounds;
import net.thesquire.backroomsmod.world.feature.ModPlacedFeatures;

public class ModBiomes {

    // biomes in level order
    public static final RegistryKey<Biome> LEVEL_0 = registerKey("level_0");
    public static final RegistryKey<Biome> LEVEL_0_DARK = registerKey("level_0_dark");
    public static final RegistryKey<Biome> LEVEL_1 = registerKey("level_1");
    public static final RegistryKey<Biome> LEVEL_2 = registerKey("level_2");
    public static final RegistryKey<Biome> LEVEL_2_DARK = registerKey("level_2_dark");
    public static final RegistryKey<Biome> LEVEL_4= registerKey("level_4");

    // biome music
    public static final MusicSound NORMAL_MUSIC = null;
    public static final MusicSound NO_MUSIC = new MusicSound(ModSounds.NO_MUSIC, Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    public static final MusicSound LEVEL_0_DARK_MUSIC = new MusicSound(ModSounds.LEVEL_0_DARK_MUSIC, 2000, 14000, false);

    // different biome effect parameters
    private static final int defaultFogColor = 12638463;
    private static final int darkFogColor = 0x919191;
    private static final int defaultWaterFogColor = 329011;
    private static final int defaultWaterColor = 4159204;
    private static final float defaultTemperature = 0.8f;

    public static void bootstrap(Registerable<Biome> context) {
        var placedFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        // level 0 biome settings
        GenerationSettings.Builder level0BiomeBuilder = new GenerationSettings.Builder();
        level0BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_THIN_CROOKED_WALL_PLACED_KEY));
        level0BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_THIN_STRAIGHT_WALL_PLACED_KEY));
        level0BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_WALL_PLACED_KEY));
        level0BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_FLUORESCENT_LIGHT_FLICKERING_PLACED_KEY));
        level0BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_FLUORESCENT_LIGHT_PLACED_KEY));

        // level 0 dark biome settings
        SpawnSettings.Builder level0DarkSpawnBuilder = new SpawnSettings.Builder();
        addBatsAndMonsters(level0DarkSpawnBuilder);

        GenerationSettings.Builder level0DarkBiomeBuilder = new GenerationSettings.Builder();
        level0DarkBiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_THIN_CROOKED_WALL_PLACED_KEY));
        level0DarkBiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_THIN_STRAIGHT_WALL_PLACED_KEY));
        level0DarkBiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_WALL_PLACED_KEY));
        level0DarkBiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_0_FLUORESCENT_LIGHT_FLICKERING_PLACED_KEY));

        // level 1 biome settings
        SpawnSettings.Builder level1SpawnBuilder = new SpawnSettings.Builder();
        addBatsAndMonsters(level1SpawnBuilder);

        GenerationSettings.Builder level1BiomeBuilder = new GenerationSettings.Builder();
        level1BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_WALL_LIGHTS_PLACED_KEY));
        level1BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_PUDDLE_DRIP_PLACED_KEY));
        level1BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.CONCRETE_PUDDLE_INDIVIDUAL_PLACED_KEY));
        level1BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_REBAR_CONCRETE_PLACED_KEY));
        level1BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_1_LOOT_CHEST_PLACED_KEY));

        // level 2 biome settings
        SpawnSettings.Builder level2SpawnBuilder = new SpawnSettings.Builder();

        GenerationSettings.Builder level2BiomeBuilder = new GenerationSettings.Builder();
        level2BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_2_PIPE_NETWORK_PLACED_KEY));
        level2BiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_2_WALL_LIGHTS_PLACED_KEY));

        // level 2 dark biome settings
        SpawnSettings.Builder level2DarkSpawnBuilder = new SpawnSettings.Builder();

        GenerationSettings.Builder level2DarkBiomeBuilder = new GenerationSettings.Builder();
        level2DarkBiomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                placedFeatureRegistryEntryLookup.getOrThrow(ModPlacedFeatures.LEVEL_2_PIPE_NETWORK_PLACED_KEY));

        // level 4 biome settings
        SpawnSettings.Builder level4SpawnBuilder = new SpawnSettings.Builder();

        GenerationSettings.Builder level4BiomeBuilder = new GenerationSettings.Builder();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        register(context, LEVEL_0,
                defaultBiomeSettings()
                        .effects(defaultBiomeEffects().fogColor(darkFogColor).loopSound(ModSounds.LEVEL_0_LOOP).build())
                        .spawnSettings((new SpawnSettings.Builder()).build())
                        .generationSettings(level0BiomeBuilder.build())
                        .build());
        register(context, LEVEL_0_DARK,
                defaultBiomeSettings()
                        .effects(defaultBiomeEffects().fogColor(darkFogColor).music(LEVEL_0_DARK_MUSIC).build())
                        .spawnSettings(level0DarkSpawnBuilder.build())
                        .generationSettings(level0DarkBiomeBuilder.build())
                        .build());
        register(context, LEVEL_1,
                defaultBiomeSettings()
                        .effects(defaultBiomeEffects().fogColor(darkFogColor).build())
                        .spawnSettings(level1SpawnBuilder.build())
                        .generationSettings(level1BiomeBuilder.build())
                        .build());
        register(context, LEVEL_2,
                defaultBiomeSettings()
                        .effects(defaultBiomeEffects().fogColor(darkFogColor).build())
                        .spawnSettings(level2SpawnBuilder.build())
                        .generationSettings(level2BiomeBuilder.build())
                        .build());
        register(context, LEVEL_2_DARK,
                defaultBiomeSettings()
                        .effects(defaultBiomeEffects().fogColor(darkFogColor).build())
                        .spawnSettings(level2DarkSpawnBuilder.build())
                        .generationSettings(level2DarkBiomeBuilder.build())
                        .build());
        register(context, LEVEL_4,
                defaultBiomeSettings()
                        .effects(defaultBiomeEffects().music(MusicType.GAME).build())
                        .spawnSettings(level4SpawnBuilder.build())
                        .generationSettings(level4BiomeBuilder.build())
                        .build());
    }

    private static RegistryKey<Biome> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, BackroomsMod.makeId(name));
    }

    private static void register(Registerable<Biome> context, RegistryKey<Biome> key, Biome biome) {
        context.register(key, biome);
    }

    // biome creation helper functions

    private static int calculateSkyColor(float color) {
        float $$1 = color / 3.0F;
        $$1 = MathHelper.clamp($$1, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

    private static Biome.Builder defaultBiomeSettings() {
        return (new Biome.Builder())
                .precipitation(false)
                .temperature(defaultTemperature)
                .downfall(0f);
    }

    private static BiomeEffects.Builder defaultBiomeEffects() {
        return (new BiomeEffects.Builder())
                .waterColor(defaultWaterColor)
                .waterFogColor(defaultWaterFogColor)
                .fogColor(defaultFogColor)
                .skyColor(calculateSkyColor(defaultTemperature))
                .moodSound(BiomeMoodSound.CAVE)
                .music(NO_MUSIC);
    }

    private static void addMonsters(SpawnSettings.Builder builder) {
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.WITCH, 5, 1, 1));
    }

    private static void addBatsAndMonsters(SpawnSettings.Builder builder) {
        DefaultBiomeFeatures.addCaveMobs(builder);
        addMonsters(builder);
    }

}
