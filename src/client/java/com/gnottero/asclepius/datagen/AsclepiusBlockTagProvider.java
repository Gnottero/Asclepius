package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class AsclepiusBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {

    public AsclepiusBlockTagProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AsclepiusBlocks.TERU_TERU_BOZU_BLOCK.builtInRegistryHolder().key());

        builder(BlockTags.MINEABLE_WITH_AXE)
                .add(AsclepiusBlocks.TERU_TERU_BOZU_BLOCK.builtInRegistryHolder().key());

        builder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(AsclepiusBlocks.TERU_TERU_BOZU_BLOCK.builtInRegistryHolder().key());

        builder(BlockTags.MINEABLE_WITH_HOE)
                .add(AsclepiusBlocks.TERU_TERU_BOZU_BLOCK.builtInRegistryHolder().key());
    }
}
