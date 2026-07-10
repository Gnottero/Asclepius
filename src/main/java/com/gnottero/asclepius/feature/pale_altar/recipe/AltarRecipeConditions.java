package com.gnottero.asclepius.feature.pale_altar.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record AltarRecipeConditions(int requiredLevels, boolean requireNight, float failureChance, AltarFailureTable failureMobs) {

    public static final AltarRecipeConditions EMPTY = new AltarRecipeConditions(0, false, 0f, AltarFailureTable.EMPTY);

    public static final Codec<AltarRecipeConditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("required_levels", 0).forGetter(AltarRecipeConditions::requiredLevels),
                    Codec.BOOL.optionalFieldOf("require_night", false).forGetter(AltarRecipeConditions::requireNight),
                    Codec.FLOAT.optionalFieldOf("failure_chance", 0f).forGetter(AltarRecipeConditions::failureChance),
                    AltarFailureTable.CODEC.optionalFieldOf("failure_mobs", AltarFailureTable.EMPTY).forGetter(AltarRecipeConditions::failureMobs)
            ).apply(instance, AltarRecipeConditions::new)
    );

    // requiredLevels is enforced separately (and more precisely, via XP points
    // rather than whole levels) in PaleAltarBlock.attemptCraft, so this only
    // needs to gate the night requirement.
    public boolean check(Player player, Level level) {
        return !requireNight || level.isDarkOutside();
    }

    public boolean rollFailure(RandomSource random) {
        return failureChance > 0f && random.nextFloat() < failureChance;
    }

}
