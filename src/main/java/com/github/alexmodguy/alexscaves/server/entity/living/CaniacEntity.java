package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.CaniacMeleeGoal;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CaniacEntity extends Monster implements IAnimatedEntity, PossessedByLicowitch {

    private static final EntityDataAccessor<Float> SPIN_SPEED = SynchedEntityData.defineId(CaniacEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(CaniacEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> POSSESSOR_LICOWITCH_ID = SynchedEntityData.defineId(CaniacEntity.class, EntityDataSerializers.INT);
    private float prevLeftArmRot;
    private float leftArmRot;
    private float prevRightArmRot;
    private float rightArmRot;
    private boolean spinSecondArm = false;

    public static final Animation ANIMATION_LUNGE = Animation.create(35);
    private Animation currentAnimation;
    private int animationTick;
    private boolean hasRunningAttributes = false;

    private float runProgress;
    private float prevRunProgress;

    private int swingSoundTimer = 0;

    public CaniacEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPIN_SPEED, 0F);
        this.entityData.define(RUNNING, false);
        this.entityData.define(POSSESSOR_LICOWITCH_ID, -1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 38.0D).add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    public static boolean checkCaniacSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(10) == 0;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Wolf.class, 10.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(1, new CaniacMeleeGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }

    public void tick() {
        super.tick();
        prevLeftArmRot = leftArmRot;
        prevRightArmRot = rightArmRot;
        prevRunProgress = runProgress;
        float armSpinSpeed = getArmSpinSpeed();
        if (armSpinSpeed > 0 && this.isAlive()) {
            if(swingSoundTimer-- < 0){
                swingSoundTimer = 5 + level().random.nextInt(10);
                this.playSound(ACSoundRegistry.CANIAC_SWING.get());
            }
            if(this.isLeftHanded()){
                leftArmRot += armSpinSpeed;
                if(leftArmRot % 360 > 180){
                    spinSecondArm = true;
                }
                if(spinSecondArm){
                    rightArmRot += armSpinSpeed;
                }
            }else{
                rightArmRot += armSpinSpeed;
                if(rightArmRot % 360 > 180){
                    spinSecondArm = true;
                }
                if(spinSecondArm){
                    leftArmRot += armSpinSpeed;
                }
            }
            if(level().isClientSide){
                if(Mth.wrapDegrees(leftArmRot) % 180 > 70){
                    spawnArmSwingParticles(true);
                }
                if(Mth.wrapDegrees(rightArmRot) % 180 > 70){
                    spawnArmSwingParticles(false);
                }
            }else{
                if(Mth.wrapDegrees(leftArmRot) % 180 > 75){
                    hurtMobsFromArmSwing(true, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 0.1F);
                }
                if(Mth.wrapDegrees(rightArmRot) % 180 > 75){
                    hurtMobsFromArmSwing(false, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), 0.1F);
                }
            }
        }else{
            spinSecondArm = false;
            float f = this.getAnimation() == ANIMATION_LUNGE ? 40 : 15;
            if(Mth.wrapDegrees(leftArmRot) != 0){
                leftArmRot = Mth.approachDegrees(leftArmRot, 0, f);
            }
            if(Mth.wrapDegrees(rightArmRot) != 0){
                rightArmRot = Mth.approachDegrees(rightArmRot, 0, f);
            }
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }
        if (isRunning() && runProgress < 5F) {
            runProgress++;
        }
        if (!isRunning() && runProgress > 0F) {
            runProgress--;
        }
        if(level().isClientSide){
            spawnPossessedParticles(getRandomX(0.5D), getRandomY(), getRandomZ(0.5D), this.level());
        }
        if(this.getAnimation() == ANIMATION_LUNGE && this.getAnimationTick() == 10){
            this.playSound(ACSoundRegistry.CANIAC_ATTACK.get());
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
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
        return new Animation[]{ANIMATION_LUNGE};
    }


    public float getArmSpinSpeed() {
        return this.entityData.get(SPIN_SPEED);
    }

    public void setArmSpinSpeed(float spinSpeed) {
        this.entityData.set(SPIN_SPEED, spinSpeed);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    private void spawnArmSwingParticles(boolean left){
        Vec3 dustMotion = new Vec3(0F, 0.2F, random.nextFloat() * 0.5F - 0.25F).scale(this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
        Vec3 armDustPosition = this.position().add(new Vec3(left ? 0.75F : -0.75F, 1, random.nextFloat() * -0.5F + 0.5F).scale(this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));

        BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), armDustPosition)).below();
        BlockState state = this.level().getBlockState(ground);
        if (state.isSolid()) {
            level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, armDustPosition.x, ground.getY() + 1, armDustPosition.z, dustMotion.x, dustMotion.y, dustMotion.z);
        }
    }

    private void hurtMobsFromArmSwing(boolean left, float damageAmount, float knockbackAmount) {
        boolean strong = random.nextFloat() < 0.1F;
        if(strong){
            damageAmount *= 1.5F;
            knockbackAmount = 1F;
        }
        Vec3 armHurtPosition = this.position().add(new Vec3(left ? 0.75F : -0.75F, 1, 1.25F).scale(this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));
        AABB hurtBox = new AABB(armHurtPosition.x - 1.0F, armHurtPosition.y - 1.0F, armHurtPosition.z - 1.0F, armHurtPosition.x + 1.0F, armHurtPosition.y + 1.0F, armHurtPosition.z + 1.0F);
        DamageSource damageSource = this.damageSources().mobAttack(this);
        for(LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, hurtBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
            if(!living.is(this) && !living.isAlliedTo(this) && living.getType() != this.getType()){
                if(this.getPossessedByLicowitchId() != -1){
                    LicowitchEntity witch = this.getPossessingLicowitch(level());
                    if(witch != null && witch.isFriendlyFire(living)){
                        return;
                    }
                }
                if(living.distanceTo(this) < 3.15F && living.hurt(damageSource, damageAmount)){
                    living.knockback(knockbackAmount, this.getX() - living.getX(), this.getZ() - living.getZ());
                }
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CANIAC_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CANIAC_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CANIAC_DEATH.get();
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

    public float getArmAngle(boolean left, float partialTicks) {
        if (left) {
            return prevLeftArmRot + (leftArmRot - prevLeftArmRot) * partialTicks;
        } else {
            return prevRightArmRot + (rightArmRot - prevRightArmRot) * partialTicks;
        }
    }

    public float getRunProgress(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public float getStepHeight() {
        return hasRunningAttributes ? 1.1F : 0.6F;
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

}
