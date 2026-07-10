package com.gnottero.asclepius.feature.forgotten_relics;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;

public enum ForgottenRelicsRarity {
    deteriorated("item.asclepius.forgotten_relics.rarity.deteriorated", 0.75f, 10, 350, ChatFormatting.DARK_GRAY),
    purified("item.asclepius.forgotten_relics.rarity.purified", 1.00f, 25, 450, ChatFormatting.GRAY),
    resonant("item.asclepius.forgotten_relics.rarity.resonant", 1.15f, 40, 150, ChatFormatting.GREEN),
    ancient("item.asclepius.forgotten_relics.rarity.ancient", 1.30f, 60, 45, ChatFormatting.AQUA),
    eternal("item.asclepius.forgotten_relics.rarity.eternal", 1.50f, 100, 5, ChatFormatting.GOLD);

    public static final Codec<ForgottenRelicsRarity> CODEC = Codec.STRING.xmap(ForgottenRelicsRarity::valueOf, Enum::name);

    private final String displayName;
    private final float multiplier;
    private final int requiredLevels;
    private final int weight;
    private final ChatFormatting color;

    ForgottenRelicsRarity(final String displayName, final float multiplier, final int requiredLevels, final int weight, final ChatFormatting color) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.requiredLevels = requiredLevels;
        this.weight = weight;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public int getRequiredLevels() {
        return requiredLevels;
    }

    public int getWeight() {
        return weight;
    }

    public ChatFormatting getColor() {
        return color;
    }

    /**
     * Rolls a rarity for a relic being awakened, skewing weights toward higher tiers as the player's level rises.
     */
    public static ForgottenRelicsRarity roll(int playerLevel, RandomSource rand) {
        int wDet = Math.max(10, deteriorated.getWeight() - (int) (playerLevel * 2.5));
        int wPur = purified.getWeight();
        int wRes = resonant.getWeight() + (playerLevel > 80 ? (playerLevel - 80) * 2 : 0);
        int wAnc = Math.min(200, ancient.getWeight() + (int) (playerLevel * 1.2));
        int wEte = playerLevel < 100 ? eternal.getWeight() : eternal.getWeight() + (int) Math.pow(playerLevel - 100, 2);

        int totalWeight = wDet + wPur + wRes + wAnc + wEte;
        int roll = rand.nextInt(totalWeight);

        // Classic weighted-roll idiom: repeatedly subtract each tier's weight from
        // the roll until it goes negative — the tier where that happens is the pick.
        if ((roll -= wDet) < 0) return deteriorated;
        if ((roll -= wPur) < 0) return purified;
        if ((roll -= wRes) < 0) return resonant;
        if (roll - wAnc < 0) return ancient;
        return eternal;
    }
}
