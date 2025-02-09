package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.HeartOfIronBlock;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetronJoint;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class MagnetronEntity extends Monster {

    private static final EntityDataAccessor<CompoundTag> BLOCKSTATES = SynchedEntityData.defineId(MagnetronEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<CompoundTag> BLOCK_POSES = SynchedEntityData.defineId(MagnetronEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Boolean> FORMED = SynchedEntityData.defineId(MagnetronEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_POSE = SynchedEntityData.defineId(MagnetronEntity.class, EntityDataSerializers.INT);

    private static final float FORM_TIME = 40F;
    private static final int BLOCK_COUNT = MagnetronJoint.values().length * 2;
    public float clientRoll;
    private float prevWheelRot;
    private float wheelRot;
    private float prevWheelYaw;
    private float wheelYaw;
    private float prevRollLeanProgress;
    private float rollLeanProgress;
    private float prevFormProgress;
    private float formProgress;
    private boolean gravityFlag;

    private AttackPose prevAttackPose = AttackPose.NONE;
    private float prevAttackPoseProgress = 0;
    private float attackPoseProgress = 0;
    public final MagnetronPartEntity[] allParts = new MagnetronPartEntity[BLOCK_COUNT];
    public Vec3[] lightningAnimOffsets = new Vec3[6];
    private int syncCooldown = 0;
    private boolean isLandNavigator;

    private boolean hasFormedAttributes = false;

    private boolean droppedHeart = false;
    private int movingSoundTimer;

    public MagnetronEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        buildParts();
        prevFormProgress = formProgress = FORM_TIME;
        switchMoveController(false);
        Arrays.fill(lightningAnimOffsets, Vec3.ZERO);
        this.xpReward = 13;
    }

    private void switchMoveController(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = createNavigation(level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new MoveController();
            this.navigation = createFormedNavigation(level());
            this.isLandNavigator = false;
        }
    }

    private PathNavigation createFormedNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos pos) {
                Vec3 vec3 = Vec3.atBottomCenterOf(pos.atY((int) MagnetronEntity.this.getY()));
                return ACMath.getGroundBelowPosition(level, vec3).distanceTo(vec3) < 6;
            }
        };
        return flyingpathnavigation;
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BLOCKSTATES, new CompoundTag());
        this.entityData.define(BLOCK_POSES, new CompoundTag());
        this.entityData.define(FORMED, false);
        this.entityData.define(ATTACK_POSE, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.ARMOR, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 30.0D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }

    public void removeParts() {
        if (allParts != null) {
            for (PartEntity part : allParts) {
                part.remove(RemovalReason.KILLED);
            }
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void buildParts() {
        int i = 0;
        for (MagnetronJoint joint : MagnetronJoint.values()) {
            allParts[i] = new MagnetronPartEntity(this, joint, false);
            allParts[i + 1] = new MagnetronPartEntity(this, joint, true);
            i += 2;
        }
    }

    public boolean isFunctionallyMultipart() {
        return this.isFormed() && !this.isRemoved();
    }

    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return allParts;
    }

    public void tick() {
        super.tick();
        prevWheelRot = wheelRot;
        prevWheelYaw = wheelYaw;
        prevRollLeanProgress = rollLeanProgress;
        prevFormProgress = formProgress;
        prevAttackPoseProgress = attackPoseProgress;
        boolean wheelSpinning = false;
        double speed = this.getDeltaMovement().horizontalDistance();

        if (speed > 0.01) {
            if (this.isFormed()) {
                float f = (float) Math.cos(this.walkAnimation.position() * 0.4F - 1.5F);
                if (this.walkAnimation.speed() > 0.2F && Math.abs(f) < 0.2F) {
                    if (movingSoundTimer == 0) {
                        movingSoundTimer = 5;
                        this.playSound(ACSoundRegistry.MAGNETRON_STEP.get());
                    }
                }
                if (movingSoundTimer > 0) {
                    movingSoundTimer--;
                }
            } else {
                wheelSpinning = true;
                wheelRot += Math.max(speed * 10, 1) * 15;
                if (movingSoundTimer++ > 20) {
                    this.playSound(ACSoundRegistry.MAGNETRON_ROLL.get());
                    movingSoundTimer = 0;
                }
            }
        }
        if (!wheelSpinning && Mth.wrapDegrees(wheelRot) != 0) {
            wheelRot = Mth.approachDegrees(wheelRot, 0, 15);
        }
        if (!this.level().isClientSide && !isFormed()) {
            LivingEntity target = this.getTarget();
            if (target instanceof Player && target.isAlive() && this.distanceTo(target) < 8) {
                this.startForming();
            }
        }
        if (wheelSpinning || this.isFormed()) {
            wheelYaw = Mth.approachDegrees(wheelYaw, yBodyRot, 15);
        }
        AttackPose attackPose = this.getAttackPose();
        if (this.prevAttackPose != attackPose) {
            if (attackPoseProgress < 10.0F) {
                attackPoseProgress += 1F;
            } else if (attackPoseProgress >= 10.0F) {
                if (attackPose == AttackPose.SLAM) {
                    spawnGroundEffects();
                }
                this.prevAttackPose = attackPose;
            }
        } else {
            this.attackPoseProgress = 10.0F;
        }
        if (isFormed() && formProgress < FORM_TIME) {
            if (formProgress == 0) {
                this.playSound(ACSoundRegistry.MAGNETRON_ASSEMBLE.get());
            }
            formProgress++;
        }
        if (!isFormed() && formProgress > 0F) {
            formProgress = 0;
        }
        if (wheelSpinning && rollLeanProgress < 5F) {
            rollLeanProgress++;
        }
        if (!wheelSpinning && rollLeanProgress > 0F) {
            rollLeanProgress--;
        }
        if (!level().isClientSide) {
            if (isFormed() && this.isLandNavigator) {
                switchMoveController(false);
            }
            if (!isFormed() && !this.isLandNavigator) {
                switchMoveController(true);
            }
        }
        tickMultipart();
        if (level().isClientSide) {
            for (int i = 0; i < lightningAnimOffsets.length; i++) {
                lightningAnimOffsets[i] = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).scale(0.3F);
            }
        }
        if (syncCooldown > 0) {
            syncCooldown--;
        } else {
            syncCooldown = 200;
            //syncBlockStatesWithMultipart();
        }
        if (!this.isAlive() && shouldDropBlocks()) {
            if (this.isFormed()) {
                for (MagnetronPartEntity part : allParts) {
                    if (part.getBlockState() != null && level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        BlockPos placeAt = part.blockPosition();
                        while (!level().getBlockState(placeAt).isAir() && placeAt.getY() < level().getMaxBuildHeight()) {
                            placeAt = placeAt.above();
                        }
                        FallingBlockEntity.fall(level(), placeAt, part.getBlockState());
                        part.setBlockState(null);
                    }
                }
            }
            if (!droppedHeart) {
                droppedHeart = true;
                FallingBlockEntity.fall(level(), this.blockPosition(), ACBlockRegistry.HEART_OF_IRON.get().defaultBlockState().setValue(HeartOfIronBlock.AXIS, this.getDirection().getAxis()));
            }
            removeParts();
        }
        if (isFormed() && !hasFormedAttributes) {
            hasFormedAttributes = true;
            refreshDimensions();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(80F);
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1F);
            this.heal(80F);
        }
        if (!isFormed() && !isBaby() && hasFormedAttributes) {
            hasFormedAttributes = false;
            refreshDimensions();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30F);
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0F);
            this.heal(30F);
        }
    }

    private boolean shouldDropBlocks() {
        DamageSource lastDamageSource = getLastDamageSource();
        if (lastDamageSource != null) {
            return lastDamageSource.getEntity() != null || lastDamageSource.getDirectEntity() != null || this.lastHurtByPlayer != null;
        }
        return false;
    }


    private List<BlockPos> findBlocksForTransformation() {
        List<BlockPos> all = new ArrayList<>();
        List<BlockPos> weapons = findBlocksMatching((state -> state.is(ACTagRegistry.MAGNETRON_WEAPONS)), pos -> false, 2, 1F);
        List<BlockPos> magnetic = findBlocksMatching((state -> state.is(ACTagRegistry.MAGNETIC_BLOCKS)), weapons::contains, BLOCK_COUNT - 2, 1F);
        all.addAll(weapons);
        all.addAll(magnetic);
        if (weapons.size() + magnetic.size() < BLOCK_COUNT) {
            List<BlockPos> everything = findBlocksMatching((state -> !state.isAir() && !state.is(ACTagRegistry.RESISTS_MAGNETRON_BODY_BUILDING)), all::contains, BLOCK_COUNT - weapons.size() - magnetic.size(), 0.3F);
            all.addAll(everything);
        }
        return all;
    }


    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setFormed(compound.getBoolean("Formed"));
        if (compound.contains("Blockstates", 10)) {
            this.entityData.set(BLOCKSTATES, compound.getCompound("Blockstates"));
        }
        if (compound.contains("BlockPoses", 10)) {
            this.entityData.set(BLOCK_POSES, compound.getCompound("BlockPoses"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Formed", this.isFormed());
        compound.put("Blockstates", this.entityData.get(BLOCKSTATES));
        compound.put("BlockPoses", this.entityData.get(BLOCK_POSES));

    }

    public boolean hurt(DamageSource damageSource, float f) {
        boolean prev = super.hurt(damageSource, f);
        if (prev && damageSource.getEntity() instanceof Player player && !player.isCreative() && !isFormed() && !this.isNoAi()) {
            this.startForming();
        }
        return prev;
    }

    private List<BlockPos> findBlocksMatching(Predicate<BlockState> blockMatch, Predicate<BlockPos> ignoreMatch, int maxCount, float rngDiscard) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int searchY = 4; searchY > -4; searchY--) {
            for (int searchWidth = 0; searchWidth < 15; ++searchWidth) {
                for (int searchX = 0; searchX <= searchWidth; searchX = searchX > 0 ? -searchX : 1 - searchX) {
                    for (int searchZ = searchX < searchWidth && searchX > -searchWidth ? searchWidth : 0; searchZ <= searchWidth; searchZ = searchZ > 0 ? -searchZ : 1 - searchZ) {
                        mutableBlockPos.setWithOffset(this.blockPosition(), searchX, searchY - 1, searchZ);
                        if (blockMatch.test(this.level().getBlockState(mutableBlockPos)) && !list.contains(mutableBlockPos.immutable()) && !ignoreMatch.test(mutableBlockPos.immutable()) && random.nextFloat() < rngDiscard) {
                            list.add(mutableBlockPos.immutable());
                            if (list.size() > maxCount) {
                                return list;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    private void tickMultipart() {
        Vec3[] avector3d = new Vec3[this.allParts.length];
        double minimumYPart = Double.MAX_VALUE;
        int lowestPartIndex = 0;
        for (int j = 0; j < this.allParts.length; ++j) {
            double y = this.allParts[j].getY();
            avector3d[j] = new Vec3(this.allParts[j].getX(), y, this.allParts[j].getZ());
            if (y < minimumYPart) {
                minimumYPart = y;
                lowestPartIndex = j;
            }
            if (this.isFunctionallyMultipart()) {
                this.allParts[j].positionMultipart(this);
            } else {
                this.allParts[j].copyPosition(this);
            }
        }
        if (this.isFunctionallyMultipart()) {
            double idealDistance = this.position().y - this.allParts[lowestPartIndex].getLowPoint();
            Vec3 bottom = new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
            Vec3 ground = ACMath.getGroundBelowPosition(level(), new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ()));
            Vec3 aboveGround = ground.add(0, idealDistance, 0);
            Vec3 diff = aboveGround.subtract(bottom);
            this.gravityFlag = true;
            if (this.isAlive() && bottom.distanceTo(ground) < 7 && ground.y > level().getMinBuildHeight()) {
                if (diff.length() > 1) {
                    diff = diff.normalize();
                }
                Vec3 delta = new Vec3(this.getDeltaMovement().x * 0.98, this.getDeltaMovement().y * 0.7F + diff.y * 0.25F, this.getDeltaMovement().z * 0.98);
                if (this.getAttackPose() != AttackPose.NONE) {
                    delta = new Vec3(0, delta.y, 0);
                }
                this.setDeltaMovement(delta);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            }
        }
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }


    public boolean isFormed() {
        return this.entityData.get(FORMED);
    }

    public void setFormed(boolean formed) {
        this.entityData.set(FORMED, formed);
    }

    private List<BlockState> getAllBlockStates() {
        List<BlockState> list = new ArrayList<>();
        CompoundTag data = this.entityData.get(BLOCKSTATES);
        if (data.contains("BlockData")) {
            ListTag listTag = data.getList("BlockData", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag innerTag = listTag.getCompound(i);
                list.add(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), innerTag));
            }
        }
        return list;
    }

    private void setAllBlockStates(List<BlockState> list) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (BlockState state : list) {
            listTag.add(NbtUtils.writeBlockState(state));
        }
        tag.put("BlockData", listTag);
        this.entityData.set(BLOCKSTATES, tag);
    }

    private List<BlockPos> getAllBlockPos() {
        List<BlockPos> list = new ArrayList<>();
        CompoundTag data = this.entityData.get(BLOCK_POSES);
        if (data.contains("BlockPos")) {
            ListTag listTag = data.getList("BlockPos", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag innerTag = listTag.getCompound(i);
                list.add(NbtUtils.readBlockPos(innerTag));
            }
        }
        return list;
    }

    private void setAllBlockPos(List<BlockPos> list) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (BlockPos pos : list) {
            listTag.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("BlockPos", listTag);
        this.entityData.set(BLOCK_POSES, tag);
    }

    public float getRollPosition(float partialTicks) {
        return prevWheelRot + (wheelRot - prevWheelRot) * partialTicks;
    }

    public float getWheelYaw(float partialTicks) {
        return prevWheelYaw + (wheelYaw - prevWheelYaw) * partialTicks;
    }

    public float getRollLeanProgress(float partialTicks) {
        return (prevRollLeanProgress + (rollLeanProgress - prevRollLeanProgress) * partialTicks) / 5F;
    }

    public AttackPose getAttackPose() {
        return AttackPose.get(this.entityData.get(ATTACK_POSE));
    }

    public void setAttackPose(AttackPose animation) {
        this.entityData.set(ATTACK_POSE, animation.ordinal());
    }

    public AttackPose getPrevAttackPose() {
        return prevAttackPose;
    }

    public float getAttackPoseProgress(float partialTick) {
        return (prevAttackPoseProgress + (this.attackPoseProgress - prevAttackPoseProgress) * partialTick) / 10F;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return isFormed() ? this.getBoundingBox().inflate(3.5F, 5, 3.5F) : this.getBoundingBox();
    }

    private void startForming() {
        if (!level().isClientSide) {
            List<BlockPos> poses = this.findBlocksForTransformation();
            if (poses.size() >= BLOCK_COUNT) {
                MagnetronPartEntity rightHand = this.allParts[0];
                MagnetronPartEntity leftHand = this.allParts[0];
                List<MagnetronPartEntity> needsABlock = new ArrayList<>();
                for (MagnetronPartEntity entity : this.allParts) {
                    if (entity.getJoint() == MagnetronJoint.HAND) {
                        if (entity.left) {
                            leftHand = entity;
                        } else {
                            rightHand = entity;
                        }
                    } else {
                        needsABlock.add(entity);
                    }
                }
                rightHand.setStartsAt(poses.get(0));
                leftHand.setStartsAt(poses.get(1));
                for (int i = 2; i < BLOCK_COUNT; i++) {
                    needsABlock.get(i - 2).setStartsAt(poses.get(i));
                }
            }
            this.setFormed(true);
            this.setAllBlockPos(poses);
            List<BlockState> saved = new ArrayList<>();
            for (MagnetronPartEntity entity : this.allParts) {
                if (entity.getBlockState() == null && entity.getStartPosition() != null) {
                    BlockState state = level().getBlockState(entity.getStartPosition());
                    saved.add(state);
                    if (level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !level().isClientSide) {
                        level().destroyBlock(entity.getStartPosition(), false);
                    }
                }
            }
            setAllBlockStates(saved);
        }
    }

    public boolean isNoGravity() {
        return isFormed() && gravityFlag && this.isAlive() || super.isNoGravity();
    }

    public float getFormProgress(float partialTicks) {
        return (prevFormProgress + (formProgress - prevFormProgress) * partialTicks) / FORM_TIME;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (BLOCKSTATES.equals(entityDataAccessor)) {
            syncBlockStatesWithMultipart();
        }
        if (BLOCK_POSES.equals(entityDataAccessor)) {
            syncBlockPosesWithMultipart();
        }
        if (ATTACK_POSE.equals(entityDataAccessor)) {
            this.prevAttackPoseProgress = 0.0F;
            this.attackPoseProgress = 0.0F;
        }
    }

    private void syncBlockPosesWithMultipart() {
        List<BlockPos> poses = getAllBlockPos();

        if (poses.size() >= BLOCK_COUNT) {
            MagnetronPartEntity rightHand = this.allParts[0];
            MagnetronPartEntity leftHand = this.allParts[0];
            List<MagnetronPartEntity> needsABlock = new ArrayList<>();
            for (MagnetronPartEntity entity : this.allParts) {
                if (entity.getJoint() == MagnetronJoint.HAND) {
                    if (entity.left) {
                        leftHand = entity;
                    } else {
                        rightHand = entity;
                    }
                } else {
                    needsABlock.add(entity);
                }
            }
            rightHand.setStartsAt(poses.get(0));
            leftHand.setStartsAt(poses.get(1));
            for (int i = 2; i < BLOCK_COUNT; i++) {
                needsABlock.get(i - 2).setStartsAt(poses.get(i));
            }
        }
    }

    private void syncBlockStatesWithMultipart() {
        List<BlockState> listStates = getAllBlockStates();
        if (listStates.size() >= allParts.length) {
            for (int i = 0; i < allParts.length; i++) {
                allParts[i].setBlockState(listStates.get(i));
            }
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.55F * dimensions.height;
    }

    public void spawnGroundEffects() {
        float radius = 2F;
        for (int i = 0; i < 4; i++) {
            for (int i1 = 0; i1 < 20 + random.nextInt(12); i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.yBodyRot) + i1;
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);
                Vec3 center = this.position().add(new Vec3(0, 0, 2).yRot((float) Math.toRadians(-MagnetronEntity.this.yBodyRot)));
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(center.x + extraX), Mth.floor(center.y + extraY) - 1, Mth.floor(center.z + extraZ))));
                BlockState state = this.level().getBlockState(ground);
                if (state.isSolid()) {
                    if (level().isClientSide) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, center.x + extraX, ground.getY() + extraY, center.z + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.MAGNETIZING.get();
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.MAGNETRON_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.MAGNETRON_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.MAGNETRON_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public enum AttackPose {
        NONE,
        RIGHT_PUNCH,
        LEFT_PUNCH,
        SLAM;

        public static AttackPose get(int i) {
            if (i <= 0) {
                return AttackPose.NONE;
            } else {
                return AttackPose.values()[Math.min(AttackPose.values().length - 1, i)];
            }
        }

        public boolean isRotatedJoint(MagnetronJoint joint, boolean left) {
            if (joint == MagnetronJoint.HAND) {
                if (this == LEFT_PUNCH) {
                    return left;
                } else if (this == RIGHT_PUNCH) {
                    return !left;
                } else {
                    return this == SLAM;
                }
            }
            return false;
        }
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;


        public MoveController() {
            super(MagnetronEntity.this);
            this.parentEntity = MagnetronEntity.this;
        }

        public void tick() {
            int maxTurn = 15;
            if (this.operation == Operation.MOVE_TO) {
                this.operation = Operation.WAIT;
                this.mob.setNoGravity(true);
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = d0 * d0 + d2 * d2;
                if (d3 < (double) 0.01F) {
                    this.mob.setZza(0.0F);
                    return;
                }
                if (d3 > mob.getBbWidth()) {
                    float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, maxTurn));
                }
                float f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) * 3F;

                this.mob.setSpeed(f1);
                double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                if (Math.abs(d1) > (double) 1.0E-5F || Math.abs(d4) > (double) 1.0E-5F) {
                    float f2 = (float) (-(Mth.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float) maxTurn));
                }
            } else {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }
        }
    }

    private class MeleeGoal extends Goal {

        private int punchCooldown = 0;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = MagnetronEntity.this.getTarget();
            return target != null && target.isAlive();
        }

        public void start() {
            punchCooldown = 0;
        }

        public void stop() {
            if (MagnetronEntity.this.getAttackPose() != AttackPose.NONE) {
                MagnetronEntity.this.setAttackPose(AttackPose.NONE);
            }
        }

        public void tick() {
            LivingEntity target = MagnetronEntity.this.getTarget();
            if (punchCooldown > 0) {
                punchCooldown--;
            }
            if (target != null) {
                double xzDist = MagnetronEntity.this.distanceToSqr(target.getX(), MagnetronEntity.this.getY(), target.getZ());
                double yDist = Math.abs(MagnetronEntity.this.getY() - target.getY());
                float trueDist = (float) Math.max(yDist * 0.75F, Math.sqrt((float) xzDist));
                if (xzDist < 10) {
                    if (!MagnetronEntity.this.isFormed()) {
                        MagnetronEntity.this.startForming();
                    }
                }
                if (MagnetronEntity.this.isFormed()) {
                    float progress = MagnetronEntity.this.getFormProgress(1.0F);
                    if (progress >= 1.0F) {
                        MagnetronEntity.this.getNavigation().moveTo(target, 1);
                        if (MagnetronEntity.this.getAttackPose() == AttackPose.NONE && MagnetronEntity.this.getAttackPoseProgress(1.0F) >= 1.0F) {
                            if (trueDist < 5F && punchCooldown <= 0) {
                                AttackPose set = getPoseForHand();
                                MagnetronEntity.this.playSound(ACSoundRegistry.MAGNETRON_ATTACK.get());
                                MagnetronEntity.this.setAttackPose(set);
                                punchCooldown = set == AttackPose.SLAM ? 15 : 10;
                            }
                        } else if (trueDist < 7.5F && MagnetronEntity.this.getAttackPoseProgress(1.0F) >= 0.9F) {
                            dealDamage(target, MagnetronEntity.this.getAttackPose());
                        }
                    }
                }
                MagnetronEntity.this.getNavigation().moveTo(target, 1);
                Vec3 lookDist = target.getEyePosition().subtract(MagnetronEntity.this.getEyePosition());
                float targetXRot = (float) (-(Mth.atan2(lookDist.y, lookDist.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                float targetYRot = (float) (-Mth.atan2(lookDist.x, lookDist.z) * (double) (180F / (float) Math.PI));
                MagnetronEntity.this.setXRot(targetXRot);
                MagnetronEntity.this.setYRot(targetYRot);
            }
            if (MagnetronEntity.this.getAttackPose() != AttackPose.NONE && punchCooldown == 0) {
                MagnetronEntity.this.setAttackPose(AttackPose.NONE);
            }
        }

        private int getHandDamageValueAdd(boolean left) {
            BlockState state = getStateForHand(left);
            if (state.is(BlockTags.ANVIL)) {
                return 6;
            }
            if (state.is(Blocks.STONECUTTER)) {
                return 4;
            }
            if (state.is(ACTagRegistry.MAGNETRON_WEAPONS)) {
                return 2;
            }
            return 0;
        }

        private void dealDamage(LivingEntity target, AttackPose attackPose) {
            int leftDmg = this.getHandDamageValueAdd(true);
            int rightDmg = this.getHandDamageValueAdd(true);
            if (attackPose == AttackPose.SLAM) {
                AABB bashBox = new AABB(-5F, -1F, -5F, 5F, 2F, 5F);
                Vec3 ground = ACMath.getGroundBelowPosition(level(), MagnetronEntity.this.position());
                bashBox = bashBox.move(ground.add(new Vec3(0, 0, 2).yRot((float) Math.toRadians(-MagnetronEntity.this.yBodyRot))));
                for (Entity entity : MagnetronEntity.this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
                    if (!isAlliedTo(entity) && !(entity instanceof MagnetronEntity)) {
                        entity.hurt(damageSources().mobAttack(MagnetronEntity.this), 2 + leftDmg + rightDmg);
                        launch(entity, true);
                    }
                }
            } else if (attackPose == AttackPose.LEFT_PUNCH) {
                BlockState state = getStateForHand(true);
                boolean magnet = state.is(ACBlockRegistry.AZURE_MAGNET.get()) || state.is(ACBlockRegistry.SCARLET_MAGNET.get());
                target.hurt(damageSources().mobAttack(MagnetronEntity.this), 2 + leftDmg);
                launch(target, magnet);
            } else if (attackPose == AttackPose.RIGHT_PUNCH) {
                BlockState state = getStateForHand(false);
                boolean magnet = state.is(ACBlockRegistry.AZURE_MAGNET.get()) || state.is(ACBlockRegistry.SCARLET_MAGNET.get());
                target.hurt(damageSources().mobAttack(MagnetronEntity.this), 2 + rightDmg);
                launch(target, magnet);
            }
        }

        private void launch(Entity e, boolean huge) {
            if (e.onGround()) {
                double d0 = e.getX() - MagnetronEntity.this.getX();
                double d1 = e.getZ() - MagnetronEntity.this.getZ();
                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                float f = huge ? 1F : 0.5F;
                e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
            }
        }

        private BlockState getStateForHand(boolean left) {
            if (MagnetronEntity.this.allParts != null) {
                for (MagnetronPartEntity part : MagnetronEntity.this.allParts) {
                    if (part.getJoint() == MagnetronJoint.HAND && part.left == left && part.getBlockState() != null) {
                        return part.getBlockState();
                    }
                }
            }
            return Blocks.IRON_BLOCK.defaultBlockState();
        }

        private AttackPose getPoseForHand() {
            RandomSource rand = MagnetronEntity.this.getRandom();
            int leftDmg = this.getHandDamageValueAdd(true);
            int rightDmg = this.getHandDamageValueAdd(true);
            boolean dual = rightDmg != 0 && leftDmg != 0;
            float overrideSlamChance = dual ? 0.4F : 0.6F;
            if (rand.nextFloat() < overrideSlamChance) {
                if (dual || rightDmg == 0 && leftDmg == 0) {
                    return rand.nextBoolean() ? AttackPose.LEFT_PUNCH : AttackPose.RIGHT_PUNCH;
                } else if (rightDmg != 0) {
                    return AttackPose.RIGHT_PUNCH;
                } else if (leftDmg != 0) {
                    return AttackPose.LEFT_PUNCH;
                }
            }
            return AttackPose.SLAM;
        }
    }

}
