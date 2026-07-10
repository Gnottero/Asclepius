package com.gnottero.asclepius.feature.decorative_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A vertical slab block that occupies half of a block's depth horizontally.
 */
public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock {

    /** Indicates if this position contains two vertical slabs merged into a full block. */
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

    /** Standard waterlogging block state property. */
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** Horizontal direction this vertical slab faces. */
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    /** Pre-computed shapes for all four cardinal facing directions. */
    public static final Map<Direction, VoxelShape> SHAPES =
            Shapes.rotateHorizontal(Block.box(0.0F, 0.0F, 8.0F, 16.0F, 16.0F, 16.0F));

    /**
     * Constructs a vertical slab block.
     */
    public VerticalSlabBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(WATERLOGGED, false)
                        .setValue(DOUBLE, false)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(DOUBLE) ? Shapes.block() : SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return !state.getValue(DOUBLE);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);

        if (state.is(this) && !state.getValue(DOUBLE)) {
            return state.setValue(DOUBLE, true).setValue(WATERLOGGED, false);
        }

        Direction face = context.getClickedFace();
        Direction facing = face.getAxis().isHorizontal()
                ? face
                : context.getHorizontalDirection().getOpposite();

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(pos).is(Fluids.WATER));
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (state.getValue(DOUBLE) || !context.getItemInHand().is(this.asItem())) return false;
        return !context.replacingClickedOnBlock() || context.getClickedFace() == state.getValue(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }
}