package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.pale_altar.recipe.AltarRecipe;
import com.gnottero.asclepius.feature.pale_altar.recipe.AltarRecipeSerializer;
import com.gnottero.asclepius.feature.pale_altar.recipe.EnchantmentMergeRecipe;
import com.gnottero.asclepius.feature.pale_altar.recipe.EnchantmentMergeRecipeSerializer;
import com.gnottero.asclepius.feature.pale_altar.recipe.SocketGrantRecipe;
import com.gnottero.asclepius.feature.pale_altar.recipe.SocketGrantRecipeSerializer;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
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

    public static final RecipeType<SocketGrantRecipe> SOCKET_GRANT_TYPE = registerType("socket_grant");
    public static final RecipeSerializer<SocketGrantRecipe> SOCKET_GRANT_SERIALIZER = registerSerializer("socket_grant", SocketGrantRecipeSerializer.CODEC, SocketGrantRecipeSerializer.STREAM_CODEC);

    public static final RecipeType<EnchantmentMergeRecipe> ENCHANTMENT_MERGE_TYPE = registerType("enchantment_merge");
    public static final RecipeSerializer<EnchantmentMergeRecipe> ENCHANTMENT_MERGE_SERIALIZER = registerSerializer("enchantment_merge", EnchantmentMergeRecipeSerializer.CODEC, EnchantmentMergeRecipeSerializer.STREAM_CODEC);


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
        // Custom recipe types aren't sent to the client by default (Fabric's opt-in
        // recipe sync) — altar/socket-grant recipes need to be synced so client-side
        // tooltip/ambient hints can read them via SynchronizedRecipes.getAllOfType.
        // EnchantmentMergeRecipe is intentionally left out: it's a zero-config
        // singleton recipe with nothing informative for a client-side hint to read.
        RecipeSynchronization.synchronizeRecipeSerializer(ALTAR_SERIALIZER);
        RecipeSynchronization.synchronizeRecipeSerializer(SOCKET_GRANT_SERIALIZER);

        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> " + "Register Recipes");
    }
}