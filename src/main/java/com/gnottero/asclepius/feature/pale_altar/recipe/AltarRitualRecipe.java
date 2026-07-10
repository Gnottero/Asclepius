package com.gnottero.asclepius.feature.pale_altar.recipe;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public interface AltarRitualRecipe extends Recipe<AltarRecipeInput> {
    boolean consumeCatalyst();
    int getCatalystAmount();
    default boolean checkConditions(Player player, Level level) { return true; }
}
