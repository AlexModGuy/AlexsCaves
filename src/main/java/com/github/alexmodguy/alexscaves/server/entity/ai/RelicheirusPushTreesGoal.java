package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingTreeBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.RelicheirusEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RelicheirusPushTreesGoal extends MoveToBlockGoal {

    private static final int MAXIMUM_BLOCKS_PUSHED = 300;
    public static final int MAX_TREE_SPREAD = 12;
    private RelicheirusEntity relicheirus;
    private boolean madeTreeEntity = false;

    public RelicheirusPushTreesGoal(RelicheirusEntity relicheirus, int range) {
        super(relicheirus, 1.0F, range, 6);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.relicheirus = relicheirus;
    }

    public boolean canUse() {
        return relicheirus.getPushingTreesFor() > 0 && !relicheirus.isBaby() && super.canUse();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && !madeTreeEntity;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(10 + relicheirus.getRandom().nextInt(20));
    }

    public double acceptedDistance() {
        return 4.0D;
    }

    @Override
    protected boolean isReachedTarget() {
        BlockPos target = getMoveToTarget();
        return target != null && relicheirus.distanceToSqr(target.getX() + 0.5F, relicheirus.getY(), target.getZ() + 0.5F) < acceptedDistance();
    }

    protected BlockPos getMoveToTarget() {
        return relicheirus.getStandAtTreePos(getBottomOfTree(relicheirus.level(), blockPos));
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        if (target != null) {
            if (isReachedTarget()) {
                if (relicheirus.lockTreePosition(blockPos)) {
                    if (relicheirus.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        relicheirus.setPeckY(blockPos.getY());
                        relicheirus.setAnimation(RelicheirusEntity.ANIMATION_PUSH_TREE);
                    } else if (relicheirus.getAnimation() == RelicheirusEntity.ANIMATION_PUSH_TREE) {
                        if (relicheirus.getAnimationTick() >= 35 && !madeTreeEntity) {
                            madeTreeEntity = true;
                            relicheirus.playSound(ACSoundRegistry.RELICHEIRUS_TOPPLE.get());
                            List<BlockPos> gathered = new ArrayList<>();
                            gatherAttachedBlocks(blockPos, blockPos, gathered);
                            if (!gathered.isEmpty()) {
                                List<MovingBlockData> allData = new ArrayList<>();
                                for (BlockPos pos : gathered) {
                                    BlockState moveState = relicheirus.level().getBlockState(pos);
                                    BlockEntity te = relicheirus.level().getBlockEntity(pos);
                                    BlockPos offset = pos.subtract(blockPos);
                                    MovingBlockData data = new MovingBlockData(moveState, moveState.getShape(relicheirus.level(), pos), offset, te == null ? null : te.saveWithoutMetadata());
                                    relicheirus.level().removeBlockEntity(pos);
                                    allData.add(data);
                                }
                                for (BlockPos pos : gathered) {
                                    relicheirus.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                }
                                FallingTreeBlockEntity fallingTree = ACEntityRegistry.FALLING_TREE_BLOCK.get().create(relicheirus.level());
                                fallingTree.moveTo(Vec3.atCenterOf(blockPos));
                                fallingTree.setAllBlockData(FallingTreeBlockEntity.createTagFromData(allData));
                                fallingTree.setPlacementCooldown(1);
                                Vec3 vec3 = Vec3.atCenterOf(blockPos).subtract(relicheirus.position());
                                float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
                                fallingTree.setFallDirection(Direction.fromYRot(f));
                                relicheirus.level().addFreshEntity(fallingTree);
                            }
                        }
                    }
                }
            } else {
                if (relicheirus.getNavigation().isDone()) {
                    Vec3 vec31 = Vec3.atCenterOf(target);
                    Vec3 vec32 = vec31.subtract(relicheirus.position());
                    if (vec32.length() > 1) {
                        vec32 = vec32.normalize();
                    }
                    Vec3 delta = new Vec3(vec32.x * 0.1F, 0F, vec32.z * 0.1F);
                    relicheirus.setDeltaMovement(relicheirus.getDeltaMovement().add(delta));
                }
            }
        }
    }

    protected void moveMobToBlock() {
        BlockPos pos = getMoveToTarget();
        this.mob.getNavigation().moveTo((double) ((float) pos.getX()) + 0.5D, (double) (pos.getY()), (double) ((float) pos.getZ()) + 0.5D, this.speedModifier);
    }


    public void stop() {
        this.blockPos = BlockPos.ZERO;
        madeTreeEntity = false;
        super.stop();
    }

    private BlockPos getBottomOfTree(LevelReader worldIn, BlockPos pos) {
        while (pos.getY() > worldIn.getMinBuildHeight() && (worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LEAVES) || worldIn.getBlockState(pos).isAir() || worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS))) {
            pos = pos.below();
        }
        return pos;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS)) {
            BlockPos treeTop = new BlockPos(pos);
            while (worldIn.getBlockState(treeTop).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS) && treeTop.getY() < worldIn.getMaxBuildHeight()) {
                treeTop = treeTop.above();
            }
            if (worldIn.getBlockState(treeTop).is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LEAVES)) {
                return true;
            }
        }
        return false;
    }

    public void gatherAttachedBlocks(BlockPos origin, BlockPos pos, List<BlockPos> list) {
        if (list.size() < MAXIMUM_BLOCKS_PUSHED) {
            if (!list.contains(pos)) {
                list.add(pos);
                for (BlockPos blockpos1 : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                    if (!blockpos1.equals(pos) && pos.distToCenterSqr(origin.getX(), pos.getY(), origin.getZ()) < MAX_TREE_SPREAD) {
                        if (isTreePart(blockpos1)) {
                            gatherAttachedBlocks(origin, blockpos1.immutable(), list);
                        }
                    }
                }
            }

        }
    }

    public boolean isTreePart(BlockPos pos) {
        BlockState state = relicheirus.level().getBlockState(pos);
        if (state.isAir() || state.is(ACTagRegistry.UNMOVEABLE)) {
            return false;
        } else {
            return state.is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LOGS) || state.is(ACTagRegistry.RELICHEIRUS_KNOCKABLE_LEAVES);
        }
    }

}
