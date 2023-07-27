package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.phys.Vec3;

public class UnderzealotOpenDoorGoal extends OpenDoorGoal {

    private UnderzealotEntity underzealot;

    public UnderzealotOpenDoorGoal(UnderzealotEntity underzealot) {
        super(underzealot, false);
        this.underzealot = underzealot;
    }

    public void start() {
        if (this.underzealot.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.underzealot.setAnimation(UnderzealotEntity.ANIMATION_BREAKTORCH);
        }
    }

    public boolean canUse() {
        return super.canUse() && !isOpen();
    }

    public boolean canContinueToUse() {
        return this.underzealot.getAnimation() == UnderzealotEntity.ANIMATION_BREAKTORCH && !isOpen();
    }

    public void tick() {
        this.underzealot.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.doorPos));
        if (this.underzealot.getAnimation() == UnderzealotEntity.ANIMATION_BREAKTORCH && this.underzealot.getAnimationTick() == 8) {
            this.setOpen(true);
        }
    }

    public void stop() {
    }

}
