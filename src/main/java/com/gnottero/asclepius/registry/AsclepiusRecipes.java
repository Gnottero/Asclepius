package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.recipe.AltarRecipe;
import com.gnottero.asclepius.recipe.AltarRecipeSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class AsclepiusRecipes {

    public static final RecipeType<AltarRecipe> ALTAR_TYPE = registerType("altar");
    public static final RecipeSerializer<AltarRecipe> ALTAR_SERIALIZER = registerSerializer("altar", AltarRecipeSerializer.CODEC, AltarRecipeSerializer.STREAM_CODEC);


    public static <T extends Recipe<?>> RecipeType<T> registerType(String path) {
        return Registry.register(
                BuiltInRegistries.RECIPE_TYPE,
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path),
                new RecipeType<T>() { }
        );
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String path, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path),
                new RecipeSerializer<>(codec, streamCodec)
        );
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> " + "Register Recipes");
    }
}