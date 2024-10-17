package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ai.CorrodentAttackGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.CorrodentDigRandomlyGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.CorrodentFearLightGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTarget3DGoal;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.entity.PartEntity;

public class CorrodentEntity extends Monster implements ICustomCollisions, IAnimatedEntity {
    public static final int LIGHT_THRESHOLD = 7;
    private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(CorrodentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AFRAID = SynchedEntityData.defineId(CorrodentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DIG_PITCH = SynchedEntityData.defineId(CorrodentEntity.class, EntityDataSerializers.FLOAT);

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public final CorrodentTailEntity tailPart;
    public final CorrodentTailEntity[] allParts;
    private float[][] trailTransformations = new float[64][2];
    protected boolean isLandNavigator;
    private int trailPointer = -1;
    private float prevDigPitch = 0;
    private float fakeYRot = 0;
    private float fearProgress;
    private float prevFearProgress;
    private float digProgress;
    private float prevDigProgress;
    public int timeDigging = 0;
    public int fleeLightFor = 0;
    private Vec3 surfacePosition;
    private Vec3 prevSurfacePosition;
    private Animation currentAnimation;
    private int animationTick;

    public CorrodentEntity(EntityType type, Level level) {
        super(type, level);
        switchNavigator(true);
        tailPart = new CorrodentTailEntity(this);
        allParts = new CorrodentTailEntity[]{tailPart};
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DIGGING, false);
        this.entityData.define(AFRAID, false);
        this.entityData.define(DIG_PITCH, 0.0F);
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = createNavigation(level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new DiggingMoveControl();
            this.navigation = new Navigator(this, level());
            this.isLandNavigator = false;
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CorrodentFearLightGoal(this));
        this.goalSelector.addGoal(2, new CorrodentAttackGoal(this));
        this.goalSelector.addGoal(2, new CorrodentDigRandomlyGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 20));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new MobTarget3DGoal(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.ATTACK_DAMAGE, 3);
    }


    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0D, 1.0D, 1.0D);
    }

    public void tick() {
        super.tick();
        prevDigPitch = this.getDigPitch();
        prevDigProgress = digProgress;
        prevFearProgress = fearProgress;
        yBodyRot = Mth.approachDegrees(yBodyRotO, this.yBodyRot, 10);
        if (!isDigging() || !this.isMoving()) {
            this.setDigPitch(Mth.approachDegrees(this.getDigPitch(), 0, 10));
        }
        tickMultipart();
        if (this.isDigging() && digProgress < 5F) {
            digProgress++;
        }
        if (!this.isDigging() && digProgress > 0F) {
            if(digProgress == 5.0F){
                this.playSound(ACSoundRegistry.CORRODENT_DIG_STOP.get());
            }
            digProgress--;
        }
        if (this.isAfraid() && fearProgress < 5F) {
            fearProgress++;
        }
        if (!this.isAfraid() && fearProgress > 0F) {
            fearProgress--;
        }
        if (!level().isClientSide) {
            if (this.isDigging()) {
                timeDigging++;
                if (this.isLandNavigator) {
                    switchNavigator(false);
                    this.level().broadcastEntityEvent(this, (byte) 77);
                    this.setDigPitch(90);
                }
                if (timeDigging > 40 && !this.isInWall()) {
                    this.setDigging(false);
                    this.setPos(this.position().add(0, 1, 0));
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.35D, 0));
                }
                if (!isSafeDig(level(), this.blockPosition())) {
                    if (canDigBlock(level().getBlockState(this.blockPosition().above()))) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1D, 0));
                    }
                    if (canDigBlock(level().getBlockState(this.blockPosition().below()))) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08D, 0));
                    }
                }
                this.setNoGravity(this.isInWall());
            } else {
                timeDigging = 0;
                if (!this.isLandNavigator) {
                    switchNavigator(true);
                    this.level().broadcastEntityEvent(this, (byte) 77);
                    this.setDigPitch(-90);
                }
                this.setNoGravity(false);
            }
        }else if(this.isDigging() && isAlive()){
            AlexsCaves.PROXY.playWorldSound(this, (byte) 6);
        }
        prevSurfacePosition = surfacePosition;
        if (isMoving() || surfacePosition == null) {
            surfacePosition = calculateLightAbovePosition();
        }
        if (isDigging() && surfacePosition != null) {
            if (level().isClientSide && isMoving()) {
                BlockState surfaceState = this.level().getBlockState(BlockPos.containing(surfacePosition).below());
                BlockState onState = this.getFeetBlockState();
                if (surfaceState.isSolid()) {
                    Vec3 head = new Vec3(0, 0, 0.7F).yRot(-this.yBodyRot * ((float) Math.PI / 180F)).add(this.getX(), surfacePosition.y, this.getZ());
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, surfaceState), true, head.x, head.y, head.z, random.nextFloat() - 0.5F, random.nextFloat() + 0.5F, random.nextFloat() - 0.5F);
                    for (int i = 0; i < 4 + random.nextInt(4); i++) {
                        float j = (float) Math.pow(i, 0.75F);
                        Vec3 offset = new Vec3(i % 2 == 0 ? -j * 0.2F : j * 0.2F, 0, -0.3F * i).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, surfaceState), true, offset.x + head.x, offset.y + head.y, offset.z + head.z, (random.nextFloat() - 0.5F) * 0.2F + offset.x, (random.nextFloat() - 0.5F) * 0.2F + offset.y, (random.nextFloat() - 0.5F) * 0.2F + offset.z);
                    }
                }
                if (onState.isSolid()) {
                    for (int i = 0; i < 2 + random.nextInt(2); i++) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, onState), true, this.getRandomX(0.8F), this.getRandomY(), this.getRandomZ(0.8F), (random.nextFloat() - 0.5F) * 0.2F, (random.nextFloat() - 0.5F) * 0.2F, (random.nextFloat() - 0.5F) * 0.2F);
                    }
                }
            }
        }
        if (fleeLightFor > 0) {
            fleeLightFor--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void handleEntityEvent(byte b) {
        if (b == 77) {
            float radius = 1F;
            float particleCount = 20 + random.nextInt(12);
            for (int i1 = 0; i1 < particleCount; i1++) {
                double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                double motionY = getRandom().nextFloat() * 0.7D + 0.8F;
                double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                float angle = (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 1.2F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY) + 2, Mth.floor(this.getZ() + extraZ))));
                BlockState groundState = this.level().getBlockState(ground);
                if (groundState.isSolid()) {
                    if (level().isClientSide) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }


    public int getCorrosionAmount(BlockPos pos) {
        double distance = this.distanceToSqr(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
        if (distance <= 10) {
            BlockState state = level().getBlockState(pos);
            if (canDigBlock(state) && !state.isAir() && !state.canBeReplaced()) {
                return 10 - (int) distance;
            }
        }
        return -1;
    }

    public void setDigPitch(float pitch) {
        this.entityData.set(DIG_PITCH, pitch);
    }

    public float getDigPitch() {
        return this.entityData.get(DIG_PITCH);
    }

    private Vec3 calculateLightAbovePosition() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY(), this.getBlockZ());
        while (mutableBlockPos.getY() < level().getMaxBuildHeight() && level().getBlockState(mutableBlockPos).isSuffocating(level(), mutableBlockPos)) {
            mutableBlockPos.move(0, 1, 0);
        }
        return new Vec3(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    private void tickMultipart() {
        float digPitch = getDigPitch();
        if (trailPointer == -1) {
            this.fakeYRot = this.yBodyRot;
            for (int i = 0; i < this.trailTransformations.length; i++) {
                this.trailTransformations[i][0] = digPitch;
                this.trailTransformations[i][1] = this.fakeYRot;
            }
        }
        this.fakeYRot = Mth.approachDegrees(this.fakeYRot, this.yBodyRot, 10);
        if (++this.trailPointer == this.trailTransformations.length) {
            this.trailPointer = 0;
        }
        this.trailTransformations[this.trailPointer][0] = digPitch;
        this.trailTransformations[this.trailPointer][1] = this.fakeYRot;

        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        this.tailPart.setToTransformation(new Vec3(0, 0, -1), this.getTrailTransformation(5, 0, 1.0F), this.getTrailTransformation(5, 1, 1.0F));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    public boolean isMoving() {
        float f = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        return f > 0.1F;
    }

    public float getDigPitch(float partialTick) {
        return (prevDigPitch + (this.getDigPitch() - prevDigPitch) * partialTick);
    }

    public float getDigAmount(float partialTick) {
        return (prevDigProgress + (digProgress - prevDigProgress) * partialTick) * 0.2F;
    }

    public float getAfraidAmount(float partialTick) {
        return (prevFearProgress + (fearProgress - prevFearProgress) * partialTick) * 0.2F;
    }

    public float getTrailTransformation(int pointer, int index, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        float d0 = this.trailTransformations[j][index];
        float d1 = this.trailTransformations[i][index] - d0;
        return d0 + d1 * partialTick;
    }

    public Vec3 collide(Vec3 vec3) {
        return !isDigging() ? super.collide(vec3) : ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
        if (allParts != null) {
            for (PartEntity part : allParts) {
                part.remove(RemovalReason.KILLED);
            }
        }
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    public boolean isDigging() {
        return this.entityData.get(DIGGING);
    }

    public void setDigging(boolean bool) {
        this.entityData.set(DIGGING, bool);
    }

    public boolean isAfraid() {
        return this.entityData.get(AFRAID);
    }

    public void setAfraid(boolean bool) {
        this.entityData.set(AFRAID, bool);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return allParts;
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockState, VoxelShape voxelShape) {
        return this.isDigging() && canDigBlock(blockState);
    }


    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return (!this.isDigging() || canDigBlock(blockstate)) && super.isColliding(pos, blockstate);
    }

    public static boolean canDigBlock(BlockState state) {
        return !state.is(ACTagRegistry.CORRODENT_BLOCKS_DIGGING) && state.getFluidState().isEmpty() && state.canOcclude();
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        if (surfacePosition != null && prevSurfacePosition != null) {
            Vec3 difference = surfacePosition.subtract(prevSurfacePosition);
            return prevSurfacePosition.add(difference.scale(f)).add(0, this.getEyeHeight(), 0);
        }
        return super.getLightProbePosition(f);
    }

    public static boolean isSafeDig(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());
        return canDigBlock(state) && canDigBlock(below);
    }


    protected int calculateFallDamage(float f1, float f2) {
        return super.calculateFallDamage(f1, f2) - 5;
    }

    public boolean canReach(BlockPos target) {
        Path path = this.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - target.getX();
                int j = node.y - target.getY();
                int k = node.z - target.getZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (!isDigging()) {
            super.checkFallDamage(y, onGroundIn, state, pos);
        }
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isDigging()) {
            super.playStepSound(pos, state);
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
        return new Animation[]{ANIMATION_BITE};
    }


    public static boolean checkCorrodentSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }


    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CORRODENT_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CORRODENT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CORRODENT_DEATH.get();
    }

    static class DiggingNodeEvaluator extends FlyNodeEvaluator {

        protected BlockPathTypes evaluateBlockPathType(BlockGetter level, BlockPos pos, BlockPathTypes typeIn) {
            BlockPathTypes def = getBlockPathTypeStatic(level, pos.mutable());
            if (def == BlockPathTypes.LAVA || def == BlockPathTypes.OPEN || def == BlockPathTypes.WATER || def == BlockPathTypes.WATER_BORDER || def == BlockPathTypes.DANGER_OTHER || def == BlockPathTypes.DAMAGE_FIRE || def == BlockPathTypes.DANGER_POWDER_SNOW) {
                return BlockPathTypes.BLOCKED;
            }
            return isSafeDig(level, pos) && pos.getY() > level.getMinBuildHeight() ? BlockPathTypes.WALKABLE : BlockPathTypes.BLOCKED;
        }
    }

    class Navigator extends FlyingPathNavigation {

        public Navigator(Mob mob, Level world) {
            super(mob, world);
        }

        public boolean isStableDestination(BlockPos blockPos) {
            return !this.level.isEmptyBlock(blockPos) && CorrodentEntity.isSafeDig(level, blockPos);
        }

        protected PathFinder createPathFinder(int i) {
            this.nodeEvaluator = new DiggingNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, i);
        }

        protected double getGroundY(Vec3 vec3) {
            return vec3.y;
        }

        protected boolean canUpdatePath() {
            return true;
        }

        protected void followThePath() {
            Vec3 vector3d = this.getTempMobPos();
            this.maxDistanceToWaypoint = this.mob.getBbWidth();
            Vec3i vector3i = this.path.getNextNodePos();
            double d0 = Math.abs(this.mob.getX() - ((double) vector3i.getX() + 0.5D));
            double d1 = Math.abs(this.mob.getY() - (double) vector3i.getY());
            double d2 = Math.abs(this.mob.getZ() - ((double) vector3i.getZ() + 0.5D));
            boolean flag = d0 < (double) this.maxDistanceToWaypoint && d2 < (double) this.maxDistanceToWaypoint && d1 <= 1;
            if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
                this.path.advance();
            }

            this.doStuckDetection(vector3d);
        }

        protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec31) {
            Vec3 vector3d = new Vec3(vec31.x, vec31.y + (double) this.mob.getBbHeight() * 0.5D, vec31.z);
            BlockHitResult result = this.level.clip(new ClipContext(vec3, vector3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
            if (!isSafeDig(level, result.getBlockPos())) {
                return false;
            }
            return true;
        }

        private boolean shouldTargetNextNodeInDirection(Vec3 currentPosition) {
            if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
                return false;
            } else {
                Vec3 vector3d = Vec3.atBottomCenterOf(this.path.getNextNodePos());
                if (!currentPosition.closerThan(vector3d, 2.0D)) {
                    return false;
                } else {
                    Vec3 vector3d1 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                    Vec3 vector3d2 = vector3d1.subtract(vector3d);
                    Vec3 vector3d3 = currentPosition.subtract(vector3d);
                    return vector3d2.dot(vector3d3) > 0.0D;
                }
            }
        }
    }

    private class DiggingMoveControl extends MoveControl {
        public DiggingMoveControl() {
            super(CorrodentEntity.this);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 ed = this.mob.getNavigation().getTargetPos().getCenter();
                Vec3 vector3d = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
                double d0 = vector3d.length();
                double width = mob.getBoundingBox().getSize();
                float burySpeed = CorrodentEntity.this.timeDigging < 40 ? 0.25F : 1.0F;
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * burySpeed * 0.025D / d0);
                if (isSafeDig(level(), BlockPos.containing(wantedX, wantedY, wantedZ))) {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(vector3d1).scale(0.9F));
                } else {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(0, 0.3, 0).scale(0.7F));
                    this.operation = Operation.WAIT;
                    this.mob.getNavigation().stop();
                }
                if (d0 < width * 0.15F) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width) {
                    mob.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                    float f2 = (float) (-(Mth.atan2(vector3d1.y, vector3d1.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                    CorrodentEntity.this.setDigPitch(Mth.approachDegrees(CorrodentEntity.this.getDigPitch(), f2, 10));
                }
            }
        }

        private boolean isWalkable(Vec3 offset) {


            return true;
        }
    }
}
