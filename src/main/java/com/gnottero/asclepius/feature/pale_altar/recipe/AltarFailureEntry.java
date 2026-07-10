package com.gnottero.asclepius.feature.pale_altar.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EntityType;

public record AltarFailureEntry(EntityType<?> entityType, int weight, int minCount, int maxCount) {
    public static final Codec<AltarFailureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityType.CODEC.fieldOf("entity").forGetter(AltarFailureEntry::entityType),
            Codec.INT.fieldOf("weight").forGetter(AltarFailureEntry::weight),
            Codec.INT.optionalFieldOf("min_count", 1).forGetter(AltarFailureEntry::minCount),
            Codec.INT.optionalFieldOf("max_count", 1).forGetter(AltarFailureEntry::maxCount)
    ).apply(instance, AltarFailureEntry::new));
}
