package com.gnottero.asclepius.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkLoaderManager {
    // Map of ServerLevel (by dimension name/key to be safe) -> ChunkPos -> Set of BlockPos
    private static final Map<String, Map<ChunkPos, Set<BlockPos>>> FORCED_CHUNKS = new HashMap<>();

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

    public static void removeLoader(ServerLevel level, ChunkPos chunkPos, BlockPos blockPos) {
        String dimKey = level.dimension().toString();
        Map<ChunkPos, Set<BlockPos>> dimMap = FORCED_CHUNKS.get(dimKey);
        if (dimMap != null) {
            Set<BlockPos> positions = dimMap.get(chunkPos);
            if (positions != null) {
                if (positions.remove(blockPos)) {
                    if (positions.isEmpty()) {
                        dimMap.remove(chunkPos);
                        int cx = chunkPos.getMinBlockX() >> 4;
                        int cz = chunkPos.getMinBlockZ() >> 4;
                        level.setChunkForced(cx, cz, false);
                    }
                }
            }
        }
    }
}
