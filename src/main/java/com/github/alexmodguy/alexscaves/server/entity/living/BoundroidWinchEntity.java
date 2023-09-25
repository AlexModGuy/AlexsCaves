package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class BoundroidWinchEntity extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID = SynchedEntityData.defineId(BoundroidWinchEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HEAD_ID = SynchedEntityData.defineId(BoundroidWinchEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LATCHED = SynchedEntityData.defineId(BoundroidWinchEntity.class, EntityDataSerializers.BOOLEAN);
    private static final float MAX_DIST_TO_CEILING = 2.9F;
    private float latchProgress;
    private float prevLatchProgress;
    private float distanceToCeiling;
    private boolean goingUp;
    private int lastStepTimestamp = -1;
    private boolean isUpsideDownNavigator = false;
    private int noLatchCooldown = 0;
    private int changeLatchStateTime = 0;

    public BoundroidWinchEntity(EntityType entityType, Level level) {
        super(entityType, level);
        switchNavigator(false);
    }

    public BoundroidWinchEntity(BoundroidEntity parent) {
        this(ACEntityRegistry.BOUNDROID_WINCH.get(), parent.level());
        this.setHeadUUID(parent.getUUID());
        this.setPos(parent.position().add(0, 0.5F, 0));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new FindShelterGoal());
        this.goalSelector.addGoal(3, new WanderUpsideDownGoal());
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D, 45));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    public boolean requiresCustomPersistence() {
        return this.getHead() != null;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity body = this.getHead();
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (body != null && !body.isInvulnerableTo(source)) {
            boolean flag = body.hurt(source, damage);
            if (flag) {
                noLatchCooldown = 60 + random.nextInt(60);
            }
            return flag;
        }
        return super.hurt(source, damage);
    }


    public void linkWithHead(Entity head) {
        this.setHeadUUID(head.getUUID());
        this.entityData.set(HEAD_ID, head.getId());
    }

    public boolean isLatched() {
        return this.entityData.get(LATCHED);
    }

    public void setLatched(boolean latched) {
        this.entityData.set(LATCHED, latched);
    }


    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("HeadUUID")) {
            this.setHeadUUID(compound.getUUID("HeadUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getHeadUUID() != null) {
            compound.putUUID("BodyUUID", this.getHeadUUID());
        }
    }

    private void switchNavigator(boolean clinging) {
        if (clinging) {
            this.moveControl = new CeilingMoveControl();
            this.navigation = createCeilingNavigator(level());
            this.isUpsideDownNavigator = true;
        } else {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isUpsideDownNavigator = false;
        }
    }

    protected PathNavigation createCeilingNavigator(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos pos) {
                int airAbove = 0;
                while (level().getBlockState(pos).isAir() && airAbove < MAX_DIST_TO_CEILING + 1) {
                    pos = pos.above();
                    airAbove++;
                }
                return airAbove < Math.min(MAX_DIST_TO_CEILING, random.nextInt((int) MAX_DIST_TO_CEILING));
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        return flyingpathnavigation;
    }

    public void tick() {
        super.tick();
        this.prevLatchProgress = latchProgress;
        if (this.isLatched() && latchProgress < 5F) {
            latchProgress++;
        }
        if (!this.isLatched() && latchProgress > 0F) {
            latchProgress--;
        }
        if (noLatchCooldown > 0) {
            noLatchCooldown--;
        }
        double d1 = (this.getX() - xo);
        double d2 = (this.getZ() - zo);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        if (isLatched() && verticalCollision && d3 > 0.1F) {
            if (tickCount - lastStepTimestamp > 6) {
                lastStepTimestamp = tickCount;
                BlockState state = level().getBlockState(BlockPos.containing(this.getX(), this.getBoundingBox().maxY + 0.5, this.getZ()));
                this.playSound(state.getSoundType().getStepSound(), 1F, 0.5F);
            }
        }
        if (!level().isClientSide) {
            if (this.isLatched() && !this.isUpsideDownNavigator) {
                switchNavigator(true);
            }
            if (!this.isLatched() && this.isUpsideDownNavigator) {
                switchNavigator(false);
            }
        }
        Entity head = getHead();
        if (head instanceof BoundroidEntity boundroid) {
            if (!level().isClientSide) {
                this.entityData.set(HEAD_ID, head.getId());
                double distance = this.distanceTo(boundroid);
                double distanceGoal = isLatched() ? 1.25F + Math.sin(tickCount * 0.1F) * 0.25F : 3.5F;
                boolean headNoClip = false;
                float pullSpeed = boundroid.getTarget() != null ? 0.3F : 0.1F;
                if (!boundroid.hasLineOfSight(this) && !boundroid.stopPullingUp() && distance > 7) {
                    headNoClip = true;
                }
                if (distance > distanceGoal && !boundroid.stopPullingUp()) {
                    double disRem = Math.min(distance - distanceGoal, 1F);
                    Vec3 moveTo = getChainFrom(1.0F).subtract(boundroid.position());
                    if (moveTo.length() > 1.0D) {
                        moveTo = moveTo.normalize();
                    }
                    boundroid.draggedClimable = true;
                    boundroid.setDeltaMovement(boundroid.getDeltaMovement().multiply(0.95F, 0.7F, 0.95F).add(moveTo.scale(disRem * pullSpeed)));
                } else {
                    boundroid.draggedClimable = false;
                }
                distanceToCeiling = calculateDistanceToCeiling();
                if (this.isLatched()) {
                    this.setNoGravity(true);
                    if (distanceToCeiling > MAX_DIST_TO_CEILING || !isAlive() || noLatchCooldown > 0) {
                        changeLatchStateTime++;
                    } else {
                        changeLatchStateTime = 0;
                    }
                    if (changeLatchStateTime > 5) {
                        this.setLatched(false);
                        if(noLatchCooldown > 0){
                            this.playSound(ACSoundRegistry.BOUNDROID_DAZED.get(), 2.0F, 1.0F);
                        }
                        changeLatchStateTime = 0;
                    }
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.14, 0).scale(0.85F));
                    goingUp = false;
                    boundroid.stopGravity = true;
                } else {
                    this.setNoGravity(false);
                    boundroid.stopGravity = false;
                    if ((distanceToCeiling < MAX_DIST_TO_CEILING || verticalCollision && !verticalCollisionBelow) && noLatchCooldown <= 0) {
                        changeLatchStateTime++;
                    } else {
                        changeLatchStateTime = 0;
                    }
                    if (changeLatchStateTime > 5) {
                        this.setLatched(true);
                        changeLatchStateTime = 0;
                    }
                    if (goingUp) {
                        this.setDeltaMovement(new Vec3(this.getDeltaMovement().x, 1.5F, this.getDeltaMovement().z));
                    } else if (this.onGround() && noLatchCooldown == 0 && this.isAlive() && random.nextInt(30) == 0 && distanceToCeiling > MAX_DIST_TO_CEILING && !this.level().canSeeSky(this.blockPosition())) {
                        goingUp = true;
                    }
                }
                boundroid.noPhysics = headNoClip;
            }
            if (boundroid.hurtTime > 0 || boundroid.deathTime > 0) {
                this.hurtTime = boundroid.hurtTime;
                this.deathTime = boundroid.deathTime;
            }
        } else if (!level().isClientSide) {
            this.remove(RemovalReason.KILLED);
        }
    }

    public float getLatchProgress(float partialTicks) {
        return (prevLatchProgress + (latchProgress - prevLatchProgress) * partialTicks) * 0.2F;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getChainLength(float partialTick) {
        return (float) getChainTo(partialTick).subtract(getChainFrom(partialTick)).length();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAD_UUID, Optional.empty());
        this.entityData.define(HEAD_ID, -1);
        this.entityData.define(LATCHED, false);
    }

    @Nullable
    public UUID getHeadUUID() {
        return this.entityData.get(HEAD_UUID).orElse(null);
    }

    public void setHeadUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HEAD_UUID, Optional.ofNullable(uniqueId));
    }


    public Vec3 getChainFrom(float partialTicks) {
        return this.getPosition(partialTicks).add(0, 0.0, 0);
    }

    public Vec3 getChainTo(float partialTicks) {
        if (getHead() instanceof BoundroidEntity boundroid) {
            return boundroid.getPosition(partialTicks).add(0, boundroid.getBbHeight(), 0);
        }
        return this.getPosition(partialTicks).add(0, 0.3, 0);
    }

    public Entity getHead() {
        if (!level().isClientSide) {
            UUID id = getHeadUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(HEAD_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isLatched()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            super.travel(travelVector);
        }

    }

    private float calculateDistanceToCeiling() {
        BlockPos ceiling = this.getCeilingOf(this.blockPosition());
        return (float) (ceiling.getY() - this.getBoundingBox().maxY);
    }

    public BlockPos getCeilingOf(BlockPos usPos) {
        while (!level().getBlockState(usPos).isFaceSturdy(level(), usPos, Direction.DOWN) && usPos.getY() < level().getMaxBuildHeight()) {
            usPos = usPos.above();
        }
        return usPos;
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.MAGNETIZING.get();
    }

    class WanderUpsideDownGoal extends RandomStrollGoal {

        private int stillTicks = 0;

        public WanderUpsideDownGoal() {
            super(BoundroidWinchEntity.this, 1D, 10);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Nullable
        protected Vec3 getPosition() {
            if (BoundroidWinchEntity.this.isLatched()) {
                int distance = 16;
                for (int i = 0; i < 15; i++) {
                    Random rand = new Random();
                    BlockPos randPos = BoundroidWinchEntity.this.blockPosition().offset(rand.nextInt(distance * 2) - distance, (int) (-MAX_DIST_TO_CEILING - 3), rand.nextInt(distance * 2) - distance);
                    BlockPos lowestPos = BoundroidWinchEntity.this.getCeilingOf(randPos).below();
                    return Vec3.atCenterOf(lowestPos);
                }
                return null;
            } else {
                return super.getPosition();
            }
        }

        public boolean canUse() {
            return super.canUse();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && random.nextInt(100) != 0;
        }

        public void stop() {
            super.stop();
            this.wantedX = 0;
            this.wantedY = 0;
            this.wantedZ = 0;
        }

        public void start() {
            this.stillTicks = 0;
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }
    }

    class FindShelterGoal extends Goal {
        private double wantedX;
        private double wantedY;
        private double wantedZ;

        public FindShelterGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (!BoundroidWinchEntity.this.level().canSeeSky(BoundroidWinchEntity.this.blockPosition()) && BoundroidWinchEntity.this.getRandom().nextInt(20) != 0) {
                return false;
            } else {
                return this.setWantedPos();
            }
        }

        protected boolean setWantedPos() {
            Vec3 vec3 = this.getHidePos();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                return true;
            }
        }

        public boolean canContinueToUse() {
            return !BoundroidWinchEntity.this.getNavigation().isDone();
        }

        public void start() {
            BoundroidWinchEntity.this.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, 1F);
        }

        @Nullable
        protected Vec3 getHidePos() {
            RandomSource randomsource = BoundroidWinchEntity.this.getRandom();
            BlockPos blockpos = BoundroidWinchEntity.this.blockPosition();

            for (int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(randomsource.nextInt(20) - 10, randomsource.nextInt(6) - 3, randomsource.nextInt(20) - 10);
                if (!BoundroidWinchEntity.this.level().canSeeSky(blockpos1) && BoundroidWinchEntity.this.getWalkTargetValue(blockpos1) < 0.0F) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }
            return null;
        }
    }

    class CeilingMoveControl extends MoveControl {
        private final Mob parentEntity;

        public CeilingMoveControl() {
            super(BoundroidWinchEntity.this);
            this.parentEntity = BoundroidWinchEntity.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.035D / d0);
                float verticalSpeed = 0.15F;
                float y = parentEntity.horizontalCollision ? -0.2F : parentEntity.verticalCollision ? 0.2F : 1.2F;
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.95F).add(0, y, 0).add(vector3d1.multiply(1F, verticalSpeed, 1F)));
                if (parentEntity.getTarget() != null) {
                    double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                    double d3 = parentEntity.getTarget().getY() - parentEntity.getY();
                    double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                    float f = Mth.sqrt((float) (d2 * d2 + d1 * d1));
                    parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    parentEntity.setXRot((float) (Mth.atan2(d3, f) * (double) (180F / (float) Math.PI)));
                    parentEntity.yBodyRot = parentEntity.getYRot();
                } else if (d0 >= width) {
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                }
            }
        }
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (BoundroidWinchEntity.this.getHead() instanceof BoundroidEntity boundroid) {
                Entity target = boundroid.getTarget();
                return target != null && target.isAlive();
            }
            return false;
        }

        @Override
        public void stop() {
            if (BoundroidWinchEntity.this.getHead() instanceof BoundroidEntity boundroid) {
                boundroid.setScared(false);
            }

        }

        public void tick() {
            if (BoundroidWinchEntity.this.getHead() instanceof BoundroidEntity boundroid) {
                Entity target = boundroid.getTarget();
                if (target != null && target.isAlive()) {
                    if (BoundroidWinchEntity.this.isLatched()) {
                        boundroid.setScared(false);
                        BlockPos lowestPos = BoundroidWinchEntity.this.getCeilingOf(target.blockPosition());
                        BoundroidWinchEntity.this.getNavigation().moveTo(lowestPos.getX(), lowestPos.getY(), lowestPos.getZ(), 1D);
                    } else {
                        if (BoundroidWinchEntity.this.getNavigation().isDone()) {
                            Vec3 vec = LandRandomPos.getPosAway(BoundroidWinchEntity.this, 15, 7, target.position());
                            if (vec != null) {
                                BoundroidWinchEntity.this.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.3);
                            }
                        }
                        boundroid.setScared(true);
                    }
                }
            }
        }
    }
}
