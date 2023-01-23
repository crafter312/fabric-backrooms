package net.thesquire.backroomsmod.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.thesquire.backroomsmod.sound.ModSounds;
import net.thesquire.backroomsmod.world.feature.ModPlacedFeatures;
import org.jetbrains.annotations.Nullable;

public class ModBackroomsBiomes {

    @Nullable
    private static final MusicSound NORMAL_MUSIC = null;
    private static final MusicSound NO_MUSIC = new MusicSound(ModSounds.NO_MUSIC, Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    private static final MusicSound LEVEL_0_DARK_MUSIC = new MusicSound(ModSounds.LEVEL_0_DARK_MUSIC, 2000, 14000, false);

    protected static int calculateSkyColor(float color)
    {
        float $$1 = color / 3.0F;
        $$1 = MathHelper.clamp($$1, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

    /**
     * Below is where all custom biomes are created,
     * including custom features for those biomes as well.
     * Note that these features should ONLY be added here,
     * and not afterwords in the {@link net.thesquire.backroomsmod.world.ModWorldGen}
     * class!
     */

    private static final int defaultFogColor = 12638463;
    private static final int darkFogColor = 0x919191;
    private static final int defaultWaterFogColor = 329011;
    private static final int defaultWaterColor = 4159204;
    private static final float defaultTemperature = 0.8f;

    private static Biome.Builder defaultBiomeSettings() {
        return (new Biome.Builder())
                .precipitation(Biome.Precipitation.NONE)
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

    public static Biome level0() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();

        GenerationSettings.Builder biomeBuilder = new GenerationSettings.Builder();
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_CROOKED_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_STRAIGHT_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.FLUORESCENT_LIGHT_FLICKERING_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.FLUORESCENT_LIGHT_PLACED);

        return defaultBiomeSettings()
                .effects(defaultBiomeEffects()
                        .fogColor(darkFogColor)
                        .loopSound(ModSounds.LEVEL_0_LOOP)
                        .build())
                .spawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public static Biome level0dark() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);

        GenerationSettings.Builder biomeBuilder = new GenerationSettings.Builder();
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_CROOKED_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_STRAIGHT_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.FLUORESCENT_LIGHT_FLICKERING_PLACED);

        return defaultBiomeSettings()
                .effects(defaultBiomeEffects()
                        .fogColor(darkFogColor)
                        .music(LEVEL_0_DARK_MUSIC)
                        .build())
                .spawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    public static Biome level1() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);

        GenerationSettings.Builder biomeBuilder = new GenerationSettings.Builder();

        return defaultBiomeSettings()
                .effects(defaultBiomeEffects()
                        .fogColor(darkFogColor)
                        .build())
                .spawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

}
