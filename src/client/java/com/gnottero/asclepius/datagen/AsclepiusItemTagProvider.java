package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusItems;
import com.gnottero.asclepius.registry.AsclepiusTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class AsclepiusItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public AsclepiusItemTagProvider(FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(ItemTags.PICKAXES)
                .add(AsclepiusItems.WOODEN_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_HAMMER.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_HAMMER.builtInRegistryHolder().key())

                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.AXES)
                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.SHOVELS)
                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.MELEE_WEAPON_ENCHANTABLE)
                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.SWEEPING_ENCHANTABLE)
                .add(AsclepiusItems.WOODEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.STONE_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.COPPER_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.IRON_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.GOLDEN_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.DIAMOND_PAXEL.builtInRegistryHolder().key())
                .add(AsclepiusItems.NETHERITE_PAXEL.builtInRegistryHolder().key());

        builder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(AsclepiusItems.FOX_AMULET.builtInRegistryHolder().key());


        builder(AsclepiusTags.FORGOTTEN_RELICS_MASS)
                .add(Items.COPPER_BLOCK.weathering().unaffected().builtInRegistryHolder().key())
                .add(Items.IRON_BLOCK.builtInRegistryHolder().key())
                .add(Items.IRON_BLOCK.builtInRegistryHolder().key())
                .add(Items.GOLD_BLOCK.builtInRegistryHolder().key())
                .add(Items.PRISMARINE_BRICKS.builtInRegistryHolder().key())
                .add(Items.AMETHYST_BLOCK.builtInRegistryHolder().key())
                .add(Items.CRYING_OBSIDIAN.builtInRegistryHolder().key())
                .add(Items.SCULK.builtInRegistryHolder().key())
                .add(Items.GILDED_BLACKSTONE.builtInRegistryHolder().key())
                .add(Items.PURPUR_BLOCK.builtInRegistryHolder().key())
                .add(Items.PURPUR_BLOCK.builtInRegistryHolder().key());

        builder(AsclepiusTags.FORGOTTEN_RELICS_VALUE)
                .add(Items.DIAMOND_BLOCK.builtInRegistryHolder().key())
                .add(Items.EMERALD_BLOCK.builtInRegistryHolder().key())
                .add(Items.TOTEM_OF_UNDYING.builtInRegistryHolder().key())
                .add(Items.HEART_OF_THE_SEA.builtInRegistryHolder().key())
                .add(Items.ENCHANTED_GOLDEN_APPLE.builtInRegistryHolder().key())
                .add(Items.ECHO_SHARD.builtInRegistryHolder().key())
                .add(Items.NETHER_STAR.builtInRegistryHolder().key())
                .add(Items.NETHERITE_SCRAP.builtInRegistryHolder().key());
    }
}
