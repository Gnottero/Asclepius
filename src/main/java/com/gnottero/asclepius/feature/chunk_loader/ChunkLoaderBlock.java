package com.gnottero.asclepius.feature.chunk_loader;

import com.gnottero.asclepius.block.SimpleEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RenderShape;

public class ChunkLoaderBlock extends SimpleEntityBlock {

    public ChunkLoaderBlock(Properties settings) {
        super(settings, ChunkLoaderBlockEntity::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
