package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

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
        if (this.operation == MoveControl.Operation.MOVE_TO && mob.isInWaterOrBubble()) {
            final Vec3 vector3d = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
            final double d5 = vector3d.length();
            double maxDist = mob.getBoundingBox().getSize() > 1.0F ? 1.0F : mob.getBoundingBox().getSize();
            if (d5 < maxDist) {
                this.operation = MoveControl.Operation.WAIT;
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.85D));
            } else {
                mob.setDeltaMovement(mob.getDeltaMovement().add(vector3d.scale(this.speedModifier * secondSpeedModifier / d5)));
                final Vec3 vector3d1 = mob.getDeltaMovement();
                float f = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * 180.0F / (float) Math.PI;
                mob.setYRot(Mth.approachDegrees(mob.getYRot(), f, this.maxRotChange));
                mob.yBodyRot = mob.getYRot();
            }
        }
    }
}
