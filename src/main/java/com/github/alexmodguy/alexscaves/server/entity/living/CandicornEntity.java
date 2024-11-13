package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.entity.util.RidingMeterMount;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

public class CandicornEntity extends TamableAnimal implements KeybindUsingMount, IAnimatedEntity, PlayerRideableJumping, Saddleable, RidingMeterMount, PossessedByLicowitch {

    public static final Animation ANIMATION_BUCK = Animation.create(25);
    public static final Animation ANIMATION_TAIL_FLICK_1 = Animation.create(12);
    public static final Animation ANIMATION_TAIL_FLICK_2 = Animation.create(12);
    public static final Animation ANIMATION_NIBBLE_IDLE = Animation.create(35);
    public static final Animation ANIMATION_STAB = Animation.create(25);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> CHARGE_YAW = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> METER_AMOUNT = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> POSSESSOR_LICOWITCH_ID = SynchedEntityData.defineId(CandicornEntity.class, EntityDataSerializers.INT);
    private float prevLeapProgress;
    private float leapProgress;
    private float prevRunProgress;
    private float runProgress;
    private float prevChargeProgress;
    private float vehicleProgress;
    private float prevVehicleProgress;
    private float chargeProgress;
    private float prevSitProgress;
    private float sitProgress;
    private float prevManeAngle;
    private float maneAngle;
    private Animation currentAnimation;
    private int animationTick;
    private float tailYaw;
    private float prevTailYaw;
    private float leapPitch;
    private float prevLeapPitch;
    private boolean hasRunningAttributes = false;
    private int chargeParticleCooldown = 0;
    private boolean leapImpulse;
    private int playerDrivenChargeTicks = 0;
    private int controllerForwardsTicks;
    protected int gallopSoundCounter;

    public CandicornEntity(EntityType entityType, Level level) {
        super(entityType, level);
        tailYaw = this.getYRot();
        prevTailYaw = this.getYRot();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    public static boolean checkCandicornSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return levelReader.getBlockState(pos.below()).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) ? 10.0F : super.getWalkTargetValue(pos, levelReader);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(LEAPING, false);
        this.entityData.define(CHARGING, false);
        this.entityData.define(SADDLED, false);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(VARIANT, 0);
        this.entityData.define(CHARGE_YAW, 0F);
        this.entityData.define(METER_AMOUNT, 1.0F);
        this.entityData.define(POSSESSOR_LICOWITCH_ID, 0);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new AnimalFollowOwnerGoal(this, 1.0D, 8.0F, 1.4F, false) {
            @Override
            public boolean shouldFollow() {
                return CandicornEntity.this.getCommand() == 2;
            }

            @Override
            public void tickDistance(float distanceTo) {
                CandicornEntity.this.setRunning(distanceTo > 5);
            }
        });
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.0D) {
            @Override
            public boolean shouldPanic() {
                return CandicornEntity.this.isBaby() && super.shouldPanic();
            }

            @Override
            public void start() {
                super.start();
                CandicornEntity.this.setRunning(true);
            }

            public void stop() {
                super.stop();
                CandicornEntity.this.setRunning(false);
            }

        });
        this.goalSelector.addGoal(4, new CandicornMeleeGoal(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new TemptGoal(this, 1.1D, Ingredient.of(ACItemRegistry.CARAMEL_APPLE.get()), false));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new AnimalHurtByTargetDefendBabiesGoal(this, CandicornEntity.class));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        CandicornEntity candicornEntity = ACEntityRegistry.CANDICORN.get().create(serverLevel);
        candicornEntity.setVariant(this.getVariant());
        return candicornEntity;
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

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean bool) {
        this.entityData.set(LEAPING, bool);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean bool) {
        this.entityData.set(CHARGING, bool);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTame() ;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundSource) {
        this.setSaddled(true);
        if (soundSource != null) {
            this.level().playSound((Player)null, this, SoundEvents.PIG_SADDLE, soundSource, 0.5F, 1.0F);
        }
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean bool) {
        this.entityData.set(SADDLED, bool);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }
    public float getChargeYaw() {
        return this.entityData.get(CHARGE_YAW);
    }

    public void setChargeYaw(float chargeYaw) {
        this.entityData.set(CHARGE_YAW, chargeYaw);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Command", this.getCommand());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCommand(compound.getInt("Command"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.setVariant(compound.getInt("Variant"));
    }

    @Override
    public void tick() {
        super.tick();
        prevLeapProgress = leapProgress;
        prevRunProgress = runProgress;
        prevChargeProgress = chargeProgress;
        prevSitProgress = sitProgress;
        prevTailYaw = tailYaw;
        prevVehicleProgress = vehicleProgress;
        prevManeAngle = maneAngle;
        prevLeapPitch = leapPitch;
        leapPitch = Mth.clamp((float) this.getDeltaMovement().y, -0.5F, 1.5F) * -(float) (180F / (float) Math.PI);
        if (this.isLeaping() && leapProgress < 5F) {
            leapProgress++;
        }
        if (!this.isLeaping() && leapProgress > 0F) {
            leapProgress--;
        }
        if (this.isRunning() && runProgress < 5F) {
            runProgress++;
        }
        if (!this.isRunning() && runProgress > 0F) {
            runProgress--;
        }
        if (this.isCharging() && chargeProgress < 5F) {
            chargeProgress++;
        }
        if (!this.isCharging() && chargeProgress > 0F) {
            chargeProgress = Math.max(0, chargeProgress - 0.25F);
        }
        if (this.isInSittingPose() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!this.isInSittingPose() && sitProgress > 0F) {
            sitProgress--;
        }
        if (this.getControllingPassenger() != null && vehicleProgress < 5F) {
            vehicleProgress++;
        }
        if (this.getControllingPassenger() == null && vehicleProgress > 0F) {
            vehicleProgress--;
        }
        if(this.onGround() && this.isLeaping() && !leapImpulse){
            this.setLeaping(false);
        }
        if(leapImpulse){
            leapImpulse = false;
        }
        if(this.isCharging()){
            Vec3 chargeFocalPoint = this.position().add(new Vec3(0F, 0F, 1F).yRot((float) Math.toRadians(-this.getChargeYaw())));
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.5D))) {
                float dist = (float) entity.distanceToSqr(chargeFocalPoint);
                if (!(this.isTame() && isAlliedTo(entity)) && !entity.isPassengerOfSameVehicle(this) && !(entity instanceof CandicornEntity) && entity != this && dist < 7.0F) {
                    float dmgExtra = 7.0F - dist;
                    entity.hurt(this.damageSources().mobAttack(this), 7.0F + dmgExtra);
                    entity.knockback(2.0F, chargeFocalPoint.x - entity.getX(), chargeFocalPoint.z - entity.getZ());
                }
            }
            if(!this.isVehicle()){
                this.setYRot(Mth.approachDegrees(this.yRotO, this.getChargeYaw(), 25));
                this.yBodyRot = Mth.approachDegrees(this.yBodyRot, this.getChargeYaw(), 25);
                this.yBodyRotO = this.yBodyRot;
                this.yHeadRot = this.getYRot();
                this.yHeadRotO = this.yHeadRot;
            }
        }
        if(chargeParticleCooldown <= 0 && this.chargeProgress > 2.5F){
            chargeParticleCooldown = 3;
            level().addAlwaysVisibleParticle(ACParticleRegistry.CANDICORN_CHARGE.get(), true, this.getX(), this.getY() + 0.5F, this.getZ(), this.getId(), 0.0F, this.getChargeYaw());
        }
        if(chargeParticleCooldown > 0){
            chargeParticleCooldown--;
        }
        if (!level().isClientSide) {
            if (this.getDeltaMovement().horizontalDistance() < 0.02 && random.nextInt(200) == 0 && controllerForwardsTicks <= 0 && this.getAnimation() == NO_ANIMATION && !this.isNoAi() && !this.isInSittingPose()) {
                Animation idle;
                float rand = random.nextFloat();
                if (rand < 0.15F) {
                    idle = ANIMATION_BUCK;
                } else if (rand < 0.6F) {
                    idle = random.nextBoolean() ? ANIMATION_TAIL_FLICK_1 : ANIMATION_TAIL_FLICK_2;
                } else {
                    idle = ANIMATION_NIBBLE_IDLE;
                }
                this.setAnimation(idle);
            }
            if (isRunning() && !hasRunningAttributes) {
                hasRunningAttributes = true;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5D);
            }
            if (!isRunning() && hasRunningAttributes) {
                hasRunningAttributes = false;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
            }
        }else{
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && getMeterAmount() >= 1.0F && this.isRunning()) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
            }
            spawnPossessedParticles(getRandomX(0.5D), getRandomY(), getRandomZ(0.5D), this.level());
        }
        Entity controllingPassenger = this.getControllingPassenger();
        if(controllingPassenger != null){
            float f = Mth.degreesDifference(controllingPassenger.getYHeadRot(), this.yBodyRot);
            if(Math.abs(f) > 2){
                if(f < 0){
                    maneAngle = Mth.approach(maneAngle, -1F, 0.3F);
                }else{
                    maneAngle = Mth.approach(maneAngle, 1F, 0.3F);
                }
            }
            if(!level().isClientSide) {
                if(this.touchingWall()){
                    this.setRunning(false);
                    this.setCharging(false);
                    playerDrivenChargeTicks = 0;
                    controllerForwardsTicks = 0;
                }
                if (playerDrivenChargeTicks > 0) {
                    this.setCharging(true);
                    this.setRunning(true);
                    this.setChargeYaw(controllingPassenger.getYHeadRot());
                    playerDrivenChargeTicks--;
                } else if (this.isCharging()) {
                    this.setCharging(false);
                }
                if (controllerForwardsTicks > 20) {
                    this.setRunning(true);
                } else {
                    this.setRunning(false);
                    this.setCharging(false);
                    playerDrivenChargeTicks = 0;
                }
                if (this.getMeterAmount() < 1.0F && !this.isCharging() && this.isRunning()) {
                    this.setMeterAmount(Math.min(this.getMeterAmount() + 0.005F, 1.0F));
                }
            }
        }else{
            if(controllerForwardsTicks > 0){
                controllerForwardsTicks = 0;
                this.setRunning(false);
            }
            if(playerDrivenChargeTicks > 0){
                playerDrivenChargeTicks = 0;
                this.setCharging(false);
            }
            this.setMeterAmount(0.0F);
        }
        if(this.isCharging() && isAlive()){
            AlexsCaves.PROXY.playWorldSound(this, (byte) 19);
        }
        tailYaw = Mth.approachDegrees(this.tailYaw, yBodyRot, 10);

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }

    public boolean touchingWall() {
        if (this.noPhysics) {
            return false;
        } else {
            float f = this.getBbWidth() + 0.1F;
            AABB aabb = AABB.ofSize(this.getEyePosition(), (double)f, 1.0E-6D, (double)f);
            return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
                BlockState blockstate = this.level().getBlockState(p_201942_);
                return !blockstate.isAir() && blockstate.isSuffocating(this.level(), p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
            });
        }
    }

    public boolean isInSittingPose() {
        return super.isInSittingPose() && !(this.isVehicle() || this.isPassenger());
    }

    public float getLeapProgress(float partialTicks) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTicks) * 0.2F;
    }

    public float getRunProgress(float partialTicks) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTicks) * 0.2F;
    }

    public float getChargeProgress(float partialTicks) {
        return (prevChargeProgress + (chargeProgress - prevChargeProgress) * partialTicks) * 0.2F;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.2F;
    }

    public float getVehicleProgress(float partialTicks) {
        return (prevVehicleProgress + (vehicleProgress - prevVehicleProgress) * partialTicks) * 0.2F;
    }

    public float getManeAngle(float partialTicks) {
        return (prevManeAngle + (maneAngle - prevManeAngle) * partialTicks);
    }

    public float getTailYaw(float partialTick) {
        return (prevTailYaw + (tailYaw - prevTailYaw) * partialTick);
    }

    public float getLeapPitch(float partialTicks) {
        return (prevLeapPitch + (leapPitch - prevLeapPitch) * partialTicks);
    }

    public float getStepHeight() {
        return isCharging() && this.isVehicle() ? 2.2F : 1.2F;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2) {
                if (this.getMeterAmount() >= 1.0F) {
                    this.setChargeYaw(keyPresser.getYHeadRot());
                    this.playerDrivenChargeTicks = 100;
                    this.setMeterAmount(0.0F);
                }
            }
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ACBlockRegistry.CANDY_CANE.get().asItem());
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ACItemRegistry.CARAMEL_APPLE.get())) {
            if (!isTame()) {
                this.usePlayerItem(player, hand, itemstack);
                if (getRandom().nextInt(3) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            }
        }
        if(this.isSaddleable() && !this.isSaddled() && itemstack.is(Items.SADDLE)){
            this.usePlayerItem(player, hand, itemstack);
            this.setSaddled(true);
            return InteractionResult.SUCCESS;
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!interactionresult.consumesAction() && !type.consumesAction()) {
            if (itemstack.is(Items.SHEARS) && this.isSaddled() && isTame() && isOwnedBy(player)) {
                this.gameEvent(GameEvent.SHEAR, player);
                itemstack.hurtAndBreak(1, player, (player1) -> {
                    player1.broadcastBreakEvent(hand);
                });
                this.level().playSound((Player) null, this, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                this.setSaddled(false);
                this.spawnAtLocation(Items.SADDLE);
                return InteractionResult.SUCCESS;
            } else if (isTame() && isOwnedBy(player) && !isFood(itemstack)) {
                if (player.isShiftKeyDown()) {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() == 3) {
                        this.setCommand(0);
                    }
                    player.displayClientMessage(Component.translatable("entity.alexscaves.all.command_" + this.getCommand(), this.getName()), true);
                    boolean sit = this.getCommand() == 1;
                    if (sit) {
                        this.setOrderedToSit(true);
                    } else {
                        this.setOrderedToSit(false);
                    }
                    return InteractionResult.SUCCESS;
                } else if(this.isSaddled()){
                    if (!level().isClientSide && player.startRiding(this)) {
                        return InteractionResult.CONSUME;
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return type;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BUCK, ANIMATION_TAIL_FLICK_1, ANIMATION_TAIL_FLICK_2, ANIMATION_NIBBLE_IDLE, ANIMATION_STAB};
    }

    public int getParticleColor() {
        switch (this.getVariant()){
            case 1:
                return 0XFFADD2;
            case 2:
                return 0XDFF3FF;
            case 3:
                return 0XA7FFD0;
            case 4:
                return 0XFFBAF4;
            default:
                return 0XFFEF57;
        }
    }


    @Override
    public void onPlayerJump(int i) {
        this.setLeaping(true);
        if(this.onGround()){
            this.leapImpulse = true;
            float f = 0.2F + i * 0.01F;
            Vec3 jumpForwards = new Vec3(0F, f, this.zza).yRot((float) Math.toRadians(-this.yBodyRot));
            this.setDeltaMovement(this.getDeltaMovement().add(jumpForwards));
        }
    }

    @Override
    public boolean canJump() {
        return this.isSaddled() && !this.isLeaping();
    }

    @Override
    public void handleStartJump(int i) {

    }

    @Override
    public void handleStopJump() {

    }

    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    protected int calculateFallDamage(float f1, float f2) {
        return Mth.ceil((f1 * 0.33F - 5.0F) * f2);
    }

    @Override
    public boolean causeFallDamage(float f1, float f2, DamageSource damageSource) {
        float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, f1, f2);
        if (ret == null) return false;
        f1 = ret[0];
        f2 = ret[1];

        boolean flag = causeInternalFallDamage(f1, f2, damageSource);
        int i = this.calculateFallDamage(f1, f2);
        if (i > 0) {
            this.playSound(i > 4 ? this.getFallSounds().big() : this.getFallSounds().small(), 1.0F, 1.0F);
            this.playBlockFallSound();
            this.hurt(damageSource, (float)i);
            return true;
        } else {
            return flag;
        }
    }

    private boolean causeInternalFallDamage(float f1, float f2, DamageSource damageSource) {
        float[] ret = net.minecraftforge.common.ForgeHooks.onLivingFall(this, f1, f2);
        if (ret == null) return false;
        f1 = ret[0];
        f2 = ret[1];

        int i = this.calculateFallDamage(f1, f2);
        if (i > 0) {
            this.playBlockFallSound();
            this.hurt(damageSource, (float)i);
            return true;
        } else {
            return this.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE);
        }
    }

    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0 || player.xxa != 0) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setYHeadRot(player.getYHeadRot());
            this.setTarget(null);
        }
        if (vec3.z <= 0.0D) {
            this.gallopSoundCounter = 0;
        }
        if(player.zza > 0){
            controllerForwardsTicks++;
        }else{
            controllerForwardsTicks = 0;
        }
    }

    protected float getRiddenSpeed(Player rider) {
        float f = 0;
        if(this.isCharging()){
            f = 0.25F;
        }else if(controllerForwardsTicks < 20){
            f = (20 - controllerForwardsTicks) * -0.005F;
        }
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED)) + f;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (CHARGING.equals(dataAccessor)) {
            if(this.isCharging()){
                this.playSound(ACSoundRegistry.CANDICORN_CHARGE_START.get());
            }
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }


    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CANDICORN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CANDICORN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CANDICORN_DEATH.get();
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            float animationUp = 0;
            float animationBack = 0;
            if (this.getAnimation() == ANIMATION_BUCK) {
                float f = ACMath.cullAnimationTick(this.getAnimationTick(), 2.5F, this.getAnimation(), 1.0F, 0, 25);
                animationUp = 0.25F * f;
                animationBack = f;
            }
            if (this.getAnimation() == ANIMATION_STAB) {
                float f = ACMath.cullAnimationTick(this.getAnimationTick(), 2.0F, this.getAnimation(), 1.0F, 0, 11);
                animationUp = 0.15F * f;
                animationBack = 1.5F * f;
            }
            Vec3 seatOffset = new Vec3(0F, -0.4F + animationUp, -0.2F - animationBack).yRot((float) Math.toRadians(-this.yBodyRot));
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset(), this.getZ() + seatOffset.z);
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

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableMob.AgeableMobGroupData(0.2F);
        }
        this.setVariant(random.nextInt(5));
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
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

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (!blockState.liquid()) {
            BlockState blockstate = this.level().getBlockState(blockPos.above());
            SoundType soundtype = blockState.getSoundType(level(), blockPos, this);
            if (blockstate.is(Blocks.SNOW)) {
                soundtype = blockstate.getSoundType(level(), blockPos, this);
            }
            if (this.isVehicle()) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 6 == 0) {
                    this.playSound(ACSoundRegistry.CANDICORN_GALLOP.get(), soundtype.getVolume() * 0.2F, soundtype.getPitch());
                } else if (this.gallopSoundCounter <= 10) {
                    this.playSound(ACSoundRegistry.CANDICORN_STEP.get(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            } else {
                this.playSound(ACSoundRegistry.CANDICORN_STEP.get(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }

        }
    }

}
