package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.mojang.datafixers.DataFixUtils;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LanternfishEntity extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(LanternfishEntity.class, EntityDataSerializers.BOOLEAN);
    private float landProgress;
    private float prevLandProgress;
    private float circleSpeed = 1.0F;
    private float fishPitch = 0;
    private float prevFishPitch = 0;

    private int baitballCooldown = 100 + random.nextInt(100);
    private int circleTime = 0;
    private int maxCircleTime = 300;
    private BlockPos circlePos;
    private LanternfishEntity groupLeader;
    private int groupSize = 1;

    public LanternfishEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new LanternfishMoveControl();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimInSchoolGoal(this));
        this.goalSelector.addGoal(1, new JoinSchoolGoal(this));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.MAX_HEALTH, 2.0D);
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
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

    protected void playSwimSound(float f) {

    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
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

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }

    @Override
    public void tick() {
        super.tick();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        boolean grounded = this.onGround() && !isInWaterOrBubble();
        if (grounded && landProgress < 5F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0F) {
            landProgress--;
        }
        fishPitch = Mth.clamp((float) this.getDeltaMovement().y * 3F, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI);
        if (!isInWaterOrBubble() && this.isAlive()) {
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(ACSoundRegistry.LANTERNFISH_FLOP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
        if (baitballCooldown > 0) {
            baitballCooldown--;
        }
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    protected void handleAirSupply(int prevAir) {
        if (this.isAlive() && !isInWaterOrBubble()) {
            this.setAirSupply(prevAir - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(200);
        }
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 10.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
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

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("FishBucketTag")) {
            this.readAdditionalSaveData(compound.getCompound("FishBucketTag"));
        }
        this.setAirSupply(2000);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.LANTERNFISH_BUCKET.get());
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    public void leaveGroup() {
        if (this.groupLeader != null) {
            this.groupLeader.decreaseGroupSize();
        }
        this.groupLeader = null;
    }

    protected boolean hasNoLeader() {
        return !this.hasGroupLeader();
    }

    public boolean hasGroupLeader() {
        return this.groupLeader != null && this.groupLeader.isAlive();
    }

    private void increaseGroupSize() {
        ++this.groupSize;
    }

    private void decreaseGroupSize() {
        --this.groupSize;
    }

    public boolean canGroupGrow() {
        return this.isGroupLeader() && this.groupSize < this.getMaxGroupSize();
    }

    private int getMaxGroupSize() {
        return 20;
    }

    public int getMaxSpawnClusterSize() {
        return getMaxGroupSize();
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public boolean isGroupLeader() {
        return this.groupSize > 1;
    }

    public boolean inRangeOfGroupLeader() {
        return this.distanceToSqr(this.groupLeader) <= 121.0D;
    }

    public static boolean checkLanternfishSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() < level.getSeaLevel() - 30;
    }

    public void moveToGroupLeader() {
        if (this.hasGroupLeader()) {
            this.getNavigation().moveTo(this.groupLeader.getX(), this.groupLeader.getY(), this.groupLeader.getZ(), 1.0D);
        }

    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition();
        while (!world.getFluidState(down).isEmpty() && down.getY() > world.getMinBuildHeight()) {
            down = down.below();
        }
        this.setPos(down.getX() + 0.5F, down.getY() + 2, down.getZ() + 0.5F);
    }

    public LanternfishEntity createAndSetLeader(LanternfishEntity leader) {
        this.groupLeader = leader;
        leader.increaseGroupSize();
        return leader;
    }

    public void createFromStream(Stream<LanternfishEntity> stream) {
        stream.limit(this.getMaxGroupSize() - this.groupSize).filter((fishe) -> {
            return fishe != this;
        }).forEach((fishe) -> {
            fishe.createAndSetLeader(this);
        });
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new LanternfishEntity.GroupData(this);
        } else {
            this.createAndSetLeader(((LanternfishEntity.GroupData) spawnDataIn).groupLeader);
        }
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean isCircling() {
        return circlePos != null && circleTime < maxCircleTime;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.LANTERNFISH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LANTERNFISH_HURT.get();
    }
    public static class GroupData extends AgeableMob.AgeableMobGroupData {
        public final LanternfishEntity groupLeader;

        public GroupData(LanternfishEntity groupLeaderIn) {
            super(0.05F);
            this.groupLeader = groupLeaderIn;
        }
    }

    private class SwimInSchoolGoal extends Goal {

        private final LanternfishEntity fish;
        float circleDistance = 3;
        boolean clockwise = false;

        public SwimInSchoolGoal(LanternfishEntity fish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.fish = fish;
        }

        @Override
        public boolean canUse() {
            return this.fish.isGroupLeader() || fish.hasNoLeader() || fish.hasGroupLeader() && fish.groupLeader.circlePos != null;
        }

        public void tick() {
            if (fish.circleTime > fish.maxCircleTime) {
                fish.circleTime = 0;
                fish.circlePos = null;
            }
            if (fish.circlePos != null && fish.circleTime <= fish.maxCircleTime) {
                fish.circleTime++;
                Vec3 movePos = orbitAroundPos(fish.circlePos);
                fish.getNavigation().moveTo(movePos.x(), movePos.y(), movePos.z(), this.fish.circleSpeed);
            } else if (this.fish.isGroupLeader()) {
                if (fish.baitballCooldown == 0) {
                    fish.baitballCooldown = 100 + this.fish.random.nextInt(150);
                    if (fish.circlePos == null || fish.circleTime >= fish.maxCircleTime) {
                        fish.circleTime = 0;
                        fish.maxCircleTime = 100 + this.fish.random.nextInt(200);
                        circleDistance = 1 + this.fish.random.nextFloat() * 1.5F;
                        fish.circleSpeed = 0.75F + this.fish.random.nextFloat() * 0.5F;
                        clockwise = this.fish.random.nextBoolean();
                        fish.circlePos = findSwimToPos(3);
                    }
                }
            } else if (fish.random.nextInt(40) == 0 || fish.hasNoLeader()) {
                Vec3 result = Vec3.atCenterOf(findSwimToPos(6));
                fish.getNavigation().moveTo(result.x, result.y, result.z, 1.0F);
            } else if (fish.hasGroupLeader() && fish.groupLeader.circlePos != null) {
                if (fish.circlePos == null) {
                    fish.circlePos = fish.groupLeader.circlePos;
                    fish.circleTime = fish.groupLeader.circleTime;
                    fish.maxCircleTime = fish.groupLeader.maxCircleTime;
                    circleDistance = 1 + this.fish.random.nextFloat() * 1.5F;
                    clockwise = this.fish.random.nextBoolean();
                    fish.circleSpeed = 0.75F + this.fish.random.nextFloat() * 0.5F;
                }
            }
        }

        public BlockPos findSwimToPos(int range) {
            int fishY = fish.getBlockY();
            int surfaceY;
            int floorY;
            BlockPos.MutableBlockPos move = new BlockPos.MutableBlockPos();
            BlockPos base;
            move.set(fish.getX(), fish.getY() + 1, fish.getZ());
            while (move.getY() < level().getMaxBuildHeight() && level().getFluidState(move).is(FluidTags.WATER)) {
                move.move(0, 5, 0);
            }
            surfaceY = move.getY();
            move.set(fish.getX(), fish.getY() - 1, fish.getZ());
            while (move.getY() > level().getMinBuildHeight() && level().getFluidState(move).is(FluidTags.WATER)) {
                move.move(0, -5, 0);
            }
            floorY = move.getY();
            int oceanHeight = surfaceY - floorY;
            if (level().isNight()) {
                base = fish.blockPosition().atY(Mth.clamp(fishY, floorY + (int) (oceanHeight * 0.85F), surfaceY));
            } else {
                base = fish.blockPosition().atY(Mth.clamp(fishY, floorY + (int) (oceanHeight * 0.25F), surfaceY - (int) (oceanHeight * 0.85F)));
            }

            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = base.offset(random.nextInt(range) - range / 2, random.nextInt(range) - range / 2, random.nextInt(range) - range / 2);
                if (fish.level().getFluidState(blockPos).is(FluidTags.WATER) && blockPos.getY() > level().getMinBuildHeight() + 1) {
                    return blockPos;
                }
            }
            return base;
        }

        public Vec3 orbitAroundPos(BlockPos target) {
            final float prog = 1F - (fish.circleTime / (float) fish.maxCircleTime);
            final float angle = (0.0174532925F * 10 * this.fish.circleSpeed * (clockwise ? -fish.circleTime : fish.circleTime));
            final double extraX = (circleDistance * prog + 1.75F) * Mth.sin((angle));
            final double extraY = Math.sin(1F + fish.getId() * 0.2F + fish.circleTime * 0.2F);
            final double extraZ = (circleDistance * prog + 1.75F) * prog * Mth.cos(angle);
            return new Vec3(target.getX() + 0.5F + extraX, Math.max(target.getY() + 0.5F + extraY, -62), target.getZ() + 0.5F + extraZ);
        }
    }

    class JoinSchoolGoal extends Goal {
        private static final int INTERVAL_TICKS = 200;
        private final LanternfishEntity mob;
        private int timeToRecalcPath;
        private int nextStartTick;

        public JoinSchoolGoal(LanternfishEntity fish) {
            this.mob = fish;
            this.nextStartTick = this.nextStartTick(fish);
        }

        protected int nextStartTick(LanternfishEntity fish) {
            return reducedTickDelay(100 + fish.getRandom().nextInt(100) % 20);
        }

        public boolean canUse() {
            if (this.mob.isGroupLeader() || this.mob.isCircling()) {
                return false;
            } else if (this.mob.hasGroupLeader()) {
                return true;
            } else if (this.nextStartTick > 0) {
                --this.nextStartTick;
                return false;
            } else {
                this.nextStartTick = this.nextStartTick(this.mob);
                Predicate<LanternfishEntity> predicate = (p_25258_) -> {
                    return p_25258_.canGroupGrow() || !p_25258_.hasGroupLeader();
                };
                List<LanternfishEntity> list = this.mob.level().getEntitiesOfClass(LanternfishEntity.class, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), predicate);
                LanternfishEntity cc = DataFixUtils.orElse(list.stream().filter(LanternfishEntity::canGroupGrow).findAny(), this.mob);
                cc.createFromStream(list.stream().filter((p_25255_) -> {
                    return !p_25255_.hasGroupLeader();
                }));
                return this.mob.hasGroupLeader();
            }
        }

        public boolean canContinueToUse() {
            return this.mob.hasGroupLeader() && this.mob.inRangeOfGroupLeader() && !this.mob.isCircling();
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.mob.leaveGroup();
        }

        public void tick() {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                this.mob.moveToGroupLeader();
            }
        }
    }

    private class LanternfishMoveControl extends MoveControl {
        public LanternfishMoveControl() {
            super(LanternfishEntity.this);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO && mob.isInWaterOrBubble()) {
                final Vec3 vector3d = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
                final double d5 = vector3d.length();
                double maxDist = mob.getBoundingBox().getSize() > 1.0F ? 1.0F : mob.getBoundingBox().getSize();
                if (d5 < maxDist) {
                    this.operation = MoveControl.Operation.WAIT;
                    mob.setDeltaMovement(mob.getDeltaMovement().scale(0.85D));
                } else {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.02F / d5)));
                    final Vec3 vector3d1 = mob.getDeltaMovement();
                    float f = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * 180.0F / (float) Math.PI;
                    mob.setYRot(Mth.approachDegrees(mob.getYRot(), f, 10));
                    mob.yBodyRot = mob.getYRot();
                }
            }
        }

    }

}
