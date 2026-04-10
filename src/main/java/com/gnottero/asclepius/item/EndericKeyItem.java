package com.gnottero.asclepius.item;

import com.gnottero.asclepius.Asclepius;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class EndericKeyItem extends SimplePolymerItem {

    public EndericKeyItem(Properties properties, Item virtualItem) {
        super(properties, virtualItem);
    }

    @Override
    public @NonNull Identifier getPolymerItemModel(ItemStack stack, @Nullable PacketContext context,
            HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "enderic_key");
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        Component description = Component.translatable("item.asclepius.enderic_key_description").withStyle(style -> style.withColor(0x888888));

        tooltipAdder.accept(description);
    }

    @Override
    public @NonNull ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, @Nullable PacketContext context, HolderLookup.Provider lookup) {
        var x = super.getPolymerItemStack(itemStack, tooltipType, context, lookup);
        x.set(DataComponents.RARITY, Rarity.COMMON);
        return x;
    }
}