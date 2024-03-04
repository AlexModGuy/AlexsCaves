package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class VerticalSwimmingMoveControl extends MoveControl {

    private final Mob mob;
    private float secondSpeedModifier;
    private float maxRotChange;

    public VerticalSwimmingMoveControl(Mob mob, float secondSpeedModifier, float maxRotChange) {
        super(mob);
        this.mob = mob;
        this.secondSpeedModifier = secondSpeedModifier;
        this.maxRotChange = maxRotChange;
    }

    public void tick() {
        if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            //Vec3 ed = this.mob.getNavigation().getTargetPos().getCenter();
            //((ServerLevel)mob.level()).sendParticles(ParticleTypes.HEART, ed.x, ed.y, ed.z, 0, 0, 0, 0, 1);
            //((ServerLevel)mob.level()).sendParticles(ParticleTypes.SNEEZE, wantedX, wantedY, wantedZ, 0, 0, 0, 0, 1);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
            double d4 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            d1 /= d3;
            this.mob.yBodyRot = this.mob.getYRot();
            float f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * secondSpeedModifier);
            float rotBy = this.maxRotChange;
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, (double) f1 * d1 * 0.4D, 0.0D));
            if (d4 < this.mob.getBbWidth() + 1.4F) {
                f1 *= 0.7F;
                if (d4 < 0.3F) {
                    rotBy = 0;
                } else {
                    rotBy = Math.max(40, this.maxRotChange);
                }
            }
            float f = (float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, rotBy));
            if (d3 > 0.3) {
                this.mob.setSpeed(f1);
            } else {
                this.mob.setSpeed(0.0F);
            }
        } else {
            this.mob.setSpeed(0.0F);
            if (mob instanceof DeepOneBaseEntity deepOne) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, -0.1D, 0.0D));
            }
        }
    }
}
