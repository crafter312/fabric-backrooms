package net.thesquire.backroomsmod.resource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;

public class ModBuiltinResourcePack implements ModInitializer {

    @Override
    public void onInitialize() {
        // Should always be present as it's **this** mod.

        // this first bit is for texture packs
        //FabricLoader.getInstance().getModContainer(BackroomsMod.MOD_ID)
        //        .map(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(BackroomsMod.MOD_ID, "test"),
        //                container, "Fabric Resource Loader Test Pack", ResourcePackActivationType.NORMAL))
        //        .filter(success -> !success).ifPresent(success -> BackroomsMod.LOGGER.warn("Could not register built-in resource pack with custom name."));

        // this next bit is for data packs
        FabricLoader.getInstance().getModContainer(BackroomsMod.MOD_ID)
                .map(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(BackroomsMod.MOD_ID, "backroomsmod"),
                        container, ResourcePackActivationType.ALWAYS_ENABLED))
                .filter(success -> !success).ifPresent(success -> BackroomsMod.LOGGER.warn("Could not register built-in resource pack."));
    }

}
