package com.gnottero.asclepius.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AsclepiusDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(AsclepiusRecipeProvider::new);
        pack.addProvider(AsclepiusModelProvider::new);
        pack.addProvider(AsclepiusBlockLootTableProvider::new);
        pack.addProvider((output, registries) -> new AsclepiusBlockTagProvider(output, registries));
        pack.addProvider((output, registries) -> new AsclepiusItemTagProvider(output, registries));
    }
}
