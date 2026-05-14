package com.gnottero.asclepius.item;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.registry.AsclepiusComponents;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class ReachGauntlet extends SimplePolymerItem {

    public ReachGauntlet(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull Identifier getPolymerItemModel(ItemStack stack, @Nullable PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "reach_gauntlet");
    }

    @Override
    public @NonNull ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, @Nullable PacketContext context, HolderLookup.Provider lookup) {
        ItemStack item = super.getPolymerItemStack(itemStack, tooltipType, context, lookup);
        item.set(DataComponents.RARITY, Rarity.COMMON);
        return item;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        int currentCharge = stack.getOrDefault(AsclepiusComponents.EYE_CHARGE, 0);

        tooltipAdder.accept(Component.empty().append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.translatable("item.asclepius.eye_of_recall_description", Items.ENDER_PEARL.getDefaultInstance().getHoverName())).withStyle(ChatFormatting.DARK_GRAY)));

//        tooltipAdder.accept(Component.translatable("item.asclepius.eye_of_recall_charge", currentCharge, MAX_CHARGE)
//                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player) {
        Slottootot

        return super.overrideStackedOnOther(self, slot, clickAction, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (clickAction != ClickAction.SECONDARY || other.isEmpty()) {
            return false;
        }

        if (!other.is(Items.ENDER_PEARL)) {
            return false;
        }

        int currentCharge = self.getOrDefault(AsclepiusComponents.EYE_CHARGE, 0);

        if (currentCharge >= MAX_CHARGE) {
            return false;
        }

        if (slot.allowModification(player)) {
            player.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
        }

        int toAdd = Math.min(other.count(), MAX_CHARGE - currentCharge);
        self.set(AsclepiusComponents.EYE_CHARGE, currentCharge + toAdd);
        other.shrink(toAdd);

        return true;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return itemStack.getOrDefault(AsclepiusComponents.EYE_CHARGE, 0) > 0;
    }
}