package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.NotLavaSwimNodeEvaluator;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class RadgillEntity extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(RadgillEntity.class, EntityDataSerializers.BOOLEAN);
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch = 0;
    private float prevFishPitch = 0;
    private boolean wasJustInAcid = false;

    public RadgillEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new AcidMoveControl();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 8.0D);
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level) {

            protected PathFinder createPathFinder(int p_26598_) {
                this.nodeEvaluator = new NotLavaSwimNodeEvaluator(true);
                return new PathFinder(this.nodeEvaluator, p_26598_);
            }

            public boolean isInLiquid() {
                return RadgillEntity.this.isInLiquid();
            }
        };
    }


    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public boolean isMaxGroupSizeReached(int i) {
        return false;
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

    protected int calculateFallDamage(float f, float f1) {
        return super.calculateFallDamage(f, f1) - 5;
    }

    public static boolean checkRadgillSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return spawnType == MobSpawnType.SPAWNER || !level.getFluidState(pos).isEmpty() && level.getFluidState(pos).getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get();
    }

    @Override
    public void tick() {
        super.tick();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        boolean grounded = this.onGround() && !isInLiquid();
        if (grounded && landProgress < 5F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0F) {
            landProgress--;
        }
        fishPitch = Mth.clamp((float) this.getDeltaMovement().y * 1.8F, -1.0F, 1.0F) * -(float) (180F / (float) Math.PI);
        boolean inAcid = this.isInAcid();
        if (inAcid != wasJustInAcid) {
            for (int i = 0; i < 5 + random.nextInt(5); i++) {
                level().addParticle(ACParticleRegistry.RADGILL_SPLASH.get(), this.getRandomX(0.8F), this.getBoundingBox().minY + 0.1F, this.getRandomZ(0.8F), (random.nextDouble() - 0.5F) * 0.3F, 0.1F + random.nextFloat() * 0.3F, (random.nextDouble() - 0.5F) * 0.3F);
            }
            wasJustInAcid = inAcid;
        }
        if (!isInLiquid() && this.isAlive()) {
            if (this.onGround() && random.nextFloat() < 0.1F) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(ACSoundRegistry.RADGILL_FLOP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    private boolean isInLiquid() {
        return this.isInWaterOrBubble() || this.isInAcid();
    }

    protected void handleAirSupply(int prevAir) {
        if (this.isAlive() && !isInLiquid()) {
            this.setAirSupply(prevAir - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(500);
        }
    }

    private boolean isInAcid() {
        return this.getFluidTypeHeight(ACFluidRegistry.ACID_FLUID_TYPE.get()) > 0;
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
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!type.consumesAction()) {
            if (itemstack.getItem() == ACItemRegistry.ACID_BUCKET.get() && this.isAlive()) {
                this.playSound(this.getPickupSound(), 1.0F, 1.0F);
                ItemStack itemstack1 = this.getBucketItemStack();
                this.saveToBucketTag(itemstack1);
                ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);
                player.setItemInHand(hand, itemstack2);
                if (!level().isClientSide) {
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack1);
                }
                this.discard();
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return type;
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
        ItemStack stack = new ItemStack(ACItemRegistry.RADGILL_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.RADGILL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RADGILL_HURT.get();
    }

    private class WanderGoal extends Goal {

        private BlockPos target;
        private boolean isJump;
        private boolean hasJumped;
        private int timeout = 0;

        private WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private boolean isLiquidAt(BlockPos pos) {
            FluidState state = RadgillEntity.this.level().getFluidState(pos);
            return state.is(FluidTags.WATER) || state.getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get();
        }

        private BlockPos findMoveToPos(boolean jump) {
            BlockPos fishPos = RadgillEntity.this.blockPosition();
            for (int i = 0; i < 15; i++) {
                BlockPos offset = fishPos.offset(RadgillEntity.this.random.nextInt(16) - 8, 0, RadgillEntity.this.random.nextInt(16) - 8);
                while (isLiquidAt(offset) && offset.getY() < level().getMaxBuildHeight()) {
                    offset = offset.above();
                }
                if (!isLiquidAt(offset) && isLiquidAt(offset.below())) {
                    BlockPos surface = offset.below();
                    if (jump) {
                        surface = surface.above(2 + random.nextInt(2));
                        return surface;
                    } else {
                        surface = surface.below(1 + random.nextInt(4));
                        return isLiquidAt(surface) ? surface : null;
                    }
                }
            }
            return null;
        }

        @Override
        public boolean canUse() {
            if (!RadgillEntity.this.isInLiquid()) {
                return false;
            } else if (RadgillEntity.this.getRandom().nextInt(10) == 0) {
                boolean jump = random.nextFloat() <= 0.4F;
                BlockPos found = findMoveToPos(jump);
                if (found != null) {
                    isJump = jump;
                    target = found;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return (RadgillEntity.this.isInLiquid() && !hasJumped || isJump) && RadgillEntity.this.distanceToSqr(Vec3.atCenterOf(target)) < 3 && timeout < 200;
        }

        public void stop() {
            hasJumped = false;
            timeout = 0;
        }

        @Override
        public void tick() {
            timeout++;
            RadgillEntity.this.getNavigation().moveTo(target.getX() + 0.5F, target.getY() + 0.25F, target.getZ() + 0.5F, 1.0D);
            double horizDistance = RadgillEntity.this.distanceToSqr(target.getX() + 0.5F, RadgillEntity.this.getY(), target.getZ() + 0.5F);
            if (horizDistance < 16 && isJump && !hasJumped) {
                Vec3 targetVec = Vec3.atCenterOf(target);
                Vec3 vec3 = targetVec.subtract(RadgillEntity.this.position());
                if (vec3.length() < 1.0F) {
                    vec3 = Vec3.ZERO;
                } else {
                    vec3 = vec3.normalize();
                }
                Vec3 vec31 = new Vec3(vec3.x * 0.8F, 0.75F + random.nextFloat() * 0.3F, vec3.y * 0.8F);
                RadgillEntity.this.setDeltaMovement(vec31);
                if (RadgillEntity.this.getY() > target.getY()) {
                    hasJumped = true;
                } else {
                    RadgillEntity.this.lookAt(EntityAnchorArgument.Anchor.EYES, targetVec);
                }
            }
        }
    }

    class AcidMoveControl extends MoveControl {

        public AcidMoveControl() {
            super(RadgillEntity.this);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO && RadgillEntity.this.isInLiquid()) {
                final Vec3 vector3d = new Vec3(this.wantedX - RadgillEntity.this.getX(), this.wantedY - RadgillEntity.this.getY(), this.wantedZ - RadgillEntity.this.getZ());
                final double d5 = vector3d.length();
                if (d5 < RadgillEntity.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    RadgillEntity.this.setDeltaMovement(RadgillEntity.this.getDeltaMovement().scale(0.5D));
                } else {
                    RadgillEntity.this.setDeltaMovement(RadgillEntity.this.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.06D / d5)));
                    final Vec3 vector3d1 = RadgillEntity.this.getDeltaMovement();
                    float f = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * 180.0F / (float) Math.PI;
                    RadgillEntity.this.setYRot(Mth.approachDegrees(RadgillEntity.this.getYRot(), f, 20));
                    RadgillEntity.this.yBodyRot = RadgillEntity.this.getYRot();
                }
            }
        }
    }
}
