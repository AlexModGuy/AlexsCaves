package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class DirectAquaticMoveControl extends MoveControl {
    private final PathfinderMob entity;
    private final float speedMulti;
    private float yawLimit = 3.0F;

    public DirectAquaticMoveControl(PathfinderMob entity, float speedMulti) {
        super(entity);
        this.entity = entity;
        this.speedMulti = speedMulti;
    }

    public DirectAquaticMoveControl(PathfinderMob entity, float speedMulti, float yawLimit) {
        super(entity);
        this.entity = entity;
        this.yawLimit = yawLimit;
        this.speedMulti = speedMulti;
    }

    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            final Vec3 vector3d = new Vec3(this.wantedX - this.entity.getX(), this.wantedY - this.entity.getY(), this.wantedZ - this.entity.getZ());
            final double d5 = vector3d.length();
            if (d5 < 1.0F) {
                this.operation = MoveControl.Operation.WAIT;
                this.entity.setDeltaMovement(this.entity.getDeltaMovement().scale(0.5D));
            } else {
                //Vec3 ed = this.mob.getNavigation().getTargetPos().getCenter();
                //((ServerLevel)mob.level()).sendParticles(ParticleTypes.HEART, ed.x, ed.y, ed.z, 0, 0, 0, 0, 1);
                //((ServerLevel)mob.level()).sendParticles(ParticleTypes.SNEEZE, wantedX, wantedY, wantedZ, 0, 0, 0, 0, 1);
                this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.1F / d5)));
                final Vec3 delta = this.entity.getDeltaMovement();
                float f = -((float) Mth.atan2(delta.x, delta.z)) * 180.0F / (float) Math.PI;
                this.entity.setYRot(Mth.approachDegrees(this.entity.getYRot(), f, this.yawLimit));
                this.entity.yBodyRot = this.entity.getYRot();
            }
        }
    }
}
