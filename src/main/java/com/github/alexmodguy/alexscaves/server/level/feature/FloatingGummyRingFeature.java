package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.FrostmintBlock;
import com.github.alexmodguy.alexscaves.server.block.GummyRingBlock;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FloatingGummyRingFeature extends Feature<NoneFeatureConfiguration> {

    public FloatingGummyRingFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();
        boolean aboveSoda = false;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        for (scanPos.set(context.origin()); scanPos.getY() > worldgenlevel.getMinBuildHeight(); scanPos.move(0, -1, 0)) {
            if (worldgenlevel.getFluidState(scanPos).getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()) {
                aboveSoda = true;
                break;
            }
        }
        if (aboveSoda) {
            boolean sunk = randomsource.nextBoolean();
            if(sunk){
                while(scanPos.getY() > worldgenlevel.getMinBuildHeight() && worldgenlevel.getFluidState(scanPos).getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()){
                    scanPos.move(0, -1, 0);
                }
            }
            scanPos.move(0, 1, 0);
            if(worldgenlevel.getBlockState(scanPos).canBeReplaced()){
                BlockState state;
                switch (randomsource.nextInt(4)){
                    case 4:
                        state = ACBlockRegistry.GUMMY_RING_PINK.get().defaultBlockState();
                        break;
                    case 3:
                        state = ACBlockRegistry.GUMMY_RING_BLUE.get().defaultBlockState();
                        break;
                    case 2:
                        state = ACBlockRegistry.GUMMY_RING_YELLOW.get().defaultBlockState();
                        break;
                    case 1:
                        state = ACBlockRegistry.GUMMY_RING_GREEN.get().defaultBlockState();
                        break;
                    default:
                        state = ACBlockRegistry.GUMMY_RING_RED.get().defaultBlockState();
                        break;
                }
                if(sunk){
                    state = state.setValue(GummyRingBlock.LIQUID_LOGGED, 2);
                }else{
                    state = state.setValue(GummyRingBlock.FLOATING, true);
                }
                worldgenlevel.setBlock(scanPos, state, 3);
                return true;
            }
        }
        return false;
    }

}