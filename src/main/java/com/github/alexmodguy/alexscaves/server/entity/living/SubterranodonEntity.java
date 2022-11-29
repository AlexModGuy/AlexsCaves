package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.SubterranodonFlightGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.SubterranodonJoinPackGoal;
import com.github.alexmodguy.alexscaves.server.message.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SubterranodonEntity extends Animal {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(SubterranodonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOVERING = SynchedEntityData.defineId(SubterranodonEntity.class, EntityDataSerializers.BOOLEAN);
    private float flyProgress;
    private float prevFlyProgress;
    private float flapAmount;
    private float prevFlapAmount;
    private float hoverProgress;
    private float prevHoverProgress;
    private float flightPitch = 0;
    private float prevFlightPitch = 0;
    private float flightRoll = 0;
    private float prevFlightRoll = 0;
    private float tailYaw;
    private float prevTailYaw;
    private boolean isLandNavigator;

    private SubterranodonEntity priorPackMember;
    private SubterranodonEntity afterPackMember;
    public int timeFlying;

    public Vec3 lastFlightTargetPos;

    public boolean resetFlightAIFlag = false;
    public boolean landingFlag;
    public SubterranodonEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        switchNavigator(true);
        tailYaw = this.yBodyRot;
        prevTailYaw = this.yBodyRot;
    }


    public static boolean checkPrehistoricSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos.below()).is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SubterranodonJoinPackGoal(this));
        this.goalSelector.addGoal(2, new SubterranodonFlightGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.timeFlying = compound.getInt("TimeFlying");
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("TimeFlying", this.timeFlying);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HOVERING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FLYING_SPEED, 1F).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveHelper(this);
            this.navigation = new FlyingPathNavigation(this, level);
            this.isLandNavigator = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        prevFlyProgress = flyProgress;
        prevHoverProgress = hoverProgress;
        prevFlapAmount = flapAmount;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        prevTailYaw = tailYaw;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (isHovering() && hoverProgress < 5F) {
            hoverProgress++;
        }
        if (!isHovering() && hoverProgress > 0F) {
            hoverProgress--;
        }
        float yMov = (float) this.getDeltaMovement().y;
        if (yMov > 0 || this.isHovering()) {
            if (flapAmount < 5F) {
                flapAmount += 1F;
            }
        } else if (yMov <= 0.05F) {
            if (flapAmount > 0) {
                flapAmount -= 0.5F;
            }
        }
        if (isFlying()) {
            timeFlying++;
            if (this.isLandNavigator) {
                switchNavigator(false);
            }
            if(this.getDeltaMovement().y < 0){
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.6D, 1));
            }
        } else {
            timeFlying = 0;
            if (!this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if(!level.isClientSide){
            this.setHovering(isHoveringFromServer() && isFlying());
            if(this.isHovering() && isFlying()){
                if(timeFlying < 30){
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.06D, 0));
                }
                if(landingFlag){
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.3D, 0));
                }

            }
        }
        tickRotation(yMov * -(float) (180F / (float) Math.PI));
    }

    private boolean isHoveringFromServer(){
        return landingFlag || timeFlying < 30;
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
        tailYaw = Mth.approachDegrees(this.tailYaw, yBodyRot, 5);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying && this.isBaby()) {
            flying = false;
        }
        this.entityData.set(FLYING, flying);
    }

    public boolean isHovering() {
        return this.entityData.get(HOVERING);
    }

    public void setHovering(boolean flying) {
        if (flying && this.isBaby()) {
            flying = false;
        }
        this.entityData.set(HOVERING, flying);
    }
    public float getFlapAmount(float partialTick) {
        return (prevFlapAmount + (flapAmount - prevFlapAmount) * partialTick) * 0.2F;
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

    public float getHoverProgress(float partialTick) {
        return (prevHoverProgress + (hoverProgress - prevHoverProgress) * partialTick) * 0.2F;
    }

    public float getFlightPitch(float partialTick) {
        return (prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick);
    }

    public float getFlightRoll(float partialTick) {
        return (prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick);
    }

    public float getTailYaw(float partialTick) {
        return (prevTailYaw + (tailYaw - prevTailYaw) * partialTick);
    }

    public boolean isNoGravity() {
        return false;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void leavePack() {
        if (this.priorPackMember != null) {
            this.priorPackMember.afterPackMember = null;
        }
        this.priorPackMember = null;
        resetFlightAIFlag = true;
    }

    public void joinPackOf(SubterranodonEntity caravanHeadIn) {
        this.priorPackMember = caravanHeadIn;
        this.priorPackMember.afterPackMember = this;
        resetFlightAIFlag = true;
    }

    public boolean isPackFollower() {
        return this.priorPackMember != null;
    }

    public boolean hasPackFollower() {
        return this.afterPackMember != null;
    }

    public SubterranodonEntity getPriorPackMember() {
        return this.priorPackMember;
    }

    public SubterranodonEntity getPackLeader() {
        SubterranodonEntity leader = this;
        while (leader.priorPackMember != null && leader.priorPackMember != this) {
            leader = leader.priorPackMember;
        }
        return leader;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public boolean isValidLeader(SubterranodonEntity packLeader) {
        return !packLeader.isPackFollower() && packLeader.isAlive();
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    class FlightMoveHelper extends MoveControl {
        private final SubterranodonEntity parentEntity;

        public FlightMoveHelper(SubterranodonEntity bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                final Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                final double d5 = vector3d.length();
                if (d5 < parentEntity.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    float hoverSlow = parentEntity.isHoveringFromServer() && !parentEntity.landingFlag ? 0.2F : 1F;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.1D / d5).multiply(hoverSlow, 1, hoverSlow)));
                    final Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    float f = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * 180.0F / (float) Math.PI;
                    parentEntity.setYRot(Mth.approachDegrees(parentEntity.getYRot(), f, 20));
                    parentEntity.yBodyRot = parentEntity.getYRot();
                }
            }
        }
    }
}
