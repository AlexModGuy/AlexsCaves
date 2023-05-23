package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GloomothEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.HullbreakerEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotSacrifice;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class UnderzealotCaptureSacrificeGoal extends Goal {
    private UnderzealotEntity entity;
    private LivingEntity sacrifice;

    public UnderzealotCaptureSacrificeGoal(UnderzealotEntity underzealot) {
        this.entity = underzealot;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        long worldTime = entity.level.getGameTime() % 10;
        if(entity.isCarrying() || entity.isPackFollower()){
            return false;
        }
        if (entity.getRandom().nextInt(30) != 0 && worldTime != 0 && (target == null || !target.isAlive())) {
            return false;
        }
        AABB aabb = entity.getBoundingBox().inflate(20);
        List<LivingEntity> list = entity.level.getEntitiesOfClass(LivingEntity.class, aabb, this::isValidSacrifice);
        if (!list.isEmpty()) {
            LivingEntity closest = null;
            for (LivingEntity mob : list) {
                if ((closest == null || mob.distanceToSqr(entity) < closest.distanceToSqr(entity)) && entity.hasLineOfSight(mob)) {
                    closest = mob;
                }
            }
            sacrifice = closest;
            return sacrifice != null;
        }
        return false;
    }

    private boolean isValidSacrifice(LivingEntity entity) {
        return entity instanceof UnderzealotSacrifice && !entity.isPassenger() && getDistanceToGround(entity) <= 3;
    }

    private int getDistanceToGround(LivingEntity entity) {
        int downBy = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
        while(entity.level.isEmptyBlock(pos) && pos.getY() > entity.level.getMinBuildHeight()){
            pos.move(0, -1, 0);
            downBy++;
        }
        return downBy;
    }

    @Override
    public boolean canContinueToUse() {
        return sacrifice != null && sacrifice.isAlive() && !entity.isPackFollower() && isValidSacrifice(sacrifice) && entity.distanceTo(sacrifice) < 32;
    }

    public void stop(){
        this.entity.getNavigation().stop();
    }

    public void tick() {
        double distance = entity.distanceTo(sacrifice);
        entity.getNavigation().moveTo(sacrifice, 1.0F);
        if(distance < 1.4F){
            sacrifice.startRiding(entity);
        }
    }
}
