package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MultipleDinosaurEggsBlock;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.FlyingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SubterranodonEntity extends DinosaurEntity implements PackAnimal, FlyingAnimal, KeybindUsingMount, FlyingMount {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(SubterranodonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOVERING = SynchedEntityData.defineId(SubterranodonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> METER_AMOUNT = SynchedEntityData.defineId(SubterranodonEntity.class, EntityDataSerializers.FLOAT);
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
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
    public boolean slowRidden;
    private int controlUpTicks = 0;
    private int controlDownTicks = 0;
    private AABB flightCollisionBox;
    private int timeVehicle;

    public SubterranodonEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        switchNavigator(true);
        tailYaw = this.yBodyRot;
        prevTailYaw = this.yBodyRot;
    }

    public static boolean checkSubterranodonSpawnRules(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource) {
        BlockState below = levelAccessor.getBlockState(pos.below());
        return (below.is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) || below.is(ACBlockRegistry.PEWEN_BRANCH.get()) || below.is(BlockTags.LEAVES)) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnimalJoinPackGoal(this, 30, 5));
        this.goalSelector.addGoal(2, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(3, new AnimalLayEggGoal(this, 40, 1));
        this.goalSelector.addGoal(4, new SubterranodonFleeGoal(this));
        this.goalSelector.addGoal(5, new SubterranodonFlightGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
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
        this.entityData.define(METER_AMOUNT, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FLYING_SPEED, 1F).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveHelper(this);
            this.navigation = new FlyingPathNavigation(this, level());
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
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2);
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
            if (this.getDeltaMovement().y < 0 && this.isAlive()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.6D, 1));
            }
            if (this.isDancing()) {
                this.setHovering(false);
                this.setFlying(false);
            }
        } else {
            timeFlying = 0;
            if (!this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if (this.isVehicle() && !this.isBaby()) {
            this.setFlying(true);
            Entity rider = getControllingPassenger();
            if(rider != null){
                this.flightCollisionBox = this.getBoundingBox().expandTowards(0, -0.5F - rider.getBbHeight(), 0);
                if(isRiderInWall()){
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2, 0));
                }
            }
        }
        if (!level().isClientSide) {
            this.setHovering(isHoveringFromServer() && isFlying());
            if (this.isHovering() && isFlying() && this.isAlive() && !this.isVehicle()) {
                if (timeFlying < 30) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.075D, 0));
                }
                if (landingFlag) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.3D, 0));
                }
            }
        }else{
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(0) && controlUpTicks < 2 && getMeterAmount() > 0.1F) {
                    if(getMeterAmount() > 0.1F){
                        AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 0));
                        controlUpTicks = 5;
                    }
                }
                if (AlexsCaves.PROXY.isKeyDown(1) && controlDownTicks < 2) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 1));
                    controlDownTicks = 5;
                }
            }
        }
        if (controlDownTicks > 0) {
            controlDownTicks--;
        } else if (controlUpTicks > 0) {
            controlUpTicks--;
        }
        if(isVehicle()){
            timeVehicle++;
        }else{
            timeVehicle = 0;
        }
        if(this.getMeterAmount() < 1.0F && controlUpTicks == 0){
            this.setMeterAmount(this.getMeterAmount() + (slowRidden ? 0.002F : 0.001F));
        }
        tickRotation( Mth.clamp(yMov, -1.0F, 1.0F) * -(float) (180F / (float) Math.PI));
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }
    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.25F, controlUpTicks > 0 ? 1 : controlDownTicks > 0 ? -1 : 0.0D, player.zza * 0.5F * f);

    }
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        slowRidden = player.zza < 0.3F || timeVehicle < 10;
        if(player.zza != 0 || player.xxa != 0){
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setTarget(null);
        }
    }

    protected float getFlyingSpeed() {
        return this.getSpeed();
    }

    protected float getRiddenSpeed(Player rider) {
        return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }

    protected void clampRotation(LivingEntity livingEntity) {
        livingEntity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(livingEntity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        livingEntity.yRotO += f1 - f;
        livingEntity.yBodyRotO += f1 - f;
        livingEntity.setYRot(livingEntity.getYRot() + f1 - f);
        livingEntity.setYHeadRot(livingEntity.getYRot());
    }

    public boolean isRiderInWall() {
        Entity rider = getControllingPassenger();
        if (rider == null || rider.noPhysics) {
            return false;
        } else {
            float f = rider.getDimensions(Pose.STANDING).width * 0.8F;
            AABB aabb = AABB.ofSize(rider.position().add(0, 0.5, 0), (double)f, 1.0E-6D, (double)f);
            return BlockPos.betweenClosedStream(aabb).anyMatch((state) -> {
                BlockState blockstate = this.level().getBlockState(state);
                return !blockstate.isAir() && blockstate.isSuffocating(this.level(), state) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), state).move((double)state.getX(), (double)state.getY(), (double)state.getZ()), Shapes.create(aabb), BooleanOp.AND);
            });
        }
    }


    private boolean isHoveringFromServer() {
        if(this.isVehicle()){
            return slowRidden;
        }else{
            return landingFlag || timeFlying < 30;
        }
    }

    private void tickRotation(float yMov) {
        flightPitch = Mth.approachDegrees(flightPitch, yMov, 10);
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
        tailYaw = Mth.approachDegrees(this.tailYaw, yBodyRot, 8);
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

    public boolean hasRidingMeter(){
        return true;
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float flightPower) {
        this.entityData.set(METER_AMOUNT, flightPower);
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

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void resetPackFlags() {
        resetFlightAIFlag = true;
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
        this.priorPackMember = (SubterranodonEntity) animal;
    }

    @Override
    public void setAfterPackMember(PackAnimal animal) {
        this.afterPackMember = (SubterranodonEntity) animal;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    @Override
    public BlockState createEggBlockState() {
        return ACBlockRegistry.SUBTERRANODON_EGG.get().defaultBlockState().setValue(MultipleDinosaurEggsBlock.EGGS, 1 + random.nextInt(3));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult prev = super.mobInteract(player, hand);
        if (prev != InteractionResult.SUCCESS) {
            ItemStack itemStack = player.getItemInHand(hand);
            if(this.canAddPassenger(player)){
                this.moveTo(this.getX(), this.getY() + player.getBbHeight() + 0.5F, this.getZ());
            }
            if (!this.level().isClientSide) {
                if (player.startRiding(this)) {
                    return InteractionResult.CONSUME;
                }
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return prev;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 0) {
                if(controlUpTicks != 10){
                    this.setMeterAmount(Math.max(this.getMeterAmount() - 0.075F, 0F));
                }
                controlUpTicks = 10;
            }
            if (type == 1) {
                controlDownTicks = 10;
            }
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            float flight = this.getFlyProgress(1.0F) - this.getHoverProgress(1.0F);
            Vec3 seatOffset = new Vec3(0F, 0.0F, 0.2F - 1.5F * flight).xRot((float) Math.toRadians(this.getXRot())).yRot((float) Math.toRadians(-this.yBodyRot));
            double targetY = this.getY() - passenger.getBbHeight() - 0.5F + 0.25F * flight;
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            clampRotation(living);
            moveFunction.accept(passenger, this.getX() + seatOffset.x, targetY, this.getZ() + seatOffset.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity p_20123_) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }


    public Vec3 collide(Vec3 movement) {
        if (this.flightCollisionBox != null && !touchingUnloadedChunk() && this.isVehicle()) {
            AABB aabb = this.flightCollisionBox;
            List<VoxelShape> list = this.level().getEntityCollisions(this, aabb.expandTowards(movement));
            Vec3 vec3 = movement.lengthSqr() == 0.0D ? movement : collideBoundingBox(this, movement, aabb, this.level(), list);
            boolean flag = movement.x != vec3.x;
            boolean flag1 = movement.y != vec3.y;
            boolean flag2 = movement.z != vec3.z;
            boolean flag3 = this.onGround() || flag1 && movement.y < 0.0D;
            if (this.getStepHeight() > 0.0F && flag3 && (flag || flag2)) {
                Vec3 vec31 = collideBoundingBox(this, new Vec3(movement.x, this.getStepHeight(), movement.z), aabb, this.level(), list);
                Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, this.getStepHeight(), 0.0D), aabb.expandTowards(movement.x, 0.0D, movement.z), this.level(), list);
                if (vec32.y < (double) this.getStepHeight()) {
                    Vec3 vec33 = collideBoundingBox(this, new Vec3(movement.x, 0.0D, movement.z), aabb.move(vec32), this.level(), list).add(vec32);
                    if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                        vec31 = vec33;
                    }
                }

                if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                    return vec31.add(collideBoundingBox(this, new Vec3(0.0D, -vec31.y + movement.y, 0.0D), aabb.move(vec31), this.level(), list));
                }
            }

            return vec3;
        } else {
            return super.collide(movement);
        }
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
