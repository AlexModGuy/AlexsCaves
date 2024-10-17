package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class GingerbreadManTargetEverythingGoal extends NearestAttackableTargetGoal {

    private GingerbreadManEntity gingerbreadMan;

    public GingerbreadManTargetEverythingGoal(GingerbreadManEntity gingerbreadMan) {
        super(gingerbreadMan, LivingEntity.class, false, true);
        this.gingerbreadMan = gingerbreadMan;
    }

    public boolean canUse() {
        return gingerbreadMan.isOvenSpawned() && super.canUse();
    }

    protected void findTarget() {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (entity) -> {
                return !(entity instanceof GingerbreadManEntity gingerbreadMan1 && gingerbreadMan1.getGingerbreadTeamColor() == gingerbreadMan.getGingerbreadTeamColor());
            }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }
}
