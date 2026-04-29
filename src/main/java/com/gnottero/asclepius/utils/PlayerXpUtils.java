package com.gnottero.asclepius.utils;

import net.minecraft.world.entity.player.Player;

public class PlayerXpUtils {

    /**
     * Returns the total XP points needed to reach a given level from 0.
     */
    public static int getTotalXpForLevel(int level) {
        if (level <= 0) return 0;
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    /**
     * Returns the total XP a player currently has,
     * combining full levels plus progress into the current level.
     */
    public static int getTotalXp(int level, float levelProgress) {
        int baseXp = getTotalXpForLevel(level);
        int xpToNextLevel = getXpNeededForLevel(level);
        return baseXp + Math.round(levelProgress * xpToNextLevel);
    }

    /**
     * Returns the XP needed to go from a given level to the next.
     */
    public static int getXpNeededForLevel(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    public static int xpPointsToLevels(int points) {
        int level = 0;
        while (getTotalXpForLevel(level + 1) <= points) {
            level++;
        }
        return level;
    }

    /**
     * Convenience method that takes a Player directly.
     */
    public static int getTotalXp(Player player) {
        return getTotalXp(player.experienceLevel, player.experienceProgress);
    }
}