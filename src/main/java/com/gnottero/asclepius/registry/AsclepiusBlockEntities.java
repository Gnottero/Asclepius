package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.chunk_loader.ChunkLoaderBlockEntity;
import com.gnottero.asclepius.feature.pale_altar.PaleAltarBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AsclepiusBlockEntities {

    public static final BlockEntityType<PaleAltarBlockEntity> PALE_ALTAR = register(PaleAltarBlockEntity::new, AsclepiusBlocks.PALE_ALTAR);
    public static final BlockEntityType<ChunkLoaderBlockEntity> CHUNK_LOADER_BE = register(ChunkLoaderBlockEntity::new, AsclepiusBlocks.CHUNK_LOADER);

    public static <T extends BlockEntity> BlockEntityType<T> register(FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        BlockEntityType<T> type = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID,
                        BuiltInRegistries.BLOCK.getKey(block).getPath()),
                FabricBlockEntityTypeBuilder.create(factory, block).build()
        );
        return type;
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register BlockEntities");
    }
}