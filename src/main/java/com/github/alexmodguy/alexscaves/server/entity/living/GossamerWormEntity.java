package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.AnimalRandomlySwimGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.EnumSet;

public class GossamerWormEntity extends WaterAnimal {

    public final GossamerWormPartEntity tail1Part;
    public final GossamerWormPartEntity tail2Part;
    public final GossamerWormPartEntity tail3Part;
    public final GossamerWormPartEntity tail4Part;
    public final GossamerWormPartEntity tail5Part;
    private final GossamerWormPartEntity[] allParts;
    private float fishPitch = 0;
    private float prevFishPitch = 0;
    private float fakeYRot = 0;
    private float[][] trailTransformations = new float[128][2];
    private int trailPointer = -1;

    private float squishProgress;
    private float prevSquishProgress;
    private BlockPos hurtPos = null;
    private int fleeFor = 0;

    public GossamerWormEntity(EntityType entityType, Level level) {
        super(entityType, level);
        tail1Part = new GossamerWormPartEntity(this, this, 1.1F, 0.5F);
        tail2Part = new GossamerWormPartEntity(this, tail1Part, 1.1F, 0.5F);
        tail3Part = new GossamerWormPartEntity(this, tail2Part, 1F, 0.5F);
        tail4Part = new GossamerWormPartEntity(this, tail3Part, 0.8F, 0.5F);
        tail5Part = new GossamerWormPartEntity(this, tail4Part, 0.6F, 0.5F);
        allParts = new GossamerWormPartEntity[]{tail1Part, tail2Part, tail3Part, tail4Part, tail5Part};
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.8F, 4);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.fakeYRot = getYRot();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AvoidHurtGoal());
        this.goalSelector.addGoal(1, new AnimalRandomlySwimGoal(this, 3, 12, 20, 1.0D));
    }

    protected void playSwimSound(float f) {

    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5F * dimensions.height;
    }


    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.1D).add(Attributes.MAX_HEALTH, 10.0D);
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        if (allParts != null) {
            for (PartEntity part : allParts) {
                part.remove(RemovalReason.KILLED);
            }
        }
    }

    public void tick() {
        prevFishPitch = fishPitch;
        prevSquishProgress = squishProgress;
        fakeYRot = Mth.approachDegrees(fakeYRot, this.yBodyRot, 10);
        super.tick();

        this.tickMultipart();
        float targetPitch = isInWaterOrBubble() ? Mth.clamp((float) this.getDeltaMovement().y * 25, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI) : 0;
        fishPitch = Mth.approachDegrees(fishPitch, targetPitch, 1);
        if (fleeFor > 0) {
            fleeFor--;
            if (fleeFor == 0) {
                hurtPos = null;
            }
        }
        boolean grounded = !isInWaterOrBubble();
        if (grounded && squishProgress < 5F) {
            squishProgress++;
        }
        if (!grounded && squishProgress > 0F) {
            squishProgress--;
        }
        if (grounded && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.playSound(SoundEvents.COD_FLOP, this.getSoundVolume(), this.getVoicePitch());

        }
    }

    private void tickMultipart() {
        if (trailPointer == -1) {
            this.fakeYRot = this.yBodyRot;
            for (int i = 0; i < this.trailTransformations.length; i++) {
                this.trailTransformations[i][0] = this.fishPitch;
                this.trailTransformations[i][1] = this.fakeYRot;
            }
        }
        if (++this.trailPointer == this.trailTransformations.length) {
            this.trailPointer = 0;
        }
        this.trailTransformations[this.trailPointer][0] = this.fishPitch;
        this.trailTransformations[this.trailPointer][1] = this.fakeYRot;

        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        this.tail1Part.setToTransformation(new Vec3(0, 0, -1), this.getTrailTransformation(5, 0, 1.0F), this.getTrailTransformation(5, 1, 1.0F));
        this.tail2Part.setToTransformation(new Vec3(0, 0, -0.9F), this.getTrailTransformation(10, 0, 1.0F), this.getTrailTransformation(10, 1, 1.0F));
        this.tail3Part.setToTransformation(new Vec3(0, 0, -0.8F), this.getTrailTransformation(15, 0, 1.0F), this.getTrailTransformation(15, 1, 1.0F));
        this.tail4Part.setToTransformation(new Vec3(0, 0, -0.7F), this.getTrailTransformation(20, 0, 1.0F), this.getTrailTransformation(20, 1, 1.0F));
        this.tail5Part.setToTransformation(new Vec3(0, 0, -0.6F), this.getTrailTransformation(25, 0, 1.0F), this.getTrailTransformation(25, 1, 1.0F));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    public float getSquishProgress(float partialTicks) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTicks) * 0.2F;
    }

    protected static float lerpRotation(float p_37274_, float p_37275_) {
        while (p_37275_ - p_37274_ < -180.0F) {
            p_37274_ -= 360.0F;
        }

        while (p_37275_ - p_37274_ >= 180.0F) {
            p_37274_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_37274_, p_37275_);
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    public float getTrailTransformation(int pointer, int index, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 127;
        int j = this.trailPointer - pointer - 1 & 127;
        float d0 = this.trailTransformations[j][index];
        float d1 = this.trailTransformations[i][index] - d0;
        return d0 + d1 * partialTick;
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageValue) {
        boolean sup = super.hurt(damageSource, damageValue);
        if (sup) {
            fleeFor = 40 + random.nextInt(40);
            hurtPos = this.blockPosition();
        }
        return sup;
    }

    public static boolean checkGossamerWormSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() < level.getSeaLevel() - 25 && randomSource.nextInt(3) == 0;
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition();
        while(!world.getFluidState(down).isEmpty() && down.getY() > world.getMinBuildHeight()){
            down = down.below();
        }
        float f = this.blockPosition().getY() - down.getY();
        this.setPos(down.getX() + 0.5F, down.getY() + f * (random.nextFloat() * 0.33F + 0.33F), down.getZ() + 0.5F);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }



    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return allParts;
    }

    class AvoidHurtGoal extends Goal {

        protected AvoidHurtGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        private Vec3 fleeTarget = null;

        @Override
        public boolean canUse() {
            return GossamerWormEntity.this.hurtPos != null && GossamerWormEntity.this.fleeFor > 0;
        }

        @Override
        public void start() {
            fleeTarget = null;
        }

        public void tick() {
            if ((fleeTarget == null || GossamerWormEntity.this.distanceToSqr(fleeTarget) < 6) && GossamerWormEntity.this.hurtPos != null) {
                fleeTarget = DefaultRandomPos.getPosAway(GossamerWormEntity.this, 16, 7, Vec3.atCenterOf(GossamerWormEntity.this.hurtPos));
            }
            if (fleeTarget != null) {
                GossamerWormEntity.this.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.6F);
            }
        }
    }
}
