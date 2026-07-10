package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.chunk_loader.ChunkLoaderBlock;
import com.gnottero.asclepius.feature.pale_altar.PaleAltarBlock;
import com.gnottero.asclepius.feature.teru_teru_bozu.TeruTeruBozuBlock;
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
            Blocks.AMETHYST_BLOCK,
            (properties, ignored) -> new TeruTeruBozuBlock(properties.noOcclusion())
    );

    public static final PaleAltarBlock PALE_ALTAR = register(
            "pale_altar",
            Blocks.AMETHYST_BLOCK,
            (properties, ignored) -> new PaleAltarBlock(properties.noOcclusion())
    );

    public static final Block VOLCANIC_ASH = register(
            "volcanic_ash",
            Blocks.SAND,
            (properties, ignored) -> new Block(properties)
    );

    public static final Block SHALE = register(
            "shale",
            Blocks.DEEPSLATE,
            (properties, ignored) -> new Block(properties)
    );

    public static final Block PACKED_SHALE = register(
            "packed_shale",
            Blocks.DEEPSLATE,
            (properties, ignored) -> new Block(properties.strength(6.0F, 9.0F))
    );

    public static final ChunkLoaderBlock CHUNK_LOADER = register(
            "chunk_loader",
            Blocks.LODESTONE,
            (properties, ignored) -> new ChunkLoaderBlock(properties)
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