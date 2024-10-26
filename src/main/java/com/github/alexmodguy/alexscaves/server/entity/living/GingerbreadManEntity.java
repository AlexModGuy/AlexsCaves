package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.entity.util.TargetsDroppedItems;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class GingerbreadManEntity extends Monster implements IAnimatedEntity, IDancesToJukebox, PossessedByLicowitch, TargetsDroppedItems {

    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CARRYING_ITEM = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> FLEEING_FROM_UUID = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> POSSESSOR_LICOWITCH_ID = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> BARREL_POS = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> TEAM_COLOR = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> OVEN_SPAWNED = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LOST_LEFT_ARM = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LOST_RIGHT_ARM = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LOST_LEFT_LEG = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LOST_RIGHT_LEG = SynchedEntityData.defineId(GingerbreadManEntity.class, EntityDataSerializers.BOOLEAN);

    public static final Animation ANIMATION_IDLE_WAVE_LEFT = Animation.create(35);
    public static final Animation ANIMATION_IDLE_WAVE_RIGHT = Animation.create(35);
    public static final Animation ANIMATION_IDLE_FALL_OVER = Animation.create(50);
    public static final Animation ANIMATION_IDLE_JUMP = Animation.create(20);
    public static final Animation ANIMATION_SWING_RIGHT = Animation.create(15);
    public static final Animation ANIMATION_SWING_LEFT = Animation.create(15);

    public static final int MAX_VARIANTS = 8;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private int animationTick;
    private Animation currentAnimation;
    private float prevSitProgress;
    private float sitProgress;
    public float prevDanceProgress;
    public float danceProgress;
    public float prevCarryItemProgress;
    public float carryItemProgress;
    public BlockPos jukeboxPosition;
    private int sitFor = -100;
    private int despawnFromOvenCooldown = 2000;
    private int fleeFor = 0;
    private double lastStepX = 0;
    private double lastStepZ = 0;
    public int[] limbLostOrder = new int[4];

    public GingerbreadManEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
        if(this.getNavigation() instanceof GroundPathNavigation groundPathNavigation){
            groundPathNavigation.setCanOpenDoors(true);
            groundPathNavigation.setCanPassDoors(true);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GingerbreadManOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new SitGoal());
        this.goalSelector.addGoal(3, new GingerbreadManFleeGoal(this));
        this.goalSelector.addGoal(4, new GingerbreadManAttackGoal(this));
        this.goalSelector.addGoal(5, new GingerbreadManStealGoal(this));
        this.goalSelector.addGoal(6, new GingerbreadManStoreStolenItemsGoal(this));
        this.goalSelector.addGoal(7, new MobWanderThroughStructureGoal(this, 1.0D, 30, ACTagRegistry.GINGERBREAD_MEN_WANDER_THROUGH, 10, 7));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0D, 30, false));
        this.goalSelector.addGoal(9, new TemptGoal(this, 1.1D, Ingredient.of(Items.POTION), false));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new MobTargetItemGoal<>(this,  isOvenSpawned() ? 10 : 50, true, true, null, 5, 10, true));
        this.targetSelector.addGoal(2, new GingerbreadManTargetEverythingGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DANCING, false);
        this.entityData.define(SITTING, false);
        this.entityData.define(VARIANT, 0);
        this.entityData.define(CARRYING_ITEM, false);
        this.entityData.define(FLEEING_FROM_UUID, Optional.empty());
        this.entityData.define(POSSESSOR_LICOWITCH_ID, -1);
        this.entityData.define(BARREL_POS, Optional.empty());
        this.entityData.define(TEAM_COLOR, -1);
        this.entityData.define(OVEN_SPAWNED, false);
        this.entityData.define(LOST_LEFT_ARM, false);
        this.entityData.define(LOST_RIGHT_ARM, false);
        this.entityData.define(LOST_RIGHT_LEG, false);
        this.entityData.define(LOST_LEFT_LEG, false);
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return levelReader.getBlockState(pos.below()).is(ACBlockRegistry.GINGERBREAD_BRICKS.get()) ? 10.0F : super.getWalkTargetValue(pos, levelReader);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.45D).add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    public static boolean checkGingerbreadManSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }

    public void tick() {
        super.tick();
        this.prevDanceProgress = danceProgress;
        this.prevSitProgress = sitProgress;
        this.prevCarryItemProgress = carryItemProgress;
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 15) || !this.level().getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.setDancing(false);
            this.jukeboxPosition = null;
        }
        if (isDancing() && danceProgress < 5F) {
            danceProgress++;
        }
        if (!isDancing() && danceProgress > 0F) {
            danceProgress--;
        }
        if (isSitting() && sitProgress < 10F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (isCarryingItem() && carryItemProgress < 10F) {
            carryItemProgress++;
        }
        if (!isCarryingItem() && carryItemProgress > 0F) {
            carryItemProgress--;
        }
        if (!level().isClientSide) {
            if (isStillEnough() && !isCarryingItem() && random.nextInt(150) == 0 && this.getAnimation() == NO_ANIMATION && !this.isDancing()) {
                if (random.nextInt(3) == 0 && sitFor == 0) {
                    sitFor = 100 + random.nextInt(80);
                } else {
                    Animation idle;
                    float rand = random.nextFloat();
                    if (!this.isSitting()) {
                        if (rand < 0.25F) {
                            idle = ANIMATION_IDLE_JUMP;
                        } else if (rand < 0.5F) {
                            idle = ANIMATION_IDLE_FALL_OVER;
                        } else {
                            idle = rand < 0.75F ? ANIMATION_IDLE_WAVE_LEFT : ANIMATION_IDLE_WAVE_RIGHT;
                        }
                    } else {
                        idle = rand < 0.5F ? ANIMATION_IDLE_WAVE_LEFT : ANIMATION_IDLE_WAVE_RIGHT;
                    }
                    this.setAnimation(idle);
                }
            }
            if(this.isCarryingItem() && this.getItemInHand(InteractionHand.OFF_HAND).isEmpty()){
                this.setCarryingItem(false);
            }
        }else{
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        }
        if (this.getAnimation() == ANIMATION_IDLE_JUMP && this.onGround() && this.getAnimationTick() == 5) {
            this.jumpFromGround();
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (sitFor > 0) {
            this.setSitting(true);
            sitFor--;
        } else {
            this.setSitting(false);
            if (sitFor < 0) {
                sitFor++;
            }
        }
        if(fleeFor > 0){
            fleeFor--;
            if(fleeFor == 0){
                this.setFleeingFromUUID(null);
            }
        }
        if(this.isOvenSpawned()){
            if(despawnFromOvenCooldown-- < 0){
                this.kill();
                this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND));
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                this.spawnAtLocation(this.getItemInHand(InteractionHand.OFF_HAND));
                this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }
        lastStepX = this.xo;
        lastStepZ = this.zo;
    }

    @Nullable
    public UUID getFleeingFromUUID() {
        return this.entityData.get(FLEEING_FROM_UUID).orElse(null);
    }

    public void setFleeingFromUUID(@Nullable UUID uniqueId) {
        this.entityData.set(FLEEING_FROM_UUID, Optional.ofNullable(uniqueId));
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public void stopSittingForAWhile() {
        sitFor = -100;
    }

    private boolean isStillEnough() {
        return this.getDeltaMovement().horizontalDistance() < 0.05;
    }

    @Override
    public void setJukeboxPos(BlockPos blockPos) {
        this.jukeboxPosition = blockPos;
    }

    public boolean isMovementBlocked() {
        return this.isSitting() || this.isDancing() || this.getAnimation() == ANIMATION_IDLE_FALL_OVER || this.getAnimation() == ANIMATION_IDLE_JUMP;
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean bool) {
        this.entityData.set(DANCING, bool);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean bool) {
        this.entityData.set(SITTING, bool);
    }

    public BlockPos getLastBarrelPos() {
        return this.entityData.get(BARREL_POS).orElse(null);
    }

    public void setLastBarrelPos(BlockPos lastAltarPos) {
        this.entityData.set(BARREL_POS, Optional.ofNullable(lastAltarPos));
    }
    public boolean isCarryingItem() {
        return this.entityData.get(CARRYING_ITEM);
    }

    public void setCarryingItem(boolean bool) {
        this.entityData.set(CARRYING_ITEM, bool);
    }

    public boolean hasLostLimb(boolean left, boolean arm) {
        return this.entityData.get(arm ? left ? LOST_LEFT_ARM : LOST_RIGHT_ARM : left ? LOST_LEFT_LEG : LOST_RIGHT_LEG);
    }

    public void setLostLimb(boolean left, boolean arm, boolean lost) {
        this.entityData.set(arm ? left ? LOST_LEFT_ARM : LOST_RIGHT_ARM : left ? LOST_LEFT_LEG : LOST_RIGHT_LEG, lost);
    }

    public boolean hasBothLegs(){
        return !hasLostLimb(true, false) && !hasLostLimb(false, false);
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.1F;
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }

    public float getCarryItemProgress(float partialTicks) {
        return (prevCarryItemProgress + (carryItemProgress - prevCarryItemProgress) * partialTicks) * 0.1F;
    }


    public int getGingerbreadTeamColor() {
        return this.entityData.get(TEAM_COLOR);
    }

    public void setGingerbreadTeamColor(int color) {
        this.entityData.set(TEAM_COLOR, color);
    }
    public boolean isOvenSpawned() {
        return this.entityData.get(OVEN_SPAWNED);
    }

    public void setOvenSpawned(boolean bool) {
        this.entityData.set(OVEN_SPAWNED, bool);
    }

    public void setDespawnFromOvenCooldown(int cooldown){
        this.despawnFromOvenCooldown = cooldown;
    }

    public void fleeFromFor(Entity entity, int fleeFor) {
        this.setFleeingFromUUID(entity.getUUID());
        this.fleeFor = fleeFor;
    }

    public int getFleeFor() {
        return fleeFor;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.lastStepX, 0, this.getZ() - this.lastStepZ);
        float f2 = Math.min(f1 * 10.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_IDLE_WAVE_LEFT, ANIMATION_IDLE_WAVE_RIGHT, ANIMATION_IDLE_FALL_OVER, ANIMATION_IDLE_JUMP, ANIMATION_SWING_RIGHT};
    }


    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setVariant(random.nextInt(MAX_VARIANTS + 1));
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("CarryingItem", this.isCarryingItem());
        compound.putBoolean("OvenSpawned", this.isOvenSpawned());
        compound.putInt("GingerbreadTeamColor", this.getGingerbreadTeamColor());
        compound.putInt("OvenDespawnCooldown", this.despawnFromOvenCooldown);
        compound.putBoolean("LostLeftLeg", this.hasLostLimb(true, false));
        compound.putBoolean("LostRightLeg", this.hasLostLimb(false, false));
        compound.putBoolean("LostLeftArm", this.hasLostLimb(true, true));
        compound.putBoolean("LostRightArm", this.hasLostLimb(false, true));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setCarryingItem(compound.getBoolean("CarryingItem"));
        this.setOvenSpawned(compound.getBoolean("OvenSpawned"));
        this.setGingerbreadTeamColor(compound.getInt("GingerbreadTeamColor"));
        this.despawnFromOvenCooldown = compound.getInt("OvenDespawnCooldown");
        this.setLostLimb(true, false, compound.getBoolean("LostLeftLeg"));
        this.setLostLimb(false, false, compound.getBoolean("LostRightLeg"));
        this.setLostLimb(true, true, compound.getBoolean("LostLeftArm"));
        this.setLostLimb(false, true, compound.getBoolean("LostRightArm"));
    }

    @Override
    public void setPossessedByLicowitchId(int entityId) {
        this.entityData.set(POSSESSOR_LICOWITCH_ID, entityId);
    }

    @Override
    public int getPossessedByLicowitchId() {
        return this.entityData.get(POSSESSOR_LICOWITCH_ID);
    }

    @Override
    public boolean canAttack(LivingEntity living) {
        if(this.getPossessedByLicowitchId() != -1){
            LicowitchEntity licowitch = this.getPossessingLicowitch(this.level());
            if(licowitch != null && licowitch.isFriendlyFire(living)){
                return false;
            }
        }
        return super.canAttack(living);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    public Animation getAnimationForHand(boolean reverse){
        return this.isLeftHanded() ? reverse ? GingerbreadManEntity.ANIMATION_SWING_RIGHT : GingerbreadManEntity.ANIMATION_SWING_LEFT : reverse ? GingerbreadManEntity.ANIMATION_SWING_LEFT : GingerbreadManEntity.ANIMATION_SWING_RIGHT;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(getAnimationForHand(false));
        }
        this.take(e, 1);
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        this.setItemInHand(InteractionHand.OFF_HAND, duplicate);
        e.getItem().shrink(1);
        this.setCarryingItem(true);
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GINGERBREAD_MAN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GINGERBREAD_MAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GINGERBREAD_MAN_DEATH.get();
    }

    public boolean hurt(DamageSource damageSource, float damageAmount) {
        boolean prev = super.hurt(damageSource, damageAmount);
        if (prev && damageAmount >= 1.0) {
            if((hasBothLegs() || this.getHealth() <= 0.0) && random.nextBoolean()){
                this.setLostLimb(random.nextBoolean(), false, true);
            }else if(random.nextInt(2) == 0){
                this.setLostLimb(random.nextBoolean(), true, true);
            }
        }
        return prev;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return (isOvenSpawned() || stack.is(ACTagRegistry.GINGERBREAD_MAN_STEALS)) && !hasLostLimb(this.isLeftHanded(), true);
    }

    @Override
    protected boolean shouldDropLoot() {
        return super.shouldDropLoot() && getPossessedByLicowitchId() == -1;
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean b) {
        if(!this.isOvenSpawned()){
            super.dropFromLootTable(damageSource, b);
        }
    }

    protected void dropExperience() {
        if(!this.isOvenSpawned()){
            super.dropExperience();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (LOST_LEFT_ARM.equals(dataAccessor)) {
            this.onLoseArm(true, true);
        }
        if (LOST_RIGHT_ARM.equals(dataAccessor)) {
            this.onLoseArm(false, true);
        }
        if (LOST_LEFT_LEG.equals(dataAccessor)) {
            this.onLoseArm(true, false);
        }
        if (LOST_RIGHT_LEG.equals(dataAccessor)) {
            this.onLoseArm(false, false);
        }
    }

    private void onLoseArm(boolean left, boolean arm) {
        Vec3 vec3 = arm ? new Vec3(left ? 0.3F : -0.3F, this.getBbHeight() * 0.8F, 0) : new Vec3(left ? 0.2F : -0.2F, this.getBbHeight() * 0.3F, 0);
        Vec3 vec31 = vec3.yRot((float) -Math.toRadians(this.yBodyRot)).add(this.position());
        ItemParticleOption itemParticleOption = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItemRegistry.GINGERBREAD_CRUMBS.get()));
        for(int i = 0; i < 5; i++){
            this.level().addParticle(itemParticleOption, vec31.x, vec31.y, vec31.z, ((double) this.random.nextFloat() - 0.5D) * 0.2D, ((double) this.random.nextFloat() - 0.5D) * 0.2D, ((double) this.random.nextFloat() - 0.5D) * 0.2D);
        }
        if(!level().isClientSide){
            if(!isOvenSpawned() && shouldDropLoot() && random.nextInt(2) == 0 && this.isAlive()){
                this.spawnAtLocation(new ItemStack(ACItemRegistry.GINGERBREAD_CRUMBS.get()));
            }
            if(arm){
                if(this.isLeftHanded() == left){
                    this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND));
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
                if(this.isLeftHanded() != left){
                    this.spawnAtLocation(this.getItemInHand(InteractionHand.OFF_HAND));
                    this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
    }

    private class SitGoal extends Goal {

        public SitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canContinueToUse() {
            return GingerbreadManEntity.this.isMovementBlocked();
        }

        public boolean canUse() {
            if (GingerbreadManEntity.this.isInWaterOrBubble()) {
                return false;
            } else {
                return GingerbreadManEntity.this.isMovementBlocked();
            }
        }

        public void start() {
            GingerbreadManEntity.this.getNavigation().stop();
        }
    }
}
