package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class StunnedEffect extends MobEffect {

    protected StunnedEffect() {
        super(MobEffectCategory.HARMFUL, 0XFFFBC5);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160892", (double)-1.0F, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.getDeltaMovement().y > 0){
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0.1D, 1));
        }
        if(entity.level().random.nextFloat() < entity.getBbWidth() * 0.12F){
            entity.level().addParticle(ACParticleRegistry.STUN_STAR.get(), entity.getX(), entity.getEyeY(), entity.getZ(), entity.getId(), entity.level().random.nextFloat() * 360, 0);
        }
        if(entity instanceof Mob mob){
            entity.setXRot(30.0F);
            entity.xRotO = 30.0F;
            if(!mob.level().isClientSide){
                mob.goalSelector.setControlFlag(Goal.Flag.MOVE, false);
                mob.goalSelector.setControlFlag(Goal.Flag.JUMP, false);
                mob.goalSelector.setControlFlag(Goal.Flag.LOOK, false);
            }
        }

    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }
}
