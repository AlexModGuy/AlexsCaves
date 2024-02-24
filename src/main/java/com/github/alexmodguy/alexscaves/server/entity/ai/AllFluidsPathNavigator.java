package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class AllFluidsPathNavigator extends SemiAquaticPathNavigatorNoSpin {
    public AllFluidsPathNavigator(Mob mob, Level worldIn) {
        super(mob, worldIn);
    }

    protected PathFinder createPathFinder(int j) {
        this.nodeEvaluator = new AllFluidsNodeEvaluator(true);
        return new PathFinder(this.nodeEvaluator, j);
    }

    protected boolean canUpdatePath() {
        return this.isInLiquid();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.isInFluidType() ? super.getTempMobPos() :  new Vec3(this.mob.getX(), Math.floor(this.mob.getY() + 0.5D), this.mob.getZ());
    }


    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos.below()).isAir();
    }

    public void setCanFloat(boolean canSwim) {
    }

    protected boolean isInLiquid() {
        return this.mob.isInFluidType();
    }
}
