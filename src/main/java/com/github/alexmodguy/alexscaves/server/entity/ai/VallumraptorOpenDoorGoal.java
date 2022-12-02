package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.phys.Vec3;

public class VallumraptorOpenDoorGoal extends OpenDoorGoal {

    private VallumraptorEntity raptor;

    public VallumraptorOpenDoorGoal(VallumraptorEntity vallumraptor) {
        super(vallumraptor, false);
        this.raptor = vallumraptor;
    }

    public void start() {
        if(this.raptor.getAnimation() == IAnimatedEntity.NO_ANIMATION){
            this.raptor.setAnimation(VallumraptorEntity.ANIMATION_GRAB);
        }
    }

    public boolean canUse() {
        return super.canUse() && !isOpen();
    }

    public boolean canContinueToUse() {
        return this.raptor.getAnimation() == VallumraptorEntity.ANIMATION_GRAB && !isOpen();
    }

    public void tick(){
        this.raptor.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.doorPos));
        if(this.raptor.getAnimation() == VallumraptorEntity.ANIMATION_GRAB && this.raptor.getAnimationTick() == 16){
            this.setOpen(true);
        }
    }

    public void stop() {
    }

}
