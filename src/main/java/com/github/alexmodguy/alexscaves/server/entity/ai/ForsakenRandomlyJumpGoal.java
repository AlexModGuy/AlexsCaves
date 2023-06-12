package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.ForsakenEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.EnumSet;

public class ForsakenRandomlyJumpGoal extends Goal {

    private ForsakenEntity entity;
    private BlockPos jumpTarget = null;
    private boolean hasPreformedJump = false;

    public ForsakenRandomlyJumpGoal(ForsakenEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        if (entity.onGround() && (target == null || !target.isAlive()) && entity.getRandom().nextInt(140) == 0 && entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            BlockPos findTarget = findJumpTarget();
            if (findTarget != null) {
                jumpTarget = findTarget;
                return true;
            }
        }
        return false;
    }

    private BlockPos findJumpTarget() {
        Vec3 vec3 = DefaultRandomPos.getPos(this.entity, 25, 10);
        if (vec3 != null) {
            BlockPos blockpos = BlockPos.containing(vec3);
            AABB aabb = this.entity.getBoundingBox().move(vec3.add(0.5F, 1, 0.5F).subtract(this.entity.position()));
            if (entity.level().getBlockState(blockpos.below()).isSolidRender(entity.level(), blockpos.below()) && entity.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(entity.level(), blockpos.mutable())) == 0.0F && entity.level().isUnobstructed(this.entity, Shapes.create(aabb))) {
                return blockpos;
            }
        }
        return null;
    }

    @Override
    public void start() {
        hasPreformedJump = false;
        entity.getNavigation().stop();
        if (entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.entity.setAnimation(ForsakenEntity.ANIMATION_PREPARE_JUMP);
        }
        entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(jumpTarget));
    }

    @Override
    public boolean canContinueToUse() {
        return (entity.getAnimation() == ForsakenEntity.ANIMATION_PREPARE_JUMP || entity.isLeaping()) && jumpTarget != null;
    }

    @Override
    public void tick() {
        if (entity.isLeaping()) {
            if(!hasPreformedJump){
                hasPreformedJump = true;
                Vec3 vec3 = this.entity.getDeltaMovement();
                Vec3 vec31 = new Vec3(this.jumpTarget.getX() + 0.5F - this.entity.getX(), 0.0D, this.jumpTarget.getZ() + 0.5F - this.entity.getZ());
                if(vec31.length() > 100){
                    vec31 = vec3.normalize().scale(100);
                }
                if (vec31.lengthSqr() > 1.0E-7D) {
                    vec31 = vec31.scale(0.155F).add(vec3.scale(0.2D));
                }
                this.entity.setDeltaMovement(vec31.x, 0.2F + (double)vec31.length() * 0.3F, vec31.z);


            }
        }
    }
}
