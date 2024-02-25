package net.thesquire.backroomsmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.config.ModConfig;
import net.thesquire.backroomsmod.dimension.Level11Weather;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.event.ModGameEvents;
import net.thesquire.backroomsmod.item.ModItemGroup;
import net.thesquire.backroomsmod.item.ModItems;
import net.thesquire.backroomsmod.portal.util.PortalStorage;
import net.thesquire.backroomsmod.recipe.ModRecipes;
import net.thesquire.backroomsmod.screen.ModGuis;
import net.thesquire.backroomsmod.sound.ModSounds;
import net.thesquire.backroomsmod.util.ModServerboundPackets;
import net.thesquire.backroomsmod.util.mixin.MixinCallbacks;
import net.thesquire.backroomsmod.world.ModWorldGen;
import net.thesquire.backroomsmod.world.feature.ModFeatures;
import net.thesquire.backroomsmod.world.feature.placement.ModPlacementModifierTypes;
import net.thesquire.backroomsmod.world.gen.ModDensityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reborncore.common.config.Configuration;

//TODO fix level 2 portal generating close to level 1 portal destination

public class BackroomsMod implements ModInitializer {
	public static final String MOD_ID = "backroomsmod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static PortalStorage portalStorage;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		new Configuration(ModConfig.class, BackroomsMod.MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerWorld serverWorld = server.getWorld(ModDimensionKeys.LEVEL_0);
			if(serverWorld == null) LOGGER.error("Failed to initialize level 0 portal storage", new NullPointerException());
			portalStorage = PortalStorage.get(serverWorld);
			portalStorage.markDirty();
		});

		ServerTickEvents.START_WORLD_TICK.register(Level11Weather::handleWeather);

		ModDimensionKeys.registerDimensionKeys();
		ModServerboundPackets.registerServerboundPackets();
		ModDensityFunctions.registerModDensityFunctions();
		ModPlacementModifierTypes.registerPlacementModifierTypes();
		ModFeatures.registerModFeatures();
		ModGuis.registerGuis();
		ModSounds.registerModSounds();
		ModGameEvents.registerModGameEvents();
		ModItemGroup.registerModItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		MixinCallbacks.registerCallbacks();
		ModWorldGen.addModWorldGen();
		ModBlockEntities.registerBlockEntities();
		ModRecipes.registerRecipes();

		LOGGER.info("Main initialization finished for " + MOD_ID);
	}

	public static Identifier makeId(String path) {
		return new Identifier(MOD_ID, path);
	}

}
