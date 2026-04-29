package com.gnottero.asclepius.item;

import com.gnottero.asclepius.Asclepius;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class HammerItem extends SimplePolymerItem {
    private final String identifier;

    public HammerItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties, String identifier, TagKey<Item> repairMaterial) {
        super(properties
                .enchantable(material.enchantmentValue())
                .pickaxe(material, attackDamage, attackSpeed)
                .durability(material.durability() * 3)
                .repairable(repairMaterial)
        );
        this.identifier = identifier;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, this.identifier);
    }
}