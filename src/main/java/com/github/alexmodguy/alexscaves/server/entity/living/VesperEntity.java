package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.FlightPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.ai.VesperAttackGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.VesperFlyAndHangGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.VesperTargetUnderneathEntities;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VesperEntity extends Monster implements IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(VesperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HANGING = SynchedEntityData.defineId(VesperEntity.class, EntityDataSerializers.BOOLEAN);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    private float flyProgress;
    private float prevFlyProgress;
    private float sleepProgress;
    private float prevSleepProgress;
    private float capturedProgress;
    private float prevCapturedProgress;
    private float groundProgress = 5.0F;
    private float prevGroundProgress = 5.0F;
    private boolean validHangingPos = false;
    private int checkHangingTime;
    private BlockPos prevHangPos;
    public int timeHanging = 0;
    public int timeFlying = 0;
    private float flightPitch = 0;
    private float prevFlightPitch = 0;
    private float flightRoll = 0;
    private float prevFlightRoll = 0;
    private Animation currentAnimation;
    private int animationTick;
    public int groundedFor = 0;
    private boolean isLandNavigator;

    public VesperEntity(EntityType entityType, Level level) {
        super(entityType, level);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new MoveController();
            this.navigation = new FlightPathNavigatorNoSpin(this, level, 1.0F);
            this.isLandNavigator = false;
        }
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new VesperAttackGoal(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 15, false) {

            @Override
            public boolean canUse() {
                return !VesperEntity.this.isFlying() && !VesperEntity.this.isHanging() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !VesperEntity.this.isFlying() && !VesperEntity.this.isHanging() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(3, new VesperFlyAndHangGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new VesperTargetUnderneathEntities(this, Pig.class));
        this.targetSelector.addGoal(2, new VesperTargetUnderneathEntities(this, Player.class));
        this.targetSelector.addGoal(3, new VesperTargetUnderneathEntities(this, GloomothEntity.class));

    }

    @Override
    public void tick() {
        super.tick();
        prevFlyProgress = flyProgress;
        prevSleepProgress = sleepProgress;
        prevGroundProgress = groundProgress;
        prevCapturedProgress = capturedProgress;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (isOnGround() && groundProgress < 5F) {
            groundProgress++;
        }
        if (!isOnGround() && groundProgress > 0F) {
            groundProgress--;
        }
        if (isHanging() && sleepProgress < 5F) {
            sleepProgress++;
        }
        if (!isHanging() && sleepProgress > 0F) {
            sleepProgress--;
        }
        boolean captured = this.isPassenger();
        if (captured && capturedProgress < 5F) {
            capturedProgress++;
        }
        if (!captured && capturedProgress > 0F) {
            capturedProgress--;
        }
        if (!level.isClientSide) {
            if (this.isHanging()) {
                BlockPos above = posAbove();
                if (checkHangingTime-- < 0 || random.nextFloat() < 0.1F || prevHangPos != above) {
                    validHangingPos = canHangFrom(above, level.getBlockState(above));
                    checkHangingTime = 5 + random.nextInt(5);
                    prevHangPos = above;
                }
                if (validHangingPos) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.1F, 0.3F, 0.1F).add(0, 0.08D, 0));
                }else{
                    this.setHanging(false);
                    this.setFlying(true);
                }
                timeHanging++;
            } else {
                timeHanging = 0;
                validHangingPos = false;
                prevHangPos = null;
            }
            if (this.isFlying()) {
                timeFlying++;
                if (this.isLandNavigator) {
                    switchNavigator(false);
                }
            } else {
                timeFlying = 0;
                if (!this.isLandNavigator) {
                    switchNavigator(true);
                }
            }
        }
        if(groundedFor > 0){
            groundedFor--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        tickRotation((float) this.getDeltaMovement().y * 2 * -(float) (180F / (float) Math.PI));
    }

    private void tickRotation(float yMov) {
        flightPitch = yMov;
        float threshold = 1F;
        boolean flag = false;
        if (isFlying() && this.yRotO - this.getYRot() > threshold) {
            flightRoll += 10;
            flag = true;
        }
        if (isFlying() && this.yRotO - this.getYRot() < -threshold) {
            flightRoll -= 10;
            flag = true;
        }
        if (!flag) {
            if (flightRoll > 0) {
                flightRoll = Math.max(flightRoll - 5, 0);
            }
            if (flightRoll < 0) {
                flightRoll = Math.min(flightRoll + 5, 0);
            }
        }
        flightRoll = Mth.clamp(flightRoll, -60, 60);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HANGING, false);
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isHanging() {
        return this.entityData.get(HANGING);
    }

    public void setHanging(boolean hanging) {
        this.entityData.set(HANGING, hanging);
    }

    public float getFlightPitch(float partialTick) {
        return (prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick);
    }

    public float getFlightRoll(float partialTick) {
        return (prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick);
    }

    public float getCapturedProgress(float partialTick) {
        return (prevCapturedProgress + (capturedProgress - prevCapturedProgress) * partialTick) * 0.2F;
    }

    public float getSleepProgress(float partialTick) {
        return (prevSleepProgress + (sleepProgress - prevSleepProgress) * partialTick) * 0.2F;
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

    public float getGroundProgress(float partialTick) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTick) * 0.2F;
    }

    public boolean canHangFrom(BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.DOWN) && level.isEmptyBlock(pos.below()) && level.isEmptyBlock(pos.below(2));
    }

    public BlockPos posAbove() {
        return BlockPos.containing(this.getX(), this.getBoundingBox().maxY + 0.1F, this.getZ());
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


    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 4.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos above = this.blockPosition();
        int upBy = 100;
        int k = 0;
        while(world.isEmptyBlock(above) && above.getY() < level.getMaxBuildHeight() && k < upBy){
            above = above.above();
            k++;
        }
        if(world.isEmptyBlock(above)){
            this.setFlying(true);
        }else{
            this.setHanging(true);
        }
        this.setPos(above.getX() + 0.5F, above.getY() - this.getBoundingBox().getYsize(), above.getZ() + 0.5F);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static boolean checkVesperSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;


        public MoveController() {
            super(VesperEntity.this);
            this.parentEntity = VesperEntity.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 ed = this.mob.getNavigation().getTargetPos().getCenter();
                //((ServerLevel)mob.level).sendParticles(ParticleTypes.HEART, ed.x, ed.y, ed.z, 0, 0, 0, 0, 1);
                //((ServerLevel)mob.level).sendParticles(ParticleTypes.SNEEZE, wantedX, wantedY, wantedZ, 0, 0, 0, 0, 1);
                double d1 = this.wantedY - this.mob.getY();
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05D / d0);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1).scale(0.95D).add(0, -0.01, 0));
                if (d0 < width) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width) {
                    float yaw = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.setYRot(Mth.approachDegrees(parentEntity.getYRot(), yaw, 8));
                }
                this.parentEntity.setNoGravity(true);
            }else{
                this.parentEntity.setNoGravity(false);
            }
        }
    }
}
