package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MultipleDinosaurEggsBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.FlyingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(SubterranodonEntity.class, EntityDataSerializers.INT);
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
    public float prevAttackProgress;
    public float attackProgress;
    private double lastStepX = 0;
    private double lastStepZ = 0;

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
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, false));
        this.goalSelector.addGoal(3, new SubterranodonFollowOwnerGoal(this, 1.2D, 5.0F, 2.0F, true));
        this.goalSelector.addGoal(4, new AnimalJoinPackGoal(this, 30, 5));
        this.goalSelector.addGoal(5, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, Ingredient.of(Items.COD, Items.COOKED_COD, ACItemRegistry.COOKED_TRILOCARIS_TAIL.get(), ACItemRegistry.TRILOCARIS_TAIL.get()), false));
        this.goalSelector.addGoal(7, new AnimalLayEggGoal(this, 100, 1));
        this.goalSelector.addGoal(8, new SubterranodonFleeGoal(this));
        this.goalSelector.addGoal(9, new SubterranodonFlightGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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
        this.entityData.define(ATTACK_TICK, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FLYING_SPEED, 1F).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = createMultithreadedPathFinder(false);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveHelper(this);
            this.navigation = createMultithreadedPathFinder(true);
            this.isLandNavigator = false;
        }
    }

    private PathNavigation createMultithreadedPathFinder(boolean flying){
        return new AdvancedPathNavigateNoTeleport(this, level(), flying ? AdvancedPathNavigate.MovementType.FLYING : AdvancedPathNavigate.MovementType.WALKING);
    }

    @Override
    public void tick() {
        super.tick();
        prevFlyProgress = flyProgress;
        prevHoverProgress = hoverProgress;
        prevAttackProgress = attackProgress;
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
            if(timeFlying % 10 == 0 && (flapAmount > 0 || controlUpTicks > 0)){
                this.playSound(ACSoundRegistry.SUBTERRANODON_FLAP.get());
            }
            timeFlying++;
            if (this.isLandNavigator) {
                switchNavigator(false);
            }
            if (this.getDeltaMovement().y < 0 && this.isAlive()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.6D, 1));
            }
            if (this.isDancing() || this.isPassenger()) {
                this.setHovering(false);
                this.setFlying(false);
            }
            if (!level().isClientSide && this.onGround()) {
                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    this.setHovering(false);
                    this.setFlying(false);
                }
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
            if (rider != null) {
                this.flightCollisionBox = this.getBoundingBox().expandTowards(0, -0.5F - rider.getBbHeight(), 0);
                if (isRiderInWall()) {
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
            if (!this.isHovering() && this.isFlying() && timeFlying > 40 && this.onGround()) {
                this.setFlying(false);
            }
        } else {
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
                if (AlexsCaves.PROXY.isKeyDown(0) && !AlexsCaves.PROXY.isKeyDown(1) && controlUpTicks < 2 && getMeterAmount() > 0.1F) {
                    if (getMeterAmount() > 0.1F) {
                        AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 0));
                        controlUpTicks = 5;
                    }
                }
                if (AlexsCaves.PROXY.isKeyDown(1) && !AlexsCaves.PROXY.isKeyDown(0) && controlDownTicks < 2) {
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
        if (isVehicle()) {
            timeVehicle++;
        } else {
            timeVehicle = 0;
        }
        if (this.getMeterAmount() < 1.0F && controlUpTicks == 0) {
            this.setMeterAmount(this.getMeterAmount() + (slowRidden ? 0.002F : 0.001F));
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            LivingEntity target = this.getTarget();
            if (attackProgress == 5F && target != null && this.distanceTo(target) < 3D + target.getBbWidth() && this.hasLineOfSight(target)) {
                target.hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                this.playSound(ACSoundRegistry.SUBTERRANODON_ATTACK.get());
            }
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        tickRotation(Mth.clamp(yMov, -1.0F, 1.0F) * -(float) (180F / (float) Math.PI));
        lastStepX = this.xo;
        lastStepZ = this.zo;
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
        slowRidden = player.zza < 0.3F || timeVehicle < 10 || this.onGround();
        if (player.zza != 0 || player.xxa != 0) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setTarget(null);
        }
    }

    protected float getFlyingSpeed() {
        return this.getSpeed();
    }

    protected float getRiddenSpeed(Player rider) {
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }


    public boolean isRiderInWall() {
        Entity rider = getControllingPassenger();
        if (rider == null || rider.noPhysics) {
            return false;
        } else {
            float f = rider.getDimensions(Pose.STANDING).width * 0.8F;
            AABB aabb = AABB.ofSize(rider.position().add(0, 0.5, 0), (double) f, 1.0E-6D, (double) f);
            return BlockPos.betweenClosedStream(aabb).anyMatch((state) -> {
                BlockState blockstate = this.level().getBlockState(state);
                return !blockstate.isAir() && blockstate.isSuffocating(this.level(), state) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), state).move((double) state.getX(), (double) state.getY(), (double) state.getZ()), Shapes.create(aabb), BooleanOp.AND);
            });
        }
    }

    public boolean doHurtTarget(Entity entityIn) {
        this.entityData.set(ATTACK_TICK, 7);
        return true;
    }

    private boolean isHoveringFromServer() {
        if (this.isVehicle()) {
            return slowRidden;
        } else {
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

    public boolean hasRidingMeter() {
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

    public float getBiteProgress(float partialTick) {
        return (prevAttackProgress + (attackProgress - prevAttackProgress) * partialTick) * 0.2F;
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
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mob) {
        return ACEntityRegistry.SUBTERRANODON.get().create(serverLevel);
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
            if (!this.isTame() && (itemStack.is(ACItemRegistry.TRILOCARIS_TAIL.get()) || itemStack.is(ACItemRegistry.COOKED_TRILOCARIS_TAIL.get()))) {
                this.usePlayerItem(player, hand, itemStack);
                if (getRandom().nextInt(3) == 0) {
                    this.tame(player);
                    this.clearRestriction();
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return prev;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 0) {
                if (controlUpTicks != 10) {
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
            clampRotation(living, 105);
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

    public boolean tamesFromHatching() {
        return true;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.lastStepX, 0, this.getZ() - this.lastStepZ);
        float f2 = Math.min(f1 * 4.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
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

    public boolean canOwnerMount(Player player) {
        return !this.isBaby();
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return ownerPlayer.isShiftKeyDown();
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COD) || stack.is(Items.COOKED_COD);
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.SUBTERRANODON_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.SUBTERRANODON_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SUBTERRANODON_DEATH.get();
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
