package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.ChestThief;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexmodguy.alexscaves.server.entity.util.TargetsDroppedItems;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class VallumraptorEntity extends Animal implements IAnimatedEntity, ICustomCollisions, PackAnimal, ChestThief, TargetsDroppedItems, IDancesToJukebox {

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
    private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(VallumraptorEntity.class, EntityDataSerializers.BOOLEAN);
    private Animation currentAnimation;
    private int animationTick;
    private float leapProgress;
    private float prevLeapProgress;
    private float runProgress;
    private float prevRunProgress;

    private float prevPuzzleHeadRot;

    private boolean hasRunningAttributes = false;
    private boolean hasElderAttributes = false;

    private float targetPuzzleRot;
    private VallumraptorEntity priorPackMember;
    private VallumraptorEntity afterPackMember;

    private int fleeTicks = 0;
    private Vec3 fleeFromPosition;
    private float tailYaw;
    private float prevTailYaw;
    private int eatHeldItemIn;
    public float prevDanceProgress;
    public float danceProgress;
    private BlockPos jukeboxPosition;
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
        this.goalSelector.addGoal(1, new AnimalJoinPackGoal(this, 60, 8));
        this.goalSelector.addGoal(2, new FleeGoal());
        this.goalSelector.addGoal(3, new VallumraptorMeleeGoal(this));
        this.goalSelector.addGoal(4, new VallumraptorWanderGoal(this, 1D, 25));
        this.goalSelector.addGoal(5, new VallumraptorOpenDoorGoal(this));
        this.goalSelector.addGoal(6, new AnimalLootChestsGoal(this, 20));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
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
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, VallumraptorEntity.class)).setAlertOthers());
        this.targetSelector.addGoal(3, new AnimalPackTargetGoal(this, GrottoceratopsEntity.class, 30, false, 5));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Frog.class, false));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Pig.class, false));
        this.targetSelector.addGoal(4, new MobTargetClosePlayers(this, 4));
    }

    public static boolean checkPrehistoricSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ACEntityRegistry.VALLUMRAPTOR.get().create(level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PUZZLED_HEAD_ROT, 0F);
        this.entityData.define(LEAPING, false);
        this.entityData.define(ELDER, false);
        this.entityData.define(RUNNING, false);
        this.entityData.define(DANCING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 28.0D);
    }

    public void tick() {
        super.tick();
        prevRunProgress = runProgress;
        prevLeapProgress = leapProgress;
        prevTailYaw = tailYaw;
        prevDanceProgress = danceProgress;
        float headPuzzleRot = getPuzzledHeadRot();
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 15) || !this.level.getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.setDancing(false);
            this.jukeboxPosition = null;
        }
        if (isDancing() && danceProgress < 5F) {
            danceProgress++;
        }
        if (!isDancing() && danceProgress > 0F) {
            danceProgress--;
        }
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
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            maxUpStep = 1F;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            maxUpStep = 0.6F;
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
            maxUpStep = 0.6F;
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
            this.getAttribute(Attributes.ARMOR).setBaseValue(0.0D);
            this.heal(28.0F);
        }
        if(this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()){
            this.heal(2);
        }
        if (!level.isClientSide) {
            puzzledTick(headPuzzleRot);
            if (isStillEnough() && random.nextInt(100) == 0 && this.getAnimation() == NO_ANIMATION && !this.isDancing()) {
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
            if(this.isLeaping()){
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.1F, 1, 1.1F));
            }
        }
        if (this.getAnimation() == ANIMATION_CALL_1 && this.getAnimationTick() == 5 || this.getAnimation() == ANIMATION_CALL_2 && this.getAnimationTick() == 8) {
            actuallyPlayAmbientSound();
        }
        if (eatHeldItemIn > 0) {
            eatHeldItemIn--;
        } else if (canTargetItem(this.getMainHandItem())) {
            this.level.broadcastEntityEvent(this, (byte) 45);
            this.heal(5);
            if (!this.level.isClientSide) {
                this.getMainHandItem().shrink(1);
            }
        }
        LivingEntity target = this.getTarget();
        if(target != null && target.isAlive()){
            if(this.isElder()){
                PackAnimal leader = this;
                while(leader.getAfterPackMember() != null){
                    leader = leader.getAfterPackMember();
                    ((VallumraptorEntity)leader).setTarget(target);
                }
            }
            if(target instanceof GrottoceratopsEntity && (tickCount + this.getId()) % 20 == 0 && getPackSize() < 4){
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
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + headPos.x, this.getY(0.5) + headPos.y, this.getZ() + headPos.z, (random.nextFloat() - 0.5F) * 0.1F, random.nextFloat() * 0.15F, (random.nextFloat() - 0.5F) * 0.1F);
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    private void puzzledTick(float current) {
        float dist = Math.abs(targetPuzzleRot - current);
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() || this.getAnimation() != NO_ANIMATION) {
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

    public boolean isDancing() {
        return this.entityData.get(DANCING);
    }

    public void setDancing(boolean bool) {
        this.entityData.set(DANCING, bool);
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean playing) {
        this.onClientPlayMusicDisc(this.getId(), pos, playing);
    }

    @Override
    public void setJukeboxPos(BlockPos blockPos) {
        this.jukeboxPosition = blockPos;
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
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Elder", this.isElder());
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData) {
            AgeableMob.AgeableMobGroupData data = (AgeableMob.AgeableMobGroupData) spawnDataIn;
            if (data.getGroupSize() == 0) {
                this.setElder(true);
            }
        } else {
            this.setElder(this.getRandom().nextBoolean());
        }
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void calculateEntityAnimation(LivingEntity living, boolean flying) {
        living.animationSpeedOld = living.animationSpeed;
        double d0 = living.getX() - living.xo;
        double d1 = flying ? living.getY() - living.yo : 0.0D;
        double d2 = living.getZ() - living.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 6.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        living.animationSpeed += (f - living.animationSpeed) * 0.4F;
        living.animationPosition += living.animationSpeed;
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_CALL_2 : ANIMATION_CALL_1);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_GRAB || this.isDancing()) {
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

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }
    @Override
    public void afterSteal(BlockPos stealPos) {
        fleeFromPosition = Vec3.atCenterOf(stealPos);
        fleeTicks = 300 + random.nextInt(80);
        eatHeldItemIn = 100 + random.nextInt(80);
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
        return stack.is(ACTagRegistry.VALLUMRAPTOR_STEALS) || stack.getItem().isEdible() && stack.getItem().getFoodProperties(stack, this).isMeat();
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
            if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
                this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
            }
            this.take(e, 1);
            ItemStack duplicate = e.getItem().copy();
            duplicate.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
            e.getItem().shrink(1);
            eatHeldItemIn = 200;
        }
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
                Vec3 vec3 = LandRandomPos.getPosAway(VallumraptorEntity.this, 8, 7, VallumraptorEntity.this.fleeFromPosition);
                if (vec3 != null) {
                    VallumraptorEntity.this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0F);
                }
            }
        }
    }
}
