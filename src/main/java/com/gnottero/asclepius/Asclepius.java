package com.gnottero.asclepius;

import com.gnottero.asclepius.registry.AsclepiusBlocks;
import com.gnottero.asclepius.registry.AsclepiusBlockEntities;
import com.gnottero.asclepius.registry.AsclepiusRecipes;
import com.gnottero.asclepius.registry.*;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Asclepius implements ModInitializer {
	public static final String MOD_ID = "asclepius";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		AsclepiusBlocks.registerAll();
		AsclepiusBlockEntities.registerAll();

		AsclepiusItems.registerAll();
		AsclepiusDispenserBehaviors.registerAll();
		AsclepiusEvents.registerAll();
		AsclepiusComponents.registerAll();

		AsclepiusLootTables.registerAll();
		AsclepiusTags.registerAll();
		AsclepiusRecipes.registerAll();

		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> " + "Loaded successfully");
	}
}