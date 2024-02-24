package net.thesquire.backroomsmod.client.render.dimension;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;

public class ModDimensionRendering {

    public static void registerDimensionRenderers() {
        DimensionRenderingRegistry.registerWeatherRenderer(ModDimensionKeys.LEVEL_11, Level11WeatherRenderer::render);

        BackroomsMod.LOGGER.info("Registering dimension renderers for " + BackroomsMod.MOD_ID);
    }

}
