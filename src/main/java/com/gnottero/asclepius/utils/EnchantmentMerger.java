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
            Holder<Enchantment> enchantmentHolder = (Holder<Enchantment>) entry.getKey();
            int current = enchantments.getLevel(enchantmentHolder);
            int level = entry.getIntValue();
            level = current == level ? level + 1 : Math.max(current, level);
            Enchantment enchantment = enchantmentHolder.value();
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
                if (level > MAX_LEVEL && enchantment.getMaxLevel() != 1) {
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

//    public static Optional<MergeResult> tryMerge(ItemStack base, ItemStack catalyst) {
//        if (base.isEmpty() || catalyst.isEmpty()) return Optional.empty();
//
//        ItemEnchantments catalystEnchants = catalyst.get(DataComponents.STORED_ENCHANTMENTS);
//        if (catalystEnchants == null || catalystEnchants.isEmpty()) return Optional.empty();
//        if (!base.isEnchantable() && !base.has(DataComponents.ENCHANTMENTS) && !base.has(DataComponents.STORED_ENCHANTMENTS)) return Optional.empty();
//
//        ItemEnchantments baseEnchants = getEnchantments(base);
//        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(baseEnchants);
//        int xpCost = 0;
//        boolean changed = false;
//
//        for (var entry : catalystEnchants.entrySet()) {
//            Holder<Enchantment> enchHolder = entry.getKey();
//            Enchantment enchantment = enchHolder.value();
//            int catalystLevel = entry.getValue();
//
//            if (!enchantment.canEnchant(base) && !base.has(DataComponents.STORED_ENCHANTMENTS)) continue;
//
//            if (catalystEnchants.entrySet().stream()
//                    .filter(e -> !e.getKey().equals(enchHolder))
//                    .anyMatch(e -> !Enchantment.areCompatible(enchHolder, e.getKey()))) continue;
//
//            if (!baseEnchants.entrySet().stream()
//                    .filter(e -> !e.getKey().equals(enchHolder))
//                    .allMatch(e -> Enchantment.areCompatible(enchHolder, e.getKey()))) continue;
//
//            int baseLevel = merged.getLevel(enchHolder);
//            if (enchantment.getMaxLevel() == 1 && baseLevel >= 1) continue;
//            if (baseLevel >= MAX_LEVEL) continue;
//
//            int newLevel = Math.min(baseLevel == catalystLevel ? baseLevel + 1 : Math.max(baseLevel, catalystLevel), MAX_LEVEL);
//            if (newLevel <= baseLevel) continue;
//
//            merged.set(enchHolder, newLevel);
//            int overCap = Math.max(0, newLevel - enchantment.getMaxLevel());
//
//            xpCost += (Math.max(1, enchantment.getAnvilCost()) * newLevel * (1 + overCap)) * 30;
//            changed = true;
//        }
//
//        if (!changed) return Optional.empty();
//
//        ItemStack output = base.copy();
//        setEnchantments(output, merged.toImmutable());
//        return Optional.of(new MergeResult(output, xpCost));
//    }

    private static ItemEnchantments getEnchantments(ItemStack stack) {
        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored != null && !stored.isEmpty()) return stored;
        ItemEnchantments regular = stack.get(DataComponents.ENCHANTMENTS);
        return regular != null ? regular : ItemEnchantments.EMPTY;
    }

    private static void setEnchantments(ItemStack stack, ItemEnchantments enchantments) {
        if (stack.has(DataComponents.STORED_ENCHANTMENTS)) {
            stack.set(DataComponents.STORED_ENCHANTMENTS, enchantments);
        } else {
            stack.set(DataComponents.ENCHANTMENTS, enchantments);
        }
    }
}