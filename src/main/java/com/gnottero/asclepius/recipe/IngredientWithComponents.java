package com.gnottero.asclepius.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Objects;

public record IngredientWithComponents(Ingredient ingredient, int count, DataComponentPatch components) {
    public static final Codec<IngredientWithComponents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("id").forGetter(IngredientWithComponents::ingredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithComponents::count),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(IngredientWithComponents::components))
            .apply(instance, IngredientWithComponents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithComponents> STREAM_CODEC = StreamCodec
            .composite(
                    Ingredient.CONTENTS_STREAM_CODEC, IngredientWithComponents::ingredient,
                    ByteBufCodecs.VAR_INT, IngredientWithComponents::count,
                    DataComponentPatch.STREAM_CODEC, IngredientWithComponents::components,
                    IngredientWithComponents::new);

    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!ingredient.test(stack)) return false;
        if (stack.getCount() < count) return false;
        if (components.isEmpty()) return true;

        for (var entry : components.entrySet()) {
            var type = entry.getKey();
            var patchValue = entry.getValue();

            if (patchValue.isPresent()) {
                Object stackValue = stack.getComponents().get(type);

                if (patchValue.get() instanceof ItemEnchantments requiredEnchants
                        && stackValue instanceof ItemEnchantments actualEnchants) {
                    for (var enchEntry : requiredEnchants.entrySet()) {
                        if (actualEnchants.getLevel(enchEntry.getKey()) < enchEntry.getValue()) {
                            return false;
                        }
                    }
                } else {
                    if (!Objects.equals(patchValue.get(), stackValue)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}