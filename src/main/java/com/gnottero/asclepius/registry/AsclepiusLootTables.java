package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class AsclepiusLootTables {

    public static final ResourceKey<LootTable> VAULT_END_SHIP = ResourceKey.create(
            Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath("asclepius", "vault/elytra")
    );

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> " + "Register Loot Tables");
    }
}
