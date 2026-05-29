package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.item.forgotten_relics.AttributeRelicItem;
import com.gnottero.asclepius.item.misc.EnderKeyItem;
import com.gnottero.asclepius.item.misc.EyeOfRecallItem;
import com.gnottero.asclepius.item.misc.FoxAmulet;
import com.gnottero.asclepius.item.tools.HammerItem;
import com.gnottero.asclepius.item.tools.PaxelItem;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class AsclepiusItems {

    // ── Misc ──────────────────────────────────────────────────────────────────

    public static final Item ENDER_KEY = registerItem("ender_key",
            p -> new EnderKeyItem(p.fireResistant()));

    public static final Item FOX_AMULET = registerItem("fox_amulet",
            p -> new FoxAmulet(p.fireResistant().stacksTo(1).durability(3)));

    public static final Item RECALL_EYE = registerItem("eye_of_recall",
            p -> new EyeOfRecallItem(p.component(AsclepiusComponents.EYE_CHARGE, 0).stacksTo(1).useCooldown(10.0f)));

    // ── Forgotten Relics ──────────────────────────────────────────────────────

    public static final Item ANCIENT_SQUID_RELIC = registerItem("ancient_squid_relic",
            p -> new AttributeRelicItem(p, "ancient_squid_relic", DataComponents.TOOL,
                    Attributes.BLOCK_INTERACTION_RANGE, 1, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, SoundEvents.GLOW_SQUID_SQUIRT));

    // ── Hammers ───────────────────────────────────────────────────────────────

    public static final Item WOODEN_HAMMER    = registerHammer("wooden_hammer",    ToolMaterial.WOOD,      ItemTags.WOODEN_TOOL_MATERIALS,  1.0F, false);
    public static final Item STONE_HAMMER     = registerHammer("stone_hammer",     ToolMaterial.STONE,     ItemTags.STONE_TOOL_MATERIALS,   1.0F, false);
    public static final Item COPPER_HAMMER    = registerHammer("copper_hammer",    ToolMaterial.COPPER,    ItemTags.COPPER_TOOL_MATERIALS,  1.0F, false);
    public static final Item IRON_HAMMER      = registerHammer("iron_hammer",      ToolMaterial.IRON,      ItemTags.IRON_TOOL_MATERIALS,    1.0F, false);
    public static final Item GOLDEN_HAMMER    = registerHammer("golden_hammer",    ToolMaterial.GOLD,      ItemTags.GOLD_TOOL_MATERIALS,    1.0F, false);
    public static final Item DIAMOND_HAMMER   = registerHammer("diamond_hammer",   ToolMaterial.DIAMOND,   ItemTags.DIAMOND_TOOL_MATERIALS, 1.0F, false);
    public static final Item NETHERITE_HAMMER = registerHammer("netherite_hammer", ToolMaterial.NETHERITE, ItemTags.DIAMOND_TOOL_MATERIALS, 1.0F, true);

    // ── Paxels ────────────────────────────────────────────────────────────────

    public static final Item WOODEN_PAXEL    = registerPaxel("wooden_paxel",    ToolMaterial.WOOD,      ItemTags.WOODEN_TOOL_MATERIALS,  6.0F, false);
    public static final Item STONE_PAXEL     = registerPaxel("stone_paxel",     ToolMaterial.STONE,     ItemTags.STONE_TOOL_MATERIALS,   7.0F, false);
    public static final Item COPPER_PAXEL    = registerPaxel("copper_paxel",    ToolMaterial.COPPER,    ItemTags.COPPER_TOOL_MATERIALS,  7.0F, false);
    public static final Item IRON_PAXEL      = registerPaxel("iron_paxel",      ToolMaterial.IRON,      ItemTags.IRON_TOOL_MATERIALS,    6.0F, false);
    public static final Item GOLDEN_PAXEL    = registerPaxel("golden_paxel",    ToolMaterial.GOLD,      ItemTags.GOLD_TOOL_MATERIALS,    6.0F, false);
    public static final Item DIAMOND_PAXEL   = registerPaxel("diamond_paxel",   ToolMaterial.DIAMOND,   ItemTags.DIAMOND_TOOL_MATERIALS, 5.0F, false);
    public static final Item NETHERITE_PAXEL = registerPaxel("netherite_paxel", ToolMaterial.NETHERITE, ItemTags.DIAMOND_TOOL_MATERIALS, 5.0F, true);

    // ── Blocks ────────────────────────────────────────────────────────────────

    public static final BlockItem TERU_TERU_BOZU = registerBlockItem(AsclepiusBlocks.TERU_TERU_BOZU);
    public static final BlockItem PALE_ALTAR     = registerBlockItem(AsclepiusBlocks.PALE_ALTAR);
    public static final BlockItem VOLCANIC_ASH   = registerBlockItem(AsclepiusBlocks.VOLCANIC_ASH);
    public static final BlockItem SHALE          = registerBlockItem(AsclepiusBlocks.SHALE);
    public static final BlockItem PACKED_SHALE   = registerBlockItem(AsclepiusBlocks.PACKED_SHALE);
    public static final BlockItem CHUNK_LOADER   = registerBlockItem(AsclepiusBlocks.CHUNK_LOADER);

    // ── Registration helpers ──────────────────────────────────────────────────

    private static Item registerHammer(String name, ToolMaterial material, TagKey<Item> repairTag, float damage, boolean fireResistant) {
        return registerTool(name, material, repairTag, damage, -2.8F, fireResistant, HammerItem::new);
    }

    private static Item registerPaxel(String name, ToolMaterial material, TagKey<Item> repairTag, float damage, boolean fireResistant) {
        return registerTool(name, material, repairTag, damage, -2.8F, fireResistant, PaxelItem::new);
    }

    private static <T extends Item> T registerTool(String name, ToolMaterial material, TagKey<Item> repairTag,
                                                   float attackDamage, float attackSpeed, boolean fireResistant, ToolFactory<T> factory) {
        return registerItem(name, p -> {
            if (fireResistant) p = p.fireResistant();
            return factory.create(material, attackDamage, attackSpeed, p, name, repairTag);
        });
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Properties, T> factory) {
        var id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, name);
        return registerItem(id, factory);
    }

    public static <T extends Item> T registerItem(Identifier id, Function<Item.Properties, T> factory) {
        return Registry.register(BuiltInRegistries.ITEM, id,
                factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }

    private static <E extends Block & PolymerBlock> FactoryBlockItem registerBlockItem(E block) {
        var id = BuiltInRegistries.BLOCK.getKey(block);
        var settings = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix();
        return Registry.register(BuiltInRegistries.ITEM, id, new FactoryBlockItem(block, settings));
    }

    @FunctionalInterface
    interface ToolFactory<T extends Item> {
        T create(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties, String name, TagKey<Item> repairTag);
    }

    // ── Creative tab ──────────────────────────────────────────────────────────

    public static void registerAll() {
        PolymerCreativeModeTabUtils.registerPolymerCreativeModeTab(
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "a_group"),
                PolymerCreativeModeTabUtils.builder()
                        .icon(() -> RECALL_EYE.getDefaultInstance())
                        .title(Component.translatable("itemgroup." + Asclepius.MOD_ID))
                        .displayItems((context, entries) -> {
                            entries.accept(ENDER_KEY);
                            entries.accept(FOX_AMULET);
                            entries.accept(ANCIENT_SQUID_RELIC);
                            entries.accept(RECALL_EYE);

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

                            entries.accept(TERU_TERU_BOZU);
                            entries.accept(PALE_ALTAR);
                            entries.accept(VOLCANIC_ASH);
                            entries.accept(SHALE);
                            entries.accept(PACKED_SHALE);
                            entries.accept(CHUNK_LOADER);
                        }).build());

        Asclepius.LOGGER.info("[{}]> Register Items", Asclepius.MOD_ID);
    }
}