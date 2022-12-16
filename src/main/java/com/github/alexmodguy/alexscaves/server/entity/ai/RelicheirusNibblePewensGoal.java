package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.RelicheirusEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RelicheirusNibblePewensGoal extends MoveToBlockGoal {

    private RelicheirusEntity relicheirus;
    private boolean stopFlag = false;

    public RelicheirusNibblePewensGoal(RelicheirusEntity relicheirus, int range) {
        super(relicheirus, 1.0F, range, 6);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.relicheirus = relicheirus;
    }


    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(80 + relicheirus.getRandom().nextInt(200));
    }

    public double acceptedDistance() {
        return 4.0D;
    }

    @Override
    protected boolean isReachedTarget() {
        BlockPos target = getMoveToTarget();
        return target != null && relicheirus.distanceToSqr(target.getX() + 0.5F, relicheirus.getY(), target.getZ() + 0.5F) < acceptedDistance();
    }

    protected BlockPos getMoveToTarget() {
        return relicheirus.getStandAtTreePos(blockPos);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        //relicheirus.level.setBlock(blockPos.above(), Blocks.GLASS.defaultBlockState(), 3);
        //relicheirus.level.setBlock(target.below(), Blocks.BLUE_STAINED_GLASS.defaultBlockState(), 3);
        if (target != null) {
            if (isReachedTarget()) {
                if (relicheirus.lockTreePosition(blockPos)) {
                    if (relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        relicheirus.setPeckY(blockPos.getY());
                        relicheirus.setAnimation(RelicheirusEntity.ANIMATION_EAT_TREE);
                    } else if (relicheirus.getAnimation() == RelicheirusEntity.ANIMATION_EAT_TREE) {
                        if (relicheirus.getAnimationTick() >= 30) {
                            stopFlag = true;
                            this.blockPos = BlockPos.ZERO;
                            return;
                        } else if (relicheirus.getAnimationTick() % 8 == 0) {
                            BlockState back = relicheirus.level.getBlockState(blockPos);
                            relicheirus.level.destroyBlock(blockPos, false, relicheirus);
                            relicheirus.level.setBlock(blockPos, back, 3);
                        }

                    }
                }
            } else {
                if(relicheirus.getNavigation().isDone()){
                    Vec3 vec31 = Vec3.atCenterOf(target);
                    Vec3 vec32 = vec31.subtract(relicheirus.position());
                    if(vec32.length() > 1){
                        vec32 = vec32.normalize();
                    }
                    Vec3 delta = new Vec3(vec32.x * 0.1F, 0F, vec32.z * 0.1F);
                    relicheirus.setDeltaMovement(relicheirus.getDeltaMovement().add(delta));
                }
            }
        }
    }

    protected void moveMobToBlock() {
        BlockPos pos = getMoveToTarget();
        this.mob.getNavigation().moveTo((double) ((float) pos.getX()) + 0.5D, (double) (pos.getY() ), (double) ((float) pos.getZ()) + 0.5D, this.speedModifier);
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
        if (relicheirus.isBaby()){
            return height <= 1;
        }
        return height > 3 && height < 7;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_NIBBLES) && highEnough(worldIn, pos);
    }
}
