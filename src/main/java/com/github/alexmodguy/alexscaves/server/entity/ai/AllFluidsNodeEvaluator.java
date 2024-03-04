package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

public class AllFluidsNodeEvaluator extends SwimNodeEvaluator {

    public AllFluidsNodeEvaluator(boolean breaching) {
        super(breaching);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter getter, int x, int y, int z, Mob mob) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i = x; i < x + entityWidth; ++i) {
            for (int j = y; j < y + entityHeight; ++j) {
                for (int k = z; k < z + entityDepth; ++k) {
                    FluidState fluidstate = getter.getFluidState(blockpos$mutableblockpos.set(i, j, k));
                    BlockState blockstate = getter.getBlockState(blockpos$mutableblockpos.set(i, j, k));

                    if (fluidstate.isEmpty() && !blockstate.isAir()) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
        }
        BlockState blockstate1 = getter.getBlockState(blockpos$mutableblockpos);
        return blockstate1.isAir() ? BlockPathTypes.BREACH : !blockstate1.getFluidState().isEmpty() ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
    }
}
