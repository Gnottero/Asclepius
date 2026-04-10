package com.gnottero.asclepius.utils;

import net.minecraft.server.level.ServerPlayer;

public class ExperienceUtils {

    public static int getLevelFromExperience(int experience) {
        if (experience < 394) {
            return (int) Math.floor((-1 + Math.sqrt(1 + 0.16 * experience)) / 0.08);
        } else if (experience < 16492) {
            return (int) Math.floor((-81 + Math.sqrt(6561 + 7 * (experience - 2220))) / 3.5);
        } else {
            return (int) Math.floor((-325 + Math.sqrt(105625 + 4.5 * (experience - 35992))) / 2.25);
        }
    }

    public static int getExperienceForLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    public static int getTotalExperience(ServerPlayer player) {
        return getExperienceForLevel(player.experienceLevel)
                + (int) (player.experienceProgress * player.getXpNeededForNextLevel());
    }
}