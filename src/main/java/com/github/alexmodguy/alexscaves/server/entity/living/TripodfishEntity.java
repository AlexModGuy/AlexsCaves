package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class TripodfishEntity extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(TripodfishEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(TripodfishEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDimensions STANDING_SIZE = EntityDimensions.scalable(0.95F, 1.5F);
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch = 0;
    private float prevFishPitch = 0;
    private float standProgress;
    private float prevStandProgress;

    private boolean hasStandingSize = false;
    private int timeSwimming;
    private int timeStanding;
    private int navigateTypeLength = 300;
    private BlockPos hurtPos = null;
    private int fleeFor = 0;

    public TripodfishEntity(EntityType type, Level level) {
        super(type, level);
        this.moveControl = new MoveControl();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STANDING, Boolean.valueOf(false));
        this.entityData.define(FROM_BUCKET, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AvoidHurtGoal());
        this.goalSelector.addGoal(2, new WanderGoal());
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !TripodfishEntity.this.isStanding();
            }
        });
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !TripodfishEntity.this.isStanding();
            }
        });
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            if (this.getTarget() == null && !this.isStanding()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(isStanding() ? 0.3F : 0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    protected void playSwimSound(float f) {

    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, Boolean.valueOf(standing));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.MAX_HEALTH, 8.0D);
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }


    public EntityDimensions getDimensions(Pose poseIn) {
        return isStanding() ? STANDING_SIZE.scale(this.getScale()) : super.getDimensions(poseIn);
    }

    @Override
    public void tick() {
        super.tick();
        prevStandProgress = standProgress;
        prevFishPitch = fishPitch;
        prevLandProgress = landProgress;
        float pitchTarget = (float) this.getDeltaMovement().y * 3F;
        if (this.isStanding()) {
            if (this.standProgress < 10F) {
                this.standProgress++;
            }
            if (!hasStandingSize) {
                hasStandingSize = true;
                refreshDimensions();
                navigateTypeLength = 400 + random.nextInt(400);
            }
            timeStanding++;
            timeSwimming = 0;
            pitchTarget = 0;
            this.getNavigation().stop();

            if (!this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0).multiply(0.5F, 1F, 0.5F));
            }
        } else {
            if (this.standProgress > 0F) {
                this.standProgress--;
            }
            timeStanding = 0;
            timeSwimming++;
            if (hasStandingSize) {
                hasStandingSize = false;
                double d = (float) (this.getBbHeight() * 0.35F + this.getY());
                refreshDimensions();
                this.setPos(this.getX(), d, this.getZ());
                navigateTypeLength = 400 + random.nextInt(400);
            }
        }
        fishPitch = Mth.approachDegrees(fishPitch, Mth.clamp(pitchTarget, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI), 5);
        boolean grounded = !isInWaterOrBubble();
        if (grounded && landProgress < 5F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0F) {
            landProgress--;
        }
        if (!isInWaterOrBubble() && this.isAlive()) {
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(ACSoundRegistry.TRIPODFISH_FLOP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
        if (fleeFor > 0) {
            fleeFor--;
            if (fleeFor == 0) {
                hurtPos = null;
            }
        }
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public static boolean checkTripodfishSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() < level.getSeaLevel() - 30 && randomSource.nextBoolean();
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public float getStandProgress(float partialTicks) {
        return (prevStandProgress + (standProgress - prevStandProgress) * partialTicks) * 0.1F;
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

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition();
        while (!world.getFluidState(down).isEmpty() && down.getY() > world.getMinBuildHeight()) {
            down = down.below();
        }
        this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.put("FishBucketTag", platTag);
    }


    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double dist) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean sit) {
        this.entityData.set(FROM_BUCKET, sit);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("FishBucketTag")) {
            this.readAdditionalSaveData(compound.getCompound("FishBucketTag"));
        }
        this.setAirSupply(2000);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.TRIPODFISH_BUCKET.get());
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TRIPODFISH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TRIPODFISH_HURT.get();
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(damageSource);
    }

    class AvoidHurtGoal extends Goal {

        protected AvoidHurtGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        private Vec3 fleeTarget = null;

        @Override
        public boolean canUse() {
            return TripodfishEntity.this.hurtPos != null && TripodfishEntity.this.fleeFor > 0;
        }

        @Override
        public void start() {
            TripodfishEntity.this.setStanding(false);
            fleeTarget = null;
        }

        public void tick() {
            if ((fleeTarget == null || TripodfishEntity.this.distanceToSqr(fleeTarget) < 6) && TripodfishEntity.this.hurtPos != null) {
                fleeTarget = DefaultRandomPos.getPosAway(TripodfishEntity.this, 16, 7, Vec3.atCenterOf(TripodfishEntity.this.hurtPos));
            }
            if (fleeTarget != null) {
                TripodfishEntity.this.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.6F);
            }
        }
    }

    private class WanderGoal extends Goal {

        private double x;
        private double y;
        private double z;
        private boolean wantsToStand;

        public WanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (TripodfishEntity.this.getRandom().nextInt(100) != 0 && TripodfishEntity.this.isStanding() && TripodfishEntity.this.timeStanding < TripodfishEntity.this.navigateTypeLength) {
                return false;
            }
            if (TripodfishEntity.this.isStanding()) {
                this.wantsToStand = false;
            } else {
                this.wantsToStand = TripodfishEntity.this.timeSwimming > 300 || TripodfishEntity.this.getRandom().nextFloat() < 0.2F;
            }
            Vec3 target = this.getPosition();
            if (target == null) {
                return false;
            } else {
                this.x = target.x;
                this.y = target.y;
                this.z = target.z;
                return true;
            }
        }

        public boolean canContinueToUse() {
            double dist = TripodfishEntity.this.distanceToSqr(x, y, z);
            return !TripodfishEntity.this.getNavigation().isDone() && dist > 9;
        }

        public void start() {
            TripodfishEntity.this.setStanding(false);
            TripodfishEntity.this.getNavigation().moveTo(this.x, this.y, this.z, 1F);
        }

        public void stop() {
            BlockPos ground = TripodfishEntity.this.blockPosition();
            int down = 0;
            while (TripodfishEntity.this.level().getFluidState(ground).is(FluidTags.WATER) && down < 3 && ground.getY() > level().getMinBuildHeight()) {
                ground = ground.below();
                down++;
            }
            if (this.wantsToStand && down <= 2) {
                TripodfishEntity.this.setStanding(true);
                TripodfishEntity.this.getNavigation().stop();
                TripodfishEntity.this.setDeltaMovement(Vec3.ZERO);
            }
        }

        public BlockPos findWaterBlock() {
            BlockPos result = null;
            RandomSource random = TripodfishEntity.this.getRandom();
            int range = 20;
            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = TripodfishEntity.this.blockPosition().offset(random.nextInt(range) - range / 2, random.nextInt(range) - range / 2, random.nextInt(range) - range / 2);
                if (TripodfishEntity.this.level().getFluidState(blockPos).is(FluidTags.WATER) && blockPos.getY() > level().getMinBuildHeight()) {
                    result = blockPos;
                }
            }
            return result;
        }


        public boolean isTargetBlocked(Vec3 target) {
            Vec3 Vector3d = new Vec3(TripodfishEntity.this.getX(), TripodfishEntity.this.getEyeY(), TripodfishEntity.this.getZ());
            return TripodfishEntity.this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, TripodfishEntity.this)).getType() != HitResult.Type.MISS;
        }

        @Nullable
        protected Vec3 getPosition() {
            BlockPos water = findWaterBlock();
            if (TripodfishEntity.this.isInWaterOrBubble()) {
                if (water == null) {
                    return null;
                }
                while (TripodfishEntity.this.level().getFluidState(water.below()).is(FluidTags.WATER) && water.getY() > level().getMinBuildHeight() + 1) {
                    water = water.below();
                }
                BlockState seafloorState = level().getBlockState(water.below());
                //don't stand on magma
                if (wantsToStand && (seafloorState.is(Blocks.MAGMA_BLOCK) || !seafloorState.getFluidState().isEmpty() && !seafloorState.getFluidState().is(FluidTags.WATER))) {
                    return null;
                }
                BlockPos above = water.above(wantsToStand ? 1 : 3 + random.nextInt(3));
                while (!TripodfishEntity.this.level().getFluidState(above).is(FluidTags.WATER) && above.getY() > water.getY()) {
                    above = above.below();
                }
                Vec3 vec3 = Vec3.atCenterOf(above);
                if (!isTargetBlocked(vec3)) {
                    return vec3;
                } else {
                    return null;
                }
            } else {
                return water == null ? DefaultRandomPos.getPos(TripodfishEntity.this, 7, 3) : Vec3.atCenterOf(water);
            }
        }

    }


    private class MoveControl extends VerticalSwimmingMoveControl {

        private MoveControl() {
            super(TripodfishEntity.this, 0.5F, 60);
        }

        @Override
        public void tick() {
            if (!TripodfishEntity.this.isStanding()) {
                super.tick();
            }
        }
    }
}
