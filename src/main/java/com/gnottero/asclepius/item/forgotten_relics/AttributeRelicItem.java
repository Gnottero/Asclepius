package com.gnottero.asclepius.item.forgotten_relics;

import com.gnottero.asclepius.registry.AsclepiusComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class AttributeRelicItem extends ForgottenRelicItem {
    private final Holder<Attribute> attribute;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final EquipmentSlotGroup slot;
    private final SoundEvent soundEvent;

    public AttributeRelicItem(Properties settings, String identifier, @Nullable DataComponentType<?> requiredComponent,
                              Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation, EquipmentSlotGroup slot) {
        this(settings, identifier, requiredComponent, attribute, amount, operation, slot, SoundEvents.RESPAWN_ANCHOR_CHARGE);
    }

    public AttributeRelicItem(Properties settings, String identifier, @Nullable DataComponentType<?> requiredComponent,
                              Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation, EquipmentSlotGroup slot, SoundEvent soundEvent) {
        super(settings, identifier, requiredComponent);
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.slot = slot;
        this.soundEvent = soundEvent;
    }

    @Override
    protected SoundEvent getApplySound() {
        return soundEvent;
    }

    @Override
    protected void applyAttribute(ItemStack self, ItemStack other) {
        ForgottenRelicsRarity rarity = self.getOrDefault(AsclepiusComponents.RELIC_RARITY, ForgottenRelicsRarity.purified);
        double scaledAmount = amount * rarity.getMultiplier();

        var current = other.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        var entries = new ArrayList<>(current.modifiers());

        // Look for an existing entry with the same attribute and same operation
        ItemAttributeModifiers.Entry matchingEntry = null;
        int matchIndex = -1;
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            if (entry.attribute().equals(attribute) && entry.modifier().operation() == operation) {
                matchingEntry = entry;
                matchIndex = i;
                break;
            }
        }

        if (matchingEntry != null) {
            // Stack the values based on operation type
            double existingAmount = matchingEntry.modifier().amount();
            double stackedAmount = switch (operation) {
                case ADD_VALUE, ADD_MULTIPLIED_BASE -> existingAmount + scaledAmount;
                case ADD_MULTIPLIED_TOTAL -> (1 + existingAmount) * (1 + scaledAmount) - 1;
            };

            var stackedModifier = new AttributeModifier(
                    matchingEntry.modifier().id(),
                    stackedAmount,
                    operation
            );

            entries.set(matchIndex, new ItemAttributeModifiers.Entry(attribute, stackedModifier, matchingEntry.slot()));
        } else {
            var modifierId = Identifier.fromNamespaceAndPath(
                    "asclepius",
                    BuiltInRegistries.ATTRIBUTE.getKey(attribute.value()).getPath() + "_relic"
            );

            entries.add(new ItemAttributeModifiers.Entry(
                    attribute,
                    new AttributeModifier(modifierId, scaledAmount, operation),
                    slot
            ));
        }

        other.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries));
    }
}

