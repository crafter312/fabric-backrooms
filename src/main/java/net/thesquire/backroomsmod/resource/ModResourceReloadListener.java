package net.thesquire.backroomsmod.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.BackroomsMod;

import java.util.Collection;
import java.util.Collections;

public class ModResourceReloadListener implements ModInitializer {

    private static boolean clientResources = false;
    private static boolean serverResources = false;

    @Override
    public void onInitialize() {
        setupClientReloadListeners();
        setupServerReloadListeners();

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (!clientResources && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                throw new AssertionError("Client reload listener was not called.");
            }

            if (!serverResources) {
                throw new AssertionError("Server reload listener was not called.");
            }
        });
    }

    private void setupClientReloadListeners() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return BackroomsMod.makeId("client_second");
            }

            @Override
            public void reload(ResourceManager manager) {
                if (!clientResources) {
                    throw new AssertionError("Second reload listener was called before the first!");
                }
            }

            @Override
            public Collection<Identifier> getFabricDependencies() {
                return Collections.singletonList(BackroomsMod.makeId("client_first"));
            }
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return BackroomsMod.makeId("client_first");
            }

            @Override
            public void reload(ResourceManager manager) {
                clientResources = true;
            }
        });
    }

    private void setupServerReloadListeners() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return BackroomsMod.makeId("server_second");
            }

            @Override
            public void reload(ResourceManager manager) {
                if (!serverResources) {
                    throw new AssertionError("Second reload listener was called before the first!");
                }
            }

            @Override
            public Collection<Identifier> getFabricDependencies() {
                return Collections.singletonList(BackroomsMod.makeId("server_first"));
            }
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return BackroomsMod.makeId("server_first");
            }

            @Override
            public void reload(ResourceManager manager) {
                serverResources = true;
            }
        });
    }

}
