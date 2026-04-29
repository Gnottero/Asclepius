package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class AsclepiusItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public AsclepiusItemTagProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(ItemTags.PICKAXES)
                .add(AsclepiusItems.WOODEN_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_HAMMER.builtInRegistryHolder().key())

                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.AXES)
                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.SHOVELS)
                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());
    }
}
