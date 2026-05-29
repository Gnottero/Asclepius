package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.mixin.client.ItemModelGeneratorsAccessor;
import com.gnottero.asclepius.registry.AsclepiusBlocks;
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
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public class AsclepiusModelProvider extends FabricModelProvider {

    public AsclepiusModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generateFullCubeModel(generators, AsclepiusBlocks.VOLCANIC_ASH);
        generateColumnModel(generators, AsclepiusBlocks.SHALE);
        generateColumnModel(generators, AsclepiusBlocks.PACKED_SHALE);
        generateCubeBottomTopModel(generators, AsclepiusBlocks.CHUNK_LOADER);
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

        generateFlat(AsclepiusItems.ANCIENT_SQUID_RELIC, itemOutput, modelOutput);

        itemOutput.accept(AsclepiusItems.TERU_TERU_BOZU, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/teru_teru_bozu")));
        itemOutput.accept(AsclepiusItems.PALE_ALTAR, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/pale_altar")));
        itemOutput.accept(AsclepiusItems.VOLCANIC_ASH, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/volcanic_ash")));
        itemOutput.accept(AsclepiusItems.SHALE, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/shale")));
        itemOutput.accept(AsclepiusItems.PACKED_SHALE, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/packed_shale")));
        itemOutput.accept(AsclepiusItems.CHUNK_LOADER, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath("asclepius", "block/chunk_loader")));
    }

    // -------------------------------------------------------------------------
    // Polymer block model helpers
    // These only write the model JSON — no blockstate file is generated.
    // The modelId you pass to GenericTexturedBlock must match the output path.
    // -------------------------------------------------------------------------

    /**
     * Full cube — every face uses the same texture.
     * Texture resolved from block registry name automatically:
     *   asclepius:my_block → textures/block/my_block.png
     *
     * Pass the modelId "block/my_block" to GenericTexturedBlock.
     */
    private static void generateFullCubeModel(BlockModelGenerators generators, Block block) {
        var mapping = TextureMapping.cube(block);
        ModelTemplates.CUBE_ALL.create(block, mapping, generators.modelOutput);
    }

    /**
     * Column block — top/bottom share one texture, sides share another.
     * Expected texture files:
     *   textures/block/<name>_top.png
     *   textures/block/<name>_side.png   (or _end for logs)
     *
     * Pass the modelId "block/my_pillar" to GenericTexturedBlock.
     */
    private static void generateColumnModel(BlockModelGenerators generators, Block block) {
        var mapping = TextureMapping.column(block);
        ModelTemplates.CUBE_COLUMN.create(block, mapping, generators.modelOutput);
    }

    /**
     * Cube bottom top model - unique textures for top, bottom, and side.
     * Expected texture files:
     *   textures/block/<name>_top.png
     *   textures/block/<name>_bottom.png
     *   textures/block/<name>_side.png
     */
    private static void generateCubeBottomTopModel(BlockModelGenerators generators, Block block) {
        var mapping = TextureMapping.cubeBottomTop(block);
        ModelTemplates.CUBE_BOTTOM_TOP.create(block, mapping, generators.modelOutput);
    }

    // -------------------------------------------------------------------------
    // Item helpers
    // -------------------------------------------------------------------------

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