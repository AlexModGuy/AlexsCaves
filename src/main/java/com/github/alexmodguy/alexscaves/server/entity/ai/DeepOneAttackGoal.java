package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class DeepOneAttackGoal extends Goal {

    private DeepOneBaseEntity deepOne;

    public DeepOneAttackGoal(DeepOneBaseEntity deepOne) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.deepOne = deepOne;
    }

    @Override
    public boolean canUse() {
        return deepOne.getTarget() != null && deepOne.getTarget().isAlive();
    }

    public void tick() {
        LivingEntity target = deepOne.getTarget();
        if (target != null) {
            deepOne.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 20.0F, (float) deepOne.getMaxHeadXRot());
            deepOne.startAttackBehavior(target);
        }
    }
}
