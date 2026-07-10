package com.gnottero.asclepius.feature.decorative_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A block representing stairs rotated 90 degrees so that they lie on their side.
 */
public class SidewaysStairBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SidewaysStairBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(FLIPPED, false)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean flipped = state.getValue(FLIPPED);

        switch (facing) {
            case NORTH:
                return flipped ? Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D), Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D))
                               : Shapes.or(Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D));
            case SOUTH:
                return flipped ? Shapes.or(Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D))
                               : Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D), Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D));
            case EAST:
                return flipped ? Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D), Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D))
                               : Shapes.or(Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D), Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D));
            case WEST:
            default:
                return flipped ? Shapes.or(Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D))
                               : Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D), Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        Direction face = context.getClickedFace();

        Direction facing = face.getAxis().isHorizontal() ? face.getOpposite() : context.getHorizontalDirection();

        // Determine flipped half based on click coordinate relative to block center
        Vec3 click = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        boolean flipped = false;

        if (facing == Direction.NORTH) {
            flipped = click.x() < 0.5;
        } else if (facing == Direction.SOUTH) {
            flipped = click.x() > 0.5;
        } else if (facing == Direction.EAST) {
            flipped = click.z() < 0.5;
        } else if (facing == Direction.WEST) {
            flipped = click.z() > 0.5;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(FLIPPED, flipped)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FLIPPED, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
