package com.gnottero.asclepius.block;

import com.gnottero.asclepius.block.entity.PaleAltarBlockEntity;
import com.gnottero.asclepius.item.HammerItem;
import com.gnottero.asclepius.recipe.AltarRecipe;
import com.gnottero.asclepius.recipe.AltarRecipeInput;
import com.gnottero.asclepius.registry.AsclepiusRecipes;
import com.gnottero.asclepius.utils.EnchantmentMerger;
import com.gnottero.asclepius.utils.PlayerXpUtils;
import com.mojang.serialization.MapCodec;
import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class PaleAltarBlock extends BaseEntityBlock implements FactoryBlock, CustomBreakingParticleBlock {

    private static final Component MSG_CONDITIONS_NOT_MET = Component.translatable("block.asclepius.altar.conditions_not_met");
    private static final Component MSG_NO_COMPATIBLE_ENCHANTS = Component.translatable("block.asclepius.altar.no_compatible_enchants");

    public PaleAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, @Nullable PacketContext context) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, PacketContext context) {
        return Blocks.PALE_OAK_WOOD.defaultBlockState();
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState blockState) {
        return new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PALE_OAK_WOOD.defaultBlockState());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(pos, initialBlockState);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PaleAltarBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (!(level.getBlockEntity(pos) instanceof PaleAltarBlockEntity altar) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        if (stack.isEmpty()) {
            ItemStack stored = altar.removeStoredItem();
            if (!stored.isEmpty()) {
                player.getInventory().add(stored);
                syncBlock(serverPlayer, level, pos);
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.PASS;
        }

        if (stack.getItem() instanceof HammerItem && !altar.getStoredItem().isEmpty() && level.canSeeSky(pos.above())) {
            return attemptCraft(altar, stack, serverPlayer, level, pos);
        }

        if (altar.getStoredItem().isEmpty()) {
            altar.setStoredItem(stack.split(1));
            syncBlock(serverPlayer, level, pos);
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    private static void syncBlock(ServerPlayer player, Level level, BlockPos pos) {
        player.connection.send(new ClientboundBlockUpdatePacket(level, pos));
        player.inventoryMenu.sendAllDataToRemote();
    }

    private InteractionResult attemptCraft(PaleAltarBlockEntity altar, ItemStack hammer, ServerPlayer player, Level level, BlockPos pos) {
        ItemStack altarItem = altar.getStoredItem();
        ItemStack offhand = player.getOffhandItem();
        boolean isCreative = player.isCreative();

        boolean consumeCatalyst = true;
        int requiredLevels = 0;
        int requiredXPPoints = 0;
        int catalystAmount = 0;
        ItemStack result;

        AltarRecipeInput input = new AltarRecipeInput(altarItem, offhand);
        Optional<RecipeHolder<AltarRecipe>> recipeMatch = ((ServerLevel) level).recipeAccess()
                .getRecipeFor(AsclepiusRecipes.ALTAR_TYPE, input, level);

        if (recipeMatch.isPresent()) {
            AltarRecipe recipe = recipeMatch.get().value();
            if (!isCreative) {
                requiredLevels = recipe.getConditions().requiredLevels();
                requiredXPPoints = PlayerXpUtils.getTotalXpForLevel(requiredLevels);
                catalystAmount = recipe.getCatalystItem().count();
                consumeCatalyst = recipe.consumeCatalyst();
            }
            result = recipe.assemble(input);
        }
        else {
            boolean offhandHasEnchants = (offhand.has(DataComponents.ENCHANTMENTS) && !offhand.get(DataComponents.ENCHANTMENTS).isEmpty()) ||
                    (offhand.has(DataComponents.STORED_ENCHANTMENTS) && !offhand.get(DataComponents.STORED_ENCHANTMENTS).isEmpty());

            if (!altarItem.isEmpty() && offhandHasEnchants && (altarItem.is(offhand.getItem()) || offhand.is(Items.ENCHANTED_BOOK))) {
                Optional<EnchantmentMerger.MergeResult> mergeResult = EnchantmentMerger.tryMerge(altarItem, offhand, player);
                if (mergeResult.isEmpty()) {
                    player.sendOverlayMessage(MSG_NO_COMPATIBLE_ENCHANTS);
                    return InteractionResult.PASS;
                }
                EnchantmentMerger.MergeResult merge = mergeResult.get();
                requiredLevels = merge.xpCost();
                requiredXPPoints = PlayerXpUtils.getTotalXpForLevel(requiredLevels);
                catalystAmount = 1;
                result = merge.output();
            } else {
                return InteractionResult.PASS;
            }
        }

        if (!isCreative && PlayerXpUtils.getTotalXp(player) < requiredXPPoints) {
            player.sendOverlayMessage(Component.translatable("block.asclepius.altar.not_enough_xp", requiredLevels));
            return InteractionResult.PASS;
        }

        if (recipeMatch.isPresent() && !isCreative) {
            AltarRecipe recipe = recipeMatch.get().value();
            if (!recipe.checkConditions(player, level)) {
                player.sendOverlayMessage(MSG_CONDITIONS_NOT_MET);
                return InteractionResult.PASS;
            }
        }

        if (!isCreative) {
            if (consumeCatalyst) {
                offhand.shrink(catalystAmount);
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

        syncBlock(player, level, pos);
        return InteractionResult.SUCCESS_SERVER;
    }

    public static final class Model extends BlockModel {
        private static final float FLOAT_AMPLITUDE = 0.06f;
        private static final float FLOAT_SPEED = 0.05f;
        private static final float ROTATION_SPEED = 0.05f;

        private final Quaternionf rotationBuffer = new Quaternionf();
        private final AxisAngle4f axisAngle = new AxisAngle4f();

        private final ItemDisplayElement main;
        private final ItemDisplayElement altarItem;

        private float lastYOffset = 0f;
        private float lastAngle = 0f;

        public Model(BlockPos pos, BlockState state) {
            this.main = ItemDisplayElementUtil.createSimple(ItemDisplayElementUtil.getModel(state.getBlock().asItem()).get());
            this.main.setDisplaySize(1, 1);
            this.main.setScale(new Vector3f(2.0F));
            this.addElement(this.main);

            this.altarItem = new ItemDisplayElement();
            this.altarItem.setDisplaySize(1, 1);
            this.altarItem.setScale(new Vector3f(0.4f));
            this.altarItem.setTranslation(new Vector3f(0f, 0.75f, 0f));
            this.addElement(this.altarItem);
        }

        public void setAltarItem(ItemStack stack) {
            if (!ItemStack.matches(this.altarItem.getItem(), stack)) {
                this.altarItem.setItem(stack.copy());
                this.altarItem.tick();
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (this.altarItem.getItem().isEmpty()) return;

            int tick = this.getTick();
            if ((tick & 1) != 0) return;

            float t = tick * FLOAT_SPEED;
            float yOffset = (float) Math.sin(t) * FLOAT_AMPLITUDE;
            float angle = tick * ROTATION_SPEED;

            if (Math.abs(yOffset - lastYOffset) < 0.001f && Math.abs(angle - lastAngle) < 0.001f) {
                return;
            }
            lastYOffset = yOffset;
            lastAngle = angle;

            axisAngle.set(angle, 0f, 1f, 0f);
            rotationBuffer.set(axisAngle);

            this.altarItem.setInterpolationDuration(3);
            this.altarItem.setTranslation(new Vector3f(0f, 0.75f + yOffset, 0f));
            this.altarItem.setLeftRotation(rotationBuffer);
            this.altarItem.startInterpolation();
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                var state = this.blockState();
                this.main.setItem(ItemDisplayElementUtil.getModel(state.getBlock().asItem()).get());
                this.main.tick();
            }
        }
    }
}