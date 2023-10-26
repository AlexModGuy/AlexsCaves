package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GeothermalVentBlock;
import com.github.alexmodguy.alexscaves.server.block.ThinGeothermalVentBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BlackVentFeature extends Feature<NoneFeatureConfiguration> {

    public BlackVentFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos ventBottom = new BlockPos.MutableBlockPos();
        ventBottom.set(context.origin());
        while (!level.getBlockState(ventBottom).getFluidState().isEmpty() && ventBottom.getY() > level.getMinBuildHeight()) {
            ventBottom.move(0, -1, 0);
        }
        if (level.getBlockState(ventBottom.below()).equals(Blocks.TUFF.defaultBlockState())) {
            drawVent(level, ventBottom.above().immutable(), randomsource);
            return true;
        }
        return false;
    }

    private static void drawVent(WorldGenLevel level, BlockPos ventBottom, RandomSource randomsource) {
        int height = randomsource.nextInt(6) + 2;
        ventBottom = ventBottom.below();
        level.setBlock(ventBottom.north(), Blocks.TUFF.defaultBlockState(), 3);
        level.setBlock(ventBottom.south(), Blocks.TUFF.defaultBlockState(), 3);
        level.setBlock(ventBottom.east(), Blocks.TUFF.defaultBlockState(), 3);
        level.setBlock(ventBottom.west(), Blocks.TUFF.defaultBlockState(), 3);
        level.setBlock(ventBottom.below(), Blocks.TUFF.defaultBlockState(), 3);
        level.setBlock(ventBottom, Blocks.LAVA.defaultBlockState(), 3);
        int middleStart = Math.max(1, height / 3);
        int middleTop = middleStart * 2;
        for (int i = 1; i <= height; i++) {
            BlockState vent;
            if (i <= middleStart) {
                vent = ACBlockRegistry.GEOTHERMAL_VENT.get().defaultBlockState().setValue(GeothermalVentBlock.SMOKE_TYPE, 2).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height);
            } else if (i > middleTop) {
                vent = ACBlockRegistry.GEOTHERMAL_VENT_THIN.get().defaultBlockState().setValue(GeothermalVentBlock.SMOKE_TYPE, 2).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height).setValue(ThinGeothermalVentBlock.WATERLOGGED, true);
            } else {
                vent = ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get().defaultBlockState().setValue(GeothermalVentBlock.SMOKE_TYPE, 2).setValue(GeothermalVentBlock.SPAWNING_PARTICLES, i == height).setValue(ThinGeothermalVentBlock.WATERLOGGED, true);
            }
            level.setBlock(ventBottom.above(i), vent, 3);
        }
    }
}
