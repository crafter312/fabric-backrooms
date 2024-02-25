package net.thesquire.backroomsmod.dimension;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.thesquire.backroomsmod.util.ModUtils;

/**
 * See {@link net.minecraft.server.command.WeatherCommand} for examples on how to set the weather
* */
public class Level11Weather {

    /**
     * Turns out one of the reasons why weather doesn't work in custom dimensions is because the {@code worldProperties}
     * member variable of {@link ServerWorld} is an instance of {@link net.minecraft.world.level.UnmodifiableLevelProperties}
     * instead of {@link LevelProperties}. This class does nothing when you try to change any of its member variables.
     * Additionally, said instance of {@link net.minecraft.world.level.UnmodifiableLevelProperties} contains an instance
     * of {@link LevelProperties}, which seems to in fact be some kind of reference to the same {@link LevelProperties} instance
     * used to create the overworld dimension. This means that every time the weather is changed in the overworld, the
     * weather changes are mirrored in the {@link net.minecraft.world.level.UnmodifiableLevelProperties} object for each
     * additional dimension. To fix this, I need to somehow modify {@link net.minecraft.world.level.UnmodifiableLevelProperties}
     * to contain separate member variables for each of the relevant weather parameters, which can then be set via "setter"
     * functions.
    * */
    //TODO modify UnmodifiableLevelProperties class with mixin to add local member variables for all relevant weather parameters

    public static void handleWeather(ServerWorld serverWorld) {
        if((serverWorld.getTime() % 200 != 0) || !serverWorld.getRegistryKey().equals(ModDimensionKeys.LEVEL_11)) return;
        ModUtils.messagePlayersInDim(serverWorld, ModDimensionKeys.LEVEL_11, "Is raining: " + serverWorld.getLevelProperties().isRaining());
/*
        // set random rain
        Random random = serverWorld.getRandom();
        ServerWorldProperties properties = (ServerWorldProperties) serverWorld.getLevelProperties();
        if(random.nextBoolean()) {
            //serverWorld.setWeather(0, ServerWorld.RAIN_WEATHER_DURATION_PROVIDER.get(random), true, false);
            properties.setClearWeatherTime(0);
            properties.setRainTime(ServerWorld.RAIN_WEATHER_DURATION_PROVIDER.get(random));
            properties.setRaining(true);
            properties.setThundering(false);
            ModUtils.messagePlayersInDim(serverWorld, ModDimensionKeys.LEVEL_11, "Level 11 weather: rain");
            ModUtils.messagePlayersInDim(serverWorld, ModDimensionKeys.LEVEL_11, "Is raining: " + serverWorld.getLevelProperties().isRaining());
            return;
        }
        //serverWorld.setWeather(ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER.get(random), 0, false, false);
        properties.setClearWeatherTime(ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER.get(random));
        properties.setRainTime(0);
        properties.setRaining(false);
        properties.setThundering(false);
        ModUtils.messagePlayersInDim(serverWorld, ModDimensionKeys.LEVEL_11, "Level 11 weather: clear");
        ModUtils.messagePlayersInDim(serverWorld, ModDimensionKeys.LEVEL_11, "Is raining: " + serverWorld.getLevelProperties().isRaining());
 */
    }

}
