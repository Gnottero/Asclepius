package com.gnottero.asclepius.feature.chunk_loader;

import com.gnottero.asclepius.registry.AsclepiusBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkLoaderBlockEntity extends BlockEntity {

    private boolean isRegistered = false;

    public ChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(AsclepiusBlockEntities.CHUNK_LOADER_BE, pos, state);
    }

    @Override
    public void setLevel(net.minecraft.world.level.Level level) {
        super.setLevel(level);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            registerLoader(serverLevel);
        }
    }

    public void registerLoader(ServerLevel level) {
        if (!isRegistered) {
            ChunkLoaderManager.addLoader(level, ChunkPos.containing(this.getBlockPos()), this.getBlockPos());
            this.isRegistered = true;
        }
    }

    public void unregisterLoader(ServerLevel level) {
        if (isRegistered) {
            ChunkLoaderManager.removeLoader(level, ChunkPos.containing(this.getBlockPos()), this.getBlockPos());
            this.isRegistered = false;
        }
    }

    @Override
    public void setRemoved() {
        if (this.getLevel() != null && !this.getLevel().isClientSide() && this.getLevel() instanceof ServerLevel serverLevel) {
            unregisterLoader(serverLevel);
        }
        super.setRemoved();
    }
}
