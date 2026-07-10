package com.gnottero.asclepius.feature.tools;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class HammerItem extends Item {

    public HammerItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties, String identifier, TagKey<Item> repairMaterial) {
        super(ToolItems.commonToolProperties(properties, material, repairMaterial)
                .pickaxe(material, attackDamage, attackSpeed));
    }
}
