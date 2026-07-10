package com.gnottero.asclepius.feature.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Registered on {@code PlayerBlockBreakEvents.BEFORE}: when a Hammer breaks a block
 * (and the player isn't sneaking), breaks every other correct-tool block in the 3x3
 * plane orthogonal to the mined face too. The two plane axes are derived differently
 * for a vertical vs. horizontal mined face (there's no "clockwise" of the vertical
 * axis to rotate from for a horizontal face, so the player's own facing is used
 * instead) — see the inline comment at the axis derivation for the exact geometry.
 */
public class HammerBreakHandler {
    public static boolean onHammerBreaksBlock(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof HammerItem)) return true;
        if (player.isCrouching()) return true;

        // Build the two axes spanning the 3x3 plane orthogonal to the mined face:
        // for a vertical face (broken sideways), the plane is horizontal-facing x
        // vertical; for a horizontal face (broken up/down), the plane is spanned by
        // rotating the player's own facing direction instead, since there's no
        // "clockwise" of the Y axis to derive it from.
        Direction hitFace = ((BlockHitResult) player.pick(player.blockInteractionRange(), 1.0F, false)).getDirection();
        Direction axis1 = hitFace.getAxis() == Direction.Axis.Y ? player.getDirection() : hitFace.getClockWise();
        Direction axis2 = hitFace.getAxis() == Direction.Axis.Y ? axis1.getClockWise() : Direction.UP;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos target = pos.relative(axis1, i).relative(axis2, j);
                if (target.equals(pos)) continue;
                BlockState targetState = level.getBlockState(target);
                if (targetState.isAir()) continue;
                if (!player.isCreative() && !itemStack.isCorrectToolForDrops(targetState)) continue;
                Block.dropResources(targetState, level, target, level.getBlockEntity(target), player, itemStack);
                level.destroyBlock(target, false, player);
                itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
        }

        return true;
    }
}
