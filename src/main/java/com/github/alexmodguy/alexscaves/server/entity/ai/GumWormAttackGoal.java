package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormAttackGoal extends Goal {

    private final GumWormEntity entity;
    private boolean leapingAttack;

    private int leapTicks;
    private int maxLeapTicks;
    private float leapHeight;

    public GumWormAttackGoal(GumWormEntity worm) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        return target != null && target.isAlive() && entity.timeBetweenAttacks <= 0 && !entity.isRidingMode();
    }

    @Override
    public void stop() {
        entity.setBiting(false);
        entity.setLeaping(false);
        leapTicks = 0;
    }

    @Override
    public void start() {
        leapTicks = 0;
        leapHeight = 0.2F + entity.getRandom().nextFloat() * 0.2F;
    }

    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target != null) {
            double dist = entity.distanceTo(target);
            if (dist > 13 && !leapingAttack && entity.leapAttackCooldown >= 0 && (entity.isInWall() || entity.onGround()) && leapTicks == 0) {
                leapingAttack = true;
                maxLeapTicks = 15 + entity.getRandom().nextInt(8);
                leapTicks = 0;
            }
            if (leapingAttack) {
                entity.getNavigation().stop();
                if ((entity.isInWall()) && !entity.isLeaping()) {
                    entity.setLeaping(true);
                    entity.leapAttackCooldown = 330;
                }
                if (entity.isLeaping()) {
                    Vec3 leapOnPos = target.position().subtract(entity.position());
                    float f = -((float) Mth.atan2(leapOnPos.x, leapOnPos.z)) * 180.0F / (float) Math.PI;
                    entity.setYRot(f);
                    float f1 = (float) (-(Mth.atan2(leapOnPos.y, leapOnPos.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                    entity.setTargetDigPitch(f1);
                    if (this.leapTicks <= maxLeapTicks) {
                        this.leapTicks++;
                        float leapUp = (float) ((1F - leapTicks / (float) maxLeapTicks) * 8 * leapHeight);
                        Vec3 delta = leapOnPos.scale(0.1F);
                        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.1F).add(delta.add(0, leapUp, 0)));
                    } else {
                        leapingAttack = false;
                        leapTicks = 0;
                        entity.leapAttackCooldown = 300;
                    }
                }
            } else {
                entity.getNavigation().moveTo(target, 1.0D);
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if(entity.isLeaping()){
                    entity.setLeaping(false);
                }
            }
            if (entity.isMouthOpen()) {
                if (entity.attackAllAroundMouth((float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 2.0F)) {
                    entity.timeBetweenAttacks = leapingAttack ? 100 : 20;
                    entity.leapAttackCooldown = 300;
                    leapingAttack = false;
                    entity.attemptPlayAttackNoise();
                }
            }
            entity.setBiting(dist < 15 + target.getBbWidth());
        }
    }
}
