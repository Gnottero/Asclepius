package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.mixin.client.ItemModelGeneratorsAccessor;
import com.gnottero.asclepius.registry.AsclepiusItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public class AsclepiusModelProvider extends FabricModelProvider {

    public AsclepiusModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        // Teru teru bozu uses a hand-crafted Blockbench model + virtual entity rendering,
        // so we skip block state generation for it (it renders as BARRIER server-side).
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        var accessor = (ItemModelGeneratorsAccessor) generators;
        var itemOutput = accessor.getItemModelOutput();
        var modelOutput = accessor.getModelOutput();

        generateFlatHandheld(AsclepiusItems.WOODEN_PAXEL, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.STONE_PAXEL, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.COPPER_PAXEL, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.IRON_PAXEL, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.GOLDEN_PAXEL, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.DIAMOND_PAXEL, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.NETHERITE_PAXEL, itemOutput, modelOutput);

        generateFlatHandheld(AsclepiusItems.WOODEN_HAMMER, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.STONE_HAMMER, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.COPPER_HAMMER, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.IRON_HAMMER, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.GOLDEN_HAMMER, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.DIAMOND_HAMMER, itemOutput, modelOutput);
        generateFlatHandheld(AsclepiusItems.NETHERITE_HAMMER, itemOutput, modelOutput);

        generateFlat(AsclepiusItems.ENDER_KEY, itemOutput, modelOutput);
        generateFlat(AsclepiusItems.RECALL_EYE, itemOutput, modelOutput);

        itemOutput.accept(
                AsclepiusItems.TERU_TERU_BOZU,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/teru_teru_bozu"))
        );
        itemOutput.accept(
                AsclepiusItems.PALE_ALTAR,
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/pale_altar"))
        );
    }

    private static void generateFlatHandheld(Item item, ItemModelOutput itemOutput,
                                              BiConsumer<Identifier, ModelInstance> modelOutput) {
        var id = ModelTemplates.FLAT_HANDHELD_ITEM.create(item, TextureMapping.layer0(item), modelOutput);
        itemOutput.accept(item, ItemModelUtils.plainModel(id));
    }

    private static void generateFlat(Item item, ItemModelOutput itemOutput,
                                      BiConsumer<Identifier, ModelInstance> modelOutput) {
        var id = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(item), modelOutput);
        itemOutput.accept(item, ItemModelUtils.plainModel(id));
    }
}
