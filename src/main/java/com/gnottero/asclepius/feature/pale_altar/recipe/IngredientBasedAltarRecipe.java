package com.gnottero.asclepius.feature.pale_altar.recipe;

/**
 * Marks altar recipe types that match by a fixed base/catalyst ingredient pair
 * (as opposed to {@link EnchantmentMergeRecipe}, which matches dynamically and
 * has no fixed ingredients) so client-side hint code can scan them uniformly.
 */
public interface IngredientBasedAltarRecipe {
    IngredientWithComponents getBaseItem();
    IngredientWithComponents getCatalystItem();
}
