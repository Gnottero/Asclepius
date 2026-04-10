package com.gnottero.asclepius;

import com.gnottero.asclepius.item.PaxelItem;
import com.gnottero.asclepius.registry.*;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Asclepius implements ModInitializer {
	public static final String MOD_ID = "asclepius";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		AsclepiusLootTables.registerAll();
		AsclepiusTags.registerAll();

		AsclepiusItems.registerAll();
		AsclepiusBlocks.registerAll();
		AsclepiusDispenserBehaviors.registerAll();

		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> " + "Loaded successfully");
	}
}