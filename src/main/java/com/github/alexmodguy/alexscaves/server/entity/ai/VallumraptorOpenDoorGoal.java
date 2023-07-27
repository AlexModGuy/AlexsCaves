package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.phys.Vec3;

public class VallumraptorOpenDoorGoal extends DoorInteractGoal {

    private VallumraptorEntity raptor;

    private int timeSincePassing = 0;

    public VallumraptorOpenDoorGoal(VallumraptorEntity vallumraptor) {
        super(vallumraptor);
        this.raptor = vallumraptor;
    }

    public boolean canUse() {
        return super.canUse() && !isOpen();
    }

    public boolean canContinueToUse() {
        return this.raptor.isTame() ? timeSincePassing < 15 : !isOpen();
    }

    public boolean hasNotPassed() {
        return super.canContinueToUse();
    }

    public void tick() {
        super.tick();
        Vec3 vec3 = Vec3.atCenterOf(this.doorPos);
        if (!isOpen() && this.raptor.distanceToSqr(vec3) < 4) {
            this.raptor.lookAt(EntityAnchorArgument.Anchor.EYES, vec3);
            if (this.raptor.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.raptor.setAnimation(VallumraptorEntity.ANIMATION_GRAB);
            }
        }
        if (this.raptor.getAnimation() == VallumraptorEntity.ANIMATION_GRAB && this.raptor.getAnimationTick() == 16) {
            this.setOpen(true);
        }
        if (!hasNotPassed()) {
            timeSincePassing++;
        }
    }

    public void stop() {
        super.stop();
        if (this.raptor.isTame()) {
            this.setOpen(false);
        }
        timeSincePassing = 0;
    }

}
