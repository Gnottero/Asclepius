package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.item.EnderKeyItem;
import com.gnottero.asclepius.item.HammerItem;
import com.gnottero.asclepius.item.PaxelItem;
import com.gnottero.asclepius.item.EyeOfRecallItem;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class AsclepiusItems {

    public static Item ENDER_KEY = registerItem(Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "ender_key"),
            properties -> new EnderKeyItem(properties.fireResistant()));

    public static Item WOODEN_HAMMER  = registerTool("wooden_hammer",  ToolMaterial.WOOD,      ItemTags.WOODEN_TOOL_MATERIALS,    1.0F, -2.8F, false, HammerItem::new);
    public static Item STONE_HAMMER  = registerTool("stone_hammer",  ToolMaterial.STONE,      ItemTags.STONE_TOOL_MATERIALS,    1.0F, -2.8F, false, HammerItem::new);
    public static Item COPPER_HAMMER  = registerTool("copper_hammer",  ToolMaterial.COPPER,      ItemTags.COPPER_TOOL_MATERIALS,    1.0F, -2.8F, false, HammerItem::new);
    public static Item IRON_HAMMER  = registerTool("iron_hammer",  ToolMaterial.IRON,      ItemTags.IRON_TOOL_MATERIALS,    1.0F, -2.8F, false, HammerItem::new);
    public static Item GOLDEN_HAMMER  = registerTool("golden_hammer",  ToolMaterial.GOLD,      ItemTags.GOLD_TOOL_MATERIALS,    1.0F, -2.8F, false, HammerItem::new);
    public static Item DIAMOND_HAMMER  = registerTool("diamond_hammer",  ToolMaterial.DIAMOND,      ItemTags.DIAMOND_TOOL_MATERIALS,    1.0F, -2.8F, false, HammerItem::new);
    public static Item NETHERITE_HAMMER  = registerTool("netherite_hammer",  ToolMaterial.NETHERITE,      ItemTags.DIAMOND_TOOL_MATERIALS,    1.0F, -2.8F, true, HammerItem::new);

    public static Item WOODEN_PAXEL   = registerTool("wooden_paxel",   ToolMaterial.WOOD,      ItemTags.WOODEN_TOOL_MATERIALS,    6.0F, -3.2F, false, PaxelItem::new);
    public static Item STONE_PAXEL    = registerTool("stone_paxel",    ToolMaterial.STONE,     ItemTags.STONE_TOOL_MATERIALS,     7.0F, -3.2F, false, PaxelItem::new);
    public static Item COPPER_PAXEL   = registerTool("copper_paxel",   ToolMaterial.COPPER,    ItemTags.COPPER_TOOL_MATERIALS,    7.0F, -3.2F, false, PaxelItem::new);
    public static Item IRON_PAXEL     = registerTool("iron_paxel",     ToolMaterial.IRON,      ItemTags.IRON_TOOL_MATERIALS,      6.0F, -3.1F, false, PaxelItem::new);
    public static Item GOLDEN_PAXEL   = registerTool("golden_paxel",   ToolMaterial.GOLD,      ItemTags.GOLD_TOOL_MATERIALS,      6.0F, -3.0F, false, PaxelItem::new);
    public static Item DIAMOND_PAXEL  = registerTool("diamond_paxel",  ToolMaterial.DIAMOND,   ItemTags.DIAMOND_TOOL_MATERIALS,   5.0F,  3.0F, false, PaxelItem::new);
    public static Item NETHERITE_PAXEL = registerTool("netherite_paxel", ToolMaterial.NETHERITE, ItemTags.DIAMOND_TOOL_MATERIALS, 5.0F, -3.0F, true, PaxelItem::new);

    public static Item RECALL_EYE = registerItem(Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "eye_of_recall"),
            properties -> new EyeOfRecallItem(properties
                    .component(AsclepiusComponents.EYE_CHARGE, 0)
                    .stacksTo(1)
                    .useCooldown(10.0f)));

    public static final BlockItem TERU_TERU_BOZU = registerBlockItem(AsclepiusBlocks.TERU_TERU_BOZU);
    public static final BlockItem PALE_ALTAR = registerBlockItem(AsclepiusBlocks.PALE_ALTAR);

    @FunctionalInterface
    interface ToolFactory<T extends Item> {
        T create(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties, String name, TagKey<Item> repairTag);
    }

    private static <T extends Item> T registerTool(String name, ToolMaterial material, TagKey<Item> repairTag, float attackDamage, float attackSpeed, boolean fireResistant, ToolFactory<T> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, name);
        return registerItem(id, properties -> {
            if (fireResistant) properties = properties.fireResistant();
            return factory.create(material, attackDamage, attackSpeed, properties, name, repairTag);
        });
    }

    public static <T extends Item> T registerItem(Identifier id, Function<Item.Properties, T> factory) {
        return Registry.register(BuiltInRegistries.ITEM, id,
                factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }

    private static <E extends Block & PolymerBlock> FactoryBlockItem registerBlockItem(E block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        Item.Properties settings = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix();
        return Registry.register(BuiltInRegistries.ITEM, id, new FactoryBlockItem(block, settings));
    }

    public static void registerAll() {
        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "a_group"),
                PolymerCreativeModeTabUtils.builder()
                        .icon(() -> RECALL_EYE.getDefaultInstance())
                        .title(Component.translatable("itemgroup." + Asclepius.MOD_ID))
                        .displayItems((context, entries) -> {
                            entries.accept(ENDER_KEY);

                            entries.accept(WOODEN_HAMMER);
                            entries.accept(STONE_HAMMER);
                            entries.accept(COPPER_HAMMER);
                            entries.accept(IRON_HAMMER);
                            entries.accept(GOLDEN_HAMMER);
                            entries.accept(DIAMOND_HAMMER);
                            entries.accept(NETHERITE_HAMMER);

                            entries.accept(WOODEN_PAXEL);
                            entries.accept(STONE_PAXEL);
                            entries.accept(COPPER_PAXEL);
                            entries.accept(IRON_PAXEL);
                            entries.accept(GOLDEN_PAXEL);
                            entries.accept(DIAMOND_PAXEL);
                            entries.accept(NETHERITE_PAXEL);

                            entries.accept(RECALL_EYE);

                            entries.accept(TERU_TERU_BOZU);
                            entries.accept(PALE_ALTAR);
                        }).build()
        );

        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Items");
    }
}