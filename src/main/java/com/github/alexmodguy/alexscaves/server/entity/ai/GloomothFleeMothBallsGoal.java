package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GloomothEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GloomothFleeMothBallsGoal extends Goal {

    private GloomothEntity gloomoth;
    private BlockPos blockPos;
    private Vec3 retreatTo = null;

    public GloomothFleeMothBallsGoal(GloomothEntity gloomoth) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.gloomoth = gloomoth;
    }

    @Override
    public boolean canUse() {
        long worldTime = gloomoth.level().getGameTime() % 10;
        if (gloomoth.getRandom().nextInt(20) != 0 && worldTime != 0) {
            return false;
        }
        if(gloomoth.level() instanceof ServerLevel serverLevel){
            BlockPos pos = gloomoth.getNearestMothBall(serverLevel, gloomoth.blockPosition(), 20);
            if(pos != null){
                this.blockPos = pos;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return blockPos != null && this.gloomoth.distanceToSqr(blockPos.getCenter()) < 32;
    }

    @Override
    public void start() {
        gloomoth.setFlying(true);
    }

    @Override
    public void stop() {
        retreatTo = null;
        blockPos = null;
    }

    @Override
    public void tick() {
        super.tick();
        if(retreatTo == null || gloomoth.distanceToSqr(retreatTo) < 4){
            for (int i = 0; i < 15; i++) {
                Vec3 vec3 = DefaultRandomPos.getPosAway(gloomoth, 32, 15, blockPos.getCenter());
                if (vec3 != null) {
                    retreatTo = vec3;
                    break;
                }
            }
        }else{
            gloomoth.getNavigation().moveTo(retreatTo.x, retreatTo.y, retreatTo.z, 1F);
        }
    }
}
