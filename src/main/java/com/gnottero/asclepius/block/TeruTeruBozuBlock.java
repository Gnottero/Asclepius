package com.gnottero.asclepius.block;

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
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TeruTeruBozuBlock extends Block implements FactoryBlock, CustomBreakingParticleBlock {

    public TeruTeruBozuBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                        .setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, @Nullable PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, PacketContext context) {
        return Blocks.WHITE_WOOL.defaultBlockState();
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
        if (itemStack.is(Items.SUNFLOWER) && (level.isRaining() || level.isThundering())) {
            level.gameEvent(null, GameEvent.ENTITY_ACTION, pos);
            itemStack.consume(1, player);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.getWeatherData().setClearWeatherTime(24000);

                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        10, 0.5D, 0.5D, 0.5D, 0.02D);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
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
        if (!state.is(oldState.getBlock())) {
            if (!level.isClientSide()) {
                level.scheduleTick(pos, this, 1);
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Direction direction) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(pos, initialBlockState);
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState blockState) {
        BlockState breakingState = Blocks.WHITE_WOOL.defaultBlockState();
        return new BlockParticleOption(ParticleTypes.BLOCK, breakingState);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    public static final class Model extends BlockModel {
        private static final float FLOAT_AMPLITUDE = 0.06F;
        private static final float FLOAT_SPEED = 0.05F;

        private final ItemDisplayElement main;
        private BlockState state;

        public Model(BlockPos pos, BlockState state) {
            this.state = state;
            this.main = ItemDisplayElementUtil.createSimple(ItemDisplayElementUtil.getModel(state.getBlock().asItem()).get());
            this.main.setDisplaySize(1, 1);
            this.main.setScale(new Vector3f(1.5F));
            this.updateState(state);
            this.addElement(this.main);
        }

        private void updateState(BlockState state) {
            this.state = state;
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            this.main.setItem(ItemDisplayElementUtil.getModel(state.getBlock().asItem()).get());
            this.main.setRightRotation(
                    new Quaternionf().rotationY((float) Math.toRadians(-dir.toYRot() + 180)));
        }

        @Override
        public void tick() {
            super.tick();

            if (this.getTick() % 2 == 0) {
                float yOffset = (float) Math.sin(this.getTick() * FLOAT_SPEED) * FLOAT_AMPLITUDE;
                float xOffset = (float) Math.cos(this.getTick() * FLOAT_SPEED) * FLOAT_AMPLITUDE;
                float zOffset = (float) Math.sin(this.getTick() * FLOAT_SPEED) * FLOAT_AMPLITUDE;
                this.main.setInterpolationDuration(3);
                this.main.setTranslation(new Vector3f(xOffset, 0.2F + yOffset, -zOffset));
                this.main.startInterpolation();
            }
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                var state = this.blockState();
                this.main.setBrightness(null);
                this.updateState(state);
            }
        }

        public BlockState getState() {
            return state;
        }

        public void setState(BlockState state) {
            this.state = state;
        }
    }
}