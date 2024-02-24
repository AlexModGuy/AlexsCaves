package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolver;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TremorsaurusEntity extends DinosaurEntity implements KeybindUsingMount, IAnimatedEntity, ShakesScreen, ITallWalker {

    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(TremorsaurusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(TremorsaurusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TAME_ATTEMPTS = SynchedEntityData.defineId(TremorsaurusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> METER_AMOUNT = SynchedEntityData.defineId(TremorsaurusEntity.class, EntityDataSerializers.FLOAT);
    public final LegSolver legSolver = new LegSolver(new LegSolver.Leg(-0.45F, 0.75F, 1.0F, false), new LegSolver.Leg(-0.45F, -0.75F, 1.0F, false));
    private Animation currentAnimation;
    private int animationTick;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private int lastScareTimestamp = 0;
    private boolean hasRunningAttributes = false;
    private int roarCooldown = 0;
    public static final Animation ANIMATION_SNIFF = Animation.create(30);
    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(55);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_SHAKE_PREY = Animation.create(40);
    private double lastStompX = 0;
    private double lastStompZ = 0;
    private int roarScatterTime = 0;
    private Entity riderHitEntity = null;

    public TremorsaurusEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new TremorsaurusMeleeGoal(this));
        this.goalSelector.addGoal(3, new AnimalFollowOwnerGoal(this, 1.2D, 5.0F, 2.0F, false) {
            @Override
            public boolean shouldFollow() {
                return TremorsaurusEntity.this.getCommand() == 2;
            }
        });
        this.goalSelector.addGoal(4, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(5, new AnimalLayEggGoal(this, 100, 1));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, Ingredient.of(ACBlockRegistry.COOKED_DINOSAUR_CHOP.get(), ACBlockRegistry.DINOSAUR_CHOP.get()), false));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 30));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, TremorsaurusEntity.class)));
        this.targetSelector.addGoal(2, new MobTargetClosePlayers(this, 50, 8));
        this.targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new MobTargetUntamedGoal<>(this, GrottoceratopsEntity.class, 100, true, false, null));
        this.targetSelector.addGoal(4, new MobTargetUntamedGoal<>(this, SubterranodonEntity.class, 50, true, false, null));
        this.targetSelector.addGoal(5, new MobTargetUntamedGoal<>(this, RelicheirusEntity.class, 250, true, false, null));
    }

    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(HELD_MOB_ID, -1);
        this.entityData.define(TAME_ATTEMPTS, 0);
        this.entityData.define(METER_AMOUNT, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 14.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 150.0D).add(Attributes.ARMOR, 8.0D);
    }

    public void tick() {
        super.tick();
        prevScreenShakeAmount = screenShakeAmount;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        if (this.onGround() && !this.isInFluidType() && this.walkAnimation.speed() > 0.1F && !this.isBaby()) {
            float f = (float) Math.cos(this.walkAnimation.position() * 0.8F - 1.5F);
            if (Math.abs(f) < 0.2) {
                if (screenShakeAmount <= 0.3) {
                    this.playSound(ACSoundRegistry.TREMORSAURUS_STOMP.get(), 2, 1.0F);
                    this.shakeWater();
                }
                screenShakeAmount = 1F;
            }
        }
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        }
        if(this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5){
            this.playRoarSound();
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() >= 5 && this.getAnimationTick() <= 40 && !this.isBaby()) {
            screenShakeAmount = 1F;
            roarScatterTime = 30;
            if (this.getAnimationTick() % 5 == 0 && level().isClientSide) {
                this.shakeWater();
            }
        }
        if (roarScatterTime > 0) {
            roarScatterTime--;
            scareMobs();
        }
        if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 5) {
            actuallyPlayAmbientSound();
        }
        if (!level().isClientSide) {
            if (this.getDeltaMovement().horizontalDistance() < 0.05 && this.getAnimation() == NO_ANIMATION && !this.isDancing() && !this.isInSittingPose()) {
                if (random.nextInt(180) == 0) {
                    this.setAnimation(ANIMATION_SNIFF);
                }
                if (random.nextInt(600) == 0 && !this.isVehicle()) {
                    this.tryRoar();
                }
            }
            boolean held = false;
            if (riderHitEntity != null && this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() > 10 && this.getAnimationTick() <= 12) {
                if (this.hasLineOfSight(riderHitEntity) && this.distanceTo(riderHitEntity) < this.getBbWidth() + riderHitEntity.getBbWidth() + 2.0D) {
                    riderHitEntity.hurt(riderHitEntity.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    if (riderHitEntity instanceof LivingEntity living) {
                        living.knockback(0.5D, this.getX() - riderHitEntity.getX(), this.getZ() - riderHitEntity.getZ());
                    }
                    riderHitEntity = null;
                }
            }
            LivingEntity target = riderHitEntity instanceof LivingEntity ? (LivingEntity) riderHitEntity : this.getTarget();
            if (target != null && target.isAlive() && target.distanceTo(this) < (isVehicle() ? 10.0F : 5.5F)) {
                if (this.getAnimation() == ANIMATION_SHAKE_PREY && this.getAnimationTick() <= 35) {
                    held = true;
                    this.setHeldMobId(target.getId());
                }
            }
            if (!held && getHeldMobId() != -1) {
                this.setHeldMobId(-1);
                this.playSound(ACSoundRegistry.TREMORSAURUS_THROW.get());
                riderHitEntity = null;
            }
        } else {
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && getMeterAmount() >= 1.0F) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
                if (AlexsCaves.PROXY.isKeyDown(3) && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 3));
                }
            }
        }
        if (this.isVehicle()) {
            if (this.getMeterAmount() < 1.0F) {
                this.setMeterAmount(Math.min(this.getMeterAmount() + 0.0035F, 1.0F));
            }
        } else {
            this.setMeterAmount(0.0F);
        }
        if(this.getAnimation() == ANIMATION_SHAKE_PREY && getHeldMobId() != -1){
            Entity entity = level().getEntity(getHeldMobId());
            if(entity != null){
                if (this.getAnimationTick() <= 35) {
                    Vec3 shakePreyPos = getShakePreyPos();
                    Vec3 minus = new Vec3(shakePreyPos.x - entity.getX(), shakePreyPos.y - entity.getY(), shakePreyPos.z - entity.getZ());
                    entity.setDeltaMovement(minus);
                    if (this.getAnimationTick() % 10 == 0) {
                        entity.hurt(damageSources().mobAttack(this), 5 + this.getRandom().nextInt(2));
                    }
                }else{
                    entity.setDeltaMovement(entity.getDeltaMovement().scale(0.6F));
                }
            }
        }
        if (roarCooldown > 0) {
            roarCooldown--;
        }
        lastStompX = this.getX();
        lastStompZ = this.getZ();
    }

    private void playRoarSound() {
        if(this.isBaby()){
            this.playSound(ACSoundRegistry.TREMORSAURUS_ROAR.get(), 1.0F, 1.5F);
        }else{
            this.playSound(ACSoundRegistry.TREMORSAURUS_ROAR.get(), 4.0F, 1.0F);
        }
    }

    private void scareMobs() {
        if (this.tickCount - lastScareTimestamp > 3) {
            lastScareTimestamp = this.tickCount;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(30, 10, 30));
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
                        Vec3 vec = LandRandomPos.getPosAway(mob, 15, 7, this.position());
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

    private void shakeWater() {
        if (level().isClientSide) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int radius = 8;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        mutableBlockPos.set(this.getX() + x, this.getY() + 5, this.getZ() + z);
                        while (mutableBlockPos.getY() > level().getMinBuildHeight() && level().getBlockState(mutableBlockPos).isAir()) {
                            mutableBlockPos.move(Direction.DOWN);
                        }
                        float water = getWaterLevelForBlock(level(), mutableBlockPos);
                        if (water > 0.0F) {
                            level().addParticle(ACParticleRegistry.WATER_TREMOR.get(), mutableBlockPos.getX() + 0.5F, mutableBlockPos.getY() + water + 0.01, mutableBlockPos.getZ() + 0.5F, 0, 0, 0);
                        }

                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public void setTameAttempts(int i) {
        this.entityData.set(TAME_ATTEMPTS, i);
    }

    public int getTameAttempts() {
        return this.entityData.get(TAME_ATTEMPTS);
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public float getScreenShakeAmount(float partialTicks) {
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    public Vec3 getShakePreyPos() {
        Vec3 jaw = new Vec3(0, -0.75, 3F);
        if (this.getAnimation() == ANIMATION_SHAKE_PREY) {
            if (this.getAnimationTick() <= 5) {
                jaw = jaw.subtract(0, 1.5F * (getAnimationTick() / 5F), 0);
            } else if (this.getAnimationTick() < 35) {
                jaw = jaw.yRot(0.8F * (float) Math.cos(this.tickCount * 0.6F));
            }
        }
        Vec3 head = jaw.xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
        return this.getEyePosition().add(head);
    }

    public void tryRoar() {
        if (roarCooldown == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ROAR);
            this.roarCooldown = 200 + random.nextInt(200);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ACEntityRegistry.TREMORSAURUS.get().create(level);
    }

    @Override
    public boolean wantsToAttack(LivingEntity living, LivingEntity owner) {
        if(living instanceof TremorsaurusEntity tremorsaurus && (tremorsaurus.getTameAttempts() > 0 || tremorsaurus.hasEffect(ACEffectRegistry.STUNNED.get()))){
            return false;
        }
        return super.wantsToAttack(living, owner);
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_ROAR || this.getAnimation() == ANIMATION_SHAKE_PREY) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public float getScale() {
        return this.isBaby() ? 0.25F : 1.0F;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.lastStompX, 0, this.getZ() - this.lastStompZ);
        float walkSpeed = 4.0F;
        if (isVehicle()) {
            walkSpeed = 1.5F;
        } else if (isRunning()) {
            walkSpeed = 2.0F;
        }
        float f2 = Math.min(f1 * walkSpeed, 1.0F);
        walkAnimation.update(f2, 0.4F);
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.setAnimation(ANIMATION_SPEAK);
        }
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
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
        return new Animation[]{ANIMATION_SNIFF, ANIMATION_SPEAK, ANIMATION_ROAR, ANIMATION_BITE, ANIMATION_SHAKE_PREY};
    }

    private float getWaterLevelForBlock(Level level, BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (state.is(Blocks.WATER_CAULDRON)) {
            return (6.0F + (float) state.getValue(LayeredCauldronBlock.LEVEL).intValue() * 3.0F) / 16.0F;
        } else if (random.nextFloat() < 0.33F && state.getFluidState().is(FluidTags.WATER)) {
            return state.getFluidState().getHeight(level, pos);
        } else {
            return 0;
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TameAttempts", this.getTameAttempts());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTameAttempts(compound.getInt("TameAttempts"));
    }

    @Override
    public BlockState createEggBlockState() {
        return ACBlockRegistry.TREMORSAURUS_EGG.get().defaultBlockState();
    }

    public float getStepHeight() {
        return 1.1F;
    }

    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0 || player.xxa != 0) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setYHeadRot(player.getYHeadRot());
            this.setTarget(null);
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

    public boolean tamesFromHatching() {
        return true;
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            Vec3 seatOffset = new Vec3(0F, 0.1F, 0.6F).yRot((float) Math.toRadians(-this.yBodyRot));
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            clampRotation(living, 105);
            float heightBackLeft = legSolver.legs[0].getHeight(1.0F);
            float heightBackRight = legSolver.legs[1].getHeight(1.0F);
            float maxLegSolverHeight = (1F - ACMath.smin(1F - heightBackLeft, 1F - heightBackRight, 0.1F)) * 0.8F;
            moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset() - maxLegSolverHeight, this.getZ() + seatOffset.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity p_20123_) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float roarPower) {
        this.entityData.set(METER_AMOUNT, roarPower);
    }

    @Override
    public boolean onFeedMixture(ItemStack itemStack, Player player) {
        if (itemStack.is(ACItemRegistry.SERENE_SALAD.get()) && this.hasEffect(ACEffectRegistry.STUNNED.get())) {
            this.removeEffect(ACEffectRegistry.STUNNED.get());
            AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(this.getId(), this.getId(), 3, 0, true));
            if (!level().isClientSide) {
                this.setTameAttempts(this.getTameAttempts() + 1);
                if (this.getTameAttempts() > 3 && this.getRandom().nextInt(2) == 0 || this.getTameAttempts() > 8) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return true;
        }
        return false;
    }

    public boolean isFood(ItemStack stack) {
        return this.isTame() && (stack.is(ACBlockRegistry.COOKED_DINOSAUR_CHOP.get().asItem()) || stack.is(ACBlockRegistry.DINOSAUR_CHOP.get().asItem()));
    }

    public boolean canOwnerMount(Player player) {
        return !this.isBaby();
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return ownerPlayer.isShiftKeyDown();
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2) {
                if (this.getMeterAmount() >= 1.0F && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    this.yBodyRot = keyPresser.getYHeadRot();
                    this.setYRot(keyPresser.getYHeadRot());
                    this.setAnimation(ANIMATION_ROAR);
                    this.setMeterAmount(0.0F);
                }
            }
            if (type == 3) {
                if (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null) {
                    HitResult hitresult = ProjectileUtil.getHitResultOnViewVector(keyPresser, entity -> !entity.is(this) && !this.isAlliedTo(entity), 10.0F);
                    this.setYHeadRot(keyPresser.getYHeadRot());
                    this.setXRot(keyPresser.getXRot());
                    boolean flag = false;
                    if (hitresult instanceof EntityHitResult entityHitResult) {
                        riderHitEntity = entityHitResult.getEntity();
                        if (this.getRandom().nextBoolean() && riderHitEntity.getBbWidth() < 2.0F || riderHitEntity instanceof FlyingAnimal) {
                            flag = true;
                        }
                    } else {
                        riderHitEntity = null;
                    }
                    this.setAnimation(flag ? ANIMATION_SHAKE_PREY : ANIMATION_BITE);
                }
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.TREMORSAURUS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TREMORSAURUS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TREMORSAURUS_DEATH.get();
    }

    @Override
    public int getMaxNavigableDistanceToGround() {
        return 2;
    }
}
