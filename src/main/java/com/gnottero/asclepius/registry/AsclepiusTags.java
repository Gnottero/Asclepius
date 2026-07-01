package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AsclepiusTags {

    public static final TagKey<Block> MINEABLE_WITH_PAXEL = register(Registries.BLOCK, "mineable/paxel");
    public static final TagKey<Item> FORGOTTEN_RELICS_MASS = register(Registries.ITEM, "forgotten_relics/mass");
    public static final TagKey<Item> FORGOTTEN_RELICS_VALUE = register(Registries.ITEM, "forgotten_relics/value");

    public static <T> TagKey<T> register(final ResourceKey<? extends Registry<T>> registry, final String path) {
        return TagKey.create(
                registry,
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path)
        );
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Tags");
    }
}