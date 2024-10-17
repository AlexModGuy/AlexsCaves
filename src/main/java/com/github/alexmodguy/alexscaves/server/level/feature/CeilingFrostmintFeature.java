package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.FrostmintBlock;
import com.github.alexmodguy.alexscaves.server.block.IceCreamBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CeilingFrostmintFeature extends Feature<NoneFeatureConfiguration> {

    public CeilingFrostmintFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();
        boolean aboveSoda = false;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        for (scanPos.set(context.origin()); scanPos.getY() > worldgenlevel.getMinBuildHeight(); scanPos.move(0, -1, 0)) {
            if (worldgenlevel.getBlockState(scanPos).is(ACBlockRegistry.PURPLE_SODA.get())) {
                aboveSoda = true;
                break;
            }
        }
        if (aboveSoda) {
            for (scanPos.set(context.origin()); scanPos.getY() >= worldgenlevel.getMaxBuildHeight() - 3; scanPos.move(0, 1, 0)) {
                if (!worldgenlevel.isEmptyBlock(scanPos)) {
                    break;
                }
            }
            if (scanPos.getY() >= worldgenlevel.getMaxBuildHeight() - 3) {
                return false;
            } else {
                BlockPos blockpos = scanPos.immutable();
                for (int l = 0; l < 3; ++l) {
                    int i = 1 + randomsource.nextInt(2);
                    int j = 1 + randomsource.nextInt(2);
                    int k = 1 + randomsource.nextInt(2);
                    float f = (float) (i + j + k) * 0.333F + 0.5F;
                    double radius = f * f;
                    for (BlockPos replacePos : BlockPos.betweenClosed(blockpos.offset(-i, -j, -k), blockpos.offset(i, j, k))) {
                        BlockState replaceState = worldgenlevel.getBlockState(replacePos);
                        double dist = replacePos.distSqr(blockpos);
                        if (dist <= radius && randomsource.nextFloat() < 0.75F && (replaceState.is(ACBlockRegistry.CAKE_LAYER.get()) || replaceState.is(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get()))) {
                            worldgenlevel.setBlock(replacePos, dist <= radius / 2 ? ACBlockRegistry.CYAN_ROCK_CANDY.get().defaultBlockState() : ACBlockRegistry.WHITE_ROCK_CANDY.get().defaultBlockState(), 3);
                        }
                    }
                }
                for (int i = 0; i < randomsource.nextInt(5); i++) {
                    BlockPos offset = blockpos.offset(randomsource.nextInt(2) - 1, 2, randomsource.nextInt(2) - 1);
                    int length = 0;
                    int maxLength = 2 + randomsource.nextInt(4);
                    while (length <= maxLength) {
                        offset = offset.below();
                        if (!worldgenlevel.getBlockState(offset).is(ACBlockRegistry.FROSTMINT.get())) {
                            worldgenlevel.setBlock(offset, ACBlockRegistry.FROSTMINT.get().defaultBlockState().setValue(FrostmintBlock.TYPE, length == maxLength && randomsource.nextBoolean() ? SlabType.TOP : SlabType.DOUBLE), 2);
                        }
                        length++;
                    }
                }

            }
            return true;
        } else {
            return false;
        }
    }

}