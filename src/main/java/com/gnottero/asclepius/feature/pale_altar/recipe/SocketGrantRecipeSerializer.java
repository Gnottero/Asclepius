package com.gnottero.asclepius.feature.pale_altar.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class SocketGrantRecipeSerializer {

    public static final MapCodec<SocketGrantRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("consume_catalyst", true).forGetter(SocketGrantRecipe::consumeCatalyst),
                    Codec.STRING.optionalFieldOf("group", "").forGetter(SocketGrantRecipe::group),
                    IngredientWithComponents.CODEC.fieldOf("base").forGetter(SocketGrantRecipe::getBaseItem),
                    IngredientWithComponents.CODEC.fieldOf("catalyst").forGetter(SocketGrantRecipe::getCatalystItem),
                    Codec.INT.fieldOf("sockets").forGetter(SocketGrantRecipe::getSocketsGranted),
                    Codec.INT.fieldOf("max_sockets").forGetter(SocketGrantRecipe::getMaxSockets),
                    AltarRecipeConditions.CODEC.optionalFieldOf("conditions", AltarRecipeConditions.EMPTY).forGetter(SocketGrantRecipe::getConditions)
            ).apply(instance, SocketGrantRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SocketGrantRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SocketGrantRecipe::consumeCatalyst,
            ByteBufCodecs.STRING_UTF8, SocketGrantRecipe::group,
            IngredientWithComponents.STREAM_CODEC, SocketGrantRecipe::getBaseItem,
            IngredientWithComponents.STREAM_CODEC, SocketGrantRecipe::getCatalystItem,
            ByteBufCodecs.VAR_INT, SocketGrantRecipe::getSocketsGranted,
            ByteBufCodecs.VAR_INT, SocketGrantRecipe::getMaxSockets,
            ByteBufCodecs.fromCodec(AltarRecipeConditions.CODEC), SocketGrantRecipe::getConditions,
            SocketGrantRecipe::new
    );
}
