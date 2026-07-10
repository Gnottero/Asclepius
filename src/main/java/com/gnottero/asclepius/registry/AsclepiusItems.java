package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.forgotten_relics.AttributeRelicItem;
import com.gnottero.asclepius.feature.forgotten_relics.ForgottenRelicItem;
import com.gnottero.asclepius.feature.misc.EnderKeyItem;
import com.gnottero.asclepius.feature.misc.FoxAmuletItem;
import com.gnottero.asclepius.feature.recall.EyeOfRecallItem;
import com.gnottero.asclepius.feature.recall.GoldenEyeOfRecall;
import com.gnottero.asclepius.feature.tools.HammerItem;
import com.gnottero.asclepius.feature.tools.PaxelItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
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

/**
 * Item registration composition root. Three tiers of registration helper exist,
 * from most to least generic — pick the narrowest one that fits a new item:
 * {@code registerItem} (bare registration, any item), {@code registerTool}/
 * {@code registerHammer}/{@code registerPaxel} (tool families needing a material +
 * repair tag + attack stats per instance), and {@code registerRelic} (Forgotten
 * Relics — constrains the factory's return type to {@code ForgottenRelicItem} for
 * call-site clarity). New content types with their own recurring registration shape
 * should get their own tier here rather than repeating boilerplate at each call site.
 */
public class AsclepiusItems {

    // ── Misc ──────────────────────────────────────────────────────────────────

    public static final Item ENDER_KEY = registerItem("ender_key",
            p -> new EnderKeyItem(p.fireResistant()));

    public static final Item FOX_AMULET = registerItem("fox_amulet",
            p -> new FoxAmuletItem(p.fireResistant().stacksTo(1).durability(3)));

    public static final Item RECALL_EYE = registerItem("eye_of_recall",
            p -> new EyeOfRecallItem(p.stacksTo(1).useCooldown(10.0f)));

    public static final Item GOLDEN_EYE = registerItem("golden_eye_of_recall",
            p -> new GoldenEyeOfRecall(p.stacksTo(1).useCooldown(10.0f)));

    // ── Materials ─────────────────────────────────────────────────────────────

    public static final Item GAIA_INGOT             = registerItem("gaia_ingot", Item::new);
    public static final Item ANCIENT_INGOT          = registerItem("ancient_ingot", Item::new);
    public static final Item CRYSTALIZED_EXPERIENCE = registerItem("crystalized_experience", Item::new);

    // ── Forgotten Relics ──────────────────────────────────────────────────────

    public static final Item ANCIENT_SQUID_RELIC = registerRelic("ancient_squid_relic",
            p -> new AttributeRelicItem(p, DataComponents.TOOL,
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

    static <E extends Block> BlockItem registerBlockItem(E block) {
        var id = BuiltInRegistries.BLOCK.getKey(block);
        var settings = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix();
        return Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, settings));
    }

    @FunctionalInterface
    interface ToolFactory<T extends Item> {
        T create(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties, String name, TagKey<Item> repairTag);
    }

    // Mirrors registerTool/ToolFactory for relic registrations — mainly a
    // call-site readability/type-bound aid (constrains T to ForgottenRelicItem),
    // since relics don't need extra per-instance registration params the way
    // tools need name/repairTag.
    private static <T extends ForgottenRelicItem> T registerRelic(String name, RelicFactory<T> factory) {
        return registerItem(name, factory::create);
    }

    @FunctionalInterface
    interface RelicFactory<T extends ForgottenRelicItem> {
        T create(Item.Properties properties);
    }

    // ── Creative tab ──────────────────────────────────────────────────────────

    public static void registerAll() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "a_group"),
                FabricCreativeModeTab.builder()
                        .icon(() -> RECALL_EYE.getDefaultInstance())
                        .title(Component.translatable("itemgroup." + Asclepius.MOD_ID))
                        .displayItems((params, output) -> {
                            output.accept(ENDER_KEY);
                            output.accept(FOX_AMULET);
                            output.accept(GAIA_INGOT);
                            output.accept(ANCIENT_INGOT);
                            output.accept(CRYSTALIZED_EXPERIENCE);
                            output.accept(ANCIENT_SQUID_RELIC);
                            output.accept(RECALL_EYE);
                            output.accept(GOLDEN_EYE);

                            output.accept(WOODEN_HAMMER);
                            output.accept(STONE_HAMMER);
                            output.accept(COPPER_HAMMER);
                            output.accept(IRON_HAMMER);
                            output.accept(GOLDEN_HAMMER);
                            output.accept(DIAMOND_HAMMER);
                            output.accept(NETHERITE_HAMMER);

                            output.accept(WOODEN_PAXEL);
                            output.accept(STONE_PAXEL);
                            output.accept(COPPER_PAXEL);
                            output.accept(IRON_PAXEL);
                            output.accept(GOLDEN_PAXEL);
                            output.accept(DIAMOND_PAXEL);
                            output.accept(NETHERITE_PAXEL);

                            output.accept(TERU_TERU_BOZU);
                            output.accept(PALE_ALTAR);
                            output.accept(VOLCANIC_ASH);
                            output.accept(SHALE);
                            output.accept(PACKED_SHALE);
                            output.accept(CHUNK_LOADER);
                        }).build());

        Asclepius.LOGGER.info("[{}]> Register Items", Asclepius.MOD_ID);
    }
}