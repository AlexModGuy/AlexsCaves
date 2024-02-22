package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.ForsakenAttackGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.ForsakenRandomlyJumpGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class ForsakenEntity extends Monster implements IAnimatedEntity, ShakesScreen {
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SONIC_CHARGE = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SONAR_ID = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DARKNESS_TIME = SynchedEntityData.defineId(ForsakenEntity.class, EntityDataSerializers.INT);
    public static final Animation ANIMATION_SUMMON = Animation.create(50);
    public static final Animation ANIMATION_PREPARE_JUMP = Animation.create(15);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_LEFT_SLASH = Animation.create(33);
    public static final Animation ANIMATION_RIGHT_SLASH = Animation.create(33);
    public static final Animation ANIMATION_GROUND_SMASH = Animation.create(30);
    public static final Animation ANIMATION_SONIC_ATTACK = Animation.create(35);
    public static final Animation ANIMATION_SONIC_BLAST = Animation.create(45);
    public static final Animation ANIMATION_LEFT_PICKUP = Animation.create(48);
    public static final Animation ANIMATION_RIGHT_PICKUP = Animation.create(48);
    private static final int LIGHT_THRESHOLD = 4;
    private Animation currentAnimation = IAnimatedEntity.NO_ANIMATION;
    private int animationTick;
    public LegSolverQuadruped legSolver = new LegSolverQuadruped(-0.4F, 1.4F, 1F, 0.75F, 1);
    private float runProgress;
    private float prevRunProgress;
    private float leapProgress;
    private float prevLeapProgress;
    private float leapPitch;
    private float prevLeapPitch;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private int timeLeaping = 0;
    private float raiseLeftArmProgress;
    private float prevRaiseLeftArmProgress;
    private float raiseRightArmProgress;
    private float prevRaiseRightArmProgress;
    private float darknessProgress;
    private float prevDarknessProgress;
    private boolean hasRunningAttributes = false;
    private int destroyBlocksTick = 10;

    public static final Predicate<LivingEntity> TARGETING = (mob) -> {
        return !mob.getType().is(ACTagRegistry.FORSAKEN_IGNORES);
    };

    public ForsakenEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 250.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 10.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(LEAPING, false);
        this.entityData.define(SONIC_CHARGE, false);
        this.entityData.define(DARKNESS_TIME, 0);
        this.entityData.define(SONAR_ID, -1);
        this.entityData.define(HELD_MOB_ID, -1);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ForsakenAttackGoal(this));
        this.goalSelector.addGoal(2, new ForsakenRandomlyJumpGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 30));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, ForsakenEntity.class)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 120, false, true, TARGETING));
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public void tick() {
        super.tick();
        this.prevRunProgress = runProgress;
        this.prevLeapProgress = leapProgress;
        this.prevRaiseLeftArmProgress = raiseLeftArmProgress;
        this.prevRaiseRightArmProgress = raiseRightArmProgress;
        this.prevDarknessProgress = darknessProgress;
        this.prevLeapPitch = leapPitch;
        this.prevScreenShakeAmount = screenShakeAmount;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        if (this.isRunning() && runProgress < 5.0F) {
            runProgress++;
        }
        if (!this.isRunning() && runProgress > 0.0F) {
            runProgress--;
        }
        if (isLeaping() && leapProgress < 5F) {
            leapProgress++;
        }
        if (!isLeaping() && leapProgress > 0F) {
            leapProgress--;
        }
        if (getDarknessTime() > 0 && darknessProgress < 5F) {
            darknessProgress++;
        }
        if (getDarknessTime() <= 0 && darknessProgress > 0F) {
            darknessProgress--;
        }
        if (this.isLeaping()) {
            if (this.onGround() && leapProgress >= 5.0F) {
                this.setLeaping(false);
            }
            timeLeaping++;
            Vec3 vec3 = this.getDeltaMovement();
            float f2 = (float) (-(Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double) (180F / (float) Math.PI)));
            this.leapPitch = Mth.approachDegrees(leapPitch, f2, 5);
        } else {
            timeLeaping = 0;
            this.leapPitch = Mth.approachDegrees(leapPitch, 0, 5);
            if (this.getAnimation() == ANIMATION_PREPARE_JUMP && this.onGround() && !level().isClientSide && this.getAnimationTick() >= 8 && this.getAnimationTick() <= 10) {
                this.setLeaping(true);
                this.playSound(ACSoundRegistry.FORSAKEN_LEAP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45D);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }
        boolean raisingLeftArm = this.isRaisingArm(true);
        boolean raisingRightArm = this.isRaisingArm(false);
        if (raisingLeftArm && raiseLeftArmProgress < 10.0F) {
            raiseLeftArmProgress++;
        }
        if (!raisingLeftArm && raiseLeftArmProgress > 0.0F) {
            raiseLeftArmProgress--;
        }
        if (raisingRightArm && raiseRightArmProgress < 10.0F) {
            raiseRightArmProgress++;
        }
        if (!raisingRightArm && raiseRightArmProgress > 0.0F) {
            raiseRightArmProgress--;
        }
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        if (level().isClientSide) {
            if (random.nextInt(6) == 0) {
                level().addParticle(ACParticleRegistry.FORSAKEN_SPIT.get(), this.getX(), this.getY() + 0.5F, this.getZ(), this.getId(), 0, 0);
            }
            if (darknessProgress > 0) {
                for (int i = 0; i < 1; i++) {
                    if (random.nextBoolean()) {
                        level().addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), this.getX(), this.getEyeY(), this.getZ());
                    } else {
                        level().addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, 0, 0);
                    }
                }
            }
            if (this.getAnimation() == ANIMATION_SONIC_ATTACK) {
                if (this.getAnimationTick() > 10 && this.getAnimationTick() < 30) {
                    if (this.getAnimationTick() % 4 == 0) {
                        level().addAlwaysVisibleParticle(ACParticleRegistry.FORSAKEN_SONAR.get(), true, this.getX(), this.getY() + 0.5F, this.getZ(), this.getId(), this.getXRot(), this.getYHeadRot());
                    }
                    this.screenShakeAmount = 1F;
                }
            }
            if (this.getAnimation() == ANIMATION_SONIC_BLAST) {
                if (this.getAnimationTick() > 10 && this.getAnimationTick() < 30) {
                    if (this.getAnimationTick() % 4 == 0) {
                        level().addAlwaysVisibleParticle(ACParticleRegistry.FORSAKEN_SONAR_LARGE.get(), true, this.getX(), this.getY() + 0.5F, this.getZ(), this.getId(), 90, 0);
                    }
                    this.screenShakeAmount = 1F;
                }
            }
            if (this.getAnimation() == ANIMATION_GROUND_SMASH) {
                if (this.getAnimationTick() >= 10 && this.getAnimationTick() <= 15) {
                    this.screenShakeAmount = 1F;
                }
                if (this.getAnimationTick() == 12) {
                    Vec3 smashPos = this.position().add(new Vec3(0, 0, 3.5F).yRot((float) -Math.toRadians(this.yBodyRot)));
                    float radius = 1.4F;
                    float particleCount = 20 + random.nextInt(12);
                    for (int i1 = 0; i1 < particleCount; i1++) {
                        double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                        double motionY = getRandom().nextFloat() * 0.7D + 1.8F;
                        double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                        float angle = (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                        double extraX = radius * Mth.sin((float) (Math.PI + angle));
                        double extraY = 1.2F;
                        double extraZ = radius * Mth.cos(angle);
                        BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(smashPos.x + extraX), Mth.floor(smashPos.y + extraY) + 2, Mth.floor(smashPos.z + extraZ))));
                        BlockState groundState = this.level().getBlockState(ground);
                        if (groundState.isSolid()) {
                            if (level().isClientSide) {
                                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, smashPos.x + extraX, ground.getY() + extraY, smashPos.z + extraZ, motionX, motionY, motionZ);
                            }
                        }
                    }
                }
            }
        } else {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && target.distanceTo(this) < 10 && this.hasLineOfSight(target) && (this.getAnimation() == ANIMATION_RIGHT_PICKUP || this.getAnimation() == ANIMATION_LEFT_PICKUP)) {
                if(getHeldMobId() == -1){
                    this.playSound(ACSoundRegistry.FORSAKEN_GRAB.get(), this.getSoundVolume(), this.getVoicePitch());
                }
                this.setHeldMobId(target.getId());
            } else if (getHeldMobId() != -1) {
                this.setHeldMobId(-1);
            }
            if (this.getHealth() < this.getMaxHealth() * 0.5F && !level().isClientSide) {
                int lightLevel = getLightLevel();
                if (lightLevel <= LIGHT_THRESHOLD) {
                    this.setDarknessTime(30);
                } else if (getDarknessTime() > 0) {
                    this.setDarknessTime(this.getDarknessTime() - 1);
                }
                if (getDarknessTime() > 0 && this.tickCount % 30 == 0) {
                    this.heal(1);
                }
            } else {
                this.setDarknessTime(0);
            }
        }
        Entity grabbedEntity = this.getHeldMob();
        if (grabbedEntity != null && grabbedEntity.isAlive() && grabbedEntity.distanceTo(this) < 10) {
            grabbedEntity.fallDistance = 0;
            if ((this.getAnimation() == ANIMATION_RIGHT_PICKUP || this.getAnimation() == ANIMATION_LEFT_PICKUP) && this.getAnimationTick() >= 10 && this.getAnimationTick() <= 38) {
                Vec3 grabPos = getPickupPos();
                Vec3 minus = new Vec3(grabPos.x - grabbedEntity.getX(), grabPos.y - grabbedEntity.getY(), grabPos.z - grabbedEntity.getZ()).scale(0.33F);
                grabbedEntity.setDeltaMovement(minus);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private int getLightLevel() {
        BlockPos blockPos = this.blockPosition().above();
        return Math.max(this.level().getBrightness(LightLayer.BLOCK, blockPos), this.level().getMaxLocalRawBrightness(blockPos));
    }

    private Vec3 getPickupPos() {
        Vec3 handRotated = getHandPos(animationTick).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
        return this.position().add(handRotated);
    }

    public int getDarknessTime() {
        return this.entityData.get(DARKNESS_TIME);
    }

    public void setDarknessTime(int time) {
        this.entityData.set(DARKNESS_TIME, time);
    }

    private Vec3 getHandPos(int animationTick) {
        float sideOffset = this.getAnimation() == ANIMATION_LEFT_PICKUP ? 1 : -1;
        Vec3 hand;
        if (animationTick <= 10) {
            hand = new Vec3(0F, 0, 4F);
        } else if (animationTick <= 15) {
            hand = new Vec3(0F, 1F, 3.7F);
        } else if (animationTick <= 25) {
            hand = new Vec3(sideOffset * 2.75F, 4.65F, 1.9F);
        } else {
            hand = new Vec3(sideOffset * 1.2F, 3.15F, 2.4F);
        }
        return hand;
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    public boolean removeWhenFarAway(double dist) {
        return dist > 16384;
    }

    public static boolean checkForsakenSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(20) == 0;
    }

    private boolean isRaisingArm(boolean left) {
        if (currentAnimation != NO_ANIMATION && currentAnimation != null && animationTick > currentAnimation.getDuration() - 5) {
            return false;
        }
        if (left && (this.currentAnimation == ANIMATION_LEFT_PICKUP || this.currentAnimation == ANIMATION_LEFT_SLASH)) {
            return true;
        }
        if (!left && (this.currentAnimation == ANIMATION_RIGHT_PICKUP || this.currentAnimation == ANIMATION_RIGHT_SLASH)) {
            return true;
        }
        return this.currentAnimation == ANIMATION_SUMMON || this.currentAnimation == ANIMATION_GROUND_SMASH;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getRunProgress(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean bool) {
        this.entityData.set(LEAPING, bool);
    }

    public float getLeapProgress(float partialTick) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTick) * 0.2F;
    }

    public void setSonarId(int i) {
        this.entityData.set(SONAR_ID, i);
    }

    public Entity getSonarTarget() {
        int id = this.entityData.get(SONAR_ID);
        return id == -1 ? null : level().getEntity(id);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }


    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public float getLeapPitch(float partialTick) {
        return prevLeapPitch + (leapPitch - prevLeapPitch) * partialTick;
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public boolean hasSonicCharge() {
        return this.entityData.get(SONIC_CHARGE);
    }

    public void setSonicCharge(boolean bool) {
        this.entityData.set(SONIC_CHARGE, bool);
    }

    public float getRaisedLeftArmAmount(float partialTicks) {
        return (prevRaiseLeftArmProgress + (raiseLeftArmProgress - prevRaiseLeftArmProgress) * partialTicks) * 0.1F;
    }

    public float getRaisedRightArmAmount(float partialTicks) {
        return (prevRaiseRightArmProgress + (raiseRightArmProgress - prevRaiseRightArmProgress) * partialTicks) * 0.1F;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
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

    public float getDarknessAmount(float partialTicks) {
        float animationValue = 0.0F;
        if (this.currentAnimation == ForsakenEntity.ANIMATION_SUMMON) {
            animationValue = 1.0F - (this.getAnimationTick() + partialTicks) / (float) ForsakenEntity.ANIMATION_SUMMON.getDuration();
        }
        return Math.max((prevDarknessProgress + (darknessProgress - prevDarknessProgress) * partialTicks) * 0.2F, animationValue);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(this.isInWall()){
            if (this.destroyBlocksTick > 0) {
                --this.destroyBlocksTick;
                if (this.destroyBlocksTick == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                    int j1 = Mth.floor(this.getY());
                    int i2 = Mth.floor(this.getX());
                    int j2 = Mth.floor(this.getZ());
                    boolean flag = false;

                    for(int j = -1; j <= 1; ++j) {
                        for(int k2 = -1; k2 <= 1; ++k2) {
                            for(int k = 0; k <= 3; ++k) {
                                int l2 = i2 + j;
                                int l = j1 + k;
                                int i1 = j2 + k2;
                                BlockPos blockpos = new BlockPos(l2, l, i1);
                                BlockState blockstate = this.level().getBlockState(blockpos);
                                if (blockstate.canEntityDestroy(this.level(), blockpos, this) && !blockstate.is(ACTagRegistry.UNMOVEABLE) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                    flag = this.level().destroyBlock(blockpos, true, this) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.level().levelEvent((Player)null, 1022, this.blockPosition(), 0);
                    }
                    this.destroyBlocksTick = 20;
                }
            }
        }
    }

        @Override
    public float getScreenShakeAmount(float partialTicks) {
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    @Override
    public boolean canFeelShake(Entity player) {
        return true;
    }

    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.is(DamageTypes.SONIC_BOOM)) {
            this.setSonicCharge(true);
            return false;
        } else{
            if(damageSource.getEntity() instanceof AbstractGolem) {
                f *= 0.5F;
            }
            return super.hurt(damageSource, f);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SUMMON, ANIMATION_PREPARE_JUMP, ANIMATION_BITE, ANIMATION_LEFT_SLASH, ANIMATION_RIGHT_SLASH, ANIMATION_GROUND_SMASH, ANIMATION_SONIC_ATTACK, ANIMATION_SONIC_BLAST, ANIMATION_LEFT_PICKUP, ANIMATION_RIGHT_PICKUP};
    }

    public float getSonicDamageAgainst(LivingEntity target) {
        return target.getType().is(ACTagRegistry.WEAK_TO_FORSAKEN_SONIC_ATTACK) ? 45.0F : 4.0F;
    }

    public float getStepHeight() {
        return hasRunningAttributes ? 1.1F : 0.6F;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.FORSAKEN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.FORSAKEN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.FORSAKEN_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 2.5F;
    }
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.FORSAKEN_STEP.get(), 1F, 1F);
        }
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }
}
