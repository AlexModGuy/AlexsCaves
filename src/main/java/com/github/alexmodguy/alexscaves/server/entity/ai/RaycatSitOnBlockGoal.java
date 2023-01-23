package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class RaycatSitOnBlockGoal extends MoveToBlockGoal {
   private final RaycatEntity cat;

   public RaycatSitOnBlockGoal(RaycatEntity raycat, double speed) {
      super(raycat, speed, 8);
      this.cat = raycat;
   }

   public boolean canUse() {
      return super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
      this.cat.setLayTime(0);
   }

   public void stop() {
      super.stop();
      this.cat.setInSittingPose(false);
      this.cat.setLayTime(0);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(this.isReachedTarget());
      this.cat.setLayTime(this.isReachedTarget() ? 10 : 0);
   }

   protected boolean isValidTarget(LevelReader level, BlockPos pos) {
      if (!level.isEmptyBlock(pos.above())) {
         return false;
      } else {
         BlockState blockstate = level.getBlockState(pos);

         if (blockstate.is(Blocks.CHEST)) {
            return ChestBlockEntity.getOpenCount(level, pos) < 1;
         } else if(blockstate.is(ACTagRegistry.RAYCAT_SLEEPS_ON)){
            return true;
         }else {
            return blockstate.is(Blocks.FURNACE) && blockstate.getValue(FurnaceBlock.LIT) ? true : blockstate.is(BlockTags.BEDS, (stateBase) -> {
               return stateBase.getOptionalValue(BedBlock.PART).map((bedPart) -> {
                  return bedPart != BedPart.HEAD;
               }).orElse(true);
            });
         }
      }
   }
}