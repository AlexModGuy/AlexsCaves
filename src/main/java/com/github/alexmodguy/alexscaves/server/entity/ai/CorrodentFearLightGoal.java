package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CorrodentFearLightGoal extends Goal {
    private CorrodentEntity entity;
    private Vec3 retreatTo = null;
    private int tryDigTime = 0;
    private BlockPos tryDigPos = null;

    public CorrodentFearLightGoal(CorrodentEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.entity.level().getBrightness(LightLayer.BLOCK, this.entity.blockPosition()) > CorrodentEntity.LIGHT_THRESHOLD && !this.entity.isDigging();
    }


    @Override
    public void tick() {
        this.entity.fleeLightFor = 50;
        int light = this.entity.level().getBrightness(LightLayer.BLOCK, this.entity.blockPosition());
        if (retreatTo == null || this.entity.distanceToSqr(retreatTo) < 6) {
            for (int i = 0; i < 15; i++) {
                Vec3 vec3 = DefaultRandomPos.getPosAway(this.entity, 30, 15, this.entity.position());
                if (vec3 != null && this.entity.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(vec3)) < CorrodentEntity.LIGHT_THRESHOLD) {
                    retreatTo = vec3;
                    break;
                }
            }
        } else {
            entity.setAfraid(true);
            entity.getNavigation().stop();
            Vec3 flip = retreatTo.subtract(entity.position()).yRot((float) (Math.PI * 0.5F)).add(entity.position());
            entity.lookAt(EntityAnchorArgument.Anchor.EYES, flip);
            entity.getMoveControl().strafe(-1F, 0F);
            if (entity.onGround() && tryDigTime++ > 20) {
                tryDigTime = 0;
                if (tryDigPos != null && tryDigPos.distSqr(entity.blockPosition()) < 2.25F) {
                    entity.setDigging(true);
                }
                tryDigPos = entity.blockPosition();
            }
        }
    }


    @Override
    public void stop() {
        this.entity.setAfraid(false);
        if (entity.onGround()) {
            this.entity.fleeLightFor = 50;
            this.entity.setDigging(true);
        }
        entity.getMoveControl().strafe(0F, 0F);
        tryDigPos = null;
        tryDigTime = 0;
    }
}
