package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public class AsclepiusComponents {

    public static final DataComponentType<Integer> EYE_CHARGE = register("eye_charge", DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(1, 64)));

    public static <T> DataComponentType<T> register(String path, DataComponentType.Builder<T> function) {
        var id = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path);
        var type = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, function.build());
        PolymerComponent.registerDataComponent(type);
        return type;
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[" + Asclepius.MOD_ID + "]> Register Components");
    }
}
