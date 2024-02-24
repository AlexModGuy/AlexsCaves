package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexmodguy.alexscaves.server.entity.util.UnderzealotSacrifice;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class UnderzealotEntity extends Monster implements PackAnimal, IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> BURIED = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CARRYING = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PRAYING = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> SACRIFICE_POS = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockPos>> PARTICLE_POS = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> WORSHIP_TIME = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.INT);
    public static final Animation ANIMATION_ATTACK_0 = Animation.create(15);
    public static final Animation ANIMATION_ATTACK_1 = Animation.create(15);
    public static final Animation ANIMATION_BREAKTORCH = Animation.create(15);
    public static final int MAX_WORSHIP_TIME = 500;
    public int sacrificeCooldown;
    public int cloudCooldown;
    private Animation currentAnimation;
    private int animationTick;
    private float buriedProgress;
    private float prevBuriedProgress;
    private float carryingProgress;
    private float prevCarryingProgress;
    private float prayingProgress;
    private float prevPrayingProgress;
    private UnderzealotEntity priorPackMember;
    private UnderzealotEntity afterPackMember;
    private int idleBuryIn = 400 + random.nextInt(800);
    private int reemergeTime = 0;
    private BlockPos remergePos = null;

    public UnderzealotEntity(EntityType type, Level level) {
        super(type, level);
        this.buriedProgress = 20.0F;
        this.prevBuriedProgress = 20.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.FOLLOW_RANGE, 20);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnimalJoinPackGoal(this, 60, 11));
        this.goalSelector.addGoal(2, new UnderzealotOpenDoorGoal(this));
        this.goalSelector.addGoal(3, new UnderzealotSacrificeGoal(this));
        this.goalSelector.addGoal(4, new UnderzealotMeleeGoal(this));
        this.goalSelector.addGoal(5, new UnderzealotCaptureSacrificeGoal(this));
        this.goalSelector.addGoal(6, new UnderzealotProcessionGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new UnderzealotBreakLightGoal(this, 32));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1.0D, 100));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new UnderzealotHurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new MobTargetClosePlayers(this, 40, 12){
            @Override
            public boolean canUse() {
                return !UnderzealotEntity.this.isTargetingBlocked() && super.canUse();
            }
        });
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BURIED, false);
        this.entityData.define(CARRYING, false);
        this.entityData.define(PRAYING, false);
        this.entityData.define(SACRIFICE_POS, Optional.empty());
        this.entityData.define(PARTICLE_POS, Optional.empty());
        this.entityData.define(WORSHIP_TIME, 0);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public void travel(Vec3 vec3d) {
        if (this.isBuried() || this.isDiggingInProgress()) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public void tick() {
        super.tick();
        prevBuriedProgress = buriedProgress;
        prevCarryingProgress = carryingProgress;
        prevPrayingProgress = prayingProgress;
        if(!this.isNoAi() && (buriedProgress == 0.0F && this.isBuried() || buriedProgress == 20.0F && !this.isBuried())){
            this.playSound(ACSoundRegistry.UNDERZEALOT_DIG.get());
        }
        if (isBuried() && buriedProgress < 20.0F) {
            buriedProgress = Math.min(20.0F, buriedProgress + 1.5F);
        }
        if (!isBuried() && buriedProgress > 0.0F) {
            buriedProgress = Math.max(0.0F, buriedProgress - 1.5F);
        }
        if (isCarrying() && carryingProgress < 5.0F) {
            carryingProgress++;
        }
        if (!isCarrying() && carryingProgress > 0.0F) {
            carryingProgress--;
        }
        if (isPraying() && prayingProgress < 5.0F) {
            prayingProgress++;
        }
        if (!isPraying() && prayingProgress > 0.0F) {
            prayingProgress--;
        }
        if (!level().isClientSide) {
            if (this.isCarrying() && this.isPackFollower()) {
                for (Entity passenger : this.getPassengers()) {
                    passenger.stopRiding();
                }
            }
            this.setCarrying(!this.getPassengers().isEmpty());
            if (this.isBuried() && buriedProgress >= 20.0F) {
                if (reemergeTime-- < 0) {
                    this.moveTo(remergePos.getX() + 0.5F, remergePos.getY(), remergePos.getZ() + 0.5F);
                    idleBuryIn = 400 + random.nextInt(800);
                    reemergeTime = 0;
                    this.setBuried(false);
                }
                Vec3 centerOf = Vec3.atBottomCenterOf(this.blockPosition()).subtract(this.position());
                this.getDeltaMovement().add(centerOf.x * 0.1F, 0, centerOf.z * 0.1F);
            } else if (digsIdle() && idleBuryIn-- < 0) {
                if (this.onGround()) {
                    this.setBuried(true);
                    idleBuryIn = 0;
                    reemergeAt(findReemergePos(this.blockPosition(), 10), 40 + random.nextInt(60));
                }
            }
            if (isDiggingInProgress()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            }
        } else if (isDiggingInProgress()) {
            BlockState stateOn = this.getBlockStateOn();
            if (stateOn.isSolid()) {
                for (int i = 0; i < 3; i++) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateOn), true, this.getRandomX(0.8F), this.getY(), this.getRandomZ(0.8F), random.nextFloat() - 0.5F, random.nextFloat() + 0.5F, random.nextFloat() - 0.5F);
                }
            }
            if (isBuried()) {
                this.setOldPosAndRot();
            }
        }
        if (cloudCooldown > 0) {
            cloudCooldown--;
        }
        if (sacrificeCooldown > 0) {
            sacrificeCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void handleEntityEvent(byte b) {
        if (b == 61) {
            Vec3 eye = this.getEyePosition();
            Vec3 leftHand = eye.add(new Vec3(0.8F, 0, 0).yRot(-yBodyRot * ((float) Math.PI / 180F)));
            Vec3 rightHand = eye.add(new Vec3(-0.8F, 0, 0).yRot(-yBodyRot * ((float) Math.PI / 180F)));
            Vec3 target = this.getParticlePos() == null ? this.position().add(0, 4, 0) : Vec3.atCenterOf(getParticlePos());
            this.level().addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), leftHand.x, leftHand.y, leftHand.z, target.x, target.y, target.z);
            this.level().addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), rightHand.x, rightHand.y, rightHand.z, target.x, target.y, target.z);
        } else if (b == 62) {
            Vec3 particleAt = this.getParticlePos() == null ? this.position().add(0, 4, 0) : Vec3.atCenterOf(getParticlePos());
            int carryingId = -1;
            if (this.isVehicle()) {
                carryingId = this.getFirstPassenger() == null ? -1 : this.getFirstPassenger().getId();
            }
            this.level().addParticle(ACParticleRegistry.VOID_BEING_CLOUD.get(), particleAt.x, particleAt.y, particleAt.z, 1F, carryingId, 5 + random.nextInt(4));
        } else if(b == 77){
            if(this.isAlive()){
                AlexsCaves.PROXY.playWorldSound(this, (byte) 5);
            }
        }else {
            super.handleEntityEvent(b);
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }

    public BlockPos findReemergePos(BlockPos origin, int range) {
        for (int i = 0; i < 15; i++) {
            BlockPos blockPos = origin.offset(this.getRandom().nextInt(range) - range / 2, this.getRandom().nextInt(range) - range / 2, this.getRandom().nextInt(range) - range / 2);
            while (!level().isEmptyBlock(blockPos) && blockPos.getY() < level().getMaxBuildHeight()) {
                blockPos = blockPos.above();
            }
            while (level().isEmptyBlock(blockPos.below()) && blockPos.getY() > level().getMinBuildHeight()) {
                blockPos = blockPos.below();
            }
            if (!level().isEmptyBlock(blockPos.below()) && blockPos.distSqr(origin) < range * range + 10) {
                return blockPos;
            }
        }
        return origin;
    }

    public void triggerIdleDigging() {
        idleBuryIn = 0;
    }

    public boolean digsIdle() {
        LivingEntity target = this.getTarget();
        return !this.isCarrying() && !this.isPackFollower() && !this.isPraying() && this.getAnimation() == NO_ANIMATION && (target == null || !target.isAlive());
    }

    public void reemergeAt(BlockPos pos, int time) {
        this.remergePos = pos;
        this.reemergeTime = time;
    }

    public void positionRider(Entity entity, MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        entity.setYRot(this.yBodyRot);
        entity.setYHeadRot(this.yBodyRot);
        entity.setYBodyRot(this.yBodyRot);
    }

    public boolean isBuried() {
        return this.entityData.get(BURIED);
    }

    public void setBuried(boolean bool) {
        this.entityData.set(BURIED, bool);
    }

    public boolean isCarrying() {
        return this.entityData.get(CARRYING);
    }

    public BlockPos getLastSacrificePos() {
        return this.entityData.get(SACRIFICE_POS).orElse(null);
    }

    public void setLastSacrificePos(BlockPos lastAltarPos) {
        this.entityData.set(SACRIFICE_POS, Optional.ofNullable(lastAltarPos));
    }

    public BlockPos getParticlePos() {
        return this.entityData.get(PARTICLE_POS).orElse(null);
    }

    public void setParticlePos(BlockPos lastAltarPos) {
        this.entityData.set(PARTICLE_POS, Optional.ofNullable(lastAltarPos));
    }

    public void setCarrying(boolean bool) {
        this.entityData.set(CARRYING, bool);
    }

    public boolean isPraying() {
        return this.entityData.get(PRAYING);
    }

    public void setPraying(boolean bool) {
        this.entityData.set(PRAYING, bool);
    }

    public int getWorshipTime() {
        return this.entityData.get(WORSHIP_TIME);
    }

    public void setWorshipTime(int worship) {
        this.entityData.set(WORSHIP_TIME, worship);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public float getBuriedProgress(float partialTick) {
        return this.isNoAi() ? 0.0F : (prevBuriedProgress + (buriedProgress - prevBuriedProgress) * partialTick) * 0.05F;
    }

    public float getCarryingProgress(float partialTick) {
        return (prevCarryingProgress + (carryingProgress - prevCarryingProgress) * partialTick) * 0.2F;
    }

    public float getPrayingProgress(float partialTick) {
        return (prevPrayingProgress + (prayingProgress - prevPrayingProgress) * partialTick) * 0.2F;
    }

    @Override
    public PackAnimal getPriorPackMember() {
        return this.priorPackMember;
    }

    @Override
    public PackAnimal getAfterPackMember() {
        return afterPackMember;
    }

    @Override
    public void setPriorPackMember(PackAnimal animal) {
        this.priorPackMember = (UnderzealotEntity) animal;
    }

    @Override
    public void setAfterPackMember(PackAnimal animal) {
        this.afterPackMember = (UnderzealotEntity) animal;
    }

    @Override
    public boolean isValidLeader(PackAnimal packLeader) {
        return ((UnderzealotEntity) packLeader).isCarrying() && !packLeader.isPackFollower() && ((LivingEntity) packLeader).isAlive();
    }

    public boolean isTargetingBlocked(){
        if(this.isPackFollower() && getPackLeader() instanceof UnderzealotEntity underzealotLeader){
            return underzealotLeader.isCarrying();
        }else if(this.isCarrying()){
            return true;
        }
        return this.isPraying();
    }

    public boolean isDiggingInProgress() {
        return buriedProgress > 0 && buriedProgress < 20;
    }

    public void push(Entity entity) {
        if (!(entity instanceof UnderzealotEntity underzealot && (underzealot.isPraying() || this.isPraying())) && !this.isBuried() && !isDiggingInProgress()) {
            super.push(entity);
        }
    }

    public double getPassengersRidingOffset() {
        return this.getBbHeight();
    }

    public boolean isPickable() {
        return !isBuried();
    }

    public boolean isPushable() {
        return !isBuried();
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(damageSource);
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
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_ATTACK_0, ANIMATION_ATTACK_1, ANIMATION_BREAKTORCH};
    }

    public static boolean checkUnderzealotSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            spawnReinforcements(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void spawnReinforcements(ServerLevelAccessor worldIn) {
        for (int i = 0; i < 3 + random.nextInt(2); i++) {
            UnderzealotEntity friend = ACEntityRegistry.UNDERZEALOT.get().create(worldIn.getLevel());
            friend.copyPosition(this);
            worldIn.addFreshEntity(friend);
        }
    }

    public boolean isSurroundedByPrayers() {
        PackAnimal leader = this.getPackLeader();
        int prayers = 0;
        while (leader.getAfterPackMember() != null) {
            leader = leader.getAfterPackMember();
            if (leader instanceof UnderzealotEntity underzealot && underzealot.isPraying()) {
                prayers++;
            }
        }
        return prayers >= 3;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SacrificeCooldown", this.sacrificeCooldown);
        compound.putBoolean("Buried", this.isBuried());
        if (remergePos != null) {
            compound.putInt("RX", this.remergePos.getX());
            compound.putInt("RY", this.remergePos.getY());
            compound.putInt("RZ", this.remergePos.getZ());
        }
        BlockPos sacrificePos = this.getLastSacrificePos();
        if (sacrificePos != null) {
            compound.putInt("SX", sacrificePos.getX());
            compound.putInt("SY", sacrificePos.getY());
            compound.putInt("SZ", sacrificePos.getZ());
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.sacrificeCooldown = compound.getInt("SacrificeCooldown");
        this.setBuried(compound.getBoolean("Buried"));
        if (compound.contains("RX") && compound.contains("RY") && compound.contains("RZ")) {
            this.remergePos = new BlockPos(compound.getInt("RX"), compound.getInt("RY"), compound.getInt("RZ"));
        }
        if (compound.contains("SX") && compound.contains("SY") && compound.contains("SZ")) {
            this.setLastSacrificePos(new BlockPos(compound.getInt("SX"), compound.getInt("SY"), compound.getInt("SZ")));
        }
    }

    public boolean hurt(DamageSource damageSource, float f) {
        boolean prev = super.hurt(damageSource, f);
        if (prev && this.isVehicle() && this.getRandom().nextFloat() < 0.65F) {
            this.ejectPassengers();
            return true;
        } else {
            return prev;
        }
    }

    public boolean isAttackable() {
        return super.isAttackable() && !this.isBuried();
    }

    public void postSacrifice(UnderzealotSacrifice sacrifice) {
        this.playSound(ACSoundRegistry.UNDERZEALOT_TRANSFORMATION.get(), 8.0F, 1.0F);
        sacrificeCooldown = 6000 + random.nextInt(6000);
        float advancementRange = 64.0F;
        for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(advancementRange))) {
            if (player.distanceTo(this) < advancementRange) {
                ACAdvancementTriggerRegistry.UNDERZEALOT_SACRIFICE.triggerForEntity(player);
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return this.isCarrying() || this.isPraying() || this.isBuried() ? super.getAmbientSound() : ACSoundRegistry.UNDERZEALOT_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.UNDERZEALOT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.UNDERZEALOT_DEATH.get();
    }

    public void jumpFromGround() {
        super.jumpFromGround();
    }
}
