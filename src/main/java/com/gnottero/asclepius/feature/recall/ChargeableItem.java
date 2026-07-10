package com.gnottero.asclepius.feature.recall;

import com.gnottero.asclepius.registry.AsclepiusComponents;
import com.gnottero.asclepius.utils.TooltipUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * Base for items with a limited, rechargeable "charge" count (recharged by
 * shift-clicking the configured fuel item onto the stack). Subclasses implement
 * {@link #getDescriptionKey()} (their tooltip's first line) and may override
 * {@link #appendExtraTooltipLines} to append their own lines after the shared
 * charge-count line — {@link #appendHoverText} is {@code final} specifically so
 * every chargeable item's tooltip keeps this same two-part shape. Reads
 * {@code CHARGE} first and falls back to the legacy {@code EYE_CHARGE_LEGACY}
 * component so stacks saved before the two items were split still show their
 * charge correctly — don't remove that fallback as dead code.
 */
public abstract class ChargeableItem extends Item {

    public static final int MAX_CHARGE = 64;
    public final Item fuel;
    public final SoundEvent sound;

    public ChargeableItem(Properties properties, Item fuel, SoundEvent sound) {
        super(properties);
        this.fuel = fuel;
        this.sound = sound;
    }

    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (clickAction != ClickAction.SECONDARY) return false;
        if (!slot.allowModification(player)) return false;
        if (!other.is(fuel)) return false;

        int currentCharge = getCharge(self);
        if (currentCharge >= MAX_CHARGE) return false;

        int toAdd = Math.min(other.getCount(), MAX_CHARGE - currentCharge);
        setCharge(self, currentCharge + toAdd);
        other.shrink(toAdd);

        player.playSound(sound, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
        return true;
    }

    // Eye of Recall and Golden Eye of Recall are the same item under the hood
    // (Golden Eye pre-dates the CHARGE component split) — CHARGE is checked first
    // and EYE_CHARGE_LEGACY is only a fallback for stacks saved before the split,
    // so old worlds keep their charge instead of resetting to 0.
    public static int getCharge(ItemStack stack) {
        if (stack.has(AsclepiusComponents.CHARGE)) return stack.getOrDefault(AsclepiusComponents.CHARGE, 0);
        return stack.getOrDefault(AsclepiusComponents.EYE_CHARGE_LEGACY, 0);
    }

    public static void setCharge(ItemStack stack, int value) {
        stack.set(AsclepiusComponents.CHARGE, value);
        if (stack.has(AsclepiusComponents.EYE_CHARGE_LEGACY)) {
            stack.remove(AsclepiusComponents.EYE_CHARGE_LEGACY);
        }
    }

    public boolean isFoil(ItemStack itemStack) {
        return getCharge(itemStack) > 0;
    }

    // Charge consumption ("do we have a charge, and if so spend one") is identical
    // for every chargeable item; only what happens after differs (teleport target).
    // Centralized here so the "no charges" message/check can't drift between them.
    protected boolean tryConsumeCharge(ItemStack stack, Player player) {
        int currentCharge = getCharge(stack);
        if (currentCharge <= 0) {
            player.sendSystemMessage(Component.translatable("item.asclepius.eye_of_recall.no_charges", fuel.getDefaultInstance().getHoverName()));
            return false;
        }
        setCharge(stack, currentCharge - 1);
        return true;
    }

    // Template Method: the tooltip skeleton (description line, then charge count)
    // is shared and fixed here, but the description text and any extra lines are
    // supplied by each subclass — keeps every chargeable item's tooltip structurally
    // consistent while letting the actual wording differ (e.g. Golden Eye of Recall
    // shows a linked-Lodestone line that Eye of Recall doesn't have).
    @Override
    public final void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {
        int currentCharge = getCharge(stack);

        tooltipAdder.accept(TooltipUtils.descriptionLine(getDescriptionKey(), fuel.getDefaultInstance().getHoverName()));

        tooltipAdder.accept(Component.translatable("item.asclepius.eye_of_recall_charge", currentCharge, MAX_CHARGE)
                .withStyle(ChatFormatting.DARK_GRAY));

        appendExtraTooltipLines(stack, tooltipAdder);
    }

    protected abstract String getDescriptionKey();

    protected void appendExtraTooltipLines(ItemStack stack, Consumer<Component> tooltipAdder) {
        // No extra lines by default.
    }
}
