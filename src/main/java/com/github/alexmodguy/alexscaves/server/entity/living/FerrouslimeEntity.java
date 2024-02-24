package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTarget3DGoal;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FerrouslimeEntity extends Monster {
    private Map<Integer, Vec3> headOffsets = new HashMap<>();
    public float prevHeadCount = 1;
    private float prevMergeProgress;
    private float mergeProgress;
    private float prevAttackProgress;
    private float attackProgress;
    private static final EntityDataAccessor<Integer> HEADS = SynchedEntityData.defineId(FerrouslimeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(FerrouslimeEntity.class, EntityDataSerializers.INT);
    private int mergeCooldown = 0;
    private int noMoveTime = 0;

    public FerrouslimeEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 1;
        this.moveControl = new MoveController();
        prevMergeProgress = 5;
        mergeProgress = 5;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new FormGoal());
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 20));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new MobTarget3DGoal(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEADS, 1);
        this.entityData.define(ATTACK_TICK, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.35D).add(Attributes.MAX_HEALTH, 10.0D);
    }

    public boolean isNoGravity() {
        return true;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void dropFromLootTable(DamageSource source, boolean b) {
        if (this.getHeadCount() <= 1) {
            super.dropFromLootTable(source, b);
        }
    }

    public boolean isFakeEntity() {
        return this.firstTick;
    }

    public void tick() {
        super.tick();
        this.setYHeadRot(this.getYRot());
        prevMergeProgress = mergeProgress;
        prevAttackProgress = attackProgress;
        if (prevHeadCount != this.getHeadCount()) {
            refreshDimensions();
            if (this.mergeProgress < 5.0F) {
                this.mergeProgress++;
            } else {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Mth.clamp(getHeadCount() * 10, 10, 100));
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Mth.clamp(getHeadCount() * 2, 2, 10));
                double d = this.getAttribute(Attributes.MAX_HEALTH).getValue();
                if(this.getHealth() < d && getHeadCount() > 0){
                    this.heal((float) Math.ceil(d - this.getHealth()));
                }
                prevHeadCount = this.getHeadCount();
            }
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            LivingEntity target = this.getTarget();
            if (attackProgress >= 3F && target != null && this.distanceTo(target) < getSlimeSize(1.0F)) {
                target.hurt(damageSources().mobAttack(this), 4F + getHeadCount() * 2);
            }
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        if (level().isClientSide && isAlive()) {
            float slimeSize = getSlimeSize(1);
            for (int i = 0; i < Math.ceil(slimeSize); i++) {
                double particleX = this.getX() + (random.nextDouble() - 0.5F) * (slimeSize + 1.5F);
                double particleY = this.getY() + (random.nextDouble() - 0.5F) * (slimeSize + 1.5F);
                double particleZ = this.getZ() + (random.nextDouble() - 0.5F) * (slimeSize + 1.5F);
                level().addParticle(ACParticleRegistry.FERROUSLIME.get(), particleX, particleY, particleZ, this.getId(), 0, 0);
            }
            AlexsCaves.PROXY.playWorldSound(this, (byte) 13);
        } else {
            LivingEntity living = this.getTarget();
            if (living != null && living.isAlive()) {
                if (this.getDeltaMovement().length() < 0.1) {
                    noMoveTime++;
                } else {
                    noMoveTime = 0;
                }
                if (noMoveTime > 40 && mergeCooldown <= 0) {
                    split(1200);
                }
            }
        }
        if (mergeCooldown > 0) {
            mergeCooldown--;
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        if (this.getHeadCount() >= 2 && this.isDeadOrDying()) {
            int ours = this.getHeadCount() / 2;
            int theirs = this.getHeadCount() - ours;
            this.mergeCooldown = 1200;
            level().addFreshEntity(this.makeSlime(ours, 1200));
            level().addFreshEntity(this.makeSlime(theirs, 1200));
        }
        super.remove(removalReason);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    protected float getStandingEyeHeight(Pose p_33614_, EntityDimensions dimensions) {
        return 0.625F * dimensions.height;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (HEADS.equals(entityDataAccessor)) {
            refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            this.mergeProgress = 0;
        }
    }

    public int getHeadCount() {
        return this.entityData.get(HEADS);
    }

    public void setHeadCount(int headCount) {
        this.entityData.set(HEADS, headCount);
    }

    public float getMergeProgress(float partialTick) {
        return (this.prevMergeProgress + (mergeProgress - prevMergeProgress) * partialTick) * 0.2F;
    }

    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level, AdvancedPathNavigate.MovementType.FLYING);
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level().getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {

    }

    public Vec3 getHeadOffsetPos(int i) {
        if (i <= 1) {
            return Vec3.ZERO;
        } else if (headOffsets.containsKey(i)) {
            return headOffsets.get(i);
        } else {
            Vec3 vec = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).scale(getSlimeSize(1.0F) * 0.5F);
            headOffsets.put(i, vec);
            return vec;
        }
    }

    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(getSlimeSize(1.0F));
    }

    public float getSlimeSize(float partialTicks) {
        float smoothHeadCount = getHeadCount() - 1 + getMergeProgress(partialTicks);
        return Math.min((float) (Math.log(smoothHeadCount) + 1) * 1.2F, 3.2F);
    }

    public boolean split(int cooldown) {
        if (this.getHeadCount() >= 2) {
            int ours = this.getHeadCount() / 2;
            int theirs = this.getHeadCount() - ours;
            this.mergeCooldown = 1200;
            level().addFreshEntity(this.makeSlime(ours, 1200));
            level().addFreshEntity(this.makeSlime(theirs, 1200));
            this.remove(RemovalReason.DISCARDED);
            return true;
        }
        return false;
    }

    private FerrouslimeEntity makeSlime(int heads, int cooldown) {
        Component component = this.getCustomName();
        FerrouslimeEntity ferrouslime = ACEntityRegistry.FERROUSLIME.get().create(level());
        ferrouslime.setPos(this.position());
        ferrouslime.setHeadCount(heads);
        ferrouslime.setCustomName(component);
        ferrouslime.setNoAi(this.isNoAi());
        ferrouslime.setInvulnerable(this.isInvulnerable());
        ferrouslime.mergeCooldown = cooldown;
        ferrouslime.setYRot(this.yHeadRot);
        ferrouslime.yBodyRot = this.yHeadRot;
        return ferrouslime;
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setHeadCount(1 + random.nextInt(2));
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canCutCorner(BlockPathTypes types) {
        return true;
    }

    public float getAttackProgress(float partialTicks) {
        return (prevAttackProgress + (attackProgress - prevAttackProgress) * partialTicks) * 0.2F;
    }

    public boolean doHurtTarget(Entity entityIn) {
        this.entityData.set(ATTACK_TICK, 10);
        return super.doHurtTarget(entityIn);
    }

    private boolean canForm() {
        return this.isAlive() && mergeCooldown <= 0;
    }

    public int getExperienceReward() {
        return 2;
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.MAGNETIZING.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.FERROUSLIME_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.FERROUSLIME_DEATH.get();
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;
        private Direction lastSlideDirection;
        private int slideStop = 0;
        private int slideFor = 0;

        public MoveController() {
            super(FerrouslimeEntity.this);
            this.parentEntity = FerrouslimeEntity.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                BlockPos target = BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ);
                BlockPos centerPos = BlockPos.containing(parentEntity.getX(), parentEntity.getY(0.5F), parentEntity.getZ());
                BlockPos closest = centerPos;
                if (slideStop <= 0) {
                    lastSlideDirection = null;
                    for (Direction direction : Direction.values()) {
                        BlockPos check = centerPos.relative(direction);
                        if (check.distSqr(target) < closest.distSqr(target) && level().getBlockState(check).isAir()) {
                            lastSlideDirection = direction;
                        }
                    }
                    slideStop = 6;
                } else {
                    slideStop--;
                }
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                if (lastSlideDirection != null && !parentEntity.horizontalCollision) {
                    vector3d = vector3d.multiply(Math.abs(lastSlideDirection.getStepX()), Math.abs(lastSlideDirection.getStepY()), Math.abs(lastSlideDirection.getStepZ()));
                }
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                LivingEntity attackTarget = parentEntity.getTarget();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.25D / d0);
                if (d0 < 0.5) {
                    this.operation = Operation.WAIT;
                } else {
                    parentEntity.setDeltaMovement(vector3d1);
                    if (d0 >= width && attackTarget == null) {
                        parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                        if (FerrouslimeEntity.this.getTarget() != null) {
                            parentEntity.yBodyRot = parentEntity.getYRot();
                        }
                    }
                }
            }
        }
    }

    private class MeleeGoal extends Goal {
        private int cooldown = 0;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return FerrouslimeEntity.this.getTarget() != null && FerrouslimeEntity.this.getTarget().isAlive();
        }

        public void tick() {
            LivingEntity target = FerrouslimeEntity.this.getTarget();
            if (target != null && target.isAlive()) {
                FerrouslimeEntity.this.getNavigation().moveTo(target, 1F);
                FerrouslimeEntity.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (FerrouslimeEntity.this.distanceTo(target) < 1 + FerrouslimeEntity.this.getSlimeSize(1.0F) && FerrouslimeEntity.this.hasLineOfSight(target) && cooldown == 0) {
                    FerrouslimeEntity.this.doHurtTarget(target);
                    cooldown = 10;
                }
            }
            if (cooldown > 0) {
                cooldown--;
            }
        }

        public void stop() {
            cooldown = 0;
        }
    }

    private class FormGoal extends Goal {

        int executionCooldown = 0;
        FerrouslimeEntity otherslime;

        public FormGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!FerrouslimeEntity.this.canForm()) {
                return false;
            }
            if (executionCooldown-- < 0) {
                executionCooldown = FerrouslimeEntity.this.getTarget() == null ? 100 : 20;
                FerrouslimeEntity closest = null;
                for (FerrouslimeEntity slime : FerrouslimeEntity.this.level().getEntitiesOfClass(FerrouslimeEntity.class, FerrouslimeEntity.this.getBoundingBox().inflate(30, 30, 30))) {
                    if (slime != FerrouslimeEntity.this && slime.canForm() && (closest == null || slime.distanceTo(FerrouslimeEntity.this) < closest.distanceTo(FerrouslimeEntity.this))) {
                        closest = slime;
                    }
                }
                otherslime = closest;
                return otherslime != null;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return otherslime != null && FerrouslimeEntity.this.canForm() && otherslime.canForm() && FerrouslimeEntity.this.distanceTo(otherslime) < 32;
        }

        public void tick() {
            FerrouslimeEntity.this.getNavigation().moveTo(otherslime, 1);
            if (FerrouslimeEntity.this.distanceTo(otherslime) <= 0.5F + (FerrouslimeEntity.this.getBbWidth() + otherslime.getBbWidth()) / 2D && otherslime.canForm()) {
                FerrouslimeEntity.this.setHeadCount(FerrouslimeEntity.this.getHeadCount() + otherslime.getHeadCount());
                otherslime.remove(RemovalReason.DISCARDED);
                FerrouslimeEntity.this.playSound(ACSoundRegistry.FERROUSLIME_COMBINE.get());
                otherslime = null;
                FerrouslimeEntity.this.mergeCooldown = 600;
            }
        }
    }
}
