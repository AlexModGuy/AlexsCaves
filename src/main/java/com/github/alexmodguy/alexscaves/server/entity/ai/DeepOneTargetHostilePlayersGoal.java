package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.DeepOneReaction;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class DeepOneTargetHostilePlayersGoal extends NearestAttackableTargetGoal {

    private DeepOneBaseEntity deepOne;

    public DeepOneTargetHostilePlayersGoal(DeepOneBaseEntity deepOne) {
        super(deepOne, Player.class, false, true);
        this.deepOne = deepOne;
    }

    @Override
    protected void findTarget() {
        this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (targetEntity) -> {
            return targetEntity instanceof Player player && this.deepOne.getReactionTo(player) == DeepOneReaction.AGGRESSIVE && !player.isCreative();
        }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }
}
