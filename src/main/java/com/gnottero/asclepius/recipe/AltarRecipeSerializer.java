package com.gnottero.asclepius.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;

public class AltarRecipeSerializer {

    public static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(AltarRecipe::group),
                    IngredientWithComponents.CODEC.fieldOf("base").forGetter(AltarRecipe::getBaseItem),
                    IngredientWithComponents.CODEC.fieldOf("catalyst").forGetter(AltarRecipe::getCatalystItem),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(AltarRecipe::getResultItem),
                    AltarRecipeConditions.CODEC.optionalFieldOf("conditions", AltarRecipeConditions.EMPTY).forGetter(AltarRecipe::getConditions)
            ).apply(instance, AltarRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, AltarRecipe::group,
                    IngredientWithComponents.STREAM_CODEC, AltarRecipe::getBaseItem,
                    IngredientWithComponents.STREAM_CODEC, AltarRecipe::getCatalystItem,
                    ItemStackTemplate.STREAM_CODEC, AltarRecipe::getResultItem,
                    ByteBufCodecs.fromCodec(AltarRecipeConditions.CODEC), AltarRecipe::getConditions,
                    AltarRecipe::new
            );
}