package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MultipleDinosaurEggsBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.ChestThief;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexmodguy.alexscaves.server.entity.util.TargetsDroppedItems;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class VallumraptorEntity extends DinosaurEntity implements IAnimatedEntity, ICustomCollisions, PackAnimal, ChestThief, TargetsDroppedItems {

    public static final Animation ANIMATION_CALL_1 = Animation.create(15);
    public static final Animation ANIMATION_CALL_2 = Animation.create(25);
    public static final Animation ANIMATION_SCRATCH_1 = Animation.create(20);
    public static final Animation ANIMATION_SCRATCH_2 = Animation.create(20);
    public static final Animation ANIMATION_SHAKE = Animation.create(40);
    public static final Animation ANIMATION_STARTLEAP = Animation.create(20);
    public static final Animation ANIMATION_MELEE_BITE = Animation.create(15);
    public static final Animation ANIMATION_MELEE_SLASH_1 = Animation.create(15);
    public static final Animation ANIMATION_MELEE_SLASH_2 = Animation.create(15);
    public static final Animation ANIMATION_GRAB = Animation.create(40);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ELDER = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PUZZLED_HEAD_ROT = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> RELAXED_FOR = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HIDING_FOR = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.INT);
    private static final Predicate<LivingEntity> VALLUMRAPTOR_TARGETS = living -> living.getType().is(ACTagRegistry.VALLUMRAPTOR_TARGETS);
    private Animation currentAnimation;
    private int animationTick;
    private float leapProgress;
    private float prevLeapProgress;
    private float runProgress;
    private float prevRunProgress;
    private float prevPuzzleHeadRot;

    public float prevRelaxedProgress;
    public float relaxedProgress;
    private float hideProgress;
    private float prevHideProgress;
    private boolean hasRunningAttributes = false;
    private boolean hasElderAttributes = false;

    private float targetPuzzleRot;
    private VallumraptorEntity priorPackMember;
    private VallumraptorEntity afterPackMember;

    private boolean justLootedChest;

    private int fleeTicks = 0;
    private Vec3 fleeFromPosition;
    private float tailYaw;
    private float prevTailYaw;
    private int eatHeldItemIn;

    public VallumraptorEntity(EntityType entityType, Level level) {
        super(entityType, level);
        tailYaw = this.getYRot();
        prevTailYaw = this.getYRot();
    }

    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigatorNoSpin(this, level);
        navigation.setCanOpenDoors(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new AnimalFollowOwnerGoal(this, 1.0D, 5.0F, 2.0F, false) {
            @Override
            public boolean shouldFollow() {
                return VallumraptorEntity.this.getCommand() == 2;
            }

            @Override
            public void tickDistance(float distanceTo) {
                VallumraptorEntity.this.setRunning(distanceTo > 5);
            }
        });
        this.goalSelector.addGoal(3, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(4, new AnimalLayEggGoal(this, 100, 1));
        this.goalSelector.addGoal(5, new AnimalJoinPackGoal(this, 60, 8));
        this.goalSelector.addGoal(6, new FleeGoal());
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.of(ACItemRegistry.DINOSAUR_NUGGET.get()), false));
        this.goalSelector.addGoal(8, new VallumraptorMeleeGoal(this));
        this.goalSelector.addGoal(9, new VallumraptorWanderGoal(this, 1D, 25));
        this.goalSelector.addGoal(10, new VallumraptorOpenDoorGoal(this));
        this.goalSelector.addGoal(11, new AnimalLootChestsGoal(this, 20));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new MobTargetItemGoal(this, true) {
            public void start() {
                super.start();
                VallumraptorEntity.this.setRunning(true);
            }

            public void stop() {
                super.stop();
                VallumraptorEntity.this.setRunning(false);
            }
        });
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, (new HurtByTargetGoal(this, VallumraptorEntity.class)).setAlertOthers());
        this.targetSelector.addGoal(5, new AnimalPackTargetGoal(this, GrottoceratopsEntity.class, 30, false, 5));
        this.targetSelector.addGoal(6, new MobTargetUntamedGoal<>(this, Mob.class, 100, true, false, VALLUMRAPTOR_TARGETS));
        this.targetSelector.addGoal(8, new MobTargetClosePlayers(this,  120,12));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ACEntityRegistry.VALLUMRAPTOR.get().create(level);
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PUZZLED_HEAD_ROT, 0F);
        this.entityData.define(LEAPING, false);
        this.entityData.define(ELDER, false);
        this.entityData.define(RUNNING, false);
        this.entityData.define(RELAXED_FOR, 0);
        this.entityData.define(HIDING_FOR, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 28.0D);
    }

    public void tick() {
        super.tick();
        prevRunProgress = runProgress;
        prevLeapProgress = leapProgress;
        prevRelaxedProgress = relaxedProgress;
        prevHideProgress = hideProgress;
        prevTailYaw = tailYaw;
        float headPuzzleRot = getPuzzledHeadRot();
        if (isRunning() && runProgress < 5F) {
            runProgress++;
        }
        if (!isRunning() && runProgress > 0F) {
            runProgress--;
        }
        if (isLeaping() && leapProgress < 5F) {
            leapProgress++;
        }
        if (!isLeaping() && leapProgress > 0F) {
            leapProgress--;
        }
        if (getRelaxedFor() > 0 && relaxedProgress < 20F) {
            relaxedProgress++;
        }
        if (getRelaxedFor() <= 0 && relaxedProgress > 0F) {
            relaxedProgress--;
        }
        if (getHideFor() > 0 && hideProgress < 20F) {
            hideProgress++;
        }
        if (getHideFor() <= 0 && hideProgress > 0F) {
            hideProgress--;
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        }
        if (isElder() && !hasElderAttributes) {
            hasElderAttributes = true;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(32.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(5.0D);
            this.heal(36.0F);
        }
        if (!isElder() && hasElderAttributes) {
            hasElderAttributes = false;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(0.0D);
            this.heal(28.0F);
        }
        if (this.tickCount % (this.getHideFor() > 0 ? 15 : 100) == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
        }
        if (!level().isClientSide) {
            puzzledTick(headPuzzleRot);
            if (isStillEnough() && random.nextInt(100) == 0 && this.getAnimation() == NO_ANIMATION && this.getRelaxedFor() <= 0 && !this.isDancing()) {
                Animation idle;
                float rand = random.nextFloat();
                if (rand < 0.45F) {
                    idle = ANIMATION_SCRATCH_1;
                } else if (rand < 0.9F) {
                    idle = ANIMATION_SCRATCH_2;
                } else {
                    idle = ANIMATION_SHAKE;
                }
                this.setAnimation(idle);
            }
            if (fleeTicks > 0) {
                fleeTicks--;
            }
            if (this.isLeaping()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.1F, 1, 1.1F));
            }
        }
        if (this.getAnimation() == ANIMATION_CALL_1 && this.getAnimationTick() == 5 || this.getAnimation() == ANIMATION_CALL_2 && this.getAnimationTick() == 4) {
            actuallyPlayAmbientSound();
        }
        if (!level().isClientSide) {
            if (eatHeldItemIn > 0) {
                eatHeldItemIn--;
            } else if (canTargetItem(this.getMainHandItem())) {
                ItemStack stack = this.getMainHandItem();
                this.level().broadcastEntityEvent(this, (byte) 45);
                this.heal(5);
                if (stack.is(ACItemRegistry.DINOSAUR_NUGGET.get()) && justLootedChest) {
                    this.setRelaxedForTime(200 + random.nextInt(200));
                }
                if (!this.level().isClientSide) {
                    stack.shrink(1);
                }
                justLootedChest = false;
            }
            if (getRelaxedFor() > 0) {
                this.setRelaxedForTime(this.getRelaxedFor() - 1);
            }
            if (getHideFor() > 0) {
                this.setHideFor(this.getHideFor() - 1);
            }
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && !(target instanceof Player player && player.isCreative())) {
            if (this.isElder()) {
                PackAnimal leader = this;
                while (leader.getAfterPackMember() != null) {
                    leader = leader.getAfterPackMember();
                    if(!((VallumraptorEntity) leader).isAlliedTo(target)){
                        ((VallumraptorEntity) leader).setTarget(target);
                    }
                }
            }
            if (this.getHealth() < this.getMaxHealth() * 0.45F && this.isTame() && this.getHideFor() <= 0) {
                int i = 80 + random.nextInt(40);
                this.setHideFor(i);
                this.fleeFromPosition = target.position();
                this.fleeTicks = i;
                if (target instanceof Mob mob) {
                    mob.setTarget(null);
                    mob.setLastHurtByMob(null);
                    mob.setLastHurtMob(null);
                }
            }
            if (target instanceof GrottoceratopsEntity && (tickCount + this.getId()) % 20 == 0 && getPackSize() < 4 && !this.isTame()) {
                this.fleeFromPosition = target.position();
                this.fleeTicks = 100 + random.nextInt(100);
                this.setTarget(null);
                this.setLastHurtByMob(null);
            }
        }
        tailYaw = Mth.approachDegrees(this.tailYaw, yBodyRot, 8);
        prevPuzzleHeadRot = headPuzzleRot;
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void handleEntityEvent(byte b) {
        if (b == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemstack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3 headPos = (new Vec3(0D, 0.1D, 0.7D)).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + headPos.x, this.getY(0.5) + headPos.y, this.getZ() + headPos.z, (random.nextFloat() - 0.5F) * 0.1F, random.nextFloat() * 0.15F, (random.nextFloat() - 0.5F) * 0.1F);
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    public float maxSitTicks() {
        return 5.0F;
    }

    private void puzzledTick(float current) {
        float dist = Math.abs(targetPuzzleRot - current);
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() || this.getAnimation() != NO_ANIMATION || this.getRelaxedFor() > 0) {
            targetPuzzleRot = 0;
        } else if (this.random.nextInt(10) == 0 && dist <= 0.1F) {
            if (random.nextFloat() < 0.25F) {
                targetPuzzleRot = 0;
            } else {
                float invSignum = random.nextFloat() < 0.1F ? Math.signum(random.nextFloat() - 0.5F) : -Math.signum(targetPuzzleRot);
                targetPuzzleRot = random.nextFloat() * 50 * invSignum;
            }
        }
        if (current < this.targetPuzzleRot && dist > 0.1F) {
            this.setPuzzledHeadRot(current + Math.min(dist, 6));
        }
        if (current > this.targetPuzzleRot && dist > 0.1F) {
            this.setPuzzledHeadRot(current - Math.min(dist, 6));
        }
    }

    public float getTailYaw(float partialTick) {
        return (prevTailYaw + (tailYaw - prevTailYaw) * partialTick);
    }

    private boolean isStillEnough() {
        return this.getDeltaMovement().horizontalDistance() < 0.05;
    }

    private float getPuzzledHeadRot() {
        return entityData.get(PUZZLED_HEAD_ROT);
    }

    public float getPuzzledHeadRot(float f) {
        return prevPuzzleHeadRot + (getPuzzledHeadRot() - prevPuzzleHeadRot) * f;
    }

    public void setPuzzledHeadRot(float rot) {
        entityData.set(PUZZLED_HEAD_ROT, rot);
    }

    public float getLeapProgress(float partialTick) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTick) * 0.2F;
    }

    public float getRunProgress(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public float getRelaxedProgress(float partialTick) {
        return (prevRelaxedProgress + (relaxedProgress - prevRelaxedProgress) * partialTick) * 0.05F;
    }

    public float getHideProgress(float partialTick) {
        return (prevHideProgress + (hideProgress - prevHideProgress) * partialTick) * 0.05F;
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

    public boolean isElder() {
        return this.entityData.get(ELDER);
    }

    public void setElder(boolean bool) {
        this.entityData.set(ELDER, bool);
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
        return new Animation[]{ANIMATION_CALL_1, ANIMATION_CALL_2, ANIMATION_SCRATCH_1, ANIMATION_SCRATCH_2, ANIMATION_SHAKE, ANIMATION_STARTLEAP, ANIMATION_MELEE_BITE, ANIMATION_MELEE_SLASH_1, ANIMATION_MELEE_SLASH_2, ANIMATION_GRAB};
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setElder(compound.getBoolean("Elder"));
        this.setRelaxedForTime(compound.getInt("RelaxedTime"));
        this.justLootedChest = compound.getBoolean("JustLootedChest");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Elder", this.isElder());
        compound.putInt("RelaxedTime", this.getRelaxedFor());
        compound.putBoolean("JustLootedChest", this.justLootedChest);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData) {
            AgeableMob.AgeableMobGroupData data = (AgeableMob.AgeableMobGroupData) spawnDataIn;
            if (data.getGroupSize() == 0) {
                this.setElder(true);
            }
        } else {
            this.setElder(this.getRandom().nextInt(2) == 0);
        }
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public void playAmbientSound() {
        if (this.getRelaxedFor() > 0) {
            super.playAmbientSound();
        } else if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.setAnimation(random.nextBoolean() && !this.isInSittingPose() ? ANIMATION_CALL_2 : ANIMATION_CALL_1);
        }
    }

    public void actuallyPlayAmbientSound() {
        float volume = this.getSoundVolume();
        SoundEvent soundevent = this.getAmbientSound();
        if (this.getAnimation() == ANIMATION_CALL_2) {
            soundevent = ACSoundRegistry.VALLUMRAPTOR_CALL.get();
            volume += 1.0F;
        }
        if (soundevent != null) {
            this.playSound(soundevent, volume, this.getVoicePitch());
        }
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_GRAB || getRelaxedFor() > 0) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
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
        this.priorPackMember = (VallumraptorEntity) animal;
    }

    @Override
    public void setAfterPackMember(PackAnimal animal) {
        this.afterPackMember = (VallumraptorEntity) animal;
    }

    @Override
    public void afterSteal(BlockPos stealPos) {
        fleeFromPosition = Vec3.atCenterOf(stealPos);
        fleeTicks = 300 + random.nextInt(80);
        justLootedChest = true;
        if (this.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.DINOSAUR_NUGGET.get())) {
            eatHeldItemIn = 40 + random.nextInt(20);
        } else {
            eatHeldItemIn = 100 + random.nextInt(80);
        }
    }

    @Override
    public boolean isValidLeader(PackAnimal packLeader) {
        return packLeader instanceof VallumraptorEntity && ((VallumraptorEntity) packLeader).isAlive() && ((VallumraptorEntity) packLeader).isElder();
    }

    @Override
    public boolean shouldLootItem(ItemStack stack) {
        return canTargetItem(stack);
    }

    public void startOpeningChest() {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_GRAB);
        }
    }

    public boolean didOpeningChest() {
        return this.getAnimation() == ANIMATION_GRAB && this.getAnimationTick() > 15;
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockState, VoxelShape voxelShape) {
        return blockState.getBlock() instanceof DoorBlock && blockState.getValue(DoorBlock.OPEN);
    }

    @Override
    public boolean isColliding(BlockPos pos, BlockState blockState) {
        return !(blockState.getBlock() instanceof DoorBlock && blockState.getValue(DoorBlock.OPEN)) && super.isColliding(pos, blockState);
    }

    @Override
    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return (stack.is(ACTagRegistry.VALLUMRAPTOR_STEALS) || stack.getItem().isEdible() && stack.getItem().getFoodProperties(stack, this).isMeat()) && !stack.is(ACBlockRegistry.VALLUMRAPTOR_EGG.get().asItem());
    }

    public double getMaxDistToItem() {
        return 2.0D;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_GRAB);
        }
        if (this.getAnimation() == ANIMATION_GRAB && this.getAnimationTick() > 15) {
            if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide) {
                this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
            }
            this.take(e, 1);
            ItemStack duplicate = e.getItem().copy();
            duplicate.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
            e.getItem().shrink(1);
            eatHeldItemIn = isTame() ? 50 : 200;
        }
    }

    @Override
    public BlockState createEggBlockState() {
        return ACBlockRegistry.VALLUMRAPTOR_EGG.get().defaultBlockState().setValue(MultipleDinosaurEggsBlock.EGGS, 1 + random.nextInt(3));
    }

    public float getStepHeight() {
        return hasRunningAttributes ? 1.1F : 0.6F;
    }

    public boolean tamesFromHatching() {
        return true;
    }

    public boolean isFood(ItemStack stack) {
        return this.isTame() && stack.is(ACItemRegistry.DINOSAUR_NUGGET.get());
    }

    public int getRelaxedFor() {
        return this.entityData.get(RELAXED_FOR);
    }

    public void setRelaxedForTime(int ticks) {
        this.entityData.set(RELAXED_FOR, ticks);
    }

    public int getHideFor() {
        return this.entityData.get(HIDING_FOR);
    }

    public void setHideFor(int ticks) {
        this.entityData.set(HIDING_FOR, ticks);
    }

    protected SoundEvent getAmbientSound() {
        return this.getRelaxedFor() > 0 ? ACSoundRegistry.VALLUMRAPTOR_SLEEP.get() : ACSoundRegistry.VALLUMRAPTOR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.VALLUMRAPTOR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.VALLUMRAPTOR_DEATH.get();
    }


    @Override
    public boolean onFeedMixture(ItemStack itemStack, Player player) {
        if (itemStack.is(ACItemRegistry.SERENE_SALAD.get()) && this.getRelaxedFor() > 0 && !this.isTame()) {
            this.heal(5);
            this.setRelaxedForTime(0);
            this.tame(player);
            this.setCommand(1);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
            return true;
        }
        return false;
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return true;
    }

    private class FleeGoal extends Goal {

        public FleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return VallumraptorEntity.this.fleeTicks > 0 && VallumraptorEntity.this.fleeFromPosition != null;
        }

        public void stop() {
            VallumraptorEntity.this.fleeFromPosition = null;
            VallumraptorEntity.this.setRunning(false);
        }

        public void tick() {
            VallumraptorEntity.this.setRunning(true);
            if (VallumraptorEntity.this.getNavigation().isDone()) {
                int dist = VallumraptorEntity.this.getHideFor() > 0 ? 4 : 8;
                Vec3 vec3 = LandRandomPos.getPosAway(VallumraptorEntity.this, dist, dist, VallumraptorEntity.this.fleeFromPosition);
                if (vec3 != null) {
                    VallumraptorEntity.this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0F);
                }
            }
        }
    }
}
