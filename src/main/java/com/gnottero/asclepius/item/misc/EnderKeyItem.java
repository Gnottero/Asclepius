package com.gnottero.asclepius.item.misc;

import com.gnottero.asclepius.Asclepius;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class EnderKeyItem extends SimplePolymerItem {

    public EnderKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull Identifier getPolymerItemModel(ItemStack stack, @Nullable PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "ender_key");
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        tooltipAdder.accept(Component.empty().append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.translatable("item.asclepius.ender_key_description").copy().withStyle(ChatFormatting.DARK_GRAY)));
    }
}