package com.gnottero.asclepius.item;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.utils.PlayerXpUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FoxAmulet extends SimplePolymerItem {

    public FoxAmulet(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull Identifier getPolymerItemModel(ItemStack stack, @Nullable PacketContext context,
            HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "fox_amulet");
    }

    @Override
    public @NonNull ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, @Nullable PacketContext context, HolderLookup.Provider lookup) {
        ItemStack item = super.getPolymerItemStack(itemStack, tooltipType, context, lookup);
        return item;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide() || !(player instanceof ServerPlayer)) return InteractionResult.PASS;

        player.sendSystemMessage(Component.literal("Player Experience: " + PlayerXpUtils.getTotalXp(player)));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        tooltipAdder.accept(Component.empty().append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.translatable("item.asclepius.ender_key_description").copy().withStyle(ChatFormatting.DARK_GRAY)));
    }
}