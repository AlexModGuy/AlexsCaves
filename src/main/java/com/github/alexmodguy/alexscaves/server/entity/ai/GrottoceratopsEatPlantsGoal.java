package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.DoublePlantWithRotationBlock;
import com.github.alexmodguy.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

public class GrottoceratopsEatPlantsGoal extends MoveToBlockGoal {
    private final GrottoceratopsEntity grottoceratops;

    public GrottoceratopsEatPlantsGoal(GrottoceratopsEntity entity, int range) {
        super(entity, 1.0F, range, 6);
        this.grottoceratops = entity;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(200 + grottoceratops.getRandom().nextInt(200));
    }

    @Override
    public boolean canUse() {
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.grottoceratops.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    public double acceptedDistance() {
        return grottoceratops.getBbWidth() + 1;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        if (target != null) {
            grottoceratops.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(target));
            if (this.isReachedTarget()) {
                if (grottoceratops.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    grottoceratops.setAnimation(GrottoceratopsEntity.ANIMATION_CHEW_FROM_GROUND);
                } else if (grottoceratops.getAnimation() == GrottoceratopsEntity.ANIMATION_CHEW_FROM_GROUND) {
                    if (grottoceratops.getAnimationTick() > 15) {
                        BlockPos plant = target;
                        BlockState state = grottoceratops.level().getBlockState(plant);
                        grottoceratops.level().destroyBlock(plant, false);
                        if (state.is(ACBlockRegistry.CURLY_FERN.get())) {
                            grottoceratops.level().setBlockAndUpdate(plant, ACBlockRegistry.FIDDLEHEAD.get().defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    public void stop() {
        super.stop();
        this.blockPos = BlockPos.ZERO;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        if (pos != null) {
            BlockState state = worldIn.getBlockState(pos.above());
            if (state.is(ACBlockRegistry.CURLY_FERN.get())) {
                return state.getValue(DoublePlantWithRotationBlock.HALF) == DoubleBlockHalf.LOWER;
            } else {
                return state.is(ACTagRegistry.GROTTOCERATOPS_FOOD_BLOCKS);
            }
        }
        return false;
    }
}
