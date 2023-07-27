package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.SemiAquaticPathNavigator;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class TrilocarisEntity extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(TrilocarisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(TrilocarisEntity.class, EntityDataSerializers.INT);
    private float groundProgress;
    private float prevGroundProgress;
    private float biteProgress;
    private float prevBiteProgress;
    private int timeSwimming = 0;
    public boolean crawling;

    public TrilocarisEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 1F, 0.65F, false);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeGoal());
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    private static boolean isInCave(ServerLevelAccessor iServerWorld, BlockPos pos) {
        while (iServerWorld.getFluidState(pos).is(FluidTags.WATER)) {
            pos = pos.above();
        }
        return !iServerWorld.canSeeSky(pos) && pos.getY() < iServerWorld.getSeaLevel();
    }

    public static boolean checkTrilocarisSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        FluidState fluidState = level.getFluidState(pos);
        return fluidState.is(FluidTags.WATER) && fluidState.getAmount() >= 8 && isInCave(level, pos);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SemiAquaticPathNavigator(this, worldIn);
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
    }

    protected void handleAirSupply(int air) {

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(ATTACK_TICK, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }

    public void tick() {
        super.tick();
        prevGroundProgress = groundProgress;
        prevBiteProgress = biteProgress;
        if (this.onGround() && groundProgress < 5.0F) {
            groundProgress++;
        }
        if (!this.onGround() && groundProgress > 0.0F) {
            groundProgress--;
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (biteProgress < 5F) {
                biteProgress++;
            }
        } else {
            if (biteProgress > 4 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 1.3D) {
                this.getTarget().hurt(damageSources().mobAttack(this), 2);
            }
            if (biteProgress > 0F) {
                biteProgress--;
            }
        }
        if (!level().isClientSide) {
            if (crawling || !this.isInWaterOrBubble()) {
                timeSwimming = 0;
            } else {
                timeSwimming++;
            }
        }
    }

    public float getGroundProgress(float partialTick) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTick) * 0.2F;
    }


    public float getBiteProgress(float partialTick) {
        return (prevBiteProgress + (biteProgress - prevBiteProgress) * partialTick) * 0.2F;
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            boolean pulldown = false;
            if (crawling) {
                delta = delta.scale(0.8D);
                if (this.jumping || horizontalCollision) {
                    delta = delta.add(0, 0.1F, 0);
                } else {
                    delta = delta.add(0, -0.05F, 0);
                }
            }
            this.setDeltaMovement(delta.scale(0.8D));
        } else {
            super.travel(travelVector);
        }
    }

    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(ACItemRegistry.TRILOCARIS_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean bucket) {
        this.entityData.set(FROM_BUCKET, bucket);
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double dist) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public boolean doHurtTarget(Entity entityIn) {
        this.entityData.set(ATTACK_TICK, 5);
        return super.doHurtTarget(entityIn);
    }


    public void calculateEntityAnimation(boolean flying) {
        float speedMod = !this.onGround() ? 4.0F : 16.0F;
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * speedMod, 1.0F);
        this.walkAnimation.update(f2, 0.4F);

    }

    private class WanderGoal extends Goal {

        private double x;
        private double y;
        private double z;
        private boolean isCrawling;

        public WanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (TrilocarisEntity.this.getRandom().nextInt(20) != 0 && TrilocarisEntity.this.crawling) {
                return false;
            }
            if (TrilocarisEntity.this.crawling) {
                this.isCrawling = TrilocarisEntity.this.getRandom().nextFloat() < 0.5F;
            } else {
                this.isCrawling = TrilocarisEntity.this.timeSwimming > 300 || TrilocarisEntity.this.getRandom().nextFloat() < 0.15F;
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
            double dist = TrilocarisEntity.this.distanceToSqr(x, y, z);
            return dist > 4F;
        }

        public void tick() {
            TrilocarisEntity.this.crawling = isCrawling;
            TrilocarisEntity.this.getNavigation().moveTo(this.x, this.y, this.z, 1F);
        }

        public BlockPos findWaterBlock() {
            BlockPos result = null;
            RandomSource random = TrilocarisEntity.this.getRandom();
            int range = 10;
            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = TrilocarisEntity.this.blockPosition().offset(random.nextInt(range) - range / 2, random.nextInt(range) - range / 2, random.nextInt(range) - range / 2);
                if (TrilocarisEntity.this.level().getFluidState(blockPos).is(FluidTags.WATER) && blockPos.getY() > level().getMinBuildHeight() + 1) {
                    result = blockPos;
                }
            }
            return result;
        }

        @Nullable
        protected Vec3 getPosition() {
            BlockPos water = findWaterBlock();
            if (TrilocarisEntity.this.isInWaterOrBubble()) {
                if (water == null) {
                    return null;
                }
                if (this.isCrawling) {
                    while (TrilocarisEntity.this.level().getFluidState(water.below()).is(FluidTags.WATER) && water.getY() > level().getMinBuildHeight() + 1) {
                        water = water.below();
                    }
                    water = water.above();
                }
                return Vec3.atCenterOf(water);
            } else {
                return water == null ? DefaultRandomPos.getPos(TrilocarisEntity.this, 7, 3) : Vec3.atCenterOf(water);

            }
        }
    }

    private class MeleeGoal extends Goal {

        private int duration = 0;
        private int cooldown = 0;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return TrilocarisEntity.this.getTarget() != null && TrilocarisEntity.this.getTarget().isAlive() && duration < 300;
        }

        public void tick() {
            duration++;
            LivingEntity target = TrilocarisEntity.this.getTarget();
            if (target != null && target.isAlive()) {
                if (target.isInWaterOrBubble()) {
                    TrilocarisEntity.this.getNavigation().moveTo(target, 1F);
                }
                if (TrilocarisEntity.this.distanceTo(target) < 1.2F && cooldown == 0) {
                    TrilocarisEntity.this.doHurtTarget(target);
                    cooldown = 30;
                }
            }
            if (cooldown > 0) {
                cooldown--;
            }
        }

        public void stop() {
            duration = 0;
            cooldown = 0;
            TrilocarisEntity.this.setLastHurtByMob(null);
            TrilocarisEntity.this.setTarget(null);
        }
    }
}
