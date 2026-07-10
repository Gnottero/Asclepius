package com.gnottero.asclepius.feature.pale_altar.client;

import com.gnottero.asclepius.feature.pale_altar.recipe.IngredientBasedAltarRecipe;
import com.gnottero.asclepius.registry.AsclepiusRecipes;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Shared "does any hintable altar recipe match" scan used by both the tooltip
 * hint and the ambient particle hint. EnchantmentMergeRecipe is intentionally
 * excluded: it dynamically matches almost any enchanted item, which would
 * spam hints everywhere.
 */
final class AltarHintRecipes {
    private AltarHintRecipes() {}

    static boolean matches(Level level, Predicate<IngredientBasedAltarRecipe> predicate) {
        var synced = level.recipeAccess().getSynchronizedRecipes();
        return Stream.concat(
                        synced.getAllOfType(AsclepiusRecipes.ALTAR_TYPE).stream().map(holder -> (IngredientBasedAltarRecipe) holder.value()),
                        synced.getAllOfType(AsclepiusRecipes.SOCKET_GRANT_TYPE).stream().map(holder -> (IngredientBasedAltarRecipe) holder.value()))
                .anyMatch(predicate);
    }
}
