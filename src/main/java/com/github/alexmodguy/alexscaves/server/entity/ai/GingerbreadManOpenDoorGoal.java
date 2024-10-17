package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.GingerbreadDoorBlock;
import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class GingerbreadManOpenDoorGoal extends Goal {
    protected GingerbreadManEntity gingerbreadMan;
    protected BlockPos doorPos = BlockPos.ZERO;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;
    private int timeSincePassing = 0;

    public GingerbreadManOpenDoorGoal(GingerbreadManEntity gingerbreadMan) {
        this.gingerbreadMan = gingerbreadMan;
    }

    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        } else {
            BlockState blockstate = this.gingerbreadMan.level().getBlockState(this.doorPos);
            if (!(blockstate.getBlock() instanceof GingerbreadDoorBlock)) {
                this.hasDoor = false;
                return false;
            } else {
                return blockstate.getValue(GingerbreadDoorBlock.OPEN);
            }
        }
    }

    protected void setOpen(boolean b) {
        if (this.hasDoor) {
            BlockState blockstate = this.gingerbreadMan.level().getBlockState(this.doorPos);
            if (blockstate.getBlock() instanceof GingerbreadDoorBlock) {
                ((GingerbreadDoorBlock) blockstate.getBlock()).setOpen(this.gingerbreadMan, this.gingerbreadMan.level(), blockstate, this.doorPos, b);
            }
        }

    }

    public boolean canUse() {
        if (!GoalUtils.hasGroundPathNavigation(this.gingerbreadMan)) {
            return false;
        } else if (!this.gingerbreadMan.horizontalCollision) {
            return false;
        } else {
            if (this.gingerbreadMan.getNavigation() instanceof GroundPathNavigation groundpathnavigation) {
                Path path = groundpathnavigation.getPath();
                if (path != null && !path.isDone() && groundpathnavigation.canOpenDoors()) {
                    for (int i = 0; i < Math.min(path.getNextNodeIndex() + 2, path.getNodeCount()); ++i) {
                        Node node = path.getNode(i);
                        this.doorPos = new BlockPos(node.x, node.y, node.z);
                        if (!(this.gingerbreadMan.distanceToSqr(this.doorPos.getX(), this.gingerbreadMan.getY(), this.doorPos.getZ()) > 2.25D)) {
                            this.hasDoor = this.gingerbreadMan.level().getBlockState(this.doorPos).getBlock() instanceof GingerbreadDoorBlock;
                            if (this.hasDoor) {
                                return !isOpen();
                            }
                        }
                    }
                    this.doorPos = this.gingerbreadMan.blockPosition();
                    this.hasDoor = this.gingerbreadMan.level().getBlockState(this.doorPos).getBlock() instanceof GingerbreadDoorBlock;
                    return this.hasDoor && !isOpen();
                }
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        return !this.passed || timeSincePassing < 15;
    }

    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float) ((double) this.doorPos.getX() + 0.5D - this.gingerbreadMan.getX());
        this.doorOpenDirZ = (float) ((double) this.doorPos.getZ() + 0.5D - this.gingerbreadMan.getZ());
        timeSincePassing = 0;
    }

    public void stop() {
        this.setOpen(false);
        timeSincePassing = 0;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        Vec3 vec3 = Vec3.atCenterOf(this.doorPos);
        if (!isOpen() && this.gingerbreadMan.distanceToSqr(vec3) < 4) {
            this.gingerbreadMan.lookAt(EntityAnchorArgument.Anchor.EYES, vec3);
            if(gingerbreadMan.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                gingerbreadMan.setAnimation(gingerbreadMan.getRandom().nextBoolean() ? GingerbreadManEntity.ANIMATION_SWING_RIGHT : GingerbreadManEntity.ANIMATION_SWING_LEFT);
            }
        }
        if ((this.gingerbreadMan.getAnimation() == GingerbreadManEntity.ANIMATION_SWING_RIGHT || this.gingerbreadMan.getAnimation() == GingerbreadManEntity.ANIMATION_SWING_LEFT) && this.gingerbreadMan.getAnimationTick() == 8) {
            this.setOpen(true);
            this.passed = true;
        }
        if (passed) {
            timeSincePassing++;
        }
    }
}