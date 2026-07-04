package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.item.forgotten_relics.ForgottenRelicsRarity;
import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

public class AsclepiusComponents {

    public static final DataComponentType<Integer> EYE_CHARGE  = register("eye_charge",  b -> b.persistent(ExtraCodecs.intRange(1, 64)));
    public static final DataComponentType<Boolean> REPAIRED     = register("repaired",    b -> b.persistent(Codec.BOOL));
    public static final DataComponentType<Integer> MAX_SOCKETS  = register("max_sockets", b -> b.persistent(Codec.INT));
    public static final DataComponentType<List<ItemStackTemplate>> SOCKETS = register("sockets", b -> b.persistent(ItemStackTemplate.CODEC.listOf()));
    public static final DataComponentType<List<ItemStackTemplate>> REPAIR_MATERIALS = register("repair_materials", b -> b.persistent(ItemStackTemplate.CODEC.listOf()));
    public static final DataComponentType<ForgottenRelicsRarity> RELIC_RARITY = register("relic_rarity", b -> b.persistent(ForgottenRelicsRarity.CODEC));

    private static <T> DataComponentType<T> register(String path, java.util.function.UnaryOperator<DataComponentType.Builder<T>> configurator) {
        var id   = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, path);
        var type = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, configurator.apply(DataComponentType.builder()).build());
        PolymerComponent.registerDataComponent(type);
        return type;
    }

    public static void registerAll() {
        Asclepius.LOGGER.info("[{}]> Register Components", Asclepius.MOD_ID);
    }
}