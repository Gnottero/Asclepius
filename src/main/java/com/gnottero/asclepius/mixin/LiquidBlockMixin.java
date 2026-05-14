package com.gnottero.asclepius.mixin;

import com.gnottero.asclepius.registry.AsclepiusBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void onShouldSpreadLiquid(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide()) return;
        if (!((LiquidBlockAccessor) this).getFluid().is(FluidTags.LAVA)) return;
        if (level.getFluidState(pos).isSource()) return;

        for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            if (level.getFluidState(pos.relative(direction.getOpposite())).is(FluidTags.WATER)) {
                if (hasMagmaNeighbor(level, pos)) {
                    level.setBlockAndUpdate(pos, AsclepiusBlocks.VOLCANIC_ASH.defaultBlockState());
                    level.levelEvent(1501, pos, 0);
                    cir.setReturnValue(false);
                }
                return;
            }
        }
    }

    private static boolean hasMagmaNeighbor(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(Blocks.MAGMA_BLOCK)) {
                return true;
            }
        }
        return false;
    }
}