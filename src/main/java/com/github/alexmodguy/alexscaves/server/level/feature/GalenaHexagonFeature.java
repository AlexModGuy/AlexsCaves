package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.config.GalenaHexagonFeatureConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;

public class GalenaHexagonFeature extends Feature<GalenaHexagonFeatureConfiguration> {

    public GalenaHexagonFeature(Codec<GalenaHexagonFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GalenaHexagonFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        List<BlockPos> genPos = new ArrayList<>();
        BlockPos chunkCenter = new BlockPos(context.origin().getX(), level.getMinBuildHeight() + 3, context.origin().getZ());
        int surface = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, chunkCenter.getX(), chunkCenter.getZ());
        while (chunkCenter.getY() < surface) {
            BlockPos next = chunkCenter.above();
            BlockState currentState = level.getBlockState(chunkCenter);
            BlockState nextState = level.getBlockState(next);
            if (context.config().ceiling) {
                if (nextState.is(ACBlockRegistry.GALENA.get()) && canReplace(currentState)) {
                    genPos.add(chunkCenter);
                }
            } else {
                if (currentState.is(ACBlockRegistry.GALENA.get()) && canReplace(nextState)) {
                    genPos.add(chunkCenter);
                }
            }
            chunkCenter = next;
        }
        for (BlockPos floor : genPos) {
            drawHexagon(level, floor, randomSource, context.config().hexBlock, 3 + randomSource.nextInt(6), 1 + randomSource.nextInt(3), !context.config().ceiling);
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    private static void drawHexagon(WorldGenLevel level, BlockPos center, RandomSource random, BlockStateProvider blockState, int height, int radius, boolean goingUp) {
        int startY = -4 - random.nextInt(4);
        int endY = height + 1;
        for (int y = startY; y < endY; y++) {
            int setY = y * (goingUp ? 1 : -1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos fill = center.offset(x, setY, z);
                    if (fill.distToLowCornerSqr(center.getX(), fill.getY(), center.getZ()) <= radius * radius) {
                        if (canReplace(level.getBlockState(fill))) {
                            level.setBlock(fill, blockState.getState(random, fill), 3);
                        }
                    }
                }
            }
        }
    }
}
