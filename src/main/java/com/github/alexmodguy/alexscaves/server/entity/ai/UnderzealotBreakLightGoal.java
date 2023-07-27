package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class UnderzealotBreakLightGoal extends MoveToBlockGoal {
    private final UnderzealotEntity underzealot;

    public UnderzealotBreakLightGoal(UnderzealotEntity entity, int range) {
        super(entity, 1.0F, range, range);
        this.underzealot = entity;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(500 + underzealot.getRandom().nextInt(1000));
    }


    @Override
    public boolean canUse() {
        return super.canUse() && !isTargetBlocked(blockPos.getCenter());
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(mob.getX(), mob.getEyeY(), mob.getZ());
        return mob.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() != HitResult.Type.MISS;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.underzealot.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.underzealot.isPackFollower() && !this.underzealot.isCarrying();
    }

    public double acceptedDistance() {
        return 2;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        if (target != null) {
            underzealot.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(target));
            if (this.isReachedTarget() && underzealot.level().getBlockState(target).is(ACTagRegistry.UNDERZEALOT_LIGHT_SOURCES)) {
                if (underzealot.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    underzealot.setAnimation(UnderzealotEntity.ANIMATION_BREAKTORCH);
                } else if (underzealot.getAnimation() == UnderzealotEntity.ANIMATION_BREAKTORCH && underzealot.getAnimationTick() == 10) {
                    underzealot.level().destroyBlock(target, true);
                }
            }
        }
    }


    protected BlockPos getMoveToTarget() {
        return this.blockPos;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return pos != null && worldIn.getBlockState(pos).is(ACTagRegistry.UNDERZEALOT_LIGHT_SOURCES) && worldIn.getLightEmission(pos) > 0;
    }
}
