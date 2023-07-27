package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MineExplosion;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class MineGuardianEntity extends Monster {

    private float explodeProgress;
    private float prevExplodeProgress;
    private float scanProgress;
    private float prevScanProgress;
    private boolean clientSideTouchedGround;
    private int scanTime = 0;
    private int maxScanTime = 0;
    private int maxSleepTime = 200 + random.nextInt(100);
    private int lastScanTime = 0;
    private int timeSinceHadTarget = 0;
    private static final EntityDataAccessor<Optional<UUID>> ANCHOR_UUID = SynchedEntityData.defineId(MineGuardianEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> ANCHOR_ID = SynchedEntityData.defineId(MineGuardianEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_CHAIN_LENGTH = SynchedEntityData.defineId(MineGuardianEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(MineGuardianEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EYE_CLOSED = SynchedEntityData.defineId(MineGuardianEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCANNING = SynchedEntityData.defineId(MineGuardianEntity.class, EntityDataSerializers.BOOLEAN);

    public MineGuardianEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.7F, 30);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANCHOR_UUID, Optional.empty());
        this.entityData.define(ANCHOR_ID, -1);
        this.entityData.define(MAX_CHAIN_LENGTH, 8);
        this.entityData.define(EXPLODING, false);
        this.entityData.define(EYE_CLOSED, false);
        this.entityData.define(SCANNING, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    @Nullable
    public UUID getAnchorUUID() {
        return this.entityData.get(ANCHOR_UUID).orElse(null);
    }

    public void setAnchorUUID(@Nullable UUID uniqueId) {
        this.entityData.set(ANCHOR_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getAnchor() {
        if (!level().isClientSide) {
            UUID id = getAnchorUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(ANCHOR_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        this.entityData.set(EXPLODING, explode);
    }

    public boolean isEyeClosed() {
        return this.entityData.get(EYE_CLOSED);
    }

    public void setEyeClosed(boolean eyeClosed) {
        this.entityData.set(EYE_CLOSED, eyeClosed);
    }

    public boolean isScanning() {
        return this.entityData.get(SCANNING);
    }

    public void setScanning(boolean scanning) {
        this.entityData.set(SCANNING, scanning);
    }

    public int getMaxChainLength() {
        return this.entityData.get(MAX_CHAIN_LENGTH);
    }

    public void setMaxChainLength(int length) {
        this.entityData.set(MAX_CHAIN_LENGTH, length);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    public void tick() {
        super.tick();
        prevExplodeProgress = explodeProgress;
        prevScanProgress = scanProgress;
        if (this.isScanning() && scanProgress < 5F) {
            scanProgress++;
        }
        if (!this.isScanning() && scanProgress > 0F) {
            scanProgress--;
        }
        if (this.isExploding() && explodeProgress < 10F) {
            explodeProgress += 0.5F;
        }
        if (!this.isExploding() && explodeProgress > 0F) {
            explodeProgress -= 0.5F;
        }
        if (this.isExploding()) {
            if (explodeProgress >= 10.0F) {
                this.remove(RemovalReason.KILLED);
                Explosion.BlockInteraction blockinteraction = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level(), this) ? level().getGameRules().getBoolean(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;

                MineExplosion explosion = new MineExplosion(level(), this, this.getX(), this.getY(0.5), this.getZ(), 5.0F, blockinteraction);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.3F, 1, 0.3F));
        }
        if (!level().isClientSide) {
            Entity entity = getAnchor();
            if (entity == null) {
                this.setMaxChainLength(7 + this.getRandom().nextInt(6));
                MineGuardianAnchorEntity created = new MineGuardianAnchorEntity(this);
                level().addFreshEntity(created);
                this.setAnchorUUID(created.getUUID());
                this.entityData.set(ANCHOR_ID, created.getId());
            } else {
                if (entity instanceof MineGuardianAnchorEntity anchorEntity) {
                    anchorEntity.linkWithGuardian(this);
                }
            }
            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            } else if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.6F), 0.6F, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.6F)));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.setOnGround(false);
                this.hasImpulse = true;
            }
            Entity target = this.getTarget();
            if (target == null || !target.isAlive()) {
                timeSinceHadTarget++;
            } else {
                timeSinceHadTarget = 0;
            }
            if (this.isScanning()) {
                this.setEyeClosed(false);
                if (scanTime < maxScanTime) {
                    if (scanTime % 5 == 0 && scanProgress >= 5.0F) {
                        Entity found = null;
                        HitResult hitresult = level().clip(new ClipContext(this.getEyePosition(), this.getEyePosition().add(this.getLookAngle().scale(8)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                        if (hitresult instanceof EntityHitResult entityHitResult && isValidTarget(entityHitResult.getEntity())) {
                            Entity inSight = entityHitResult.getEntity();
                            if (!inSight.equals(this) && !inSight.isAlliedTo(this) && !this.isAlliedTo(inSight) && this.hasLineOfSight(inSight)) {
                                found = inSight;
                            }
                        } else {
                            AABB around = new AABB(hitresult.getLocation().add(-0.5F, -0.5F, -0.5F), hitresult.getLocation().add(0.5F, 0.5F, 0.5F)).inflate(3);
                            for (Entity inSight : level().getEntitiesOfClass(LivingEntity.class, around)) {
                                if (!inSight.equals(this) && !inSight.isAlliedTo(this) && !this.isAlliedTo(inSight) && this.hasLineOfSight(inSight)) {
                                    if (found == null && isValidTarget(inSight) || found != null && isValidTarget(inSight) && inSight.distanceTo(this) < found.distanceTo(this)) {
                                        found = inSight;
                                    }
                                }
                            }
                        }
                        if (found instanceof LivingEntity living) {
                            this.setTarget(living);
                            this.setScanning(false);
                        }
                    }
                    scanTime++;
                } else {
                    scanTime = 0;
                    lastScanTime = tickCount;
                    this.setScanning(false);
                }
            } else if (this.isEyeClosed()) {
                int j = this.tickCount - lastScanTime;
                if (timeSinceHadTarget == 0 || !this.isInWaterOrBubble()) {
                    this.setEyeClosed(false);
                } else if (this.isInWaterOrBubble() && (timeSinceHadTarget > maxSleepTime && j > 200 || this.hurtTime > 0)) {
                    maxSleepTime = 200 + random.nextInt(100);
                    this.setScanning(true);
                    scanTime = 0;
                    maxScanTime = 100 + random.nextInt(100);
                }
            } else {
                if (this.isInWaterOrBubble() && timeSinceHadTarget > 100) {
                    this.setEyeClosed(true);
                }
            }
        } else {
            Vec3 vec3 = this.getDeltaMovement();
            if (vec3.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(), this.getSoundSource(), 1.0F, 1.0F, false);
            }

            this.clientSideTouchedGround = vec3.y < 0.0D && this.level().loadedAndEntityCanStandOn(this.blockPosition().below(), this);

        }
    }

    private boolean isValidTarget(Entity entity) {
        return entity instanceof Player player && canAttack(player);
    }

    public static boolean checkMineGuardianSpawnRules(EntityType entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        if (!level.getFluidState(blockPos).is(FluidTags.WATER)) {
            return false;
        } else {
            boolean flag = level.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(level, blockPos, randomSource) && (mobSpawnType == MobSpawnType.SPAWNER || level.getFluidState(blockPos).is(FluidTags.WATER));
            if (randomSource.nextInt(10) == 0 && blockPos.getY() < level.getSeaLevel() - 50 && flag) {
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                while (!level.getFluidState(pos).isEmpty() && pos.getY() < level.getSeaLevel()) {
                    pos.move(0, 1, 0);
                }
                int belowAirBy = pos.getY() - blockPos.getY();
                pos.set(blockPos);
                while (!level.getFluidState(pos).isEmpty() && pos.getY() > level.getMinBuildHeight()) {
                    pos.move(0, -1, 0);
                }
                BlockState groundState = level.getBlockState(pos);
                return belowAirBy > 15 && (groundState.is(ACBlockRegistry.MUCK.get()) || groundState.is(ACBlockRegistry.ABYSSMARINE.get()) || groundState.is(Blocks.DEEPSLATE));
            }
            return false;
        }
    }

    public float getScanProgress(float partialTick) {
        return (prevScanProgress + (scanProgress - prevScanProgress) * partialTick) * 0.2F;
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.1F;
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.GUARDIAN_FLOP;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("AnchorUUID")) {
            this.setAnchorUUID(compound.getUUID("AnchorUUID"));
        }
        this.setMaxChainLength(compound.getInt("MaxChainLength"));
        this.scanTime = compound.getInt("ScanTime");
        this.setEyeClosed(compound.getBoolean("EyeClosed"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getAnchorUUID() != null) {
            compound.putUUID("AnchorUUID", this.getAnchorUUID());
        }
        compound.putInt("MaxChainLength", this.getMaxChainLength());
        compound.putBoolean("EyeClosed", this.isEyeClosed());
        compound.putInt("ScanTime", this.scanTime);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setEyeClosed(true);
        timeSinceHadTarget = 10;
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);
        if (!level().isClientSide && damageSource.getEntity() instanceof Player player) {
            ACWorldData worldData = ACWorldData.get(level());
            int relations = worldData.getDeepOneReputation(player.getUUID());
            if (relations < 0) {
                worldData.setDeepOneReputation(player.getUUID(), relations + random.nextInt(3) + 1);
            }
        }
    }

    private class MeleeGoal extends Goal {

        private int timer = 0;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = MineGuardianEntity.this.getTarget();
            return target != null;
        }

        public void start() {
            timer = 0;
        }

        public void tick() {
            LivingEntity target = MineGuardianEntity.this.getTarget();
            if (target != null) {
                timer++;
                double dist = MineGuardianEntity.this.distanceTo(target);
                MineGuardianEntity.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (dist > 2.0F) {
                    if (MineGuardianEntity.this.isInWaterOrBubble()) {
                        MineGuardianEntity.this.getNavigation().moveTo(target, 1.6D);
                    }
                } else {
                    MineGuardianEntity.this.setExploding(true);
                }
                if (timer > 300) {
                    MineGuardianEntity.this.lastScanTime = MineGuardianEntity.this.tickCount;
                    MineGuardianEntity.this.timeSinceHadTarget = 5;
                    MineGuardianEntity.this.setEyeClosed(true);
                    MineGuardianEntity.this.setTarget(null);
                    MineGuardianEntity.this.setLastHurtByMob(null);
                }
            }
        }
    }
}
