package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.NotLavaSwimNodeEvaluator;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.entity.util.HasGummyColors;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class SweetishFishEntity extends WaterAnimal implements Bucketable, HasGummyColors {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SweetishFishEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<GummyColors> GUMMY_COLOR = SynchedEntityData.defineId(SweetishFishEntity.class, ACEntityDataRegistry.GUMMY_COLOR.get());
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch = 0;
    private float prevFishPitch = 0;

    public SweetishFishEntity(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.7F, 10);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(GUMMY_COLOR, GummyColors.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 4.0D);
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level) {

            protected PathFinder createPathFinder(int p_26598_) {
                this.nodeEvaluator = new NotLavaSwimNodeEvaluator(true);
                return new PathFinder(this.nodeEvaluator, p_26598_);
            }

            public boolean isInLiquid() {
                return SweetishFishEntity.this.isInLiquid();
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

    public static boolean checkSweetishFishSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return spawnType == MobSpawnType.SPAWNER || !level.getFluidState(pos).isEmpty() && level.getFluidState(pos).getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get();
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
        compound.putInt("GummyColor", this.getGummyColor().ordinal());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setGummyColor(GummyColors.fromOrdinal(compound.getInt("GummyColor")));
    }

    private boolean isInLiquid() {
        return this.isInWaterOrBubble() || this.isInSoda();
    }

    private boolean isInSoda() {
        return this.getFluidTypeHeight(ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()) > 0;
    }

    protected void handleAirSupply(int prevAir) {
        if (this.isAlive() && !isInLiquid()) {
            this.setAirSupply(prevAir - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(400);
        }
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
        this.setAirSupply(400);
    }

    @Override
    public ItemStack getBucketItemStack() {
        ItemStack stack;
        switch (this.getGummyColor()){
            case RED:
                stack = new ItemStack(ACItemRegistry.SWEETISH_FISH_RED_BUCKET.get());
                break;
            case GREEN:
                stack = new ItemStack(ACItemRegistry.SWEETISH_FISH_GREEN_BUCKET.get());
                break;
            case YELLOW:
                stack = new ItemStack(ACItemRegistry.SWEETISH_FISH_YELLOW_BUCKET.get());
                break;
            case BLUE:
                stack = new ItemStack(ACItemRegistry.SWEETISH_FISH_BLUE_BUCKET.get());
                break;
            default:
                stack = new ItemStack(ACItemRegistry.SWEETISH_FISH_PINK_BUCKET.get());
                break;
        }
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!type.consumesAction()) {
            if (itemstack.getItem() == ACItemRegistry.PURPLE_SODA_BUCKET.get() && this.isAlive()) {
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

    public void tick(){
        super.tick();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        boolean grounded = !isInLiquid();
        if (grounded && landProgress < 5F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0F) {
            landProgress--;
        }
        if (grounded && this.isAlive()) {
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(ACSoundRegistry.SWEETISH_FISH_FLOP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
        float pitchTarget = (float) this.getDeltaMovement().y * 3F;
        if(grounded){
            pitchTarget = 0;
        }
        fishPitch = Mth.approachDegrees(fishPitch, Mth.clamp(pitchTarget, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI), 5);
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    public GummyColors getGummyColor() {
        return this.entityData.get(GUMMY_COLOR);
    }

    public void setGummyColor(GummyColors color) {
        this.entityData.set(GUMMY_COLOR, color);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInLiquid()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            if(!this.onGround()){
                delta = delta.add(0.0D, -0.009D, 0.0D);
            }
            this.setDeltaMovement(delta.scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setGummyColor(GummyColors.getRandom(random, true));
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.SWEETISH_FISH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SWEETISH_FISH_HURT.get();
    }

    private class WanderGoal extends Goal {

        private BlockPos target;
        private int timeout = 0;

        private WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private boolean isLiquidAt(BlockPos pos) {
            FluidState state = SweetishFishEntity.this.level().getFluidState(pos);
            return state.is(FluidTags.WATER) || state.getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get();
        }

        private BlockPos findMoveToPos() {
            BlockPos fishPos = SweetishFishEntity.this.blockPosition();
            for (int i = 0; i < 15; i++) {
                BlockPos offset = fishPos.offset(SweetishFishEntity.this.random.nextInt(10) - 5, 0, SweetishFishEntity.this.random.nextInt(10) - 5);
                while (isLiquidAt(offset) && offset.getY() < level().getMaxBuildHeight()) {
                    offset = offset.above();
                }
                if (!isLiquidAt(offset) && isLiquidAt(offset.below())) {
                    BlockPos surface = offset.below();
                    surface = surface.below(random.nextInt(4));
                    return isLiquidAt(surface) ? surface : null;
                }
            }
            return null;
        }

        @Override
        public boolean canUse() {
            if (!SweetishFishEntity.this.isInLiquid()) {
                return false;
            } else if(SweetishFishEntity.this.random.nextInt(4) == 0){
                BlockPos found = findMoveToPos();
                if (found != null) {
                    target = found;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return SweetishFishEntity.this.isInLiquid() && !SweetishFishEntity.this.navigation.isDone() && timeout < 40;
        }

        public void stop() {
            timeout = 0;
        }

        public void start() {
            timeout = 0;
            SweetishFishEntity.this.getNavigation().moveTo(target.getX() + 0.5F, target.getY() + 0.25F, target.getZ() + 0.5F, 1.0D);
        }

        @Override
        public void tick() {
            timeout++;
        }
    }
}
