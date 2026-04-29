package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class AsclepiusBlockLootTableProvider extends FabricBlockLootSubProvider {

    public AsclepiusBlockLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        this.dropSelf(AsclepiusBlocks.TERU_TERU_BOZU);
        this.dropSelf(AsclepiusBlocks.PALE_ALTAR);
    }
}
