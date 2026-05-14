package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.block.ChunkLoaderBlock;
import com.gnottero.asclepius.block.GenericTexturedBlock;
import com.gnottero.asclepius.block.PaleAltarBlock;
import com.gnottero.asclepius.block.TeruTeruBozuBlock;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AsclepiusBlocks {

    public static final TeruTeruBozuBlock TERU_TERU_BOZU = register(
            "teru_teru_bozu",
            Blocks.LANTERN,
            (properties, ignored) -> new TeruTeruBozuBlock(properties.noOcclusion())
    );

    public static final PaleAltarBlock PALE_ALTAR = register(
            "pale_altar",
            Blocks.AMETHYST_BLOCK,
            (properties, ignored) -> new PaleAltarBlock(properties.noOcclusion())
    );

    public static final GenericTexturedBlock VOLCANIC_ASH = register(
            "volcanic_ash",
            Blocks.SAND,
            (properties, ignored) -> new GenericTexturedBlock(properties, BlockModelType.FULL_BLOCK, "block/volcanic_ash")
    );

    public static final GenericTexturedBlock SHALE = register(
            "shale",
            Blocks.DEEPSLATE,
            (properties, ignored) -> new GenericTexturedBlock(properties, BlockModelType.FULL_BLOCK, "block/shale")
    );

    public static final GenericTexturedBlock PACKED_SHALE = register(
            "packed_shale",
            Blocks.DEEPSLATE,
            (properties, ignored) -> new GenericTexturedBlock(properties.strength(6.0F, 9.0F), BlockModelType.FULL_BLOCK, "block/packed_shale")
    );

    public static final ChunkLoaderBlock CHUNK_LOADER = register(
            "chunk_loader",
            Blocks.LODESTONE,
            (properties, ignored) -> new ChunkLoaderBlock(properties, BlockModelType.FULL_BLOCK, "block/chunk_loader")
    );

    public static <T extends Block, Y extends Block> T register(String path, Y copyFrom, BiFunction<BlockBehaviour.Properties, Y, T> function) {
        return register(path, BlockBehaviour.Properties.ofFullCopy(copyFrom), (properties) -> function.apply(properties, copyFrom));
    }

    public static <T extends Block> T register(String path, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, T> function) {
        var id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path);
        var item = function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
        if (item == null) {
            return null;
        }
        return Registry.register(BuiltInRegistries.BLOCK, id, item);
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Blocks");
    }
}