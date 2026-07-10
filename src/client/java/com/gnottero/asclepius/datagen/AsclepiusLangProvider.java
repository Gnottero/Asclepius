package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusDecorativeBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/**
 * English translation provider. Covers both the hand-authored strings (item/block
 * names, tooltips, messages) and the bulk-generated decorative block family names —
 * both must live in the same provider since Fabric's datagen writes one en_us.json
 * per pack and a second provider targeting the same locale would simply overwrite it,
 * not merge with it (this is what happened before: introducing a decorative-only lang
 * provider silently wiped every hand-authored key out of the resources file the next
 * time {@code ./datagen.sh} ran).
 */
public class AsclepiusLangProvider extends FabricLanguageProvider {

    protected AsclepiusLangProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        addHandAuthoredTranslations(translationBuilder);
        addDecorativeBlockTranslations(translationBuilder);
    }

    private static void addHandAuthoredTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add("itemgroup.asclepius", "Asclepius");

        translationBuilder.add("item.asclepius.ender_key", "Ender Key");
        translationBuilder.add("item.asclepius.eye_of_recall", "Eye of Recall");
        translationBuilder.add("item.asclepius.golden_eye_of_recall", "Golden Eye of Recall");
        translationBuilder.add("item.asclepius.fox_amulet", "Fox Amulet");

        translationBuilder.add("item.asclepius.gaia_ingot", "Gaia Ingot");
        translationBuilder.add("item.asclepius.ancient_ingot", "Ancient Ingot");
        translationBuilder.add("item.asclepius.crystalized_experience", "Crystalized Experience");

        translationBuilder.add("item.asclepius.forgotten_relics.needs_repair.line_1", "A damaged relic pulled from a fallen foe.");
        translationBuilder.add("item.asclepius.forgotten_relics.needs_repair.line_2", "Its power slumbers, waiting to be restored");
        translationBuilder.add("item.asclepius.forgotten_relics.needs_repair.line_3", "Awakening Materials:");
        translationBuilder.add("tooltip.asclepius.forgotten_relic.material_entry", "%sx %s");
        translationBuilder.add("item.asclepius.forgotten_relics.required_levels", "Requires %s levels to Awaken");
        translationBuilder.add("item.asclepius.forgotten_relics.not_enough_xp", "You need %s levels to awaken this relic's power");
        translationBuilder.add("item.asclepius.forgotten_relics.instant_repair_hint", "Or right-click with %s levels to repair instantly");
        translationBuilder.add("item.asclepius.forgotten_relics.not_enough_xp_to_repair", "You need %s levels to instantly repair this relic");
        translationBuilder.add("item.asclepius.sockets_title", "Embedded Relics");

        translationBuilder.add("item.asclepius.forgotten_relics.rarity.begin", "Rarity: [%s]");
        translationBuilder.add("item.asclepius.forgotten_relics.rarity.deteriorated", "Deteriorated");
        translationBuilder.add("item.asclepius.forgotten_relics.rarity.purified", "Purified");
        translationBuilder.add("item.asclepius.forgotten_relics.rarity.resonant", "Resonant");
        translationBuilder.add("item.asclepius.forgotten_relics.rarity.ancient", "Ancient");
        translationBuilder.add("item.asclepius.forgotten_relics.rarity.eternal", "Eternal");

        translationBuilder.add("item.asclepius.wooden_hammer", "Wooden Hammer");
        translationBuilder.add("item.asclepius.stone_hammer", "Stone Hammer");
        translationBuilder.add("item.asclepius.copper_hammer", "Copper Hammer");
        translationBuilder.add("item.asclepius.iron_hammer", "Iron Hammer");
        translationBuilder.add("item.asclepius.golden_hammer", "Golden Hammer");
        translationBuilder.add("item.asclepius.diamond_hammer", "Diamond Hammer");
        translationBuilder.add("item.asclepius.netherite_hammer", "Netherite Hammer");

        translationBuilder.add("item.asclepius.wooden_paxel", "Wooden Paxel");
        translationBuilder.add("item.asclepius.stone_paxel", "Stone Paxel");
        translationBuilder.add("item.asclepius.copper_paxel", "Copper Paxel");
        translationBuilder.add("item.asclepius.iron_paxel", "Iron Paxel");
        translationBuilder.add("item.asclepius.golden_paxel", "Golden Paxel");
        translationBuilder.add("item.asclepius.diamond_paxel", "Diamond Paxel");
        translationBuilder.add("item.asclepius.netherite_paxel", "Netherite Paxel");

        translationBuilder.add("item.asclepius.teru_teru_bozu", "Teru Teru Bozu");
        translationBuilder.add("item.asclepius.pale_altar", "Pale Altar");
        translationBuilder.add("item.asclepius.volcanic_ash", "Volcanic Ash");
        translationBuilder.add("item.asclepius.shale", "Shale");
        translationBuilder.add("item.asclepius.packed_shale", "Packed Shale");
        translationBuilder.add("item.asclepius.chunk_loader", "Chunk Loader");

        translationBuilder.add("block.asclepius.teru_teru_bozu", "Teru Teru Bozu");
        translationBuilder.add("block.asclepius.pale_altar", "Pale Altar");
        translationBuilder.add("block.asclepius.volcanic_ash", "Volcanic Ash");
        translationBuilder.add("block.asclepius.shale", "Shale");
        translationBuilder.add("block.asclepius.packed_shale", "Packed Shale");
        translationBuilder.add("block.asclepius.chunk_loader", "Chunk Loader");

        translationBuilder.add("item.asclepius.ancient_squid_relic", "Ancient Squid Relic");

        translationBuilder.add("item.asclepius.eye_of_recall_charge", "Charges: %s/%s");
        translationBuilder.add("item.asclepius.eye_of_recall_description", "Teleports to spawn point, consuming %s");
        translationBuilder.add("item.asclepius.eye_of_recall.no_charges", "No charges remaining. Use %s to recharge");

        translationBuilder.add("item.asclepius.golden_eye_of_recall_description", "Teleports to the linked Lodestone, consuming %s");
        translationBuilder.add("item.asclepius.golden_eye_of_recall.linked_to", "Linked to: %s [%s, %s, %s]");
        translationBuilder.add("item.asclepius.golden_eye_of_recall.not_linked", "Not linked to a Lodestone");

        translationBuilder.add("item.asclepius.ender_key_description", "Unlocks vaults found in End City Ships");

        translationBuilder.add("block.asclepius.altar.conditions_not_met", "The altar does not respond to your offering...");
        translationBuilder.add("block.asclepius.altar.not_enough_xp", "You need %s levels to perform this ritual");
        translationBuilder.add("block.asclepius.altar.no_compatible_enchants", "No enchantments can be transferred...");
    }

    private static void addDecorativeBlockTranslations(TranslationBuilder translationBuilder) {
        // 1. Translations for vertical slabs and sideways stairs derived from vanilla slabs
        AsclepiusDecorativeBlocks.VANILLA_SLAB_BLOCKS.forEach(vanillaSlab -> {
            String vanillaSlabName = vanillaSlab.getName().getString();

            Block vertSlab = AsclepiusDecorativeBlocks.VERTICAL_SLABS.get(vanillaSlab);
            if (vertSlab != null) {
                translationBuilder.add(vertSlab.asItem(), deriveName(vanillaSlabName, "Vertical Slab"));
            }
            Block sidewaysStair = AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.get(vanillaSlab);
            if (sidewaysStair != null) {
                translationBuilder.add(sidewaysStair.asItem(), deriveName(vanillaSlabName, "Sideways Stairs"));
            }
        });

        // 2. Translations for custom blocks (wool/concrete slabs, stairs, sideways stairs, vertical slabs, and walls)
        AsclepiusDecorativeBlocks.CUSTOM_BASE_BLOCKS.forEach(baseBlock -> {
            Block slab = AsclepiusDecorativeBlocks.SLABS.get(baseBlock);
            Block stairs = AsclepiusDecorativeBlocks.STAIRS.get(baseBlock);
            Block sidewaysStairs = AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.get(baseBlock);
            Block vertSlab = AsclepiusDecorativeBlocks.VERTICAL_SLABS.get(baseBlock);
            Block wall = AsclepiusDecorativeBlocks.WALLS.get(baseBlock);

            String baseName = baseBlock.getName().getString();

            if (slab != null) {
                translationBuilder.add(slab.asItem(), baseName + " Slab");
            }
            if (stairs != null) {
                translationBuilder.add(stairs.asItem(), baseName + " Stairs");
            }
            if (sidewaysStairs != null) {
                translationBuilder.add(sidewaysStairs.asItem(), baseName + " Sideways Stairs");
            }
            if (vertSlab != null) {
                translationBuilder.add(vertSlab.asItem(), baseName + " Vertical Slab");
            }
            if (wall != null) {
                translationBuilder.add(wall.asItem(), baseName + " Wall");
            }
        });

        // 3. Creative tab translation
        translationBuilder.add("itemgroup.asclepius.decorative_blocks", "Decorative Blocks");
    }

    // "Oak Slab" -> "Oak Vertical Slab" by replacing "Slab" with the suffix; falls back to
    // appending when the base name doesn't contain "Slab" at all (e.g. names not ending in it).
    private static String deriveName(String baseName, String suffix) {
        String replaced = baseName.replace("Slab", suffix);
        return replaced.contains(suffix) ? replaced : baseName + " " + suffix;
    }
}
