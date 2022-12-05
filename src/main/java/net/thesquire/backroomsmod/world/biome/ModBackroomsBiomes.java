package net.thesquire.backroomsmod.world.biome;

import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
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

    private static Biome biome(Biome.Precipitation precipitation, float temperature,
                               float downfall, SpawnSettings.Builder spawnBuilder, GenerationSettings.Builder biomeBuilder,
                               @Nullable MusicSound music)
    {
        return biome(precipitation, temperature, downfall, 4159204, 329011,
                spawnBuilder, biomeBuilder, music);
    }

    private static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall,
                               int waterColor, int waterFogColor, SpawnSettings.Builder spawnBuilder,
                               GenerationSettings.Builder biomeBuilder, @Nullable MusicSound music)
    {
        return (new Biome.Builder()).precipitation(precipitation).temperature(temperature)
                .downfall(downfall).effects((new BiomeEffects.Builder()).waterColor(waterColor)
                        .waterFogColor(waterFogColor).fogColor(12638463).skyColor(calculateSkyColor(temperature))
                        .moodSound(BiomeMoodSound.CAVE).music(music).build())
                .spawnSettings(spawnBuilder.build()).generationSettings(biomeBuilder.build()).build();
    }

    private static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall,
                               int waterColor, int waterFogColor, SpawnSettings.Builder spawnBuilder,
                               GenerationSettings.Builder biomeBuilder, @Nullable MusicSound music, SoundEvent loop)
    {
        return (new Biome.Builder()).precipitation(precipitation).temperature(temperature)
                .downfall(downfall).effects((new BiomeEffects.Builder()).waterColor(waterColor)
                        .waterFogColor(waterFogColor).fogColor(12638463).skyColor(calculateSkyColor(temperature))
                        .moodSound(BiomeMoodSound.CAVE).loopSound(loop).music(music).build())
                .spawnSettings(spawnBuilder.build()).generationSettings(biomeBuilder.build()).build();
    }

    /**
     * Below is where all custom biomes are created,
     * including custom features for those biomes as well.
     * Note that these features should ONLY be added here,
     * and not afterwords in the {@link net.thesquire.backroomsmod.world.ModWorldGen}
     * class!
     */

    public static Biome level0() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();

        GenerationSettings.Builder biomeBuilder = new GenerationSettings.Builder();
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_CROOKED_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_STRAIGHT_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.FLUORESCENT_LIGHT_FLICKERING_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.FLUORESCENT_LIGHT_PLACED);

        return biome(Biome.Precipitation.NONE, 0.8f, 0f, 4159204,
                0x919191, spawnBuilder, biomeBuilder, NO_MUSIC, ModSounds.LEVEL_0_LOOP);
    }

    public static Biome level0dark() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);

        GenerationSettings.Builder biomeBuilder = new GenerationSettings.Builder();
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_CROOKED_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_THIN_STRAIGHT_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.LEVEL_0_WALL_PLACED);
        biomeBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ModPlacedFeatures.FLUORESCENT_LIGHT_FLICKERING_PLACED);

        return biome(Biome.Precipitation.NONE, 0.8f, 0f, 4159204,
                0x919191, spawnBuilder, biomeBuilder, LEVEL_0_DARK_MUSIC);
    }

}
