//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class TremorzillaLegSolver {
    public final Leg backLeft;
    public final Leg backRight;

    public final Leg[] legs;

    public TremorzillaLegSolver(float forward, float side, float range) {
        this.legs = (new Leg[]{new Leg(forward, side, range, false), new Leg(forward, -side, range, false)});
        this.backLeft = this.legs[0];
        this.backRight = this.legs[1];
    }


    public final void update(LivingEntity entity, float scale) {
        this.update(entity, entity.yBodyRot, scale);
    }

    public final void update(LivingEntity entity, float yaw, float scale) {
        double sideTheta = (double)yaw / 57.29577951308232;
        double sideX = Math.cos(sideTheta) * (double)scale;
        double sideZ = Math.sin(sideTheta) * (double)scale;
        double forwardTheta = sideTheta + 1.5707963267948966;
        double forwardX = Math.cos(forwardTheta) * (double)scale;
        double forwardZ = Math.sin(forwardTheta) * (double)scale;
        Leg[] var16 = this.legs;
        int var17 = var16.length;

        for(int var18 = 0; var18 < var17; ++var18) {
            Leg leg = var16[var18];
            leg.update(entity, sideX, sideZ, forwardX, forwardZ, scale);
        }

    }

    public static final class Leg {
        public final float forward;
        public final float side;
        public Vec3 samplePos = Vec3.ZERO;
        private final float range;
        private float height;
        private float prevHeight;
        private boolean isWing;

        public Leg(float forward, float side, float range, boolean isWing) {
            this.forward = forward;
            this.side = side;
            this.range = range;
            this.isWing = isWing;
        }

        public float getHeight(float delta) {
            return this.prevHeight + (this.height - this.prevHeight) * delta;
        }

        public void update(LivingEntity entity, double sideX, double sideZ, double forwardX, double forwardZ, float scale) {
            this.prevHeight = this.height;
            double posY = entity.getY();
            samplePos = new Vec3(entity.getX() + sideX * (double)this.side + forwardX * (double)this.forward, posY, entity.getZ() + sideZ * (double)this.side + forwardZ * (double)this.forward);
            float settledHeight = this.settle(entity, entity.getX() + sideX * (double)this.side + forwardX * (double)this.forward, posY - 1, entity.getZ() + sideZ * (double)this.side + forwardZ * (double)this.forward, this.height);
            this.height = Mth.clamp(settledHeight, -this.range * scale, this.range * scale);
        }

        protected float settle(LivingEntity entity, double x, double y, double z, float height) {
            BlockPos pos = new BlockPos((int)Math.floor(x), (int)Math.floor(y + 0.001), (int)Math.floor(z));
            Vec3 vec3 = new Vec3(x, y, z);
            float dist = this.getDistance(entity.level(), pos, vec3);
            float lastDistance = dist;
            while(lastDistance == 1.0F){
                pos = pos.below();
                lastDistance = this.getDistance(entity.level(), pos, vec3);
                dist += lastDistance;
            }
            if (entity.onGround() && height <= dist) {
                return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
            } else if (height > 0.0F) {
                return height == dist ? height : Math.max(height - this.getRiseSpeed(), dist);
            } else {
                return height;
            }
        }

        protected float getDistance(Level world, BlockPos pos, Vec3 position) {
            BlockState state = world.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(world, pos);
            if(pos.getY() < world.getMinBuildHeight()){
                return 0.0F;
            }else if (shape.isEmpty()) {
                return 1.0F;
            } else {
                Vec3 modIn = new Vec3(position.x % 1.0D, position.y, position.z % 1.0D);
                Optional<Vec3> closest = shape.closestPointTo(modIn);
                if (closest.isEmpty()) {
                    return 1.0F;
                } else {
                    float closestY = Math.min((float)((Vec3)closest.get()).y, 1.0F);
                    return position.y < 0.0 ? closestY : 1.0F - closestY;
                }
            }
        }

        protected float getFallSpeed() {
            return 0.15F;
        }

        protected float getRiseSpeed() {
            return 0.15F;
        }
    }
}
