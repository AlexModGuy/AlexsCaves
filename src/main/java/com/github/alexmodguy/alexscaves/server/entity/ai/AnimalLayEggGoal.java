package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class AnimalLayEggGoal extends MoveToBlockGoal {
    private final Animal mob;

    private final LaysEggs laysEggs;

    private final int maxTime;

    private int layEggCounter;

    public AnimalLayEggGoal(Animal mob, int maxTime, double speed) {
        super(mob, speed, 16);
        this.mob = mob;
        this.laysEggs = (LaysEggs) this.mob;
        this.maxTime = maxTime;
    }

    public boolean canUse() {
        return this.laysEggs.hasEgg() && super.canUse();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.laysEggs.hasEgg();
    }

    public void start() {
        super.start();
        layEggCounter = 0;
    }

    public double acceptedDistance() {
        return Math.ceil(this.mob.getBbWidth()) + 0.5D;
    }

    public void tick() {
        super.tick();
        if (this.isReachedTarget()) {
            laysEggs.onLayEggTick(this.blockPos.above(), layEggCounter);
            if (this.layEggCounter++ > this.maxTime) {
                Level level = this.mob.level();
                level.playSound((Player) null, blockPos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                BlockPos blockpos1 = this.blockPos.above();
                BlockState blockstate = laysEggs.createEggBlockState();
                level.setBlockAndUpdate(blockpos1, blockstate);
                level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.mob, blockstate));
                this.laysEggs.setHasEgg(false);
                this.mob.setInLoveTime(600);
                this.mob.level().broadcastEntityEvent(this.mob, (byte) 78);
                if (this.mob instanceof DinosaurEntity dinosaur && level.getBlockState(this.blockPos).is(BlockTags.DIRT)) {
                    level.setBlockAndUpdate(this.blockPos, dinosaur.createEggBeddingBlockState());
                }
            }
        } else {
            layEggCounter = 0;
        }

    }

    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        if(levelReader.isEmptyBlock(blockPos.above()) && this.mob instanceof DinosaurEntity dinosaur){
            BlockState eggState = dinosaur.createEggBlockState();
            if(eggState != null && eggState.getBlock() instanceof DinosaurEggBlock dinosaurEggBlock){
                return dinosaurEggBlock.isProperHabitat(levelReader, blockPos.above());
            }
            return true;
        }
        return false;
    }

}
