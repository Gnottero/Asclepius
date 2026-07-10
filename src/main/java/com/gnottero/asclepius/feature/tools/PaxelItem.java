package com.gnottero.asclepius.feature.tools;

import com.gnottero.asclepius.registry.AsclepiusTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class PaxelItem extends Item {

    public PaxelItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties, String identifier, TagKey<Item> repairMaterial) {
        super(ToolItems.commonToolProperties(properties, material, repairMaterial)
                .tool(material, AsclepiusTags.MINEABLE_WITH_PAXEL, attackDamage, attackSpeed, 0F));
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