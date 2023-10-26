package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NuclearSirenFeature extends Feature<NoneFeatureConfiguration> {

    public NuclearSirenFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos below = context.origin();
        if (!level.getBlockState(below.below()).isSolid()) {
            return false;
        }
        BlockPos.MutableBlockPos pillar = new BlockPos.MutableBlockPos();
        pillar.set(below);
        int concrete = 0;
        while(concrete < 1 || !level.getFluidState(pillar).isEmpty()){
            concrete++;
            level.setBlock(pillar, ACBlockRegistry.CINDER_BLOCK.get().defaultBlockState(), 3);
            pillar.move(0, 1, 0);
        }
        level.setBlock(pillar, ACBlockRegistry.CINDER_BLOCK_WALL.get().defaultBlockState(), 3);
        pillar.move(0, 1, 0);
        for (int i = 0; i < 1 + randomsource.nextInt(2); i++) {
            level.setBlock(pillar, Blocks.DARK_OAK_FENCE.defaultBlockState(), 3);
            pillar.move(0, 1, 0);
        }
        level.setBlock(pillar, ACBlockRegistry.NUCLEAR_SIREN.get().defaultBlockState(), 3);
        return true;
    }
}
