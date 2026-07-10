package com.gnottero.asclepius.feature.recall;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;

public class EyeOfRecallItem extends ChargeableItem {

    public EyeOfRecallItem(Properties properties) {
        super(properties, Items.ENDER_PEARL, SoundEvents.RESPAWN_ANCHOR_AMBIENT);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide() || !(player instanceof ServerPlayer)) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);
        ServerPlayer.RespawnConfig respawnConfig = ((ServerPlayer) player).getRespawnConfig();

        if (respawnConfig == null || ((ServerLevel) level).getServer().getLevel(respawnConfig.respawnData().dimension()) == null) {
            player.sendSystemMessage(Component.translatable("block.minecraft.spawn.not_valid"));
            return InteractionResult.PASS;
        }

        TeleportTransition transition = ((ServerPlayer) player).findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);

        if (transition.missingRespawnBlock()) {
            player.sendSystemMessage(Component.translatable("block.minecraft.spawn.not_valid"));
            return InteractionResult.PASS;
        }

        // Charge is only spent once every other precondition is confirmed, so a
        // failed/invalid respawn attempt never wastes a charge.
        if (!tryConsumeCharge(heldItem, player)) return InteractionResult.FAIL;

        player.teleport(transition.withRotation(player.yBodyRot, player.xRotO));
        level.playSound(null, player.blockPosition(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected String getDescriptionKey() {
        return "item.asclepius.eye_of_recall_description";
    }
}