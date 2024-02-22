package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;

public class AnimalBreedEggsGoal extends BreedGoal {
    private final Animal mob;
    private int eggLoveTime;
    private double speed;

    public AnimalBreedEggsGoal(Animal mob, double speed) {
        super(mob, speed);
        this.mob = mob;
        this.speed = speed;
    }

    public boolean canUse() {
        return super.canUse() && !((LaysEggs) this.mob).hasEgg();
    }

    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.eggLoveTime < 60;
    }

    public void stop() {
        this.partner = null;
        this.eggLoveTime = 0;
    }

    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speed);
        ++this.eggLoveTime;
        double width = Math.max(this.animal.getBbWidth() * 2.0F + 0.5F, 3.0D);
        if (this.eggLoveTime >= this.adjustedTickDelay(60) && Mth.sqrt((float) this.animal.distanceToSqr(this.partner)) < width) {
            this.breed();
        }
    }

    @Override
    protected void breed() {
        ServerPlayer serverplayer = this.animal.getLoveCause();
        if (serverplayer == null && this.partner.getLoveCause() != null) {
            serverplayer = this.partner.getLoveCause();
        }

        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, (AgeableMob) null);
        }

        ((LaysEggs) this.mob).setHasEgg(true);
        this.animal.setAge(6000);
        this.partner.setAge(6000);
        this.animal.resetLove();
        this.partner.resetLove();
        RandomSource randomsource = this.animal.getRandom();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), randomsource.nextInt(7) + 1));
        }
    }
}
