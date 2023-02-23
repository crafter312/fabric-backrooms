package net.thesquire.backroomsmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.PersistentStateManager;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.event.ModGameEvents;
import net.thesquire.backroomsmod.item.ModItems;
import net.thesquire.backroomsmod.portal.util.PortalStorage;
import net.thesquire.backroomsmod.recipe.ModRecipes;
import net.thesquire.backroomsmod.screen.ModClientGuis;
import net.thesquire.backroomsmod.screen.ModGuis;
import net.thesquire.backroomsmod.sound.ModSounds;
import net.thesquire.backroomsmod.util.ModServerboundPackets;
import net.thesquire.backroomsmod.util.mixin.MixinCallbacks;
import net.thesquire.backroomsmod.world.ModBiomes;
import net.thesquire.backroomsmod.world.ModWorldGen;
import net.thesquire.backroomsmod.world.feature.ModConfiguredFeatures;
import net.thesquire.backroomsmod.world.feature.ModFeatures;
import net.thesquire.backroomsmod.world.feature.ModPlacedFeatures;
import net.thesquire.backroomsmod.world.gen.ModDensityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

//TODO make light flickering behavior more rare
//TODO add level 1 water puddle feature with dripping water
//TODO modify GridWalls density function to add passageways
//TODO test different smooth noise type for GridWalls density function

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

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(ModDimensionKeys.LEVEL_0)).getPersistentStateManager();
			portalStorage = persistentStateManager.getOrCreate(PortalStorage::createFromNBT, PortalStorage::new, MOD_ID);
			portalStorage.markDirty();
		});

		ModServerboundPackets.registerServerboundPackets();
		ModDensityFunctions.registerModDensityFunctions();
		ModFeatures.registerModFeatures();
		ModGuis.registerGuis();
		ModSounds.registerModSounds();
		ModGameEvents.registerModGameEvents();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		MixinCallbacks.registerCallbacks();
		ModWorldGen.addModWorldGen();
		ModBlockEntities.registerBlockEntities();
		ModRecipes.registerRecipes();
	}

}
