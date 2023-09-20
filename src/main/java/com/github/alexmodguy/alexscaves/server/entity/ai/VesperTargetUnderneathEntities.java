package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VesperEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

public class VesperTargetUnderneathEntities<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    private VesperEntity entity;

    public VesperTargetUnderneathEntities(VesperEntity mob, Class<T> clzz) {
        super(mob, clzz, 10, true, true, null);
        this.entity = mob;
    }

    @Override
    protected AABB getTargetSearchArea(double distance) {
        if (entity.isHanging()) {
            AABB aabb = this.entity.getBoundingBox();
            double newDistance = 2.0F;
            return new AABB(aabb.minX - newDistance, entity.level().getMinBuildHeight() - 5, aabb.minZ - newDistance, aabb.maxX + newDistance, aabb.maxY + 1, aabb.maxZ + newDistance);
        } else {
            double newDistance = 32.0F;
            return this.entity.getBoundingBox().inflate(newDistance, newDistance, newDistance);
        }
    }
}