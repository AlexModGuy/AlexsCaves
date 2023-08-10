package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GloomothEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GloomothFindLightGoal extends MoveToBlockGoal {
    private final GloomothEntity gloomoth;

    public GloomothFindLightGoal(GloomothEntity entity, int range) {
        super(entity, 1.0F, range, range);
        this.gloomoth = entity;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(50 + gloomoth.getRandom().nextInt(50));
    }

    @Override
    public boolean canUse() {
        return this.gloomoth.lightPos == null && super.canUse() && !isTargetBlocked(blockPos.getCenter());
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(mob.getX(), mob.getEyeY(), mob.getZ());
        return mob.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() != HitResult.Type.MISS;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.gloomoth.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.gloomoth.lightPos == null;
    }

    public double acceptedDistance() {
        return gloomoth.getBbWidth() + 1;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos target = getMoveToTarget();
        if (target != null) {
            gloomoth.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(target));
            if (this.isReachedTarget()) {
                gloomoth.lightPos = blockPos;
            }
        }
    }


    public void start() {
        gloomoth.setFlying(true);
        super.start();
    }

    public void stop() {
        super.stop();
    }

    protected BlockPos getMoveToTarget() {
        return this.blockPos;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
         if(pos != null && worldIn.getBlockState(pos).is(ACTagRegistry.GLOOMOTH_LIGHT_SOURCES) && worldIn.getLightEmission(pos) > 0 && worldIn instanceof ServerLevel serverLevel){
            return gloomoth.getNearestMothBall(serverLevel, blockPos, 10) == null;
         }else{
             return false;
         }
    }
}
