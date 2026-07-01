package com.gnottero.asclepius.item.forgotten_relics;

import net.minecraft.util.RandomSource;

public enum ForgottenRelicsRarity {
    DETERIORATED("item.asclepius.forgotten_relics.rarity.deteriorated", 0.75f, 10, 350),
    PURIFIED("item.asclepius.forgotten_relics.rarity.purified", 1.00f, 25, 450),
    RESONANT("§item.asclepius.forgotten_relics.rarity.resonant", 1.15f, 40, 150),
    ANCIENT("item.asclepius.forgotten_relics.rarity.ancient", 1.30f, 60, 45),
    ETERNAL("item.asclepius.forgotten_relics.rarity.eternal", 1.50f, 100, 5);

    private final String displayName;
    private final float multiplier;
    private final int xpCost;
    private final int rollChance;

    ForgottenRelicsRarity(final String displayName, final float multiplier, final int xpCost, final int rollChance) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.xpCost = xpCost;
        this.rollChance = rollChance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public int getXpCost() {
        return xpCost;
    }

    public int getRollChance() {
        return rollChance;
    }

    private ForgottenRelicsRarity calculateDynamicRarity(int level, RandomSource rand) {
        int wDet = Math.max(10, DETERIORATED.getRollChance() - (int)(level * 2.5));
        int wPur = PURIFIED.getRollChance();
        int wRes = RESONANT.getRollChance() + (level > 80 ? (level - 80) * 2 : 0);
        int wAnc = Math.min(200, ANCIENT.getRollChance() + (int)(level * 1.2));
        int wEte = level < 100 ? ETERNAL.getRollChance() : ETERNAL.getRollChance() + (int)Math.pow(level - 100, 2);

        int totalWeight = wDet + wPur + wRes + wAnc + wEte;
        int roll = rand.nextInt(totalWeight);

        if ((roll -= wDet) < 0) return DETERIORATED;
        if ((roll -= wPur) < 0) return PURIFIED;
        if ((roll -= wRes) < 0) return RESONANT;
        if (roll - wAnc < 0) return ANCIENT;
        return ETERNAL;
    }
}
