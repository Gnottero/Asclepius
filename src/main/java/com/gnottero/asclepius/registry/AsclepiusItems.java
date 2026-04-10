package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.item.EndericKeyItem;
import com.gnottero.asclepius.item.PaxelItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AsclepiusItems {

    private static final Map<Registry<?>, List<Tuple<Identifier, ?>>> REG_CACHE = new HashMap<>();

    public static Item ENDERIC_KEY = registerItem(Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "enderic_key"),
            (properties) -> new EndericKeyItem(properties.fireResistant(), Items.CLOCK));

    public static Item WOODEN_PAXEL = registerPaxel("wooden", ToolMaterial.WOOD, Items.CLOCK, ItemTags.WOODEN_TOOL_MATERIALS,6.0F, -3.2F, false);

    public static Item STONE_PAXEL = registerPaxel("stone", ToolMaterial.STONE, Items.CLOCK, ItemTags.STONE_TOOL_MATERIALS, 7.0F, -3.2F, false);

    public static Item COPPER_PAXEL = registerPaxel("copper", ToolMaterial.COPPER, Items.CLOCK, ItemTags.COPPER_TOOL_MATERIALS, 7.0F, -3.2F, false);

    public static Item IRON_PAXEL = registerPaxel("iron", ToolMaterial.IRON, Items.CLOCK, ItemTags.IRON_TOOL_MATERIALS, 6.0F, -3.1F, false);

    public static Item GOLDEN_PAXEL = registerPaxel("golden", ToolMaterial.GOLD, Items.CLOCK, ItemTags.GOLD_TOOL_MATERIALS, 6.0F, -3.0F, false);

    public static Item DIAMOND_PAXEL = registerPaxel("diamond", ToolMaterial.DIAMOND, Items.CLOCK, ItemTags.DIAMOND_TOOL_MATERIALS, 5.0F, 3.0F, false);

    public static Item NETHERITE_PAXEL = registerPaxel("netherite", ToolMaterial.NETHERITE, Items.CLOCK, ItemTags.NETHERITE_TOOL_MATERIALS, 5.0F, -3.0F, true);

    // ── helpers ──────────────────────────────────────────────────────────

    private static PaxelItem registerPaxel(String prefix, ToolMaterial material, Item vanillaItem, TagKey<Item> repairItem, float attackDamage, float attackSpeed, boolean fireResistant) {
        Identifier id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, prefix + "_paxel");
        return registerItem(id, (properties) -> {
            if (fireResistant)
                properties = properties.fireResistant();
            return new PaxelItem(vanillaItem, material, attackDamage, attackSpeed, properties, id.getPath().toString(), repairItem);
        });
    }

    public static <T extends Item> T registerItem(Identifier id, Function<Item.Properties, T> obj) {
        return Registry.register(BuiltInRegistries.ITEM, id,
                obj.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Items");
    }

}