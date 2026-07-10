package com.gnottero.asclepius.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Several misc/relic items render a "{@code | <description>}" tooltip row in dark
 * gray as their first line. Centralizing the shape here keeps that row visually
 * identical everywhere it appears instead of relying on each item to hand-copy
 * the same append/style chain.
 */
public class TooltipUtils {

    public static Component descriptionLine(String translationKey, Object... args) {
        return Component.empty()
                .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable(translationKey, args).withStyle(ChatFormatting.DARK_GRAY));
    }
}
