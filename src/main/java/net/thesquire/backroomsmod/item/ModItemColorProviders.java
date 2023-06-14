package net.thesquire.backroomsmod.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.thesquire.backroomsmod.BackroomsMod;

@Environment(EnvType.CLIENT)
public class ModItemColorProviders {

    public static void registerModItemColorProviders() {
        BackroomsMod.LOGGER.info("Registring item color providers for " + BackroomsMod.MOD_ID);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : 0xE7DB9F, ModItems.ALMOND_WATER);
    }

}
