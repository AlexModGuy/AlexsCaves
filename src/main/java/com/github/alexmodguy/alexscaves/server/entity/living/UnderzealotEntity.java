package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class UnderzealotEntity extends Monster implements PackAnimal, IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> BURIED = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CARRYING = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PRAYING = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> SACRIFICE_POS = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockPos>> PARTICLE_POS = SynchedEntityData.defineId(UnderzealotEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final Animation ANIMATION_ATTACK_0 = Animation.create(15);
    public static final Animation ANIMATION_ATTACK_1 = Animation.create(15);
    public static final Animation ANIMATION_BREAKTORCH = Animation.create(15);
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
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 4);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnimalJoinPackGoal(this, 60, 11));
        this.goalSelector.addGoal(2, new UnderzealotMeleeGoal(this));
        this.goalSelector.addGoal(3, new UnderzealotSacrificeGoal(this));
        this.goalSelector.addGoal(4, new UnderzealotCaptureSacrificeGoal(this));
        this.goalSelector.addGoal(5, new UnderzealotProcessionGoal(this, 1.0F));
        this.goalSelector.addGoal(6, new UnderzealotBreakLightGoal(this, 32));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 100));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, UnderzealotEntity.class, WatcherEntity.class)));
        this.targetSelector.addGoal(2, new MobTargetClosePlayers(this, 12));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BURIED, false);
        this.entityData.define(CARRYING, false);
        this.entityData.define(PRAYING, false);
        this.entityData.define(SACRIFICE_POS, Optional.empty());
        this.entityData.define(PARTICLE_POS, Optional.empty());
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
        if (isBuried() && buriedProgress < 20.0F) {
            buriedProgress++;
        }
        if (!isBuried() && buriedProgress > 0.0F) {
            buriedProgress--;
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
        if (!level.isClientSide) {
            if(this.isCarrying() && this.isPackFollower()){
                for(Entity passenger : this.getPassengers()){
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
            } else if (digsIdle() && idleBuryIn-- < 0) {
                if (this.isOnGround()) {
                    this.setBuried(true);
                    idleBuryIn = 0;
                    reemergeAt(findReemergePos(this.blockPosition(), 10), 40 + random.nextInt(60));
                }
            }
            if (isDiggingInProgress()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            }
        } else if (isDiggingInProgress()) {
            BlockState BlockState = this.getBlockStateOn();
            if (BlockState.getMaterial() != Material.AIR) {
                for (int i = 0; i < 3; i++) {
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockState), true, this.getRandomX(0.8F), this.getY(), this.getRandomZ(0.8F), random.nextFloat() - 0.5F, random.nextFloat() + 0.5F, random.nextFloat() - 0.5F);
                }
            }
            if(isBuried()){
                this.setOldPosAndRot();
            }
        }
        if(cloudCooldown > 0){
            cloudCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void handleEntityEvent(byte b) {
        if (b == 61) {
            Vec3 eye = this.getEyePosition();
            Vec3 leftHand = eye.add(new Vec3(0.8F, 0, 0).yRot(-yBodyRot * ((float) Math.PI / 180F)));
            Vec3 rightHand = eye.add(new Vec3(-0.8F, 0, 0).yRot(-yBodyRot * ((float) Math.PI / 180F)));
            Vec3 target = this.getParticlePos() == null ? this.position().add(0, 4, 0) : Vec3.atCenterOf(getParticlePos());
            this.level.addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), leftHand.x, leftHand.y, leftHand.z, target.x, target.y, target.z);
            this.level.addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), rightHand.x, rightHand.y, rightHand.z, target.x, target.y, target.z);
        } else if (b == 62) {
            Vec3 particleAt = this.getParticlePos() == null ? this.position().add(0, 4, 0) : Vec3.atCenterOf(getParticlePos());
            int carryingId = -1;
            if(this.isVehicle()){
                carryingId = this.getFirstPassenger() == null ? -1 : this.getFirstPassenger().getId();
            }
            this.level.addParticle(ACParticleRegistry.VOID_BEING_CLOUD.get(), particleAt.x, particleAt.y, particleAt.z, 1F, carryingId, 3 + random.nextInt(2));
        } else {
            super.handleEntityEvent(b);
        }
    }

    public BlockPos findReemergePos(BlockPos origin, int range) {
        for (int i = 0; i < 15; i++) {
            BlockPos blockPos = origin.offset(this.getRandom().nextInt(range) - range / 2, this.getRandom().nextInt(range) - range / 2, this.getRandom().nextInt(range) - range / 2);
            while (!level.isEmptyBlock(blockPos) && blockPos.getY() < level.getMaxBuildHeight()) {
                blockPos = blockPos.above();
            }
            while (level.isEmptyBlock(blockPos.below()) && blockPos.getY() > level.getMinBuildHeight()) {
                blockPos = blockPos.below();
            }
            if (!level.isEmptyBlock(blockPos.below()) && blockPos.distSqr(origin) < range * range + 10) {
                return blockPos;
            }
        }
        return origin;
    }

    public void triggerIdleDigging(){
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
    public void positionRider(Entity entity) {
        super.positionRider(entity);
        entity.setYRot(this.yBodyRot);
        entity.setYHeadRot(this.yBodyRot);
        entity.setYBodyRot(this.yBodyRot);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return super.mobInteract(player, hand);
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

    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public float getBuriedProgress(float partialTick) {
        return (prevBuriedProgress + (buriedProgress - prevBuriedProgress) * partialTick) * 0.05F;
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
        return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && this.isBuried() || damageSource.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(damageSource);
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
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(5) == 0;
    }

    public boolean isSurroundedByPrayers() {
        PackAnimal leader = this.getPackLeader();
        int prayers = 0;
        while(leader.getAfterPackMember() != null){
            leader = leader.getAfterPackMember();
            if(leader instanceof UnderzealotEntity underzealot && underzealot.isPraying()){
                prayers++;
            }
        }
        return prayers >= 4;
    }
}
