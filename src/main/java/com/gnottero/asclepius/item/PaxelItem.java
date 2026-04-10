package com.gnottero.asclepius.item;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.registry.AsclepiusTags;
import eu.pb4.polymer.core.api.item.VanillaModeledPolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class PaxelItem extends Item implements VanillaModeledPolymerItem {
    private final Item polymerItem;
    private final String identifier;

    public PaxelItem(Item polymerItem, ToolMaterial material, float attackDamage, float attackSpeed, Properties properties, String identifier, TagKey<Item> repairMaterial) {
        super(properties
                .enchantable(material.enchantmentValue()).tool(material, AsclepiusTags.MINEABLE_WITH_PAXEL, attackDamage, attackSpeed, 0F)
                .repairable(repairMaterial)
        );
        this.polymerItem = polymerItem;
        this.identifier = identifier;
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {

        InteractionResult result = Items.NETHERITE_SHOVEL.useOn(context);
        if (result.consumesAction()) {
            context.getPlayer().swing(context.getHand());
            return InteractionResult.SUCCESS_SERVER;
        }

        result = Items.NETHERITE_AXE.useOn(context);
        if (result.consumesAction()) return InteractionResult.SUCCESS_SERVER;

        return super.useOn(context);
    }

    @Override
    public boolean isPolymerItemInteraction(ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, InteractionResult actionResult) {
        return true;
    }

    @Override
    public boolean isPolymerBlockInteraction(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult, InteractionResult actionResult) {
        return true;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return this.polymerItem;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, this.identifier);
    }
}