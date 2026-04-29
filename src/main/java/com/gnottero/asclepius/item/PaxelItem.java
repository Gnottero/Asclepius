package com.gnottero.asclepius.item;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.registry.AsclepiusTags;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class PaxelItem extends SimplePolymerItem {
    private final String identifier;

    public PaxelItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties, String identifier, TagKey<Item> repairMaterial) {
        super(properties
                .enchantable(material.enchantmentValue())
                .tool(material, AsclepiusTags.MINEABLE_WITH_PAXEL, attackDamage, attackSpeed, 0F)
                .durability(material.durability() * 3)
                .repairable(repairMaterial)
        );
        this.identifier = identifier;
    }

    @Override
    public boolean isPolymerBlockInteraction(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult, InteractionResult actionResult) {
        return true;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, this.identifier);
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.PASS;

        InteractionResult result = Items.NETHERITE_AXE.useOn(context);
        if (result.consumesAction()) {
            level.playSound(null, context.getClickedPos(), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.useOn(context);
    }
}