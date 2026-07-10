package com.gnottero.asclepius.feature.recall;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;

import com.gnottero.asclepius.registry.AsclepiusItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


public class GoldenEyeOfRecall extends ChargeableItem {

    public GoldenEyeOfRecall(Properties properties) {
        super(properties, Items.GOLD_INGOT, SoundEvents.RESPAWN_ANCHOR_AMBIENT);
    }

    // Reads the linked Lodestone position, if any — shared by use() (to decide
    // whether there's anywhere to recall to) and the tooltip (to display it).
    private static Optional<GlobalPos> getLinkedTarget(ItemStack stack) {
        if (!stack.has(DataComponents.LODESTONE_TRACKER)) return Optional.empty();
        return stack.get(DataComponents.LODESTONE_TRACKER).target();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();

        // Item.useOn() has no side effects of its own (plain PASS), so a direct
        // return here is equivalent to delegating to it but doesn't imply any
        // inherited block-interaction behavior that doesn't actually exist.
        if (!level.getBlockState(blockPos).is(Blocks.LODESTONE)) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        if (!(player instanceof ServerPlayer)) return InteractionResult.PASS;

        level.playSound(null, blockPos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        ItemStack stack = context.getItemInHand();
        // Only mutate the held stack in place when it's a single non-creative item;
        // otherwise splitting off one linked copy avoids silently linking an entire
        // stack of compasses to the same lodestone.
        boolean replaceExistingStack = !player.hasInfiniteMaterials() && stack.getCount() == 1;
        LodestoneTracker target = new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), blockPos)), true);

        if (replaceExistingStack) {
            stack.set(DataComponents.LODESTONE_TRACKER, target);
        } else {
            ItemStack lodestoneCompass = stack.transmuteCopy(AsclepiusItems.GOLDEN_EYE, 1);
            stack.consume(1, player);
            lodestoneCompass.set(DataComponents.LODESTONE_TRACKER, target);
            if (!player.getInventory().add(lodestoneCompass)) player.drop(lodestoneCompass, false);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Optional<GlobalPos> target = getLinkedTarget(stack);

        // Not yet linked (or a client-side/non-player call): there's nowhere to
        // recall to, so this behaves like a plain item (PASS) — an unlinked Golden
        // Eye is inert until useOn() links it to a Lodestone.
        if (level.isClientSide() || !(player instanceof ServerPlayer) || target.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!tryConsumeCharge(stack, player)) return InteractionResult.FAIL;

        GlobalPos globPos = target.get();
        // +1 on Y so the player lands on top of the lodestone instead of inside it.
        player.teleportTo(((ServerLevel) level).getServer().getLevel(globPos.dimension()), globPos.pos().getX(), globPos.pos().above(1).getY(), globPos.pos().getZ(), EnumSet.noneOf(Relative.class), player.yBodyRot, player.xRotO, false);
        level.playSound(null, player.blockPosition(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected String getDescriptionKey() {
        return "item.asclepius.golden_eye_of_recall_description";
    }

    // The linked position is the one piece of state that differs per-stack and
    // isn't shown anywhere else in the UI, so it's worth surfacing in the tooltip.
    @Override
    protected void appendExtraTooltipLines(ItemStack stack, Consumer<Component> tooltipAdder) {
        Optional<GlobalPos> target = getLinkedTarget(stack);

        if (target.isEmpty()) {
            tooltipAdder.accept(Component.translatable("item.asclepius.golden_eye_of_recall.not_linked")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        GlobalPos globPos = target.get();
        BlockPos pos = globPos.pos();
        String dimensionName = prettifyDimensionName(globPos.dimension().identifier());

        tooltipAdder.accept(Component.translatable("item.asclepius.golden_eye_of_recall.linked_to",
                        dimensionName, pos.getX(), pos.getY(), pos.getZ())
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    // Vanilla has no generic "dimension display name" translation key — not even for
    // the overworld/nether/end — so derive one from the dimension id's path instead of
    // translating to a key that doesn't exist. Splitting on "_" and title-casing each
    // word happens to reproduce vanilla's own names exactly ("overworld" -> "Overworld",
    // "the_nether" -> "The Nether", "the_end" -> "The End") while also giving custom/
    // modded dimensions ("my_custom_dimension" -> "My Custom Dimension") a sensible name.
    private static String prettifyDimensionName(Identifier dimensionId) {
        StringBuilder result = new StringBuilder();
        for (String word : dimensionId.getPath().split("_")) {
            if (word.isEmpty()) continue;
            if (!result.isEmpty()) result.append(' ');
            result.append(Character.toUpperCase(word.charAt(0))).append(word, 1, word.length());
        }
        return result.toString();
    }
}
