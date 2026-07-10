package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.decorative_blocks.VerticalSlabBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

import static com.gnottero.asclepius.datagen.DatagenUtils.*;

/**
 * Generates block-state definitions and models for vertical slab blocks.
 */
public class VerticalSlabModelGenerator {

    public static final ModelTemplate VERTICAL_SLAB = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "block/vertical_slab")),
            Optional.empty(),
            TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );

    /**
     * Resolves textures for a vertical slab from the source block.
     */
    public static TextureMapping getTextures(Block sourceBlock, Identifier fullBlockModel) {
        var id       = BuiltInRegistries.BLOCK.getKey(sourceBlock);
        var model    = readJson(id, "models/block");
        var textures = model != null ? model.getAsJsonObject("textures") : null;

        if (textures != null
                && textures.has("bottom")
                && textures.has("top")
                && textures.has("side")) {
            return new TextureMapping()
                    .put(TextureSlot.BOTTOM, new Material(parseId(textures.get("bottom").getAsString())))
                    .put(TextureSlot.TOP,    new Material(parseId(textures.get("top").getAsString())))
                    .put(TextureSlot.SIDE,   new Material(parseId(textures.get("side").getAsString())));
        }

        var fullModel    = readModelJson(fullBlockModel);
        var fullTextures = fullModel != null ? fullModel.getAsJsonObject("textures") : null;
        if (fullTextures == null) {
            throw new IllegalStateException(
                    "Missing textures for source " + id + " and fallback " + fullBlockModel);
        }

        var top    = fullTextures.has("top")    ? fullTextures.get("top")    : fullTextures.get("all");
        var bottom = fullTextures.has("bottom") ? fullTextures.get("bottom") : fullTextures.get("all");
        var side   = fullTextures.has("side")   ? fullTextures.get("side")   : fullTextures.get("all");

        if (top == null || bottom == null || side == null) {
            throw new IllegalStateException(
                    "Missing required texture slots for source " + id + " and fallback " + fullBlockModel);
        }

        return new TextureMapping()
                .put(TextureSlot.BOTTOM, new Material(parseId(bottom.getAsString())))
                .put(TextureSlot.TOP,    new Material(parseId(top.getAsString())))
                .put(TextureSlot.SIDE,   new Material(parseId(side.getAsString())));
    }

    private static BlockModelDefinitionGenerator createVerticalSlabBlockStates(
            Block vertSlabBlock,
            Identifier vertSlabId,
            Identifier fullBlockId
    ) {
        var vertSlabModel  = BlockModelGenerators.plainVariant(vertSlabId);
        var fullBlockModel = BlockModelGenerators.plainVariant(fullBlockId);
        return MultiVariantGenerator.dispatch(vertSlabBlock)
                .with(PropertyDispatch.initial(VerticalSlabBlock.FACING, VerticalSlabBlock.DOUBLE)
                        .select(Direction.SOUTH, false, vertSlabModel.with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.SOUTH, true,  fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.WEST,  false, vertSlabModel.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))
                        .select(Direction.WEST,  true,  fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.NORTH, false, vertSlabModel.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.NORTH, true,  fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.EAST,  false, vertSlabModel.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.EAST,  true,  fullBlockModel.with(BlockModelGenerators.UV_LOCK))
                );
    }

    /**
     * Registers assets for vertical slabs derived from a vanilla slab.
     */
    public static void registerVerticalSlabFromVanillaSlab(
            BlockModelGenerators generator,
            Block vertSlab,
            Block vanillaSlab
    ) {
        Identifier fullBlockModel = getFullBlockModel(vanillaSlab);
        registerVerticalSlab(generator, vertSlab, fullBlockModel, getTextures(vanillaSlab, fullBlockModel));
    }

    /**
     * Registers assets for vertical slabs derived from a custom base block.
     */
    public static void registerVerticalSlabFromBaseBlock(
            BlockModelGenerators generator,
            Block vertSlab,
            Block baseBlock
    ) {
        Identifier fullBlockModel = ModelLocationUtils.getModelLocation(baseBlock);
        registerVerticalSlab(generator, vertSlab, fullBlockModel, getTextures(baseBlock, fullBlockModel));
    }

    private static void registerVerticalSlab(
            BlockModelGenerators generator,
            Block vertSlab,
            Identifier fullBlockModel,
            TextureMapping textures
    ) {
        var slabModel = VERTICAL_SLAB.create(vertSlab, textures, generator.modelOutput);
        generator.blockStateOutput.accept(createVerticalSlabBlockStates(vertSlab, slabModel, fullBlockModel));
        generator.registerSimpleItemModel(vertSlab, slabModel);
    }
}