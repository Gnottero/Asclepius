package com.gnottero.asclepius.block;

import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class ChunkLoaderBlock extends Block implements FactoryBlock, CustomBreakingParticleBlock {

    public ChunkLoaderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(BlockStateProperties.POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED);
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean isPowered = state.getValue(BlockStateProperties.POWERED);
            if (isPowered != level.hasNeighborSignal(pos)) {
                if (isPowered) {
                    level.scheduleTick(pos, this, 4);
                } else {
                    level.setBlock(pos, state.cycle(BlockStateProperties.POWERED), 2);
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ChunkPos chunkPos = new ChunkPos(pos.getX(), pos.getZ());
        boolean isPowered = state.getValue(BlockStateProperties.POWERED);

        if (isPowered && level.hasNeighborSignal(pos)) {
            level.getChunkSource().addTicketWithRadius(TicketType.PLAYER_LOADING, chunkPos, 2);
            level.setChunkForced(chunkPos.x(), chunkPos.z(), true);
//            level.scheduleTick(pos, this, 4);
        } else {
            if (isPowered) level.setBlock(pos, state.cycle(BlockStateProperties.POWERED), 2);
//            level.getChunkSource().removeTicketWithRadius(TicketType.PLAYER_LOADING, chunkPos, 2);
//            level.setChunkForced(chunkPos.x(), chunkPos.z(), false);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.POWERED, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState blockState) {
        BlockState breakingState = Blocks.OBSIDIAN.defaultBlockState();
        return new BlockParticleOption(ParticleTypes.BLOCK, breakingState);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, @Nullable PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }
}
