package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.GuanoLayerBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GuanoPileFeature extends Feature<NoneFeatureConfiguration> {

    public GuanoPileFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos pileBottom = new BlockPos.MutableBlockPos();
        pileBottom.set(context.origin());
        if (!level.getBlockState(pileBottom).isAir() || level.canSeeSky(pileBottom)) {
            return false;
        }
        while (pileBottom.getY() > level.getMinBuildHeight() && level.getBlockState(pileBottom).canBeReplaced()) {
            pileBottom.move(0, -1, 0);
        }
        int centerLayerHeight = 1 + randomsource.nextInt(3);
        int radius = 3 + randomsource.nextInt(4);
        BlockPos center = pileBottom.immutable().above(centerLayerHeight).below();
        BlockPos.MutableBlockPos side = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int placedLayers = 0;
                side.set(pileBottom.getX() + x, pileBottom.getY() + placedLayers, pileBottom.getZ() + z);
                while (!level.getBlockState(side).canBeReplaced() && side.getY() < level.getMaxBuildHeight()) {
                    side.move(0, 1, 0);
                }
                while (level.getBlockState(side).canBeReplaced() && side.getY() > level.getMinBuildHeight()) {
                    side.move(0, -1, 0);
                }
                double dist = side.distToLowCornerSqr(center.getX(), side.getY(), center.getZ());
                float seaFloorExtra = (1.0F + ACMath.sampleNoise2D(side.getX(), side.getZ(), 6)) * 2;
                double radiusSq = radius * (radius - seaFloorExtra);
                if (dist <= radiusSq) {
                    int y = 0;
                    double invDist = (1F - (dist / (float) radiusSq));
                    while (y < centerLayerHeight * invDist) {
                        BlockState guanoState = ACBlockRegistry.GUANO_LAYER.get().defaultBlockState();
                        y++;
                        if (y < centerLayerHeight) {
                            guanoState = guanoState.setValue(GuanoLayerBlock.LAYERS, 8);
                        } else {
                            int j = Mth.clamp((int) Math.round(8 * invDist * invDist) - randomsource.nextInt(2), 1, 8);
                            guanoState = guanoState.setValue(GuanoLayerBlock.LAYERS, j);
                        }
                        if (canReplace(level.getBlockState(side)) && !level.canSeeSky(side.above()) && level.getBlockState(side.below()).isCollisionShapeFullBlock(level, side.below())) {
                            level.setBlock(side, guanoState, 3);
                        }
                        side.move(0, 1, 0);
                    }
                }
                if(level.canSeeSky(side)){
                    break;
                }
            }
        }
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced()) && !state.is(ACTagRegistry.UNMOVEABLE) && state.getFluidState().isEmpty();
    }
}
