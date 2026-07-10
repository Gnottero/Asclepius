package com.gnottero.asclepius.feature.tools;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * Hammer and Paxel share every Item.Properties call except the harvest-tool
 * designator ({@code .pickaxe(...)} vs {@code .tool(...)}); factoring the common
 * part here keeps durability/enchantability tuning from drifting between the
 * two tool families as new tiers are added.
 */
public class ToolItems {

    public static Item.Properties commonToolProperties(Item.Properties properties, ToolMaterial material, TagKey<Item> repairMaterial) {
        return properties
                .enchantable(material.enchantmentValue())
                .durability(material.durability() * 3)
                .repairable(repairMaterial);
    }
}
