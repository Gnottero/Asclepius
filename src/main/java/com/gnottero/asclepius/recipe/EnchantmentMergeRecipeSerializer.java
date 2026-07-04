package com.gnottero.asclepius.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EnchantmentMergeRecipeSerializer {
    public static final MapCodec<EnchantmentMergeRecipe> CODEC = MapCodec.unit(EnchantmentMergeRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentMergeRecipe> STREAM_CODEC =
            StreamCodec.unit(new EnchantmentMergeRecipe());
}
