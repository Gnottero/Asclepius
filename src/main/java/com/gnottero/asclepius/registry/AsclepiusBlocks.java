package com.gnottero.asclepius.registry;
import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.block.BaseBlockItem;
import com.gnottero.asclepius.block.ChunkLoaderBlock;
import com.gnottero.asclepius.block.TeruTeruBozuBlock;
import eu.pb4.polymer.blocks.api.BlockModelType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AsclepiusBlocks {

    private static final List<Block> BLOCKS = new ArrayList<>();

    public static final TeruTeruBozuBlock TERU_TERU_BOZU_BLOCK = register(
            "teru_teru_bozu",
            Blocks.LANTERN,
            (properties, ignored) -> new TeruTeruBozuBlock(properties.noOcclusion())
    );

    public static final BaseBlockItem TERU_TERU_BOZU_ITEM = registerBlockItem(
            "teru_teru_bozu", TERU_TERU_BOZU_BLOCK
    );

    public static final ChunkLoaderBlock CHUNK_LOADER_BLOCK = register(
            "chunk_loader",
            Blocks.LANTERN,
            (properties, ignored) -> new ChunkLoaderBlock(properties.noOcclusion())
    );

    public static final BaseBlockItem CHUNK_LOADER_ITEM = registerBlockItem(
            "chunk_loader", CHUNK_LOADER_BLOCK
    );

    private static BaseBlockItem registerBlockItem(String path, Block block) {
        var id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path);
        var item = new BaseBlockItem(
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)),
                block
        );
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static <T extends Block, Y extends Block> T register(String path, Y copyFrom, BiFunction<BlockBehaviour.Properties, Y, T> function) {
        return register(path, BlockBehaviour.Properties.ofFullCopy(copyFrom), (properties) -> function.apply(properties, copyFrom));
    }

    public static <T extends Block> T register(String path, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, T> function) {
        var id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path);
        var item = function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
        if (item == null) {
            return null;
        }
        BLOCKS.add(item);
        return Registry.register(BuiltInRegistries.BLOCK, id, item);
    }


    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Blocks");
    }
}