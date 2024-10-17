package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingTreeBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GummyBearEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.RelicheirusEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GummyBearBackScratchGoal  extends MoveToBlockGoal {

    private static final int MAXIMUM_BLOCKS_PUSHED = 300;
    public static final int MAX_TREE_SPREAD = 12;
    private GummyBearEntity gummyBear;
    private int readjustTime;

    public GummyBearBackScratchGoal(GummyBearEntity relicheirus, int range) {
        super(relicheirus, 1.0F, range, 6);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.gummyBear = relicheirus;
    }

    public boolean canUse() {
        return !gummyBear.isBaby() && !gummyBear.isSleepy() && !gummyBear.isBearSleeping() && gummyBear.isDigesting() && super.canUse();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && readjustTime < 10;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(20 + gummyBear.getRandom().nextInt(20));
    }

    public double acceptedDistance() {
        return 4.0D;
    }

    @Override
    protected boolean isReachedTarget() {
        BlockPos target = getMoveToTarget();
        return target != null && gummyBear.distanceToSqr(target.getX() + 0.5F, gummyBear.getY(), target.getZ() + 0.5F) < acceptedDistance();
    }

    protected BlockPos getMoveToTarget() {
        return gummyBear.getStandAtTreePos(blockPos);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        if (target != null) {
            if (isReachedTarget()) {
                if (gummyBear.lockTreePosition(blockPos)) {
                    readjustTime++;
                    if (gummyBear.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        gummyBear.setAnimation(GummyBearEntity.ANIMATION_BACKSCRATCH);
                    }
                }
            } else {
                if (gummyBear.getNavigation().isDone()) {
                    Vec3 vec31 = Vec3.atCenterOf(target);
                    Vec3 vec32 = vec31.subtract(gummyBear.position());
                    if (vec32.length() > 1) {
                        vec32 = vec32.normalize();
                    }
                    Vec3 delta = new Vec3(vec32.x * 0.1F, 0F, vec32.z * 0.1F);
                    gummyBear.setDeltaMovement(gummyBear.getDeltaMovement().add(delta));
                }
            }
        }
    }

    protected void moveMobToBlock() {
        BlockPos pos = getMoveToTarget();
        this.mob.getNavigation().moveTo((double) ((float) pos.getX()) + 0.5D, (double) (pos.getY()), (double) ((float) pos.getZ()) + 0.5D, this.speedModifier);
    }


    public void stop() {
        this.blockPos = BlockPos.ZERO;
        super.stop();
        this.readjustTime = 0;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).is(ACBlockRegistry.LICOROOT.get())) {
            for(Direction direction : ACMath.HORIZONTAL_DIRECTIONS){
                BlockState neighbor = worldIn.getBlockState(pos.relative(direction));
                if(neighbor.isAir() || neighbor.canBeReplaced()){
                    BlockState neighborBeneath = worldIn.getBlockState(pos.relative(direction).below());
                    return !neighborBeneath.isAir() && neighborBeneath.isSolid();
                }
            }
        }
        return false;
    }

}
