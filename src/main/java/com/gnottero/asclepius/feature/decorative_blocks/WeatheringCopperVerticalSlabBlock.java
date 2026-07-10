package com.gnottero.asclepius.feature.decorative_blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A vertical slab block that oxidizes over time.
 */
public class WeatheringCopperVerticalSlabBlock extends VerticalSlabBlock implements WeatheringCopper {

    public static final MapCodec<WeatheringCopperVerticalSlabBlock> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
                    propertiesCodec()
            ).apply(instance, WeatheringCopperVerticalSlabBlock::new));

    private final WeatheringCopper.WeatherState weatherState;

    public WeatheringCopperVerticalSlabBlock(
            WeatheringCopper.WeatherState weatherState,
            BlockBehaviour.Properties properties
    ) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public MapCodec<WeatheringCopperVerticalSlabBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}
