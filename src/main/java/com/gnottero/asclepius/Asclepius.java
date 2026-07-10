package com.gnottero.asclepius;

import com.gnottero.asclepius.registry.*;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Asclepius implements ModInitializer {
	public static final String MOD_ID = "asclepius";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Each registry class registers its actual content at static-init time, via its
	 * {@code public static final} field initializers — {@code registerAll()} calls
	 * below only trigger that class-loading (in this fixed order) and run
	 * side-effecting hookups (event listeners, dispenser behaviors, log output).
	 * The order is a real dependency chain, not cosmetic grouping: Components must
	 * load before Blocks/Items (both reference component types in their
	 * {@code Properties}), Blocks before BlockEntities (which reference
	 * {@code AsclepiusBlocks} fields directly), and both before Recipes/Tags/Loot
	 * Tables (which reference blocks/items/components as recipe/tag/table content).
	 * {@code AsclepiusDecorativeBlocks} runs after Items rather than alongside Blocks
	 * because — like Items — it registers its own creative tab, and it has no
	 * BlockEntities/DispenserBehaviors/Events depending on it.
	 */
	@Override
	public void onInitialize() {
		AsclepiusComponents.registerAll();

		AsclepiusBlocks.registerAll();
		AsclepiusBlockEntities.registerAll();
		AsclepiusItems.registerAll();
		AsclepiusDecorativeBlocks.registerAll();
		AsclepiusDispenserBehaviors.registerAll();
		AsclepiusEvents.registerAll();

		AsclepiusLootTables.registerAll();
		AsclepiusTags.registerAll();
		AsclepiusRecipes.registerAll();

		LOGGER.info("[{}]> Loaded successfully", MOD_ID);
	}
}