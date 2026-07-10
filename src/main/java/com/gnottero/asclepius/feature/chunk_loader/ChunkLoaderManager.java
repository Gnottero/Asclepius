package com.gnottero.asclepius.feature.chunk_loader;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Static, non-persisted, ref-counted registry of which chunks are force-loaded by
 * which Chunk Loader block entities. State lives only in memory and is rebuilt as
 * loader block entities re-{@code setLevel} on world load — nothing here is saved
 * to or restored from NBT. Keyed by dimension string (not by holding onto
 * {@link ServerLevel} instances) since this map is static and outlives any single
 * level's lifecycle.
 */
public class ChunkLoaderManager {
    // Keyed by dimension string rather than the ServerLevel instance itself, since
    // this map is static (survives across level (re)loads) and levels shouldn't be
    // held onto outside their own lifecycle.
    // Map of dimension key -> ChunkPos -> Set of loader BlockPos.
    private static final Map<String, Map<ChunkPos, Set<BlockPos>>> FORCED_CHUNKS = new HashMap<>();

    // Multiple Chunk Loader blocks can reference the same chunk, so the position
    // set acts as a ref count: setChunkForced(true) only fires on the transition
    // from empty to non-empty, so it's called exactly once no matter how many
    // loaders are placed in that chunk.
    public static void addLoader(ServerLevel level, ChunkPos chunkPos, BlockPos blockPos) {
        String dimKey = level.dimension().toString();
        Map<ChunkPos, Set<BlockPos>> dimMap = FORCED_CHUNKS.computeIfAbsent(dimKey, k -> new HashMap<>());

        Set<BlockPos> positions = dimMap.computeIfAbsent(chunkPos, k -> new HashSet<>());

        if (positions.isEmpty()) {
            int cx = chunkPos.getMinBlockX() >> 4;
            int cz = chunkPos.getMinBlockZ() >> 4;
            level.setChunkForced(cx, cz, true);
        }
        positions.add(blockPos);
    }

    // Mirrors addLoader's ref-counting: setChunkForced(false) only fires once the
    // last loader referencing this chunk is removed (positions becomes empty).
    public static void removeLoader(ServerLevel level, ChunkPos chunkPos, BlockPos blockPos) {
        String dimKey = level.dimension().toString();
        Map<ChunkPos, Set<BlockPos>> dimMap = FORCED_CHUNKS.get(dimKey);
        if (dimMap == null) return;

        Set<BlockPos> positions = dimMap.get(chunkPos);
        if (positions == null) return;
        if (!positions.remove(blockPos)) return;
        if (!positions.isEmpty()) return;

        dimMap.remove(chunkPos);
        int cx = chunkPos.getMinBlockX() >> 4;
        int cz = chunkPos.getMinBlockZ() >> 4;
        level.setChunkForced(cx, cz, false);
    }
}
