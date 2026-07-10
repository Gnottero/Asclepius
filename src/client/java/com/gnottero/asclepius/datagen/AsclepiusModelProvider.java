package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.mixin.client.ItemModelGeneratorsAccessor;
import com.gnottero.asclepius.registry.AsclepiusBlocks;
import com.gnottero.asclepius.registry.AsclepiusDecorativeBlocks;
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
import net.minecraft.core.registries.BuiltInRegistries;
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
        generateDecorativeBlockModels(generators);
    }

    // -------------------------------------------------------------------------
    // Decorative block-family models
    // Kept as a separate method (rather than a second FabricModelProvider) because
    // vanilla's ModelProvider#getName() is final — a second provider instance would
    // collide with this one under the same pack.
    // -------------------------------------------------------------------------

    private static void generateDecorativeBlockModels(BlockModelGenerators generators) {
        // 1. Vanilla slabs -> Register vertical slab and sideways stairs
        AsclepiusDecorativeBlocks.VANILLA_SLAB_BLOCKS.forEach(vanillaSlab -> {
            Block vertSlab = AsclepiusDecorativeBlocks.VERTICAL_SLABS.get(vanillaSlab);
            if (vertSlab != null) {
                VerticalSlabModelGenerator.registerVerticalSlabFromVanillaSlab(generators, vertSlab, vanillaSlab);
            }
            Block sidewaysStair = AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.get(vanillaSlab);
            if (sidewaysStair != null) {
                SidewaysStairModelGenerator.registerSidewaysStairFromVanillaSlab(generators, sidewaysStair, vanillaSlab);
            }
        });

        // 2. Custom base blocks -> Register custom slab, stairs, sideways stairs, vertical slab, and wall
        AsclepiusDecorativeBlocks.CUSTOM_BASE_BLOCKS.forEach(baseBlock -> {
            new DecorativeFamilyProvider(generators, baseBlock)
                    .slab(AsclepiusDecorativeBlocks.SLABS.get(baseBlock))
                    .stairs(AsclepiusDecorativeBlocks.STAIRS.get(baseBlock))
                    .sidewaysStair(AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.get(baseBlock))
                    .verticalSlab(AsclepiusDecorativeBlocks.VERTICAL_SLABS.get(baseBlock))
                    .wall(AsclepiusDecorativeBlocks.WALLS.get(baseBlock));
        });
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
        generateFlat(AsclepiusItems.GAIA_EYE, itemOutput, modelOutput);

        generateFlat(AsclepiusItems.GAIA_INGOT, itemOutput, modelOutput);
        generateFlat(AsclepiusItems.ANCIENT_INGOT, itemOutput, modelOutput);
        generateFlat(AsclepiusItems.CRYSTALIZED_EXPERIENCE, itemOutput, modelOutput);

        generateFlat(AsclepiusItems.ANCIENT_SQUID_RELIC, itemOutput, modelOutput);

        registerBlockItemModel(AsclepiusBlocks.TERU_TERU_BOZU, AsclepiusItems.TERU_TERU_BOZU, itemOutput);
        registerBlockItemModel(AsclepiusBlocks.PALE_ALTAR, AsclepiusItems.PALE_ALTAR, itemOutput);
        registerBlockItemModel(AsclepiusBlocks.VOLCANIC_ASH, AsclepiusItems.VOLCANIC_ASH, itemOutput);
        registerBlockItemModel(AsclepiusBlocks.SHALE, AsclepiusItems.SHALE, itemOutput);
        registerBlockItemModel(AsclepiusBlocks.PACKED_SHALE, AsclepiusItems.PACKED_SHALE, itemOutput);
        registerBlockItemModel(AsclepiusBlocks.CHUNK_LOADER, AsclepiusItems.CHUNK_LOADER, itemOutput);
    }

    // -------------------------------------------------------------------------
    // Block model helpers
    // These only write the model JSON — the matching blockstate file (referencing
    // the generated model by registry name) is hand-authored under
    // assets/asclepius/blockstates/.
    // -------------------------------------------------------------------------

    /**
     * Full cube — every face uses the same texture.
     * Texture resolved from block registry name automatically:
     *   asclepius:my_block → textures/block/my_block.png
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

    // Points the item model at the block model generated above by deriving the
    // path from the block's own registry name, instead of a hand-typed string
    // literal that could silently drift from the block if either is renamed.
    private static void registerBlockItemModel(Block block, Item item, ItemModelOutput itemOutput) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        itemOutput.accept(item, ItemModelUtils.plainModel(blockId.withPrefix("block/")));
    }
}