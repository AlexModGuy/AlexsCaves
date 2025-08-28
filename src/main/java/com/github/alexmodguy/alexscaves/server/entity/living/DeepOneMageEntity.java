package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.item.WaterBoltEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.WaveEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DeepOneMageEntity extends DeepOneBaseEntity {

    public static final Animation ANIMATION_DISAPPEAR = Animation.create(55);
    public static final Animation ANIMATION_ATTACK = Animation.create(25);
    public static final Animation ANIMATION_SPIN = Animation.create(70);
    public static final Animation ANIMATION_TRADE = Animation.create(75);
    private static final EntityDimensions SWIMMING_SIZE = new EntityDimensions(1.2F, 1.5F, false);

    private int spinCooldown = 0;
    private int rangedCooldown = 0;
    private Vec3 strafeTarget = null;
    public static final ResourceLocation BARTER_LOOT = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gameplay/deep_one_mage_barter");
    private boolean isMageInWater = true;

    public DeepOneMageEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 80.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DeepOneAttackGoal(this));
        this.goalSelector.addGoal(1, new DeepOneBarterGoal(this));
        this.goalSelector.addGoal(2, new DeepOneReactToPlayerGoal(this));
        this.goalSelector.addGoal(3, new DeepOneDisappearGoal(this));
        this.goalSelector.addGoal(4, new DeepOneWanderGoal(this, 12, 1D));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D, 45) {
            @Override
            public boolean canUse() {
                return !DeepOneMageEntity.this.isInWaterOrBubble() && super.canUse() && DeepOneMageEntity.this.getAnimation() != DeepOneMageEntity.ANIMATION_TRADE;
            }

            @Override
            public boolean canContinueToUse() {
                return !DeepOneMageEntity.this.isInWaterOrBubble() && DeepOneMageEntity.this.getAnimation() != DeepOneMageEntity.ANIMATION_TRADE && super.canContinueToUse();
            }

            protected Vec3 getPosition() {
                Vec3 prev = super.getPosition();
                return prev == null ? prev : prev.add(0, 1, 0);
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByHostileTargetGoal());
        this.targetSelector.addGoal(2, new DeepOneTargetHostilePlayersGoal(this));
    }

    @Override
    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1, 0));
            this.navigation = createFlightNavigation(level());
            this.moveControl = new FlightMoveController();
            this.isLandNavigator = true;
        } else {
            this.navigation = createNavigation(level());
            this.moveControl = new VerticalSwimmingMoveControl(this, 0.8F, 10);
            this.isLandNavigator = false;
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level().getBlockState(pos).isAir() ? 10.0F : super.getWalkTargetValue(pos, level);
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.isInWaterOrBubble() && !this.hasEffect(ACEffectRegistry.BUBBLED.get())) {
            this.addEffect(new MobEffectInstance(ACEffectRegistry.BUBBLED.get(), 200));
        }
        if (this.isInWaterOrBubble() && this.hasEffect(ACEffectRegistry.BUBBLED.get())) {
            this.removeEffect(ACEffectRegistry.BUBBLED.get());
        }
        isMageInWater = this.isInWaterOrBubble();
        if (this.getAnimation() == ANIMATION_SPIN) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6F));
            LivingEntity target = this.getTarget();
            if (target != null) {
                Vec3 vec = target.position().subtract(this.position()).normalize();
                this.setDeltaMovement(this.getDeltaMovement().add(vec.scale(0.1)));
            }
            if (this.getAnimationTick() % 6 == 0) {
                AABB bashBox = this.getBoundingBox().inflate(2.0D, 0, 2.0D);
                for (LivingEntity entity : DeepOneMageEntity.this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
                    if (!isAlliedTo(entity) && !(entity instanceof DeepOneBaseEntity)) {
                        checkAndDealMeleeDamage(entity, 0.4F, 1.0F);
                    }
                }

            }
        }
        if (this.getAnimation() == ANIMATION_ATTACK) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                if (this.getAnimationTick() == 16) {
                    useMagicAttack(target);
                } else if (this.getAnimationTick() < 16) {
                    this.level().broadcastEntityEvent(this, (byte) 68);
                }
                this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 180.0F, 10.0F);
            }
        }
        if (spinCooldown > 0) {
            spinCooldown--;
        }
        if (rangedCooldown > 0) {
            rangedCooldown--;
        }
    }

    @Override
    protected ResourceLocation getBarterLootTable() {
        return BARTER_LOOT;
    }

    public boolean isNoGravity() {
        return !this.isDeepOneSwimming() || super.isNoGravity();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void useMagicAttack(LivingEntity target) {
        this.level().broadcastEntityEvent(this, (byte) 68);
        if (random.nextBoolean()) {
            int lifespan = (int) (Math.floor(this.distanceTo(target))) + 10;
            Vec3 vec3 = target.position().subtract(this.position());
            for (int i = -2; i <= 2; i++) {
                WaveEntity waveEntity = new WaveEntity(this.level(), this);
                waveEntity.setPos(this.getX(), target.getY(), this.getZ());
                waveEntity.setLifespan(lifespan);
                waveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) + (i * 10));
                this.level().addFreshEntity(waveEntity);
            }
        } else {
            WaterBoltEntity waterBoltEntity = new WaterBoltEntity(this.level(), this);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - waterBoltEntity.getY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            waterBoltEntity.setBubbling(random.nextInt(2) == 0);
            waterBoltEntity.setArcingTowards(target.getUUID());
            waterBoltEntity.shoot(d0, d1 + d3 * (double) 0.67F, d2, 0.6F, 30);
            this.level().addFreshEntity(waterBoltEntity);

        }
    }

    @Override
    public void startAttackBehavior(LivingEntity target) {
        this.yBodyRot = this.getYRot();
        double distance = this.distanceTo(target);
        float f = this.getBbWidth() + target.getBbWidth();
        if (distance > 20 + f) {
            this.getNavigation().moveTo(target, 1.2);
        } else if (distance < 2F + f && spinCooldown <= 0) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_SPIN);
                spinCooldown = 1000 + random.nextInt(60);
            }
        } else {
            if (strafeTarget == null || strafeTarget.distanceTo(this.position()) < 4) {
                Vec3 possible = target.position().add(random.nextInt(20) - 10, random.nextInt(2), random.nextInt(20) - 10);
                if (!isTargetBlocked(possible)) {
                    strafeTarget = possible;
                }
            } else {
                this.getNavigation().moveTo(strafeTarget.x, strafeTarget.y, strafeTarget.z, 1.5F);
            }
            if (rangedCooldown <= 0 && this.getAnimation() == NO_ANIMATION && hasLineOfSight(target)) {
                this.setAnimation(ANIMATION_ATTACK);
                this.playSound(ACSoundRegistry.DEEP_ONE_MAGE_ATTACK.get());
                rangedCooldown = 30 + random.nextInt(20);
            }
            this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 30.0F, 10.0F);

        }
    }

    private boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public EntityDimensions getSwimmingSize() {
        return SWIMMING_SIZE;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_DISAPPEAR, ANIMATION_ATTACK, ANIMATION_SPIN, ANIMATION_TRADE};
    }

    public boolean isDeepOneSwimming() {
        return isMageInWater && !this.onGround();
    }

    @Override
    public Animation getTradingAnimation() {
        return ANIMATION_TRADE;
    }

    protected PathNavigation createFlightNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public boolean startDisappearBehavior(Player player) {
        this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ(), 20.0F, (float) this.getMaxHeadXRot());
        this.getNavigation().stop();
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_DISAPPEAR);
        }
        if (this.getAnimation() == ANIMATION_DISAPPEAR) {
            if (this.getAnimationTick() > 50) {
                this.level().broadcastEntityEvent(this, (byte) 67);
                this.remove(RemovalReason.DISCARDED);
                return true;
            } else {
                this.level().broadcastEntityEvent(this, (byte) 66);
            }
        }
        return false;
    }

    public void handleEntityEvent(byte b) {
        if (b == 66) {
            for (int i = 0; i < 2 + random.nextInt(4); i++) {
                this.level().addParticle(random.nextBoolean() ? ACParticleRegistry.DEEP_ONE_MAGIC.get() : ParticleTypes.DOLPHIN, this.getRandomX(1F), this.getRandomY(), this.getRandomZ(1F), 0F, -0.1F, 0F);
            }
        } else if (b == 67) {
            for (int i = 0; i < 13 + random.nextInt(6); i++) {
                this.level().addParticle(ACParticleRegistry.DEEP_ONE_MAGIC.get(), this.getRandomX(1F), this.getRandomY(), this.getRandomZ(1F), random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
                this.level().addParticle(ParticleTypes.NAUTILUS, this.getRandomX(1F), this.getRandomY() + 1, this.getRandomZ(1F), random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            }
        } else if (b == 68) {
            Vec3 deltaPos = this.position().add(getDeltaMovement());
            Vec3 rVec = new Vec3(0.65F, this.getBbHeight() * 0.5F + 0.15F, 0.2F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F)).add(deltaPos);
            Vec3 lVec = new Vec3(-0.65F, this.getBbHeight() * 0.5F + 0.15F, 0.2F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F)).add(deltaPos);
            this.level().addParticle(ACParticleRegistry.DEEP_ONE_MAGIC.get(), rVec.x + (random.nextFloat() - 0.5F) * 0.1F, rVec.y + (random.nextFloat() - 0.5F) * 0.1F, rVec.z + (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().x, 1, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().z);
            this.level().addParticle(ACParticleRegistry.DEEP_ONE_MAGIC.get(), lVec.x + (random.nextFloat() - 0.5F) * 0.1F, lVec.y + (random.nextFloat() - 0.5F) * 0.1F, lVec.z + (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().x, 1, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().z);

        } else {
            super.handleEntityEvent(b);
        }
    }


    public float getStepHeight() {
        return 1.3F;
    }

    @Override
    public SoundEvent getAdmireSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_ADMIRE.get();
    }

    protected SoundEvent getAmbientSound() {
        return soundsAngry() ? ACSoundRegistry.DEEP_ONE_MAGE_HOSTILE.get() : ACSoundRegistry.DEEP_ONE_MAGE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.DEEP_ONE_MAGE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_DEATH.get();
    }
    
    class FlightMoveController extends MoveControl {
        private final Mob parentEntity;


        public FlightMoveController() {
            super(DeepOneMageEntity.this);
            this.parentEntity = DeepOneMageEntity.this;
        }

        public void tick() {
            parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0, Math.sin(tickCount * 0.1) * 0.005F, 0));
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                LivingEntity attackTarget = parentEntity.getTarget();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.025D / d0);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1));
                if (d0 < width * 0.3F) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width && attackTarget == null) {
                    if (DeepOneMageEntity.this.getTarget() != null) {
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    } else {
                        parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                    }
                }
            }
        }
    }
}
