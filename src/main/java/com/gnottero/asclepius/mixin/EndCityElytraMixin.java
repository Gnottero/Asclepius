package com.gnottero.asclepius.mixin;

import com.gnottero.asclepius.registry.AsclepiusItems;
import com.gnottero.asclepius.registry.AsclepiusLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.structures.EndCityPieces$EndCityPiece")
public abstract class EndCityElytraMixin extends StructurePiece {

    protected EndCityElytraMixin(StructurePieceType type, int genDepth, BoundingBox boundingBox) {
        super(type, genDepth, boundingBox);
    }

    // Injects at the head of vanilla's per-structure-piece data-marker handler and
    // cancels it for the "Elytra" marker specifically, hijacking the spot where
    // vanilla would place the item-frame-with-elytra to place a loot vault instead.
    @Inject(method = "handleDataMarker", at = @At("HEAD"), cancellable = true)
    private void replaceElytraWithVault(String markerId, BlockPos position, ServerLevelAccessor level, RandomSource random, BoundingBox chunkBB, CallbackInfo ci) {
        if (!markerId.equals("Elytra")) return;

        ci.cancel();

        // Structure pieces store their rotation relative to a canonical orientation;
        // rotating SOUTH (the template's "front") gives the facing this piece was
        // actually placed with, which the vault needs to face the right way.
        Direction facing = this.getRotation().rotate(Direction.SOUTH);

        BlockPos vaultPos = position.below();
        BlockState vaultState = Blocks.VAULT.defaultBlockState().setValue(VaultBlock.FACING, facing);
        level.setBlock(vaultPos, vaultState, 3);

        if (level.getBlockEntity(vaultPos) instanceof VaultBlockEntity vault) {
            VaultConfig config = new VaultConfig(
                    AsclepiusLootTables.VAULT_END_SHIP,
                    4.0,
                    4.5,
                    AsclepiusItems.ENDER_KEY.getDefaultInstance(),
                    Optional.empty()
            );
            vault.setConfig(config);
        }
    }
}
