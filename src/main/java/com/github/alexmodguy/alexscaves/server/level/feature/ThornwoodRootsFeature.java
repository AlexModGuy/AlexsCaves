package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class ThornwoodRootsFeature extends Feature<NoneFeatureConfiguration> {


    public ThornwoodRootsFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos generateAt = new BlockPos.MutableBlockPos();
        generateAt.set(context.origin());
        if (!level.getBlockState(generateAt).getFluidState().is(Fluids.WATER) && !level.isEmptyBlock(generateAt)) {
            return false;
        }
        while ((level.getBlockState(generateAt).getFluidState().is(Fluids.WATER) || !level.getBlockState(generateAt).isFaceSturdy(level, generateAt, Direction.DOWN)) && generateAt.getY() < level.getMaxBuildHeight()) {
            generateAt.move(0, 1, 0);
        }
        if (level.getBlockState(generateAt).isAir()) {
            return false;
        }
        BlockPos rootsFrom = generateAt.immutable();
        level.setBlock(rootsFrom, ACBlockRegistry.THORNWOOD_WOOD.get().defaultBlockState(), 3);
        ThornwoodTreeFeature.generateRoot(level, rootsFrom, 0F, randomsource, Direction.DOWN, 1 + randomsource.nextInt(2));
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            ThornwoodTreeFeature.generateRoot(level, rootsFrom, 0.4F, randomsource, direction, 2 + randomsource.nextInt(5));
        }
        return true;
    }
}