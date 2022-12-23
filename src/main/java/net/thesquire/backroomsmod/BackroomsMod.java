package net.thesquire.backroomsmod;

import net.fabricmc.api.ModInitializer;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.entity.ModBlockEntities;
import net.thesquire.backroomsmod.item.ModItems;
import net.thesquire.backroomsmod.portal.ModPortals;
import net.thesquire.backroomsmod.recipe.ModRecipes;
import net.thesquire.backroomsmod.screen.ModClientGuis;
import net.thesquire.backroomsmod.screen.ModGuis;
import net.thesquire.backroomsmod.util.mixin.MixinCallbacks;
import net.thesquire.backroomsmod.util.ModRegistries;
import net.thesquire.backroomsmod.util.ModServerboundPackets;
import net.thesquire.backroomsmod.world.ModBiomes;
import net.thesquire.backroomsmod.world.ModWorldGen;
import net.thesquire.backroomsmod.world.feature.ModConfiguredFeatures;
import net.thesquire.backroomsmod.world.feature.ModPlacedFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackroomsMod implements ModInitializer {
	public static final String MOD_ID = "backroomsmod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModServerboundPackets.registerServerboundPackets();
		ModGuis.registerGuis();
		ModClientGuis.registerModClientGuis();
		ModBlocks.registerModBlocks();
		ModConfiguredFeatures.registerConfiguredFeatures();
		ModPlacedFeatures.registerPlacedFeatures();
		ModBiomes.registerBiomes();
		ModItems.registerModItems();
		MixinCallbacks.registerCallbacks();
		ModRegistries.registerModContents();
		ModWorldGen.generateModWorldGen();
		ModBlockEntities.registerBlockEntities();
		ModRecipes.registerRecipes();
		ModPortals.registerModPortals();
	}
}
