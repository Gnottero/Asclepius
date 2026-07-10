package com.gnottero.asclepius.feature.pale_altar;

import com.gnottero.asclepius.block.SimpleEntityBlock;
import com.gnottero.asclepius.feature.pale_altar.recipe.AltarRecipe;
import com.gnottero.asclepius.feature.pale_altar.recipe.AltarRecipeInput;
import com.gnottero.asclepius.feature.pale_altar.recipe.AltarRitualRecipe;
import com.gnottero.asclepius.feature.pale_altar.recipe.EnchantmentMergeRecipe;
import com.gnottero.asclepius.feature.pale_altar.recipe.EnchantmentMerger;
import com.gnottero.asclepius.feature.pale_altar.recipe.SocketGrantRecipe;
import com.gnottero.asclepius.feature.tools.HammerItem;
import com.gnottero.asclepius.registry.AsclepiusComponents;
import com.gnottero.asclepius.registry.AsclepiusRecipes;
import com.gnottero.asclepius.utils.PlayerXpUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * The Pale Altar's interaction and ritual-crafting logic. {@code useItemOn} handles
 * three interaction modes keyed off the held item (withdraw with an empty hand,
 * craft with a Hammer, insert with anything else) and must return the same
 * {@link InteractionResult} on both the client and server branches, since the
 * method runs on both sides and the client has no other way to know the eventual
 * server outcome. {@code attemptCraft} tries the three ritual recipe types in a
 * fixed precedence (transform &gt; socket-grant &gt; enchantment-merge) — they're
 * mutually exclusive by design, not by accident.
 */
public class PaleAltarBlock extends SimpleEntityBlock {

    private static final Component MSG_CONDITIONS_NOT_MET = Component.translatable("block.asclepius.altar.conditions_not_met");
    private static final Component MSG_NO_COMPATIBLE_ENCHANTS = Component.translatable("block.asclepius.altar.no_compatible_enchants");

    public PaleAltarBlock(Properties properties) {
        super(properties, PaleAltarBlockEntity::new);
    }

    // useItemOn runs on both the logical client (for interaction prediction) and
    // the logical server; only the server branch (serverPlayer != null) is allowed
    // to mutate the altar/inventory, but every branch must still return the same
    // InteractionResult on both sides so the client doesn't fall back to a
    // different vanilla interaction while waiting for the server to confirm.
    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (!(level.getBlockEntity(pos) instanceof PaleAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        boolean altarHasItem = !altar.getStoredItem().isEmpty();
        ServerPlayer serverPlayer = (level instanceof ServerLevel && player instanceof ServerPlayer sp) ? sp : null;

        // Empty hand: withdraw the stored item.
        if (stack.isEmpty()) {
            if (!altarHasItem) return InteractionResult.PASS;
            if (serverPlayer != null) {
                player.getInventory().add(altar.removeStoredItem());
                syncBlock(serverPlayer);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        // Hammer with an item already on the altar and open sky above: attempt the ritual craft.
        if (stack.getItem() instanceof HammerItem && altarHasItem && level.canSeeSky(pos.above())) {
            if (serverPlayer != null) {
                return attemptCraft(altar, stack, serverPlayer, level, pos);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        // Any other item with an empty altar: insert it. Creative players keep their
        // stack — split(1) would remove the item from their hand like survival mode.
        if (!altarHasItem) {
            if (serverPlayer != null) {
                ItemStack toStore = player.hasInfiniteMaterials() ? stack.copyWithCount(1) : stack.split(1);
                altar.setStoredItem(toStore);
                syncBlock(serverPlayer);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    private static void syncBlock(ServerPlayer player) {
        player.inventoryMenu.sendAllDataToRemote();
    }

    private InteractionResult attemptCraft(PaleAltarBlockEntity altar, ItemStack hammer, ServerPlayer player, Level level, BlockPos pos) {
        ItemStack altarItem = altar.getStoredItem();
        ItemStack offhand = player.getOffhandItem();
        boolean isCreative = player.isCreative();

        AltarRecipeInput input = new AltarRecipeInput(altarItem, offhand);
        var recipeAccess = ((ServerLevel) level).recipeAccess();

        // Recipe types are mutually exclusive by precedence — transform > socketGrant
        // > enchantMerge — so an altar+catalyst pair matching an earlier type never
        // falls through to a later one, even if it would also technically match.
        Optional<RecipeHolder<AltarRecipe>> transform = recipeAccess.getRecipeFor(AsclepiusRecipes.ALTAR_TYPE, input, level);
        Optional<RecipeHolder<SocketGrantRecipe>> socketGrant = transform.isPresent()
                ? Optional.empty()
                : recipeAccess.getRecipeFor(AsclepiusRecipes.SOCKET_GRANT_TYPE, input, level);
        Optional<RecipeHolder<EnchantmentMergeRecipe>> enchantMerge = (transform.isPresent() || socketGrant.isPresent())
                ? Optional.empty()
                : recipeAccess.getRecipeFor(AsclepiusRecipes.ENCHANTMENT_MERGE_TYPE, input, level);

        AltarRitualRecipe recipe;
        ItemStack result;
        int requiredLevels;

        if (transform.isPresent()) {
            AltarRecipe r = transform.get().value();
            recipe = r;
            requiredLevels = r.getConditions().requiredLevels();
            result = r.assemble(input);
        } else if (socketGrant.isPresent()) {
            SocketGrantRecipe r = socketGrant.get().value();
            if (altarItem.getOrDefault(AsclepiusComponents.MAX_SOCKETS, 0) >= r.getMaxSockets()) {
                player.sendOverlayMessage(MSG_CONDITIONS_NOT_MET);
                return InteractionResult.PASS;
            }
            recipe = r;
            requiredLevels = r.getConditions().requiredLevels();
            result = r.assemble(input);
        } else if (enchantMerge.isPresent()) {
            EnchantmentMergeRecipe r = enchantMerge.get().value();
            Optional<EnchantmentMerger.MergeResult> merge = r.tryMerge(input);
            if (merge.isEmpty()) {
                player.sendOverlayMessage(MSG_NO_COMPATIBLE_ENCHANTS);
                return InteractionResult.PASS;
            }
            recipe = r;
            requiredLevels = merge.get().xpCost();
            result = merge.get().output();
        } else {
            return InteractionResult.PASS;
        }

        int requiredXPPoints = PlayerXpUtils.getTotalXpForLevel(requiredLevels);

        if (!isCreative && PlayerXpUtils.getTotalXp(player) < requiredXPPoints) {
            player.sendOverlayMessage(Component.translatable("block.asclepius.altar.not_enough_xp", requiredLevels));
            return InteractionResult.PASS;
        }

        if (!isCreative && !recipe.checkConditions(player, level)) {
            player.sendOverlayMessage(MSG_CONDITIONS_NOT_MET);
            return InteractionResult.PASS;
        }

        if (!isCreative) {
            if (recipe.consumeCatalyst()) {
                offhand.shrink(recipe.getCatalystAmount());
            } else if (offhand.isDamageableItem()) {
                offhand.hurtAndBreak(1, player, InteractionHand.OFF_HAND);
            }
            hammer.hurtAndBreak(1, player, InteractionHand.MAIN_HAND);
            if (requiredXPPoints > 0) player.giveExperiencePoints(-requiredXPPoints);
        }

        altar.setStoredItem(result);

        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        ((ServerLevel) level).sendParticles(ParticleTypes.OMINOUS_SPAWNING,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                30, 0.3, 1.0, 0.3, 0.05);

        syncBlock(player);
        return InteractionResult.SUCCESS_SERVER;
    }
}
