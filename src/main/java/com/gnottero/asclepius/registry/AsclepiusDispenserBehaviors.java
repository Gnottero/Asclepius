package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.block.TeruTeruBozuBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NonNull;

public class AsclepiusDispenserBehaviors {

    public static void registerAll() {
        DispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
            @Override
            public @NonNull ItemStack execute(@NonNull BlockSource source, @NonNull ItemStack stack) {
                ServerLevel level = source.level();
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos pos = source.pos().relative(direction);
                BlockState state = level.getBlockState(pos);

                if (state.is(AsclepiusBlocks.TERU_TERU_BOZU_BLOCK) && (level.isRaining() || level.isThundering())) {
                    level.gameEvent(null, GameEvent.ENTITY_ACTION, pos);
                    stack.shrink(1);
                    level.getWeatherData().setClearWeatherTime(24000);

                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                            10, 0.5D, 0.5D, 0.5D, 0.02D);
                    
                    return stack;
                }
                return super.execute(source, stack);
            }
        };

        DispenserBlock.registerBehavior(Items.SUNFLOWER, behavior);
    }
}
