package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;

public class SeaPigEntity extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SeaPigEntity.class, EntityDataSerializers.BOOLEAN);
    private float digestProgress;
    private float prevDigestProgress;

    private float squishProgress;
    private float prevSquishProgress;
    public static final ResourceLocation DIGESTION_LOOT_TABLE = ResourceLocation.fromNamespaceAndPath("alexscaves", "gameplay/sea_pig_digestion");

    public SeaPigEntity(EntityType entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.75F, 0.5F, false);
        this.setAirSupply(40);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.05D).add(Attributes.MAX_HEALTH, 8.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WanderGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public static boolean checkSeaPigSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() < level.getSeaLevel() - 25;
    }


    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getFluidState(pos.below()).isEmpty() && worldIn.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : 0.0F;
    }

    public void tick() {
        super.tick();
        prevDigestProgress = digestProgress;
        prevSquishProgress = squishProgress;
        if (isDigesting() && digestProgress < 1.0F) {
            if(digestProgress == 0.0F){
                this.playSound(ACSoundRegistry.SEA_PIG_EAT.get());
            }
            digestProgress += 0.05F;
            if (digestProgress >= 1.0F) {
                digestProgress = 0;
                prevDigestProgress = 0;
                this.digestItem();
            }
        }
        boolean grounded = this.onGround() && !isInWaterOrBubble();
        if (grounded && squishProgress < 5F) {
            squishProgress++;
        }
        if (!grounded && squishProgress > 0F) {
            squishProgress--;
        }
    }


    protected void playSwimSound(float f) {

    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    protected void handleAirSupply(int prevAir) {
        if (this.isAlive() && !isInWaterOrBubble()) {
            this.setAirSupply(prevAir - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(40);
        }
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            delta = delta.scale(0.8D);
            if (this.jumping || horizontalCollision && level().getBlockState(this.blockPosition().above()).getFluidState().is(FluidTags.WATER)) {
                delta = delta.add(0, 0.03F, 0);
            } else {
                delta = delta.add(0, -0.03F, 0);
            }
            this.setDeltaMovement(delta.scale(0.8D));
        } else {
            super.travel(travelVector);
        }
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


    private void digestItem() {
        if (!level().isClientSide) {
            LootTable loottable = level().getServer().getLootData().getLootTable(DIGESTION_LOOT_TABLE);
            List<ItemStack> items = loottable.getRandomItems((new LootParams.Builder((ServerLevel) this.level())).withParameter(LootContextParams.THIS_ENTITY, this).create(LootContextParamSets.PIGLIN_BARTER));
            items.forEach(this::spawnAtLocation);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        final InteractionResult type = Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
        if (itemstack.is(ACTagRegistry.SEA_PIG_DIGESTS) && !this.isDigesting() && !type.consumesAction()) {
            ItemStack copy = itemstack.copy();
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            copy.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, copy);
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public float getDigestProgress(float partialTick) {
        return Math.min(1.0F, (prevDigestProgress + (digestProgress - prevDigestProgress) * partialTick));
    }

    public float getSquishProgress(float partialTicks) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTicks) * 0.2F;
    }

    public boolean isDigesting() {
        return this.getItemInHand(InteractionHand.MAIN_HAND).is(ACTagRegistry.SEA_PIG_DIGESTS);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 128.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);

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


    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("FishBucketTag")) {
            this.readAdditionalSaveData(compound.getCompound("FishBucketTag"));
        }
        this.setAirSupply(2000);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ACItemRegistry.SEA_PIG_BUCKET.get());
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.SEA_PIG_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.SEA_PIG_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SEA_PIG_DEATH.get();
    }


    private class WanderGoal extends Goal {

        private double x;
        private double y;
        private double z;

        public WanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (SeaPigEntity.this.getRandom().nextInt(50) != 0) {
                return false;
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
            double dist = SeaPigEntity.this.distanceToSqr(x, y, z);
            return dist > 4F;
        }

        public void tick() {
            SeaPigEntity.this.getNavigation().moveTo(this.x, this.y, this.z, 1F);
        }

        public BlockPos findWaterBlock() {
            BlockPos result = null;
            RandomSource random = SeaPigEntity.this.getRandom();
            int range = 10;
            for (int i = 0; i < 15; i++) {
                BlockPos blockPos = SeaPigEntity.this.blockPosition().offset(random.nextInt(range) - range / 2, random.nextInt(range) - range / 2, random.nextInt(range) - range / 2);
                if (SeaPigEntity.this.level().getFluidState(blockPos).is(FluidTags.WATER) && blockPos.getY() > level().getMinBuildHeight() + 1) {
                    result = blockPos;
                }
            }
            return result;
        }

        @javax.annotation.Nullable
        protected Vec3 getPosition() {
            BlockPos water = findWaterBlock();
            if (SeaPigEntity.this.isInWaterOrBubble()) {
                if (water == null) {
                    return null;
                }
                while (SeaPigEntity.this.level().getFluidState(water.below()).is(FluidTags.WATER) && water.getY() > level().getMinBuildHeight() + 1) {
                    water = water.below();
                }
                water = water.above();
                return Vec3.atCenterOf(water);
            } else {
                return water == null ? DefaultRandomPos.getPos(SeaPigEntity.this, 7, 3) : Vec3.atCenterOf(water);

            }
        }
    }

}
