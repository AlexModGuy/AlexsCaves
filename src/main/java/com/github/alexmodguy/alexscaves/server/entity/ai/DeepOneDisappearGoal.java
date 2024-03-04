package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class DeepOneDisappearGoal extends Goal {

    private DeepOneBaseEntity deepOne;
    private int bombCooldown = 0;
    private int dissapearIn = 20;

    public DeepOneDisappearGoal(DeepOneBaseEntity deepOne) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.deepOne = deepOne;
    }

    @Override
    public boolean canUse() {
        Player player = this.deepOne.getCorneringPlayer();
        return player != null && !player.isSpectator() && player.isAlive() && this.deepOne.distanceTo(player) < 10 && !deepOne.isTradingLocked();
    }

    @Override
    public void stop() {
        this.deepOne.setCorneredBy(null);
    }

    public void start() {
        this.deepOne.setDeepOneSwimming(false);
        bombCooldown = 10;
        dissapearIn = 20;
    }

    @Override
    public void tick() {
        Player player = this.deepOne.getCorneringPlayer();
        if (player != null) {
            deepOne.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ(), 20.0F, (float) this.deepOne.getMaxHeadXRot());
            bombCooldown--;
            if (player.hasEffect(MobEffects.BLINDNESS)) {
                if (dissapearIn-- < 0) {
                    this.deepOne.remove(Entity.RemovalReason.KILLED);
                }
            } else if (bombCooldown < 0 && !this.deepOne.isDeepOneSwimming()) {
                if (this.deepOne.startDisappearBehavior(player)) {
                    bombCooldown = 10;
                    dissapearIn = 0;
                }
            }
        }
    }
}
