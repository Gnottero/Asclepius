package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AsclepiusTags {

    public static final TagKey<Block> MINEABLE_WITH_PAXEL = TagKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "mineable/paxel"));

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Tags");
    }
}