package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.*;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import com.google.common.base.Predicates;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TremorzillaEntity extends DinosaurEntity implements KeybindUsingMount, IAnimatedEntity, ShakesScreen, KaijuMob, ActivatesSirens, ITallWalker {
    private static EntityDataAccessor<Optional<Vec3>> BEAM_END_POSITION = SynchedEntityData.defineId(TremorzillaEntity.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());
    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(TremorzillaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHARGE = SynchedEntityData.defineId(TremorzillaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MAX_BEAM_BREAK_LENGTH = SynchedEntityData.defineId(TremorzillaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPIKES_DOWN_PROGRESS = SynchedEntityData.defineId(TremorzillaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FIRING = SynchedEntityData.defineId(TremorzillaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TAME_ATTEMPTS = SynchedEntityData.defineId(TremorzillaEntity.class, EntityDataSerializers.INT);
    public static final Animation ANIMATION_SPEAK = Animation.create(20);
    public static final Animation ANIMATION_ROAR_1 = Animation.create(60);
    public static final Animation ANIMATION_ROAR_2 = Animation.create(60);
    public static final Animation ANIMATION_RIGHT_SCRATCH = Animation.create(35);
    public static final Animation ANIMATION_LEFT_SCRATCH = Animation.create(35);
    public static final Animation ANIMATION_RIGHT_TAIL = Animation.create(40);
    public static final Animation ANIMATION_LEFT_TAIL = Animation.create(40);
    public static final Animation ANIMATION_RIGHT_STOMP = Animation.create(35);
    public static final Animation ANIMATION_LEFT_STOMP = Animation.create(35);
    public static final Animation ANIMATION_BITE = Animation.create(25);
    public static final Animation ANIMATION_PREPARE_BREATH = Animation.create(20);
    public static final Animation ANIMATION_CHEW = Animation.create(35);
    private static final int MAX_CHARGE = 1000;
    private final TremorzillaPartEntity[] allParts;
    public final TremorzillaPartEntity tailPart1;
    public final TremorzillaPartEntity tailPart2;
    public final TremorzillaPartEntity tailPart3;
    public final TremorzillaPartEntity tailPart4;
    public final TremorzillaPartEntity tailPart5;
    private float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    protected float tailXRot;
    protected float tailYRot;
    public TremorzillaLegSolver legSolver = new TremorzillaLegSolver(1F, 2.15F, 3);
    private static final EntityDimensions SWIMMING_SIZE = new EntityDimensions(4.0F, 5.0F, true);
    private Animation currentAnimation;
    private int animationTick;
    private float lastYawBeforeWhip;
    protected boolean isLandNavigator;
    private double lastStompX = 0;
    private double lastStompZ = 0;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private float beamProgress;
    private float prevBeamProgress;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private int lastScareTimestamp;
    private int blockBreakCounter = 0;
    private int steamFromMouthFor = 0;
    private int roarCooldown = 0;
    public Vec3 beamServerTarget;
    public Vec3 prevClientBeamEndPosition;
    public Vec3 clientBeamEndPosition;
    public boolean wantsToUseBeamFromServer = false;
    private float prevClientSpikesDownAmount = 0;
    private float clientSpikesDownAmount = 0;
    private int beamTime = 0;
    private int maxBeamTime = 200;
    private int timeWithoutTarget = 0;
    public int timeSwimming;
    private boolean wasPreviouslyChild;
    private final Explosion dummyExplosion;
    private int chargeSoundCooldown = 0;
    private boolean makingBeamSoundOnClient = false;
    private Player lastFedPlayer = null;

    private int killCountFromBeam = 0;

    public TremorzillaEntity(EntityType entityType, Level level) {
        super(entityType, level);
        switchNavigator(true);
        this.tailPart1 = new TremorzillaPartEntity(this, this, 3F, 3F);
        this.tailPart2 = new TremorzillaPartEntity(this, tailPart1, 2.5F, 2F);
        this.tailPart3 = new TremorzillaPartEntity(this, tailPart2, 2.5F, 1.5F);
        this.tailPart4 = new TremorzillaPartEntity(this, tailPart3, 2.5F, 1.5F);
        this.tailPart5 = new TremorzillaPartEntity(this, tailPart4, 2F, 1F);
        this.allParts = new TremorzillaPartEntity[]{tailPart1, tailPart2, tailPart3, tailPart4, tailPart5};
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        dummyExplosion = new Explosion(level(), null, this.getX(), this.getY(), this.getZ(), 10.0F, List.of());
    }

    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.lookControl = new LookControl(this);
            this.moveControl = new MoveControl(this);
            this.navigation = createNavigation(level());
            this.isLandNavigator = true;
        } else {
            this.lookControl = new SmoothSwimmingLookControl(this, 10);
            this.moveControl = new DirectAquaticMoveControl(this, 0.8F, 40);
            this.navigation = new AllFluidsPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEAM_END_POSITION, Optional.empty());
        this.entityData.define(SWIMMING, false);
        this.entityData.define(CHARGE, MAX_CHARGE);
        this.entityData.define(SPIKES_DOWN_PROGRESS, 0F);
        this.entityData.define(MAX_BEAM_BREAK_LENGTH, 100F);
        this.entityData.define(TAME_ATTEMPTS, 0);
        this.entityData.define(FIRING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.ARMOR, 10.0D).add(Attributes.FOLLOW_RANGE, 128.0D).add(Attributes.ATTACK_DAMAGE, 30.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new TremorzillaAttackGoal(this));
        this.goalSelector.addGoal(2, new TremorzillaFollowOwnerGoal(this, 1.0D, 14.0F, 6.0F));
        this.goalSelector.addGoal(3, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(4, new AnimalLayEggGoal(this, 100, 1));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, Ingredient.of(ACBlockRegistry.WASTE_DRUM.get(), ACBlockRegistry.NUCLEAR_BOMB.get()), false));
        this.goalSelector.addGoal(6, new TremorzillaWanderGoal(this));
        this.goalSelector.addGoal(7, new LookAtLargeMobsGoal(this, 3.0F, 30.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
    }

    public boolean isFakeEntity() {
        return this.firstTick;
    }

    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        prevScreenShakeAmount = screenShakeAmount;
        prevBeamProgress = beamProgress;
        prevClientBeamEndPosition = clientBeamEndPosition;
        prevClientSpikesDownAmount = clientSpikesDownAmount;
        boolean water = this.isInFluidType();
        if (water && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!water && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (isTremorzillaSwimming()) {
            timeSwimming++;
            this.setAirSupply(getMaxAirSupply());
        } else {
            timeSwimming = 0;
        }

        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        if (this.isFiring() && beamProgress < 5F) {
            beamProgress++;
        }
        if (!this.isFiring() && beamProgress > 0F) {
            beamProgress--;
        }

        clientSpikesDownAmount = Mth.approach(clientSpikesDownAmount, this.getSpikesDownAmount(), 0.1F);
        Vec3 beamEnd = getBeamEndPosition();
        clientBeamEndPosition = beamEnd;
        if (this.isFiring()) {
            boolean flag = false;
            if (this.isFiring() && beamEnd != null) {
                Vec3 vec3 = beamEnd.subtract(getBeamShootFrom(1.0F));
                float beamYaw = -((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI);
                if (Mth.degreesDifferenceAbs(beamYaw, Mth.wrapDegrees(this.yBodyRot)) > 80F) {
                    flag = true;
                    this.setYRot(Mth.approachDegrees(this.getYRot(), beamYaw, 10));
                    this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, beamYaw, 10);
                    lastYawBeforeWhip = beamYaw;
                }
            }
            if (!flag) {
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, lastYawBeforeWhip, 15);
            }
        } else if (this.getAnimation() != ANIMATION_RIGHT_TAIL && this.getAnimation() != ANIMATION_LEFT_TAIL) {
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.getYRot(), 4);
            this.yHeadRot = Mth.approachDegrees(this.yHeadRotO, this.yHeadRot, 2);
            lastYawBeforeWhip = this.yBodyRot;
        } else {
            float negative = this.getAnimation() == ANIMATION_RIGHT_TAIL ? -1 : 1;
            float target = 0;
            if (this.getAnimationTick() < 5) {
                float f = this.getAnimationTick() / 5F;
                target = f * -10;
            } else {
                float f = (this.getAnimationTick() - 10) / 15F;
                target = Mth.clamp(f, 0F, 1F) * 170;
            }
            if (this.getAnimationTick() > 25F) {
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, lastYawBeforeWhip, 15);
            } else {
                this.walkAnimation.setSpeed(1 + AlexsCaves.PROXY.getPartialTicks());
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, lastYawBeforeWhip + negative * target, 70);
            }
        }
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.15F);
        }
        if (this.onGround() && !this.isInFluidType() && this.walkAnimation.speed() > 0.1F && !this.isBaby() && !this.isNoAi() && this.isAlive()) {
            float f = (float) Math.cos(this.walkAnimation.position() * 0.25F - 1.5F);
            float f1 = (float) Math.cos(this.walkAnimation.position() * 0.25F - 1.0F);
            float f2 = (float) Math.sin(this.walkAnimation.position() * 0.25F - 1.0F);
            if (Math.abs(f) < 0.2F) {
                if (screenShakeAmount <= 0.3) {
                    this.playSound(ACSoundRegistry.TREMORZILLA_STOMP.get(), 6.0F, 0.7F);
                }
                screenShakeAmount = 2.0F;
            }
            if (this.walkAnimation.speed() > 0.5F && Math.abs(f1) < 0.1F) {
                stompEffect(f2 > 0, 1F, 1.3F, 0.4F + this.walkAnimation.speed(), 2F);
            }
        }
        tickMultipart();
        if (level().isClientSide) {
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
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && getMeterAmount() >= 1.0F) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
                if (AlexsCaves.PROXY.isKeyDown(3) && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 3));
                }
            }
            if (this.isFiring() && beamProgress > 0) {
                if (!makingBeamSoundOnClient) {
                    AlexsCaves.PROXY.playWorldSound((Object) this, (byte) 16);
                    makingBeamSoundOnClient = true;
                }
            }
            if (!isFiring() && makingBeamSoundOnClient) {
                AlexsCaves.PROXY.clearSoundCacheFor(this);
                makingBeamSoundOnClient = false;
            }
        } else {
            double waterHeight = getMaxFluidHeight();
            if (waterHeight > 0 && waterHeight < this.getBbHeight() - 1.0F && !this.verticalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.02, 0));
            }
            this.setTremorzillaSwimming(waterHeight > 2.0F);
        }
        if (this.isAlive()) {
            if (this.isFiring()) {
                tickBreath();
            } else if (steamFromMouthFor > 0 && level().isClientSide) {
                level().addAlwaysVisibleParticle(ACParticleRegistry.TREMORZILLA_STEAM.get(), true, this.getX(), this.getEyeY(), this.getZ(), this.getId(), 0, 0);
            }
            if(!this.isFiring() && killCountFromBeam > 0){
                if(killCountFromBeam > 20 && !this.level().isClientSide){
                    if(this.isVehicle()){
                        for(Entity passenger : this.getPassengers()){
                            ACAdvancementTriggerRegistry.TREMORZILLA_KILL_BEAM.triggerForEntity(passenger);
                        }
                    }
                }
                killCountFromBeam = 0;
            }
            if ((this.getAnimation() == ANIMATION_RIGHT_SCRATCH || this.getAnimation() == ANIMATION_LEFT_SCRATCH) && this.getAnimationTick() == 18) {
                Vec3 center = new Vec3(0, 5 * this.getScale(), 6 * this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F)).add(position());
                this.hurtEntitiesAround(center, 6.0F, 10.0F, 2.0F, false, true, true);
                if (!level().isClientSide) {
                    this.breakBlocksAround(center, 3.0F, false, false, 0.6F);
                }
            }
            if ((this.getAnimation() == ANIMATION_RIGHT_TAIL || this.getAnimation() == ANIMATION_LEFT_TAIL) && this.getAnimationTick() >= 10 && this.getAnimationTick() < 25) {
                this.hurtEntitiesAround(tailPart1.centeredPosition(), 4.0F, 10.0F, 2.0F, false, true, true);
                this.hurtEntitiesAround(tailPart2.centeredPosition(), 4.0F, 10.0F, 2.0F, false, true, true);
                this.hurtEntitiesAround(tailPart3.centeredPosition(), 4.0F, 10.0F, 2.0F, false, true, true);
                this.hurtEntitiesAround(tailPart4.centeredPosition(), 3.0F, 10.0F, 2.0F, false, true, true);
                this.hurtEntitiesAround(tailPart5.centeredPosition(), 3.0F, 10.0F, 2.0F, false, true, true);
                if (!level().isClientSide) {
                    this.breakBlocksAround(tailPart1.centeredPosition(), 2.0F, false, false, 0.6F);
                    this.breakBlocksAround(tailPart2.centeredPosition(), 2.0F, false, false, 0.6F);
                    this.breakBlocksAround(tailPart3.centeredPosition(), 2.0F, false, false, 0.6F);
                    this.breakBlocksAround(tailPart4.centeredPosition(), 1.0F, false, false, 0.6F);
                    this.breakBlocksAround(tailPart5.centeredPosition(), 1.0F, false, false, 0.6F);
                }
            }
            if ((this.getAnimation() == ANIMATION_LEFT_STOMP || this.getAnimation() == ANIMATION_RIGHT_STOMP) && this.getAnimationTick() == 18) {
                this.stompEffect(this.getAnimation() == ANIMATION_LEFT_STOMP, 2.0F, 5F, 1.2F, 10F);
                screenShakeAmount = 4.0F;
            }
            if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 10) {
                Vec3 center = new Vec3(0, 7 * this.getScale(), 5 * this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F)).add(position());
                this.hurtEntitiesAround(center, 7.5F, 10.0F, 2.0F, false, true, true);
                if (!level().isClientSide) {
                    this.breakBlocksAround(center, 4.0F, false, false, 0.6F);
                }
            }
            if (this.getAnimation() == ANIMATION_ROAR_1 && this.getAnimationTick() > 10 && this.getAnimationTick() < 50 || this.getAnimation() == ANIMATION_ROAR_2 && this.getAnimationTick() > 15 && this.getAnimationTick() < 50) {
                screenShakeAmount = 8.0F;
                if (!level().isClientSide) {
                    scareMobs();
                }
            }
            if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 5 && !this.isFiring()) {
                actuallyPlayAmbientSound();
            }
        }
        if (!level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target == null || !target.isAlive()) {
                timeWithoutTarget++;
            } else {
                timeWithoutTarget = 0;
            }
            if (wantsToUseBeamFromServer && (timeWithoutTarget > 100 && !this.isVehicle() || this.isInSittingPose())) {
                wantsToUseBeamFromServer = false;
            }
            if (isFiring()) {
                wantsToUseBeamFromServer = false;
                int iterateBy = 1;
                if (!isVehicle()) {
                    if (target == null || !target.isAlive()) {
                        iterateBy = 3;
                    } else if (target.distanceTo(this) > 100) {
                        iterateBy = 8;
                    }
                }
                beamTime += iterateBy;
                if (beamTime > maxBeamTime) {
                    beamTime = 0;
                    this.setFiring(false);
                    this.playSound(ACSoundRegistry.TREMORZILLA_BEAM_END.get(), 8.0F, 1.0F);
                    this.beamServerTarget = null;
                    this.setBeamEndPosition(null);
                    this.setCharge(0);
                } else {
                    if (!isStunned()) {
                        tickBeamTargeting();
                    }
                    this.setCharge(MAX_CHARGE);
                }
            } else if (wantsToUseBeamFromServer && this.isPowered()) {
                float spikesThreshold = 0.95F;
                if (this.getAnimation() == ANIMATION_PREPARE_BREATH && this.getSpikesDownAmount() >= spikesThreshold && this.getAnimationTick() > 15 && !this.isFiring()) {
                    this.maxBeamTime = 100 + random.nextInt(150);
                    this.beamServerTarget = createInitialBeamVec();
                    this.lookAt(EntityAnchorArgument.Anchor.EYES, beamServerTarget);
                    this.setFiring(true);
                    this.setMaxBeamBreakLength(100F);
                }
                if (this.getSpikesDownAmount() >= spikesThreshold && this.getAnimation() == NO_ANIMATION && !isStunned()) {
                    this.setAnimation(ANIMATION_PREPARE_BREATH);
                    this.playSound(ACSoundRegistry.TREMORZILLA_BEAM_START.get(), 8.0F, 1.0F);
                }
                this.setSpikesDownAmount(Math.min(this.getSpikesDownAmount() + 0.005F, 1F));
                if ((tickCount + this.getId()) % 10 == 0 && level() instanceof ServerLevel serverLevel) {
                    getNearbySirens(serverLevel, 256).forEach(this::activateSiren);
                }
                float f = calculateSpikesDownAmount(this.getSpikesDownAmount(), 6F);
                if (Math.floor(f - 0.005F) != Math.floor(f) && chargeSoundCooldown <= 0 && f <= 5) {
                    float pitch = 0.7F + this.getSpikesDownAmount() * 0.7F;
                    this.playSound(f > 4 ? ACSoundRegistry.TREMORZILLA_CHARGE_COMPLETE.get() : ACSoundRegistry.TREMORZILLA_CHARGE_NORMAL.get(), 8.0F, pitch);
                    chargeSoundCooldown = 19;
                }
                if (chargeSoundCooldown > 0) {
                    chargeSoundCooldown--;
                }
            } else {
                this.setSpikesDownAmount(Math.max(this.getSpikesDownAmount() - 0.05F, 0F));
                if (this.getDeltaMovement().horizontalDistance() < 0.05 && this.getAnimation() == NO_ANIMATION && !this.isDancing() && !this.isInSittingPose() && !this.isNoAi()) {
                    if (random.nextInt(800) == 0 && !this.isVehicle()) {
                        this.tryRoar();
                    }
                }
            }
            if (!this.isPowered()) {
                this.setCharge(this.getCharge() + 1);
            }
            float healthAmount = this.getHealth() / this.getMaxHealth();
            if (healthAmount <= 0.2F) {
                healEveryTick(10, 5.0F);
            } else if (healthAmount <= 0.5F) {
                healEveryTick(20, 3.0F);
            } else {
                healEveryTick(100, 2.0F);
            }
        }
        if (!this.isPowered()) {
            this.setSpikesDownAmount(0.0F);
        }
        if (steamFromMouthFor > 0) {
            steamFromMouthFor--;
        }
        if (roarCooldown > 0) {
            roarCooldown--;
        }
        if (wasPreviouslyChild != this.isBaby()) {
            wasPreviouslyChild = this.isBaby();
            this.refreshDimensions();
            for (TremorzillaPartEntity tremorzillaPartEntity : this.allParts) {
                tremorzillaPartEntity.refreshDimensions();
            }
        }
        if (this.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
            MobEffectInstance instance = this.getEffect(ACEffectRegistry.IRRADIATED.get());
            int level = instance == null ? 1 : 1 + instance.getAmplifier();
            this.heal(level * 12);
            this.removeEffect(ACEffectRegistry.IRRADIATED.get());
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_BITE.get(), 4.0F, this.getVoicePitch());
        }
        if ((this.getAnimation() == ANIMATION_RIGHT_SCRATCH || this.getAnimation() == ANIMATION_LEFT_SCRATCH) && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_SCRATCH_ATTACK.get(), 4.0F, this.getVoicePitch());
        }
        if ((this.getAnimation() == ANIMATION_RIGHT_STOMP || this.getAnimation() == ANIMATION_LEFT_STOMP) && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_STOMP_ATTACK.get(), 4.0F, this.getVoicePitch());
        }
        if ((this.getAnimation() == ANIMATION_RIGHT_TAIL || this.getAnimation() == ANIMATION_LEFT_TAIL) && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_TAIL_ATTACK.get(), 4.0F, this.getVoicePitch());
        }
        if(this.getAnimation() == ANIMATION_CHEW && this.getAnimationTick() % 6 == 0 && this.getAnimationTick() <= 30){
            this.playSound(ACSoundRegistry.TREMORZILLA_EAT.get(), 4.0F, this.getVoicePitch());
            if(level().isClientSide){
                BlockParticleOption particleOption1 = new BlockParticleOption(ParticleTypes.BLOCK, ACBlockRegistry.BLOCK_OF_URANIUM.get().defaultBlockState());
                BlockParticleOption particleOption2 = new BlockParticleOption(ParticleTypes.BLOCK, ACBlockRegistry.WASTE_DRUM.get().defaultBlockState());
                for(int i = 0; i < 8; i++){
                    Vec3 particlesPos = this.getBeamShootFrom(1.0F).add(new Vec3(random.nextBoolean() ? -0.8F : 0.8F, 2, 2.5F + random.nextFloat()).scale(getScale()).xRot((float) Math.toRadians(-this.getXRot())).yRot((float) Math.toRadians(-this.getYHeadRot())));
                    level().addAlwaysVisibleParticle(random.nextInt(3) == 0 ? particleOption2 : particleOption1, true, particlesPos.x, particlesPos.y, particlesPos.z, 0, 0, 0);
                }
            }
        }
        if(!level().isClientSide && this.getAnimation() == ANIMATION_CHEW && this.getAnimationTick() == 34 && lastFedPlayer != null){
            if(!this.isTame()){
                this.setTameAttempts(this.getTameAttempts() + 1);
                if (this.getTameAttempts() >= 4 && getRandom().nextInt(3) == 0) {
                    this.tame(lastFedPlayer);
                    this.clearRestriction();
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            lastFedPlayer = null;
        }
        lastStompX = xo;
        lastStompZ = zo;
    }

    private double getMaxFluidHeight() {
        return getFluidTypeHeight(getMaxHeightFluidType());
    }

    private void healEveryTick(int i, float health) {
        if (tickCount % i == 0) {
            this.heal(health);
        }
    }

    private void tickBeamTargeting() {
        LivingEntity target = this.getTarget();
        Vec3 vec3 = beamServerTarget == null ? this.position() : beamServerTarget;
        Vec3 shootFrom = this.getBeamShootFrom(1.0F);
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            Vec3 riderPointing = player.getViewVector(1.0F).scale(100);
            Vec3 approach = shootFrom.add(riderPointing).subtract(vec3).scale(0.2F).add(vec3);
            beamServerTarget = approach;
        } else if (target != null && target.isAlive()) {
            float time = (float) beamTime / maxBeamTime;
            float accuracy = 1F - (Math.min(0.75F, time) / 0.75F);
            Vec3 position = target.position();
            Vec3 swingVec = new Vec3(Math.sin(tickCount * 0.2F) * 6, 0, Math.cos(tickCount * 0.2F) * -6).yRot((float) Math.toRadians(-this.yBodyRot)).scale(accuracy);
            Vec3 approach = position.add(swingVec).subtract(vec3).scale(0.1F).add(vec3);
            beamServerTarget = approach;
        } else {
            Vec3 newTarget = new Vec3(Math.sin(tickCount * 0.1F) * 10, beamServerTarget.y - shootFrom.y, 6).yRot((float) Math.toRadians(-this.yBodyRot));
            Vec3 approach = shootFrom.add(newTarget).subtract(vec3).scale(0.1F).add(vec3);
            beamServerTarget = approach;
        }
    }

    private Vec3 createInitialBeamVec() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            Vec3 randomRot = new Vec3(-100 + random.nextFloat() * 200F, 0, 15 + 15 * random.nextFloat()).yRot((float) Math.toRadians(-this.yBodyRot + 50 - random.nextFloat() * 100));
            Vec3 position = target instanceof KaijuMob ? target.getEyePosition() : target.position();
            return position.add(randomRot);
        } else if (this.isVehicle()) {
            Vec3 vec3 = new Vec3(0, 0, 10).yRot((float) Math.toRadians(-this.yBodyRot));
            return this.getBeamShootFrom(1.0F).add(vec3);
        } else {
            Vec3 vec3 = new Vec3(0, random.nextBoolean() ? 100 : 20, 6).yRot((float) Math.toRadians(-this.yBodyRot));
            return this.getBeamShootFrom(1.0F).add(vec3);
        }
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_LEFT_STOMP || this.getAnimation() == ANIMATION_RIGHT_STOMP || this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL || this.isFiring() && !this.isVehicle()) {
            vec3d = Vec3.ZERO;
            super.travel(vec3d);
        } else if (this.isInFluidType() && (this.isEffectiveAi() || this.isVehicle())) {
            this.moveRelative(this.getSpeed(), vec3d);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            if (this.horizontalCollision) {
                delta = delta.add(0, 0.05, 0);
            }
            this.setDeltaMovement(delta.scale(0.8D));
            this.calculateEntityAnimation(false);
        } else {
            super.travel(vec3d);
        }
    }

    public int getHeadRotSpeed() {
        return 3;
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.lastStompX, 0, this.getZ() - this.lastStompZ);
        float walkSpeed = 4.0F;
        if (isVehicle()) {
            walkSpeed = 1.5F;
        }
        float f2 = Math.min(f1 * walkSpeed, 1.0F);
        walkAnimation.update(f2, 0.4F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this) && blockBreakCounter <= 0) {
                this.breakBlocksInBoundingBox(0.1F);
                blockBreakCounter = 10;
            }
            if (blockBreakCounter > 0) {
                blockBreakCounter--;
            }
        }
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide && !this.isFiring()) {
            this.setAnimation(ANIMATION_SPEAK);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }

    private void tickMultipart() {
        if (yawPointer == -1) {
            for (int i = 0; i < yawBuffer.length; i++) {
                yawBuffer[i] = this.yBodyRot;
            }
        }
        if (++this.yawPointer == this.yawBuffer.length) {
            this.yawPointer = 0;
        }
        this.yawBuffer[this.yawPointer] = this.yBodyRot;

        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        boolean tail = this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL;
        float tailRotateSpeed = tail ? 25F : this.isTremorzillaSwimming() ? 20F : 5F;
        this.tailXRot = wrapTailDegrees(Mth.approachDegrees(this.tailXRot, getTargetTailXRot(), tailRotateSpeed));
        this.tailYRot = wrapTailDegrees(Mth.approachDegrees(this.tailYRot, getTargetTailYRot(), tailRotateSpeed));
        Vec3 center = this.position().add(0, this.getBbHeight() * 0.5F - getLegSolverBodyOffset(), 0);
        float tailXStep = tailXRot / 5F;
        float tailYStep = tailYRot / 5F;
        this.tailPart1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, isTremorzillaSwimming() ? 0F : -4F, -3.5F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep)).add(center));
        this.tailPart2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, -0.25F, -3.25F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep * 2F)).add(this.tailPart1.centeredPosition()));
        this.tailPart3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -2.5F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep * 3F)).add(this.tailPart2.centeredPosition()));
        this.tailPart4.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -2.5F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep * 4F)).add(this.tailPart3.centeredPosition()));
        this.tailPart5.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -2F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep * 5F)).add(this.tailPart4.centeredPosition()));

        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    private float getTargetTailXRot() {
        if (this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL) {
            return this.getAnimationTick() > 10 ? 45F : 0F;
        }
        return 0;
    }

    private float getTargetTailYRot() {
        float target = (float) (getYawFromBuffer(isTremorzillaSwimming() ? 2 : 20, 1.0F) - this.yBodyRot);
        float swimAmount = this.getSwimAmount(1.0F);
        float swimAddition = (float) (swimAmount * Math.sin(tickCount * 0.4F) * 25F);
        float swingAddition = (float) (Math.sin(tickCount * 0.03F) * 10F);
        if (this.isInSittingPose() && !this.isDancing()) {
            return target + 90;
        }
        if (this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL) {
            return (this.lastYawBeforeWhip - this.yBodyRot) + this.getAnimationTick() > 15 ? -70F : 70;
        }
        return target + swimAddition + swingAddition;
    }

    public float getLegSolverBodyOffset() {
        float swimAmount = this.getSwimAmount(1.0F);
        float heightBackLeft = legSolver.backLeft.getHeight(1.0F);
        float heightBackRight = legSolver.backRight.getHeight(1.0F);
        return Math.max(heightBackLeft, heightBackRight) * 0.8F * (1F - swimAmount);
    }

    protected Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * ((float) Math.PI / 180F)).yRot(-yRot * ((float) Math.PI / 180F));
    }

    public boolean isStunned() {
        return this.hasEffect(ACEffectRegistry.STUNNED.get());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCharge(compound.getInt("Charge"));
        this.setSpikesDownAmount(compound.getFloat("SpikesDownAmount"));
        this.wantsToUseBeamFromServer = compound.getBoolean("ServerBeamTrigger");
        this.setTameAttempts(compound.getInt("TameAttempts"));
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Charge", this.getCharge());
        compound.putFloat("SpikesDownAmount", this.getSpikesDownAmount());
        compound.putBoolean("ServerBeamTrigger", this.wantsToUseBeamFromServer);
        compound.putInt("TameAttempts", this.getTameAttempts());
    }

    private void tickBreath() {
        Vec3 endBeamPos;
        if (level().isClientSide) {
            endBeamPos = this.getClientBeamEndPosition(1.0F);
            if (endBeamPos != null) {
                Vec3 particleVec = endBeamPos.add((random.nextFloat() - 0.5F) * 3F, (random.nextFloat() - 0.5F) * 3F, (random.nextFloat() - 0.5F) * 3F);
                level().addAlwaysVisibleParticle(this.getAltSkin() == 2 ? ACParticleRegistry.TREMORZILLA_TECTONIC_EXPLOSION.get() : this.getAltSkin() == 1 ? ACParticleRegistry.TREMORZILLA_RETRO_EXPLOSION.get() : ACParticleRegistry.TREMORZILLA_EXPLOSION.get(), true, particleVec.x, particleVec.y, particleVec.z, 0, 0, 0);
                level().addAlwaysVisibleParticle(this.getAltSkin() == 2 ? ACParticleRegistry.TREMORZILLA_TECTONIC_LIGHTNING.get() : this.getAltSkin() == 1 ? ACParticleRegistry.TREMORZILLA_RETRO_LIGHTNING.get() : ACParticleRegistry.TREMORZILLA_LIGHTNING.get(), true, this.getX(), this.getEyeY(), this.getZ(), this.getId(), 0, 0);
                if (this.getRandom().nextFloat() < 0.3F) {
                    level().addAlwaysVisibleParticle(this.getAltSkin() == 2 ? ACParticleRegistry.TREMORZILLA_TECTONIC_PROTON.get() : this.getAltSkin() == 1 ? ACParticleRegistry.TREMORZILLA_RETRO_PROTON.get() : ACParticleRegistry.TREMORZILLA_PROTON.get(), true, this.getX(), this.getEyeY(), this.getZ(), this.getId(), 0, 0);
                }
            }
        } else {
            if (beamServerTarget != null) {
                Vec3 from = this.getBeamShootFrom(1.0F);
                Vec3 normalized = from.add(beamServerTarget.subtract(from).normalize().scale(100F));
                this.setBeamEndPosition(this.level().clip(new ClipContext(from, normalized, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation());
            }
            endBeamPos = this.getBeamEndPosition();
            boolean brokenClosestBlocks = false;
            float furthestBlockDist = 10.0F;
            if (endBeamPos != null && beamTime % 3 == 0) {
                Vec3 start = this.getBeamShootFrom(1.0F);
                Vec3 startClip = start;
                Vec3 viewVec = endBeamPos.subtract(startClip).normalize();
                float destructionScale = 5F;
                float walkThroughBeam = 1.0F;
                while (walkThroughBeam < this.getMaxBeamBreakLength()) {
                    startClip = startClip.add(viewVec.scale(destructionScale * 1.5F));
                    if (!brokenClosestBlocks) {
                        brokenClosestBlocks = this.breakBlocksAround(startClip, AlexsCaves.COMMON_CONFIG.devastatingTremorzillaBeam.get() ? destructionScale : destructionScale * 0.75F, false, true, 0.08F);
                        furthestBlockDist = (float) startClip.distanceTo(start);
                    }
                    this.hurtEntitiesAround(startClip, destructionScale + 1, 20.0F, 1.0F, true, true, false);
                    walkThroughBeam += destructionScale;
                }
                this.hurtEntitiesAround(endBeamPos, 6F, 20.0F, 1.0F, true, true, false);
                if (AlexsCaves.COMMON_CONFIG.devastatingTremorzillaBeam.get() && beamTime % 6 == 0) {
                    this.breakBlocksAround(endBeamPos, 4F, false, true, 0.08F);
                }
            }
            if (brokenClosestBlocks) {
                this.setMaxBeamBreakLength((float) Math.max(furthestBlockDist, this.getMaxBeamBreakLength() - 5));
            }
        }
        steamFromMouthFor = 200;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (SWIMMING.equals(dataAccessor)) {
            this.refreshDimensions();
        }
    }

    private void stompEffect(boolean left, float size, float hurtSize, float forwards, float damage) {
        float particleRadius = 0.3F + size * this.getScale();
        Vec3 center = this.position().add(new Vec3(left ? 2.2F : -2.2F, 0, forwards).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));
        if (level().isClientSide) {
            for (int i = 0; i < 4; i++) {
                for (int i1 = 0; i1 < 10 + random.nextInt(10); i1++) {
                    double motionX = getRandom().nextGaussian() * 0.07D;
                    double motionY = 0.07D + getRandom().nextGaussian() * 0.07D;
                    double motionZ = getRandom().nextGaussian() * 0.07D;
                    float angle = (0.01745329251F * this.yBodyRot) + i1;
                    double extraX = particleRadius * Mth.sin((float) (Math.PI + angle));
                    double extraY = 1.0F;
                    double extraZ = particleRadius * Mth.cos(angle);
                    Vec3 groundedVec = ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(center.x + extraX), Mth.floor(center.y + extraY) - 1, Mth.floor(center.z + extraZ)));
                    BlockPos ground = BlockPos.containing(groundedVec.subtract(0, 0.5F, 0));
                    BlockState state = this.level().getBlockState(ground);
                    if (state.isSolid()) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, center.x + extraX, ground.getY() + extraY, center.z + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
        hurtEntitiesAround(center, particleRadius + hurtSize, damage, 0.5F, false, false, false);
    }

    public boolean hurtEntitiesAround(Vec3 center, float radius, float damageAmount, float knockbackAmount, boolean radioactive, boolean hurtsOtherKaiju, boolean stretchY) {
        AABB aabb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        if (stretchY) {
            aabb.setMinY(this.getY() - 1);
            aabb.setMaxY(this.getEyeY() + 3);
        }
        boolean flag = false;
        DamageSource damageSource = radioactive ? ACDamageTypes.causeTremorzillaBeamDamage(level().registryAccess(), this) : this.damageSources().mobAttack(this);
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (!living.is(this) && !this.isAlliedTo(living) && living.getType() != this.getType() && living.distanceToSqr(center.x, stretchY ? living.getY() : center.y, center.z) <= radius * radius && (!radioactive || canEntityBeHurtByBeam(living, center)) && (hurtsOtherKaiju || !(living instanceof KaijuMob))) {
                if (living.hurt(damageSource, damageAmount)) {
                    flag = true;
                    knockbackTarget(living, knockbackAmount, this.getX() - living.getX(), this.getZ() - living.getZ(), !(living instanceof KaijuMob));
                    if (radioactive) {
                        if(living.getHealth() <= 0.0F && living instanceof Enemy){
                            killCountFromBeam++;
                        }
                        living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 6000, 2));
                    }
                }
            }
        }
        return flag;
    }

    private boolean canEntityBeHurtByBeam(LivingEntity living, Vec3 center) {
        return this.level().clip(new ClipContext(center, living.position().add(0, living.getBbHeight() * 0.5, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    public void knockbackTarget(Entity target, double strength, double x, double z, boolean ignoreResistance) {
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(this, (float) strength, x, z);
        if (event.isCanceled()) return;
        strength = event.getStrength();
        x = event.getRatioX();
        z = event.getRatioZ();
        if (!ignoreResistance) {
            strength *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        }
        if (!(strength <= 0.0D)) {
            this.hasImpulse = true;
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = (new Vec3(x, 0.0D, z)).normalize().scale(strength);
            target.setDeltaMovement(vec3.x / 2.0D - vec31.x, this.onGround() ? Math.min(0.4D, vec3.y / 2.0D + strength) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    public boolean breakBlocksAround(Vec3 center, float radius, boolean square, boolean triggerExplosions, float dropChance) {
        if (this.isBaby() || !net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this) || level().isClientSide) {
            return false;
        }
        boolean flag = false;
        for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(center.x - radius), Mth.floor(center.y - radius), Mth.floor(center.z - radius), Mth.floor(center.x + radius), Mth.floor(center.y + radius), Mth.floor(center.z + radius))) {
            BlockState blockstate = this.level().getBlockState(blockpos);
            boolean nuke = blockstate.is(ACBlockRegistry.NUCLEAR_BOMB.get());
            if (!blockstate.is(ACTagRegistry.NUKE_PROOF) && blockstate.blocksMotion() && (blockstate.getBlock().getExplosionResistance() <= 15 || nuke) && (square || blockpos.distToCenterSqr(center.x, center.y, center.z) < radius * radius)) {
                if (random.nextFloat() <= dropChance && !nuke) {
                    level().destroyBlock(blockpos, true);
                } else {
                    blockstate.onBlockExploded(level(), blockpos, dummyExplosion);
                }
                if (triggerExplosions) {
                    if (nuke) {
                        NuclearBombEntity bomb = ACEntityRegistry.NUCLEAR_BOMB.get().create(level());
                        bomb.setPos((double) blockpos.getX() + 0.5D, (double) blockpos.getY(), (double) blockpos.getZ() + 0.5D);
                        bomb.setTime(NuclearBombEntity.MAX_TIME);
                        level().addFreshEntity(bomb);
                    }
                }
                flag = true;
            }
        }
        return flag;
    }

    public boolean breakBlocksInBoundingBox(float dropChance) {
        if (this.isBaby() || !net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this) || level().isClientSide) {
            return false;
        }
        boolean flag = false;
        AABB boundingBox = this.getBoundingBox().inflate(2.0D);
        int swimUp = this.isTremorzillaSwimming() ? 3 : 1 - (int) this.getLegSolverBodyOffset();
        for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(boundingBox.minX), Mth.floor(boundingBox.minY + swimUp), Mth.floor(boundingBox.minZ), Mth.floor(boundingBox.maxX), Mth.floor(boundingBox.maxY), Mth.floor(boundingBox.maxZ))) {
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.isAir()) {
                continue;
            }

            if (!blockstate.is(ACTagRegistry.NUKE_PROOF) && !blockstate.isAir() && (blockstate.is(BlockTags.LEAVES) || blockpos.getY() > this.getBlockY()) && blockstate.getBlock().getExplosionResistance() <= 15 && (blockstate.is(Blocks.COBWEB) || !blockstate.getCollisionShape(level(), blockpos).isEmpty())) {
                if (random.nextFloat() <= dropChance) {
                    level().destroyBlock(blockpos, true);
                } else {
                    level().setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                }
                flag = true;
            }
        }
        return flag;
    }

    public void tryRoar() {
        if (roarCooldown == 0 && this.getAnimation() == NO_ANIMATION && !this.isFiring() && !this.isStunned() && !this.isBaby()) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_ROAR_2 : ANIMATION_ROAR_1);
            this.playSound(ACSoundRegistry.TREMORZILLA_ROAR.get(), 8.0F, 1.0F);
            this.roarCooldown = 300 + random.nextInt(400);
        }
    }

    public int getMaxHeadYRot() {
        return 60;
    }

    private float wrapTailDegrees(float f) {
        return f % 360.0F;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    private void scareMobs() {
        if (this.tickCount - lastScareTimestamp > 5) {
            lastScareTimestamp = this.tickCount;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(64, 20, 64));
        for (LivingEntity e : list) {
            if (!e.getType().is(ACTagRegistry.RESISTS_TREMORSAURUS_ROAR) && !isAlliedTo(e)) {
                if (e instanceof PathfinderMob mob && (!(mob instanceof TamableAnimal) || !((TamableAnimal) mob).isInSittingPose())) {
                    mob.setTarget(null);
                    mob.setLastHurtByMob(null);
                    if (mob.onGround()) {
                        Vec3 randomShake = new Vec3(random.nextFloat() - 0.5F, 0, random.nextFloat() - 0.5F).scale(0.1F);
                        mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.7F, 1, 0.7F).add(randomShake));
                    }
                    if (lastScareTimestamp == tickCount) {
                        mob.getNavigation().stop();
                    }
                    if (mob.getNavigation().isDone()) {
                        Vec3 vec = LandRandomPos.getPosAway(mob, 30, 7, this.position());
                        if (vec != null) {
                            mob.getNavigation().moveTo(vec.x, vec.y, vec.z, 2D);
                        }
                    }
                }
                if (this.isTame()) {
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, true, true));
                }
            }

        }
    }

    public float getScreenShakeAmount(float partialTicks) {
        if (this.isBaby()) {
            return 0;
        }
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    public double getShakeDistance() {
        return 64F;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d0 = entity.getX() - this.getX();
                double d1 = entity.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= (double)0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= (double)0.05F;
                    d1 *= (double)0.05F;
                    if (!entity.isVehicle() && (entity.isPushable() || entity instanceof KaijuMob)) {
                        entity.push(d0, 0.0D, d1);
                    }
                }
            }
        }
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

    public float getYawFromBuffer(int pointer, float partialTick) {
        int i = this.yawPointer - pointer & 127;
        int j = this.yawPointer - pointer - 1 & 127;
        float d0 = this.yawBuffer[j];
        float d1 = this.yawBuffer[i] - d0;
        return d0 + d1 * partialTick;
    }

    @Override
    public BlockState createEggBlockState() {
        return ACBlockRegistry.TREMORZILLA_EGG.get().defaultBlockState();
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return allParts;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ACEntityRegistry.TREMORZILLA.get().create(serverLevel);
    }

    public static float calculateSpikesDownAmount(float progress, float spikeCount) {
        float scaledTo = progress * spikeCount;
        float remains = scaledTo % 1.0F;
        return Mth.floor(scaledTo) + (float) Math.pow(remains, 5);
    }

    public static float calculateSpikesDownAmountAtIndex(float progress, float spikeCount, float spikeIndex) {
        return Mth.clamp(calculateSpikesDownAmount(progress, spikeCount) - spikeIndex, 0F, 1F);
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2) {
                if (this.getMeterAmount() >= 1.0F && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null) && !wantsToUseBeamFromServer) {
                    this.yBodyRot = keyPresser.getYHeadRot();
                    this.setYRot(keyPresser.getYHeadRot());
                    wantsToUseBeamFromServer = true;
                    maxBeamTime = 200;
                }
            }
            if (type == 3) {
                if (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null) {
                    this.setYHeadRot(keyPresser.getYHeadRot());
                    this.setXRot(keyPresser.getXRot());
                    float decision = this.getRandom().nextFloat();
                    if (decision < 0.33F) {
                        this.setAnimation(this.getRandom().nextBoolean() ? TremorzillaEntity.ANIMATION_LEFT_SCRATCH : TremorzillaEntity.ANIMATION_RIGHT_SCRATCH);
                    } else if (decision < 0.66F && !this.isSwimming()) {
                        this.setAnimation(this.getRandom().nextBoolean() ? TremorzillaEntity.ANIMATION_LEFT_STOMP : TremorzillaEntity.ANIMATION_RIGHT_STOMP);
                    } else {
                        this.setAnimation(TremorzillaEntity.ANIMATION_BITE);
                    }
                }
            }
        }
    }

    public float maxSitTicks() {
        return 20.0F;
    }

    private Stream<BlockPos> getNearbySirens(ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.NUCLEAR_SIREN.getKey()), Predicates.alwaysTrue(), this.blockPosition(), range, PoiManager.Occupancy.ANY);
    }

    private void activateSiren(BlockPos pos) {
        if (level().getBlockEntity(pos) instanceof NuclearSirenBlockEntity nuclearSirenBlock) {
            nuclearSirenBlock.setNearestNuclearBomb(this);
        }
    }

    @Override
    public boolean shouldStopBlaringSirens() {
        return !this.isPowered() || this.getSpikesDownAmount() <= 0 || this.isRemoved();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult prev = super.mobInteract(player, hand);
        if (prev != InteractionResult.SUCCESS) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!this.isTame() && (itemStack.is(ACBlockRegistry.WASTE_DRUM.get().asItem())) && this.getAnimation() == NO_ANIMATION) {
                this.usePlayerItem(player, hand, itemStack);
                this.setAnimation(ANIMATION_CHEW);
                lastFedPlayer = player;
                return InteractionResult.SUCCESS;
            }
        }
        return prev;
    }

    public boolean canOwnerMount(Player player) {
        return !this.isBaby();
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return ownerPlayer.isShiftKeyDown();
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity living) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        this.setTarget(null);
        if (player.zza != 0 || player.xxa != 0) {
            if (this.getAnimation() != ANIMATION_LEFT_TAIL && this.getAnimation() != ANIMATION_RIGHT_TAIL) {
                this.setRot(player.getYRot(), player.getXRot() * 0.25F);
                this.setYHeadRot(player.getYHeadRot());
            }
        }
    }

    protected float getRiddenSpeed(Player rider) {
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }

    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    public void setTameAttempts(int i) {
        this.entityData.set(TAME_ATTEMPTS, i);
    }

    public int getTameAttempts() {
        return this.entityData.get(TAME_ATTEMPTS);
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    protected float getBlockSpeedFactor() {
        return this.isTremorzillaSwimming() || this.onSoulSpeedBlock() ? 1.0F : super.getBlockSpeedFactor();
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        if (this.isInFluidType()) {
            Vec3 lookVec = player.getLookAngle();
            float y = (float) lookVec.y;
            return new Vec3(player.xxa * 0.25F, y, player.zza * 0.8F * f);
        }
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            float swimAmount = this.getSwimAmount(1.0F);
            float walkSwing = (float) (Math.cos(this.walkAnimation.position() * 0.25F + 1F) * 0.75F * this.walkAnimation.speed() - 1.5F * this.walkAnimation.speed()) * (1F - swimAmount);
            float animationExtraBack = 0;
            if (this.getAnimation() == ANIMATION_ROAR_2) {
                animationExtraBack = 4 * ACMath.cullAnimationTick(this.getAnimationTick(), 1, this.getAnimation(), 1.0F, 10, 60);
            }
            if (this.getAnimation() == ANIMATION_PREPARE_BREATH) {
                animationExtraBack = 4 * ACMath.cullAnimationTick(this.getAnimationTick(), 1, this.getAnimation(), 1.0F, 0, 20);
            }
            Vec3 seatOffset = new Vec3(0F, 2F - 6.5D * swimAmount, 1.0F + 6 * swimAmount - walkSwing - animationExtraBack).yRot((float) Math.toRadians(-this.yBodyRot));
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            if (!this.isFiring()) {
                clampRotation(living, 105);
            }
            float heightBackLeft = legSolver.legs[0].getHeight(1.0F);
            float heightBackRight = legSolver.legs[1].getHeight(1.0F);
            float maxLegSolverHeight = (1F - ACMath.smin(1F - heightBackLeft, 1F - heightBackRight, 0.1F)) * 0.8F * (1F - swimAmount);
            moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset() - maxLegSolverHeight, this.getZ() + seatOffset.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    public double getPassengersRidingOffset() {
        return 8.25D * this.getScale();
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(6);
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

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.TREMORZILLA_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TREMORZILLA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TREMORZILLA_DEATH.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPEAK, ANIMATION_ROAR_1, ANIMATION_ROAR_2, ANIMATION_RIGHT_SCRATCH, ANIMATION_LEFT_SCRATCH, ANIMATION_RIGHT_TAIL, ANIMATION_LEFT_TAIL, ANIMATION_RIGHT_STOMP, ANIMATION_LEFT_STOMP, ANIMATION_BITE, ANIMATION_PREPARE_BREATH, ANIMATION_CHEW};
    }

    public boolean isVisuallySwimming() {
        return isTremorzillaSwimming();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isTremorzillaSwimming() ? SWIMMING_SIZE.scale(this.getScale()) : super.getDimensions(poseIn);
    }

    public boolean isTremorzillaSwimming() {
        return this.entityData.get(SWIMMING);
    }

    public void setTremorzillaSwimming(boolean bool) {
        this.entityData.set(SWIMMING, bool);
    }

    public float getSpikesDownAmount() {
        return this.entityData.get(SPIKES_DOWN_PROGRESS);
    }

    public void setSpikesDownAmount(float spikesDownProgress) {
        this.entityData.set(SPIKES_DOWN_PROGRESS, spikesDownProgress);
    }

    public float getClientSpikeDownAmount(float partialTicks) {
        return prevClientSpikesDownAmount + (clientSpikesDownAmount - prevClientSpikesDownAmount) * partialTicks;
    }

    public boolean isFiring() {
        return this.entityData.get(FIRING);
    }

    public void setFiring(boolean firing) {
        this.entityData.set(FIRING, firing);
    }

    public float getBeamProgress(float partialTicks) {
        return (prevBeamProgress + (beamProgress - prevBeamProgress) * partialTicks) * 0.2F;
    }

    public int getCharge() {
        return this.entityData.get(CHARGE);
    }

    public void setCharge(int charge) {
        this.entityData.set(CHARGE, charge);
    }

    public boolean isPowered() {
        return this.getCharge() >= MAX_CHARGE;
    }

    @Nullable
    public Vec3 getBeamEndPosition() {
        return this.entityData.get(BEAM_END_POSITION).orElse(null);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.35F;
        }
        return super.hurt(source, amount);
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public float getMeterAmount() {
        return this.getCharge() / (float) MAX_CHARGE;
    }

    public void setBeamEndPosition(@Nullable Vec3 vec3) {
        this.entityData.set(BEAM_END_POSITION, Optional.ofNullable(vec3));
    }

    @Nullable
    public Vec3 getClientBeamEndPosition(float partialTicks) {
        if (clientBeamEndPosition != null && prevClientBeamEndPosition != null) {
            return prevClientBeamEndPosition.add(clientBeamEndPosition.subtract(prevClientBeamEndPosition).scale(partialTicks));
        } else {
            return null;
        }
    }

    public int getExperienceReward() {
        return 70;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(ACBlockRegistry.NUCLEAR_BOMB.get().asItem());
    }

    public Vec3 getBodyRotViewVector(float partialTicks) {
        return this.calculateViewVector(this.getViewXRot(partialTicks), this.yBodyRotO + (this.yBodyRot - this.yBodyRotO) * partialTicks);
    }

    public void setMaxBeamBreakLength(float f) {
        this.entityData.set(MAX_BEAM_BREAK_LENGTH, f);
    }

    public double getMaxBeamBreakLength() {
        return this.entityData.get(MAX_BEAM_BREAK_LENGTH);
    }

    public float getStepHeight() {
        return 1.6F;
    }

    public Vec3 getBeamShootFrom(float partialTicks) {
        return this.getPosition(partialTicks).add(0, 7.5F * getScale(), 0);
    }

    @Override
    public int getMaxNavigableDistanceToGround() {
        return 4;
    }

    public float getScale() {
        return this.isBaby() ? 0.15F : 1.0F;
    }

    @Override
    public BlockState createEggBeddingBlockState() {
        return ACBlockRegistry.UNREFINED_WASTE.get().defaultBlockState();
    }
}
