package com.gnottero.asclepius.feature.pale_altar.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/**
 * Fully data-driven off the existing recipe JSONs — no hardcoded item list.
 * Hovering an item that appears as a base or catalyst in any loaded altar or
 * socket-grant recipe gets a generic hint, without spoiling which recipe.
 * EnchantmentMergeRecipe is intentionally excluded: it dynamically matches
 * almost any enchanted item, which would spam this line everywhere.
 */
public class AltarTooltipHint {

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            boolean matches = AltarHintRecipes.matches(level,
                    recipe -> recipe.getBaseItem().test(stack) || recipe.getCatalystItem().test(stack));

            if (matches) {
                lines.add(Component.translatable("item.asclepius.altar_hint")
                        .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)));
            }
        });
    }
}
