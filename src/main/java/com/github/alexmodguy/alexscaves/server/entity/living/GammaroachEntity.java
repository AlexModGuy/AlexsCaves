package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTarget3DGoal;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class GammaroachEntity extends PathfinderMob implements IAnimatedEntity {

    private static final Predicate<LivingEntity> IRRADIATED_TARGET = (mob) -> {
        return mob.hasEffect(ACEffectRegistry.IRRADIATED.get()) && !(mob instanceof RaycatEntity);
    };
    private Animation currentAnimation;
    private int animationTick;
    public static final Animation ANIMATION_SPRAY = Animation.create(40);
    public static final Animation ANIMATION_RAM = Animation.create(25);

    private static final EntityDataAccessor<Integer> SPRAY_COOLDOWN = SynchedEntityData.defineId(GammaroachEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FED = SynchedEntityData.defineId(GammaroachEntity.class, EntityDataSerializers.BOOLEAN);

    public GammaroachEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 20, false, true, IRRADIATED_TARGET));
    }

    public static boolean isValidLightLevel(ServerLevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource) {
        if (levelAccessor.getBrightness(LightLayer.SKY, blockPos) > randomSource.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = levelAccessor.getLevel().isThundering() ? levelAccessor.getMaxLocalRawBrightness(blockPos, 10) : levelAccessor.getMaxLocalRawBrightness(blockPos);
            return lvt_3_1_ <= randomSource.nextInt(8);
        }
    }

    public static boolean canMonsterSpawnInLight(EntityType<GammaroachEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return isValidLightLevel(levelAccessor, blockPos, randomSource) && checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }

    public static <T extends Mob> boolean checkGammaroachSpawnRules(EntityType<GammaroachEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return canMonsterSpawnInLight(entityType, iServerWorld, reason, pos, random);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPRAY_COOLDOWN, 0);
        this.entityData.define(FED, false);
    }

    public int getSprayCooldown() {
        return this.entityData.get(SPRAY_COOLDOWN);
    }

    public void setSprayCooldown(int time) {
        this.entityData.set(SPRAY_COOLDOWN, time);
    }

    public boolean isFed() {
        return this.entityData.get(FED);
    }

    public void setFed(boolean fed) {
        this.entityData.set(FED, fed);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.4D).add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ATTACK_DAMAGE, 2);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.IRRADIATED.get();
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !requiresCustomPersistence();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || this.isFed();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return 0.5F - Math.max(worldIn.getBrightness(LightLayer.BLOCK, pos), worldIn.getBrightness(LightLayer.SKY, pos));
    }

    public void tick() {
        super.tick();
        if (this.getSprayCooldown() > 0) {
            this.setSprayCooldown(this.getSprayCooldown() - 1);
        }
        if (this.getAnimation() == ANIMATION_SPRAY) {
            if (this.getAnimationTick() == 10) {
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY() + 0.2F, this.getZ());
                areaeffectcloud.setParticle(ACParticleRegistry.GAMMAROACH.get());
                areaeffectcloud.setFixedColor(0X77D60E);
                areaeffectcloud.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 2000));
                areaeffectcloud.setRadius(2.3F);
                areaeffectcloud.setDuration(200);
                areaeffectcloud.setWaitTime(10);
                areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());
                this.level().addFreshEntity(areaeffectcloud);
            } else if (this.getAnimationTick() >= 10 && this.getAnimationTick() <= 30) {
                Vec3 randomOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalize().scale(1).add(this.getEyePosition());
                this.level().addParticle(ACParticleRegistry.GAMMAROACH.get(), this.getRandomX(2), this.getEyeY(), this.getRandomZ(2), randomOffset.x, randomOffset.y + 0.23D, randomOffset.z);

            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
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
        return new Animation[]{ANIMATION_SPRAY, ANIMATION_RAM};
    }

    public void triggerSpraying() {
        if (this.getSprayCooldown() <= 0 && this.getAnimation() == NO_ANIMATION) {
            this.playSound(ACSoundRegistry.GAMMAROACH_SPRAY.get());
            this.setAnimation(ANIMATION_SPRAY);
            this.setSprayCooldown(10000 + random.nextInt(24000));
        }
    }

    public boolean hurt(DamageSource damageSource, float damageAmount) {
        boolean prev = super.hurt(damageSource, damageAmount);
        Entity hurter = damageSource.getEntity();
        if (prev && hurter instanceof LivingEntity living && !living.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
            triggerSpraying();
        }
        return prev;
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_RAM || this.getAnimation() == ANIMATION_SPRAY) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSprayCooldown(compound.getInt("SprayCooldown"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SprayCooldown", this.getSprayCooldown());
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public float getStepHeight() {
        return 1.1F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult prev = super.mobInteract(player, hand);
        if (prev != InteractionResult.SUCCESS) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(ACItemRegistry.SPELUNKIE.get()) && (!level().isClientSide && this.getTarget() == player || !isFed())) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.setFed(true);
                this.setLastHurtByMob(null);
                this.setTarget(null);
                this.level().broadcastEntityEvent(this, (byte) 49);
                return InteractionResult.SUCCESS;
            }
        }
        return prev;
    }

    public void handleEntityEvent(byte b) {
        if (b == 49) {
            ItemStack itemstack = new ItemStack(ACItemRegistry.SPELUNKIE.get());
            for (int i = 0; i < 8; ++i) {
                Vec3 headPos = (new Vec3(0D, 0.1D, 0.5D)).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + headPos.x, this.getY(0.5) + headPos.y, this.getZ() + headPos.z, (random.nextFloat() - 0.5F) * 0.1F, random.nextFloat() * 0.15F, (random.nextFloat() - 0.5F) * 0.1F);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GAMMAROACH_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GAMMAROACH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GAMMAROACH_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GAMMAROACH_STEP.get(), 1.0F, 1.0F);
        }
    }

    private class MeleeGoal extends Goal {

        private int checkForMobsTime = 0;
        private LivingEntity pickupMonster = null;

        public MeleeGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GammaroachEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        public void stop() {
            if (pickupMonster != null) {
                if (pickupMonster.isPassengerOfSameVehicle(GammaroachEntity.this)) {
                    pickupMonster.stopRiding();
                }
                pickupMonster = null;
                checkForMobsTime = 20;
            }
        }

        @Override
        public void tick() {
            checkForMobsTime--;
            LivingEntity target = GammaroachEntity.this.getTarget();
            if (target != null && target.isAlive()) {
                if (checkForMobsTime < 0) {
                    checkForMobsTime = 120 + GammaroachEntity.this.random.nextInt(100);
                    Predicate<Entity> monsterAway = (animal) -> animal instanceof Enemy && !(animal instanceof GammaroachEntity) && animal.distanceTo(target) > 5 && !animal.isPassenger();
                    List<Mob> list = GammaroachEntity.this.level().getEntitiesOfClass(Mob.class, GammaroachEntity.this.getBoundingBox().inflate(30, 12, 30), EntitySelector.NO_SPECTATORS.and(monsterAway));
                    list.sort(Comparator.comparingDouble(GammaroachEntity.this::distanceToSqr));
                    if (!list.isEmpty()) {
                        pickupMonster = list.get(0);
                    }
                }
                if (pickupMonster == null || pickupMonster.isPassengerOfSameVehicle(GammaroachEntity.this)) {
                    GammaroachEntity.this.getNavigation().moveTo(target, 1.0D);
                    GammaroachEntity.this.lookAt(target, 180, 30);
                    if (GammaroachEntity.this.distanceTo(target) < 1.5F + target.getBbWidth()) {
                        if (GammaroachEntity.this.getAnimation() == NO_ANIMATION) {
                            GammaroachEntity.this.setAnimation(GammaroachEntity.ANIMATION_RAM);
                        } else if (GammaroachEntity.this.getAnimation() == GammaroachEntity.ANIMATION_RAM && GammaroachEntity.this.getAnimationTick() > 8 && GammaroachEntity.this.getAnimationTick() < 15) {
                            GammaroachEntity.this.playSound(ACSoundRegistry.GAMMAROACH_ATTACK.get());
                            target.hurt(damageSources().mobAttack(GammaroachEntity.this), (float) GammaroachEntity.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        }
                        if (pickupMonster != null) {
                            pickupMonster.stopRiding();
                            pickupMonster = null;
                        }
                    }
                } else if (pickupMonster.isAlive() && (!pickupMonster.isPassenger())) {
                    GammaroachEntity.this.getNavigation().moveTo(pickupMonster, 1.0D);
                    GammaroachEntity.this.lookAt(pickupMonster, 180, 30);
                    if (GammaroachEntity.this.distanceTo(pickupMonster) < 1.5F + pickupMonster.getBbWidth()) {
                        pickupMonster.startRiding(GammaroachEntity.this, true);
                        pickupMonster.hurt(damageSources().cactus(), 1);
                    }
                } else {
                    pickupMonster = null;
                }
            }
        }
    }
}
