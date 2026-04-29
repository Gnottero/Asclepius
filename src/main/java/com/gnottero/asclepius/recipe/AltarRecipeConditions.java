package com.gnottero.asclepius.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record AltarRecipeConditions(int requiredLevels, boolean requireNight) {

    public static final AltarRecipeConditions EMPTY = new AltarRecipeConditions(0, false);

    public static final Codec<AltarRecipeConditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("required_levels", 0).forGetter(AltarRecipeConditions::requiredLevels),
                    Codec.BOOL.optionalFieldOf("require_night", false).forGetter(AltarRecipeConditions::requireNight)
            ).apply(instance, AltarRecipeConditions::new)
    );

    public boolean check(Player player, Level level) {
        if (player.experienceLevel < requiredLevels) return false;
        if (requireNight && !level.isDarkOutside()) return false;
        return true;
    }

}
