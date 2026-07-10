package com.gnottero.asclepius.feature.pale_altar.recipe;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

/**
 * Merges a catalyst item's enchantments onto a base item, anvil-style (matching
 * levels bump by one, otherwise the higher level wins), computing an XP cost from
 * enchantment rarity and level. Standalone from {@link EnchantmentMergeRecipe}'s own
 * {@code assemble()}/{@code matches()} — this class does the actual merge math and
 * cost calculation; the recipe class only wires it into the altar's recipe-matching
 * flow.
 */
public class EnchantmentMerger {

    public record MergeResult(ItemStack output, int xpCost) {}

    public static Optional<MergeResult> tryMerge(ItemStack base, ItemStack catalyst) {
        if (base.isEmpty() || catalyst.isEmpty()) return Optional.empty();

        ItemEnchantments.Mutable baseEnchants = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(base));
        ItemEnchantments catalystEnchants = EnchantmentHelper.getEnchantmentsForCrafting(catalyst);

        int totalXpCost = 0;
        int appliedCount = 0;
        int baseRepairCost = base.getOrDefault(DataComponents.REPAIR_COST, 0);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : catalystEnchants.entrySet()) {
            Holder<Enchantment> enchHolder = entry.getKey();
            Enchantment ench = enchHolder.value();
            int baseLevel = baseEnchants.getLevel(enchHolder);
            int catalystLevel = entry.getIntValue();

            // Mirrors vanilla anvil combining: matching levels bump by one, otherwise
            // the higher of the two wins outright (no addition).
            int targetLevel = (baseLevel == catalystLevel && ench.getMaxLevel() > 1)
                    ? baseLevel + 1
                    : Math.max(baseLevel, catalystLevel);

            // Allow exceeding the enchantment's normal max level by exactly one —
            // an intentional "over-enchant" ceiling, one level past what an
            // enchantment table/anvil could ever produce.
            if (targetLevel > ench.getMaxLevel() + 1) {
                targetLevel = ench.getMaxLevel() + 1;
            }
            if (targetLevel <= baseLevel) continue;

            // Compatibility is only checked against enchantments already on the
            // base item, not against other catalyst enchantments being merged in
            // this same pass — two mutually-incompatible catalyst enchantments can
            // both slip through if neither conflicts with anything on the base.
            boolean compatible = true;
            for (Holder<Enchantment> existing : baseEnchants.keySet()) {
                if (!existing.equals(enchHolder) && !Enchantment.areCompatible(enchHolder, existing)) {
                    compatible = false;
                    break;
                }
            }
            if (!compatible) continue;

            baseEnchants.set(enchHolder, targetLevel);
            appliedCount++;

            // XP cost scales with level^1.3 (steeper than linear, cheaper than
            // quadratic) and a per-rarity multiplier matching vanilla's enchantment
            // weight tiers (10/5/2/1 = common/uncommon/rare/epic).
            int weight = ench.definition().weight();
            double rarityMult = switch (weight) {
                case 10 -> 1.0;  // Common
                case 5  -> 1.8;  // Uncommon
                case 2  -> 2.6;  // Rare
                case 1  -> 3.5;  // Epic
                default -> 1.0;
            };

            int enchCost = (int) Math.floor(5 * Math.pow(targetLevel, 1.3) * rarityMult);
            totalXpCost += enchCost;
        }

        if (appliedCount == 0) return Optional.empty();

        // Small surcharge (+2% per extra enchantment) discourages dumping many
        // enchantments into a single merge purely to save on per-merge overhead.
        if (appliedCount > 1) {
            totalXpCost = (int) Math.ceil(totalXpCost * (1 + 0.02 * (appliedCount - 1)));
        }

        ItemStack result = base.copy();
        EnchantmentHelper.setEnchantments(result, baseEnchants.toImmutable());
        result.set(DataComponents.REPAIR_COST, Math.min(baseRepairCost + totalXpCost, Integer.MAX_VALUE));

        return Optional.of(new MergeResult(result, totalXpCost));
    }
}