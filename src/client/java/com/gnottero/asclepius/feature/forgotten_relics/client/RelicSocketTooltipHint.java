package com.gnottero.asclepius.feature.forgotten_relics.client;

import com.gnottero.asclepius.feature.forgotten_relics.ForgottenRelicsRarity;
import com.gnottero.asclepius.registry.AsclepiusComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

/**
 * Renders the "Embedded Relics" list live from the persisted {@code SOCKETS}
 * component instead of the old approach of baking a lore line into the item's
 * {@code DataComponents.LORE} at the moment each relic was applied.
 */
public class RelicSocketTooltipHint {

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            List<ItemStackTemplate> sockets = stack.getOrDefault(AsclepiusComponents.SOCKETS, List.of());
            int maxSockets = stack.getOrDefault(AsclepiusComponents.MAX_SOCKETS, 0);
            int emptySockets = Math.max(0, maxSockets - sockets.size());
            if (sockets.isEmpty() && emptySockets == 0) return;

            if (!lines.isEmpty()) lines.add(Component.empty());
            lines.add(Component.translatable("item.asclepius.sockets_title")
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)));

            for (ItemStackTemplate entry : sockets) {
                ForgottenRelicsRarity rarity = entry.get(AsclepiusComponents.RELIC_RARITY);
                if (rarity == null) rarity = ForgottenRelicsRarity.purified;

                Identifier spriteId = BuiltInRegistries.ITEM.getKey(entry.item().value()).withPrefix("item/");
                MutableComponent spriteComponent = Component.object(new AtlasSprite(AtlasIds.ITEMS, spriteId))
                        .withStyle(style -> style.withColor(ChatFormatting.WHITE).withItalic(false));

                lines.add(spriteComponent
                        .append(Component.literal(" "))
                        .append(entry.create().getHoverName().copy().withStyle(rarity.getColor())));
            }

            // Empty capacity is shown too, so a player can tell at a glance whether
            // an item still has room for more relics.
            for (int i = 0; i < emptySockets; i++) {
                lines.add(Component.translatable("item.asclepius.empty_socket")
                        .withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withItalic(false)));
            }
        });
    }
}
