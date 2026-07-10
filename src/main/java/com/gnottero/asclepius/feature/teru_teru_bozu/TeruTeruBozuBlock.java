package com.gnottero.asclepius.feature.teru_teru_bozu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TeruTeruBozuBlock extends Block {

    // The model is a small plush hanging in the upper half of the block, not a
    // full cube — matches its Blockbench bounds roughly so the selection outline
    // and collision/light shape aren't a full-block box around empty space.
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 13, 13);

    public TeruTeruBozuBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                        .setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean isWeatherActive = ctx.getLevel().isRaining() || ctx.getLevel().isThundering();
        return this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection())
                .setValue(BlockStateProperties.POWERED, isWeatherActive);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isRaining() && !level.isThundering()) return InteractionResult.PASS;
        if (!itemStack.is(Items.SUNFLOWER)) return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer) {
            level.gameEvent(null, GameEvent.ENTITY_ACTION, pos);
            itemStack.consume(1, player);

            serverLevel.getWeatherData().setClearWeatherTime(24000);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10, 0.5D, 0.5D, 0.5D, 0.02D);
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean isWeatherActive = level.isRaining() || level.isThundering();
        boolean isPowered = state.getValue(BlockStateProperties.POWERED);

        if (isPowered != isWeatherActive) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, isWeatherActive), 3);
        }

        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!state.is(oldState.getBlock()) && !level.isClientSide()) {
            level.scheduleTick(pos, this, 1);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }
}