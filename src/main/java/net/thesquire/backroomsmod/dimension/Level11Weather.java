package net.thesquire.backroomsmod.dimension;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.level.LevelProperties;

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
     * additional dimension. To fix this, I modified {@link net.minecraft.world.level.UnmodifiableLevelProperties}
     * to contain member variables for each relevant weather parameter, along with the corresponding getters and setters.
    * */

    //TODO add config option for weather change time
    public static void handleWeather(ServerWorld serverWorld) {
        if((serverWorld.getTime() % 200 != 0) || !serverWorld.getRegistryKey().equals(ModDimensionKeys.LEVEL_11)) return;

        // set random rain
        Random random = serverWorld.getRandom();
        if(random.nextBoolean()) {
            serverWorld.setWeather(0, ServerWorld.RAIN_WEATHER_DURATION_PROVIDER.get(random), true, false);
            return;
        }
        serverWorld.setWeather(ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER.get(random), 0, false, false);
    }

}
