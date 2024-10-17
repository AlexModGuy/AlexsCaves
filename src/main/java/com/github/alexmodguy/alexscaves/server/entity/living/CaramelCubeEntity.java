package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MeltedCaramelEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CaramelCubeEntity extends Monster implements PossessedByLicowitch {

    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(CaramelCubeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WANTS_TO_JUMP = SynchedEntityData.defineId(CaramelCubeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_JUMPED = SynchedEntityData.defineId(CaramelCubeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> POSSESSOR_LICOWITCH_ID = SynchedEntityData.defineId(CaramelCubeEntity.class, EntityDataSerializers.INT);
    private float squishProgress;
    private float prevSquishProgress;
    private float jumpProgress;
    private float prevJumpProgress;
    private float jiggleTime;
    private float prevJiggleTime;

    protected static final EntityDimensions SMALL_DIMENSIONS = EntityDimensions.fixed(0.8F, 0.8F);

    protected static final EntityDimensions MEDIUM_DIMENSIONS = EntityDimensions.fixed(1.5F, 1.5F);

    protected static final EntityDimensions LARGE_DIMENSIONS = EntityDimensions.fixed(3.5F, 3.5F);

    public CaramelCubeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveHelper();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public static boolean checkCaramelCubeSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal());
        this.goalSelector.addGoal(2, new AttackGoal());
        this.goalSelector.addGoal(3, new RandomDirectionGoal());
        this.goalSelector.addGoal(4, new KeepOnJumpingGoal());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (y) -> {
            return Math.abs(y.getY() - this.getY()) <= 4.0D;
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Husk.class, true));
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 0);
        this.entityData.define(WANTS_TO_JUMP, false);
        this.entityData.define(HAS_JUMPED, false);
        this.entityData.define(POSSESSOR_LICOWITCH_ID, -1);
    }

    public float getJumpProgress(float partialTick) {
        return (prevJumpProgress + (jumpProgress - prevJumpProgress) * partialTick) * 0.33F;
    }

    public float getSquishProgress(float partialTick) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTick) * 0.2F;
    }

    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
    }

    protected float getJumpPower() {
        float f = this.getSlimeSize() == 2 ? 0.3F : this.getSlimeSize() == 1 ? 0.1F : 0.0F;
        return super.getJumpPower() + f;
    }

    public int getMaxHeadXRot() {
        return 0;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSlimeSize(compound.getInt("SlimeSize"), false);

    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SlimeSize", this.getSlimeSize());
    }

    public void setSlimeSize(int i, boolean heal) {
        int size = Mth.clamp(i, 0, 2);
        this.entityData.set(SIZE, size);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(4.0F + 6.0F * size);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F + 0.1F * size);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double) 2.0F + size * 2.0F);
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
    }

    protected int calculateFallDamage(float f, float f1) {
        return super.calculateFallDamage(f, f1) - 5;
    }

    public int getSlimeSize() {
        return Math.min(this.entityData.get(SIZE), 2);
    }

    public void setWantsToJump(boolean wantsToJump) {
        this.entityData.set(WANTS_TO_JUMP, wantsToJump);
    }

    public boolean wantsToJump() {
        return this.entityData.get(WANTS_TO_JUMP);
    }

    public void setHasJumped(boolean hasJumped) {
        this.entityData.set(HAS_JUMPED, hasJumped);
    }

    public boolean hasJumped() {
        return this.entityData.get(HAS_JUMPED);
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
    protected boolean shouldDropLoot() {
        return super.shouldDropLoot() && getPossessedByLicowitchId() == -1;
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

    public void tick() {
        super.tick();
        prevJumpProgress = jumpProgress;
        prevSquishProgress = squishProgress;
        prevJiggleTime = jiggleTime;
        boolean jumping = !this.onGround() && tickCount > 4;
        boolean squish = !jumping && (this.wantsToJump() || this.hasJumped() && this.onGround());
        if (jumping && jumpProgress < 3.0F) {
            jumpProgress++;
        }
        if (!jumping && jumpProgress > 0.0F) {
            jumpProgress--;
        }
        if (squish && squishProgress < 5.0F) {
            squishProgress++;
            if (squishProgress >= 5.0F) {
                this.setHasJumped(false);
            }
        }
        if (!squish && squishProgress > 0.0F) {
            squishProgress--;
        }
        if (this.hasJumped() && this.onGround()) {
            jiggleTime = 5;
        } else if (jiggleTime > 0) {
            if(jiggleTime == 4){
                this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }
            if (jiggleTime > 4) {
                spawnLandParticles();
            }
            jiggleTime--;
        }
        if(level().isClientSide){
            spawnPossessedParticles(getRandomX(0.5D), getRandomY(), getRandomZ(0.5D), this.level());
        }
    }

    private void spawnLandParticles() {
        int i = 1 + this.getSlimeSize();
        for (int j = 0; j < i * 6; ++j) {
            float f = this.random.nextFloat() * ((float) Math.PI * 2F);
            float f1 = this.random.nextFloat() * 0.5F + 0.65F;
            float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
            float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
            this.level().addParticle(ACParticleRegistry.CARAMEL_DROP.get(), this.getX() + (double) f2, this.getY() + 0.15F, this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
        }
    }

    private void spawnMeltedCaramel() {
        int i = 1 + this.getSlimeSize();
        for (int j = 0; j < i; ++j) {
            float f = this.random.nextFloat() * ((float) Math.PI * 2F);
            float f1 = this.random.nextFloat() * 0.5F + 0.65F;
            float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
            float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
            MeltedCaramelEntity meltedCaramel = ACEntityRegistry.MELTED_CARAMEL.get().create(level());
            Vec3 vec3 = new Vec3(this.getX() + (double) f2, this.getY() + 0.02, this.getZ() + (double) f3);
            meltedCaramel.setPos(ACMath.getGroundBelowPosition(level(), vec3));
            meltedCaramel.setDespawnsIn(40 + (i - 1) * 40);
            meltedCaramel.setDeltaMovement(this.getDeltaMovement().multiply(-1.0F, 0.0F, -1.0F));
            level().addFreshEntity(meltedCaramel);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageValue) {
        boolean sup = super.hurt(damageSource, damageValue);
        if (sup) {
            spawnMeltedCaramel();
        }
        return sup;
    }

    public float getJiggleTime(float partialTick) {
        return (prevJiggleTime + (jiggleTime - prevJiggleTime) * partialTick) * 0.2F;
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setSlimeSize(random.nextInt(3), true);
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (SIZE.equals(dataAccessor)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    public EntityDimensions getDimensions(Pose pose) {
        switch (this.getSlimeSize()) {
            case 2:
                return LARGE_DIMENSIONS;
            case 1:
                return MEDIUM_DIMENSIONS;
            default:
                return SMALL_DIMENSIONS;
        }
    }

    public void remove(Entity.RemovalReason reason) {
        int ourSize = this.getSlimeSize();
        if (!this.level().isClientSide && ourSize > 0 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) ourSize / 4.0F;
            int j = ourSize - 1;
            int slimesSpawned = ourSize >= 2 ? 2 : 2 + this.random.nextInt(2);

            for (int l = 0; l < slimesSpawned; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                CaramelCubeEntity slime = ACEntityRegistry.CARAMEL_CUBE.get().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }
                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSlimeSize(j, true);
                    slime.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(slime);
                }
            }
        }

        super.remove(reason);
    }

    protected void dropFromLootTable(DamageSource source, boolean b) {
        if (this.getSlimeSize() == 0) {
            super.dropFromLootTable(source, b);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5F * dimensions.height;
    }

    public int getExperienceReward() {
        return 2;
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    protected SoundEvent getHurtSound(DamageSource p_33631_) {
        return this.getSquishSound();
    }

    protected SoundEvent getDeathSound() {
        return this.getSquishSound();
    }

    protected SoundEvent getJumpSound() {
        return this.getSquishSound();
    }

    protected SoundEvent getSquishSound() {
        return this.getSlimeSize() == 0 ? ACSoundRegistry.CARAMEL_CUBE_SMALL.get() : ACSoundRegistry.CARAMEL_CUBE_BIG.get();
    }

    class MoveHelper extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private boolean isAggressive;

        public MoveHelper() {
            super(CaramelCubeEntity.this);
            this.yRot = 180.0F * CaramelCubeEntity.this.getYRot() / (float) Math.PI;
        }

        public void setDirection(float yRot, boolean aggressive) {
            this.yRot = yRot;
            this.isAggressive = aggressive;
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    float f = this.isAggressive ? 1.5F : 1F;
                    this.mob.setSpeed((float) (this.speedModifier * f * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        CaramelCubeEntity.this.setWantsToJump(false);
                        this.jumpDelay = 20;
                        if (this.isAggressive) {
                            this.jumpDelay = 6;
                        }
                        CaramelCubeEntity.this.getJumpControl().jump();
                        CaramelCubeEntity.this.setHasJumped(true);
                        CaramelCubeEntity.this.playSound(CaramelCubeEntity.this.getJumpSound(), CaramelCubeEntity.this.getSoundVolume(), CaramelCubeEntity.this.getVoicePitch());
                    } else {
                        CaramelCubeEntity.this.xxa = 0.0F;
                        CaramelCubeEntity.this.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                        CaramelCubeEntity.this.setWantsToJump(true);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    class AttackGoal extends Goal {
        private int growTiredTimer;
        private int attackLogicCooldown = 0;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = CaramelCubeEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return CaramelCubeEntity.this.canAttack(livingentity) && CaramelCubeEntity.this.getMoveControl() instanceof MoveHelper;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = CaramelCubeEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!CaramelCubeEntity.this.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = CaramelCubeEntity.this.getTarget();
            if (livingentity != null) {
                CaramelCubeEntity.this.lookAt(livingentity, 10.0F, 10.0F);
                double dist = CaramelCubeEntity.this.distanceTo(livingentity);
                if(dist < CaramelCubeEntity.this.getBbWidth() + 0.25D + livingentity.getBbWidth() && CaramelCubeEntity.this.hasJumped() && CaramelCubeEntity.this.onGround() && attackLogicCooldown == 0){
                    attackLogicCooldown = 5;
                    CaramelCubeEntity.this.playSound(ACSoundRegistry.CARAMEL_CUBE_ATTACK.get(), CaramelCubeEntity.this.getSoundVolume(), CaramelCubeEntity.this.getVoicePitch());
                    livingentity.hurt(damageSources().mobAttack(CaramelCubeEntity.this), (float) CaramelCubeEntity.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                }
            }
            if(attackLogicCooldown > 0){
                attackLogicCooldown--;
            }
            MoveControl movecontrol = CaramelCubeEntity.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(CaramelCubeEntity.this.getYRot(), true);
            }

        }
    }

    class FloatGoal extends Goal {

        public FloatGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            CaramelCubeEntity.this.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (CaramelCubeEntity.this.isInWater() || CaramelCubeEntity.this.isInLava()) && CaramelCubeEntity.this.getMoveControl() instanceof MoveHelper;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (CaramelCubeEntity.this.getRandom().nextFloat() < 0.8F) {
                CaramelCubeEntity.this.getJumpControl().jump();
            }

            MoveControl movecontrol = CaramelCubeEntity.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.2D);
            }

        }
    }

    class KeepOnJumpingGoal extends Goal {

        public KeepOnJumpingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !CaramelCubeEntity.this.isPassenger();
        }

        public void tick() {
            MoveControl movecontrol = CaramelCubeEntity.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }

        }
    }

    class RandomDirectionGoal extends Goal {
        private float chosenDegrees;
        private int nextRandomizeTime;

        public RandomDirectionGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return CaramelCubeEntity.this.getTarget() == null && (CaramelCubeEntity.this.onGround() || CaramelCubeEntity.this.isInWater() || CaramelCubeEntity.this.isInLava() || CaramelCubeEntity.this.hasEffect(MobEffects.LEVITATION)) && CaramelCubeEntity.this.getMoveControl() instanceof MoveHelper;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + CaramelCubeEntity.this.getRandom().nextInt(60));
                this.chosenDegrees = (float) CaramelCubeEntity.this.getRandom().nextInt(360);
            }

            MoveControl movecontrol = CaramelCubeEntity.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.chosenDegrees, false);
            }

        }
    }

}
