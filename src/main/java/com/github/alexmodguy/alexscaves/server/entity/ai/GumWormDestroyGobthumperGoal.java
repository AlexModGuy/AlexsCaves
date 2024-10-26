package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormDestroyGobthumperGoal extends Goal {

    private final GumWormEntity entity;
    private int continueLeapFor = 0;

    public GumWormDestroyGobthumperGoal(GumWormEntity worm) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        BlockPos gobthumperPos = entity.getGobthumperPos();
        return gobthumperPos != null && entity.level().getBlockState(gobthumperPos).is(ACBlockRegistry.GOBTHUMPER.get()) && entity.distanceToSqr(gobthumperPos.getX() + 0.5F, gobthumperPos.getY() + 0.5F, gobthumperPos.getZ() + 0.5F) < 50000 && !entity.isRidingMode();
    }

    @Override
    public boolean canContinueToUse() {
        return (super.canContinueToUse() || continueLeapFor > 0) && !entity.isRidingMode();
    }

    @Override
    public void start() {
        continueLeapFor = 40;
    }

    @Override
    public void stop() {
        entity.setBiting(false);
        entity.setLeaping(false);
    }

    public void tick() {
        BlockPos gobthumperPos = entity.getGobthumperPos();
        if(gobthumperPos != null){
            BlockPos leapFromPos = getUnderGobthumperPos(gobthumperPos, entity.getY());
            double distance = entity.distanceToSqr(gobthumperPos.getX() + 0.5F, gobthumperPos.getY() + 0.5F, gobthumperPos.getZ() + 0.5F);
            double distance2 = entity.distanceToSqr(leapFromPos.getX() + 0.5F, leapFromPos.getY() + 0.5F, leapFromPos.getZ() + 0.5F);
            if(entity.isLeaping()){
                entity.getNavigation().stop();
                if(gobthumperPos.getY() + 2 > entity.getY() && entity.isInWall()){
                    Vec3 extraDeltaHelp = Vec3.atCenterOf(gobthumperPos).subtract(entity.position());
                    if(extraDeltaHelp.length() > 1.0F){
                        extraDeltaHelp = extraDeltaHelp.normalize();
                    }
                    entity.setDeltaMovement(entity.getDeltaMovement().add(extraDeltaHelp.x * 0.2F, 0.4F, extraDeltaHelp.z * 0.2F));
                }
            }else if(Math.min(distance2, distance) < 8.0F){
                entity.getNavigation().stop();
                entity.setLeaping(true);
            }else if(!entity.isLeaping()){
                if(entity.level().getBlockState(this.entity.blockPosition()).isAir()){
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3F, 0));
                }
                entity.getNavigation().moveTo(leapFromPos.getX() + 0.5F, leapFromPos.getY() + 0.5F, leapFromPos.getZ() + 0.5F, 1.0D);
            }
            if(entity.isLeaping() && distance < 20.0F){
                entity.attemptPlayAttackNoise();
                Vec3 leapDelta = new Vec3(0, -0.2F, 1).yRot((float) -Math.toRadians(entity.yBodyRot));
                entity.level().destroyBlock(gobthumperPos, false);
                entity.setGobthumperPos(null);
                entity.setDeltaMovement(entity.getDeltaMovement().add(leapDelta));
            }
            if(distance < 40.0F){
                entity.setBiting(true);
            }
            if(distance < 200.0F){
                Vec3 wiggleExtraVec = gobthumperPos.getCenter().subtract(entity.position()).normalize().scale(0.1F);
                entity.setDeltaMovement(entity.getDeltaMovement().add(wiggleExtraVec));
            }
        }else if(continueLeapFor > 0 && entity.isLeaping()){
            continueLeapFor--;
            Vec3 leapDelta = new Vec3(0,  continueLeapFor < 24 ? -0.3F : 0.1F, 0.2F).yRot((float) -Math.toRadians(entity.yBodyRot));
            entity.setDeltaMovement(entity.getDeltaMovement().add(leapDelta));
        }
    }

    private BlockPos getUnderGobthumperPos(BlockPos gobthumperPos, double y) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(gobthumperPos.getX(), Math.min(gobthumperPos.getY() - 1, y), gobthumperPos.getZ());
        while(entity.level().getBlockState(mutableBlockPos).isAir() && mutableBlockPos.getY() < y || mutableBlockPos.getY() < y - 10){
            mutableBlockPos.move(0, 1, 0);
        }
        return mutableBlockPos;
    }
}
