package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.EnumSet;

public class AnimalLayEggGoal  extends MoveToBlockGoal {
    private final Animal mob;

    private final LaysEggs laysEggs;

    private final int maxTime;

    private int layEggCounter;

    public AnimalLayEggGoal(Animal mob, int maxTime, double speed) {
        super(mob, speed, 16);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = mob;
        this.laysEggs = (LaysEggs) this.mob;
        this.maxTime = maxTime;
    }


    public boolean canUse() {
        return this.laysEggs.hasEgg() ? super.canUse() : false;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.laysEggs.hasEgg();
    }

    public void start(){
        layEggCounter = 0;
    }

    public void tick() {
        super.tick();
        BlockPos blockpos = this.mob.blockPosition();
        if (!this.isReachedTarget()) {
            laysEggs.onLayEgg(this.blockPos, layEggCounter);
            if (this.layEggCounter++ > this.maxTime) {
                Level level = this.mob.level();
                level.playSound((Player)null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                BlockPos blockpos1 = this.blockPos.above();
                BlockState blockstate = laysEggs.createEggBlockState();
                level.setBlock(blockpos1, blockstate, 3);
                level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.mob, blockstate));
                this.laysEggs.setHasEgg(false);
                this.mob.setInLoveTime(600);
            }
        }else{
            layEggCounter = 0;
        }

    }

    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return !levelReader.isEmptyBlock(blockPos.above()) ? false : DinosaurEggBlock.isProperHabitat(levelReader, blockPos);
    }

}
