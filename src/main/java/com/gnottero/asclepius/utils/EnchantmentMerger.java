package com.gnottero.asclepius.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

public class EnchantmentMerger {

    private static final int MAX_LEVEL = 10;

    public record MergeResult(ItemStack output, int xpCost) {}

    public static Optional<MergeResult> tryMerge(ItemStack base, ItemStack catalyst, ServerPlayer player) {
        if (base.isEmpty() || catalyst.isEmpty()) return Optional.empty();

        int price = 0;
        int finalPrice = 0;

        ItemStack result = base.copy();
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        long tax = base.getOrDefault(DataComponents.REPAIR_COST, 0).longValue();

        ItemEnchantments additionalEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(catalyst);
        boolean isAnyEnchantmentCompatible = false;
        boolean isAnyEnchantmentNotCompatible = false;

        for (Object2IntMap.Entry<Holder<Enchantment>> entry  :additionalEnchantments.entrySet()) {
            Holder<Enchantment> enchantmentHolder = entry.getKey();
            int current = enchantments.getLevel(enchantmentHolder);
            int level = entry.getIntValue();
            Enchantment enchantment = enchantmentHolder.value();
            level = (current == level && enchantment.getMaxLevel() != 1) ? level + 1 : Math.max(current, level);
            boolean compatible = enchantment.canEnchant(base);

            if (player.hasInfiniteMaterials() || base.is(Items.ENCHANTED_BOOK)) {
                compatible = true;
            }

            for (Holder<Enchantment> other : enchantments.keySet()) {
                if (!other.equals(enchantmentHolder) && !Enchantment.areCompatible(enchantmentHolder, other)) {
                    compatible = false;
                    price++;
                }
            }

            if (!compatible) {
                isAnyEnchantmentNotCompatible = true;
            } else {
                isAnyEnchantmentCompatible = true;
                if (level > MAX_LEVEL) {
                    level = MAX_LEVEL;
                }

                enchantments.set(enchantmentHolder, level);
                int fee = enchantment.getAnvilCost();
                fee = Math.max(1, fee / 2);
                price += fee * level;
            }

            finalPrice = price <= 0 ? 0 : (int) Mth.clamp(tax + price, 0L, 2147483647L);
        }

        if (isAnyEnchantmentNotCompatible && !isAnyEnchantmentCompatible) return Optional.empty();

        result.set(DataComponents.REPAIR_COST, finalPrice);
        EnchantmentHelper.setEnchantments(result, enchantments.toImmutable());
        return Optional.of(new MergeResult(result, finalPrice));
    }
}