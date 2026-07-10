package com.gnottero.asclepius.feature.pale_altar.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

public record AltarFailureTable(List<AltarFailureEntry> entries) {

    public static final AltarFailureTable EMPTY = new AltarFailureTable(List.of());

    public static final Codec<AltarFailureTable> CODEC =
            AltarFailureEntry.CODEC.listOf().xmap(AltarFailureTable::new, AltarFailureTable::entries);

    // Same cascading-subtraction weighted-roll idiom as ForgottenRelicsRarity.roll.
    public Optional<AltarFailureEntry> roll(RandomSource rand) {
        if (entries.isEmpty()) return Optional.empty();

        int total = entries.stream().mapToInt(AltarFailureEntry::weight).sum();
        if (total <= 0) return Optional.empty();

        int roll = rand.nextInt(total);
        for (AltarFailureEntry entry : entries) {
            if ((roll -= entry.weight()) < 0) return Optional.of(entry);
        }
        return Optional.empty();
    }
}
