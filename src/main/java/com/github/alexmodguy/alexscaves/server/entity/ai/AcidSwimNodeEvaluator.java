package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

public class AcidSwimNodeEvaluator extends SwimNodeEvaluator {

    public AcidSwimNodeEvaluator(boolean breach) {
        super(breach);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter getter, int x, int y, int z, Mob p_77476_, int xSize, int ySize, int zSize, boolean unused1, boolean unused2) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int i = x; i < x + xSize; ++i) {
            for(int j = y; j < y + ySize; ++j) {
                for(int k = z; k < z + zSize; ++k) {
                    FluidState fluidstate = getter.getFluidState(blockpos$mutableblockpos.set(i, j, k));
                    BlockState blockstate = getter.getBlockState(blockpos$mutableblockpos.set(i, j, k));
                    if (fluidstate.isEmpty() && blockstate.isPathfindable(getter, blockpos$mutableblockpos.below(), PathComputationType.WATER) && blockstate.isAir()) {
                        return BlockPathTypes.BREACH;
                    }

                    //works in water and acid, not lava
                    if (fluidstate.is(FluidTags.LAVA)) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
        }

        BlockState blockstate1 = getter.getBlockState(blockpos$mutableblockpos);
        return blockstate1.isPathfindable(getter, blockpos$mutableblockpos, PathComputationType.WATER) ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
    }
}
