package com.gnottero.asclepius.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * Base for the mod's simple single-block-entity blocks (Pale Altar, Chunk Loader).
 * Template-method-style base: codec() is implemented once here since none of these
 * blocks participate in vanilla's block-predicate/structure-serialization codec
 * system, and newBlockEntity delegates to a factory supplied by the subclass so
 * each one only has to say which BlockEntity type it creates.
 */
public abstract class SimpleEntityBlock extends BaseEntityBlock {

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory;

    protected SimpleEntityBlock(Properties properties, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory) {
        super(properties);
        this.blockEntityFactory = blockEntityFactory;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.apply(pos, state);
    }
}
