package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusDecorativeBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

/**
 * Loot table provider for all decorative block-family drops.
 */
public class AsclepiusDecorativeBlockLootTableProvider extends FabricBlockLootSubProvider {

    protected AsclepiusDecorativeBlockLootTableProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        AsclepiusDecorativeBlocks.VERTICAL_SLABS.values().forEach(this::dropSelf);
        AsclepiusDecorativeBlocks.SLABS.values().forEach(this::dropSelf);
        AsclepiusDecorativeBlocks.STAIRS.values().forEach(this::dropSelf);
        AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.values().forEach(this::dropSelf);
        AsclepiusDecorativeBlocks.WALLS.values().forEach(this::dropSelf);
    }

    // Overridden because FabricBlockLootSubProvider#getName() isn't distinct per
    // subclass by default, and this pack already has one block loot table provider.
    @Override
    public String getName() {
        return "Asclepius Decorative Block Loot Tables";
    }
}
