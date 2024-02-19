package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.AtlatitanEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AtlatitanNibbleTreesGoal extends MoveToBlockGoal {

    private AtlatitanEntity atlatitan;
    private boolean stopFlag = false;

    private int reachCheckTime = 50;

    public AtlatitanNibbleTreesGoal(AtlatitanEntity atlatitan, int range) {
        super(atlatitan, 1.0F, range, 16);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.atlatitan = atlatitan;
    }

    public boolean canUse() {
        this.verticalSearchStart = this.atlatitan.isBaby() ? 3 : 6;
        return super.canUse();
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(200 + atlatitan.getRandom().nextInt(200));
    }

    public double acceptedDistance() {
        return (int) Math.floor(14 * atlatitan.getScale());
    }

    @Override
    protected boolean isReachedTarget() {
        BlockPos target = getMoveToTarget();
        return target != null && atlatitan.distanceToSqr(target.getX() + 0.5F, atlatitan.getY(), target.getZ() + 0.5F) < acceptedDistance();
    }

    protected BlockPos getMoveToTarget() {
        return atlatitan.getStandAtTreePos(blockPos);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        if (target != null) {
            if(reachCheckTime > 0){
                reachCheckTime--;
            }else{
                reachCheckTime = 50 + atlatitan.getRandom().nextInt(100);
                if(!canReach(target)){
                    stopFlag = true;
                    this.blockPos = BlockPos.ZERO;
                    return;
                }
            }
            if (isReachedTarget()) {
                if (atlatitan.lockTreePosition(blockPos)) {
                    if (atlatitan.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        atlatitan.setEatingPos(blockPos);
                        atlatitan.setAnimation(AtlatitanEntity.ANIMATION_EAT_LEAVES);
                    } else if (atlatitan.getAnimation() == AtlatitanEntity.ANIMATION_EAT_LEAVES) {
                        if (atlatitan.getAnimationTick() >= 35) {
                            stopFlag = true;
                            this.blockPos = BlockPos.ZERO;
                            return;
                        } else if (atlatitan.getAnimationTick() == 20) {
                            BlockState back = atlatitan.level().getBlockState(blockPos);
                            atlatitan.setLastEatenBlock(back);
                            atlatitan.level().destroyBlock(blockPos, false, atlatitan);
                            atlatitan.level().setBlock(blockPos, back, 3);
                        }

                    }
                }
            } else {
                if (atlatitan.getNavigation().isDone()) {
                    Vec3 vec31 = Vec3.atCenterOf(target);
                    this.atlatitan.getMoveControl().setWantedPosition(vec31.x, this.atlatitan.getY(), vec31.z, 1.0D);
                }
            }
        }
    }

    protected void moveMobToBlock() {
        BlockPos pos = getMoveToTarget();
        this.mob.getNavigation().moveTo((double) ((float) pos.getX()) + 0.5D, (double) (pos.getY()), (double) ((float) pos.getZ()) + 0.5D, this.speedModifier);
    }


    public boolean canContinueToUse() {
        return super.canContinueToUse() && !stopFlag;
    }

    public void stop() {
        this.blockPos = BlockPos.ZERO;
        super.stop();
        stopFlag = false;
    }

    private int getHeightOfBlock(LevelReader worldIn, BlockPos pos) {
        int i = 0;
        while (pos.getY() > worldIn.getMinBuildHeight() && (worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_NIBBLES) || worldIn.getBlockState(pos).isAir() || worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS))) {
            pos = pos.below();
            i++;
        }
        return i;
    }

    private boolean highEnough(LevelReader worldIn, BlockPos pos) {
        int height = getHeightOfBlock(worldIn, pos);
        if (atlatitan.isBaby()) {
            return height <= 2;
        }
        return height > 3 && height < 20;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_NIBBLES) && highEnough(worldIn, pos);
    }

    private boolean canReach(BlockPos target) {
        Path path = atlatitan.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - target.getX();
                int j = node.y - target.getY();
                int k = node.z - target.getZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

}
