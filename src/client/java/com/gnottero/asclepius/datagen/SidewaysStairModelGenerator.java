package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.decorative_blocks.SidewaysStairBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

import static com.gnottero.asclepius.datagen.DatagenUtils.*;
import static com.gnottero.asclepius.datagen.VerticalSlabModelGenerator.getTextures;

/**
 * Generates block-state definitions and models for sideways stair blocks.
 */
public class SidewaysStairModelGenerator {

    public static final ModelTemplate SIDEWAYS_STAIR = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "block/sideways_stairs")),
            Optional.empty(),
            TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );

    public static final ModelTemplate SIDEWAYS_STAIR_FLIPPED = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "block/sideways_stairs_flipped")),
            Optional.empty(),
            TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );

    private static BlockModelDefinitionGenerator createSidewaysStairBlockStates(
            Block block,
            Identifier normalModel,
            Identifier flippedModel
    ) {
        var normal = BlockModelGenerators.plainVariant(normalModel);
        var flipped = BlockModelGenerators.plainVariant(flippedModel);

        return MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(SidewaysStairBlock.FACING, SidewaysStairBlock.FLIPPED)
                        .select(Direction.SOUTH, false, normal.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.SOUTH, true,  flipped.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.WEST,  false, normal.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.WEST,  true,  flipped.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.NORTH, false, normal.with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.NORTH, true,  flipped.with(BlockModelGenerators.UV_LOCK))
                        .select(Direction.EAST,  false, normal.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))
                        .select(Direction.EAST,  true,  flipped.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))
                );
    }

    /**
     * Registers assets for sideways stairs derived from a vanilla slab.
     */
    public static void registerSidewaysStairFromVanillaSlab(
            BlockModelGenerators generator,
            Block sidewaysStair,
            Block vanillaSlab
    ) {
        Identifier fullBlockModel = getFullBlockModel(vanillaSlab);
        registerSidewaysStair(generator, sidewaysStair, getTextures(vanillaSlab, fullBlockModel));
    }

    /**
     * Registers assets for sideways stairs derived from a custom base block.
     */
    public static void registerSidewaysStairFromBaseBlock(
            BlockModelGenerators generator,
            Block sidewaysStair,
            Block baseBlock
    ) {
        Identifier fullBlockModel = ModelLocationUtils.getModelLocation(baseBlock);
        registerSidewaysStair(generator, sidewaysStair, getTextures(baseBlock, fullBlockModel));
    }

    private static void registerSidewaysStair(
            BlockModelGenerators generator,
            Block sidewaysStair,
            TextureMapping textures
    ) {
        var normalModel = SIDEWAYS_STAIR.create(sidewaysStair, textures, generator.modelOutput);

        var flippedModelId = ModelLocationUtils.getModelLocation(sidewaysStair).withSuffix("_flipped");
        var flippedModel = SIDEWAYS_STAIR_FLIPPED.create(flippedModelId, textures, generator.modelOutput);

        generator.blockStateOutput.accept(createSidewaysStairBlockStates(sidewaysStair, normalModel, flippedModel));
        generator.registerSimpleItemModel(sidewaysStair, normalModel);
    }
}
