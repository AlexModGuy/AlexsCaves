package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class SubmarineEntity extends Entity implements KeybindUsingMount {
    private static final EntityDataAccessor<Float> RIGHT_PROPELLER_ROT = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LEFT_PROPELLER_ROT = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BACK_PROPELLER_ROT = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ACCELERATION = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> LIGHTS = SynchedEntityData.defineId(SubmarineEntity.class, EntityDataSerializers.BOOLEAN);

    private float prevLeftPropellerRot;
    private float prevRightPropellerRot;
    private float prevBackPropellerRot;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private int controlUpTicks = 0;
    private int controlDownTicks = 0;
    private int turnRightTicks = 0;
    private int turnLeftTicks = 0;
    private int floodlightToggleCooldown = 0;

    public int submergedTicks = 0;
    public SubmarineEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public SubmarineEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ACEntityRegistry.SUBMARINE.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RIGHT_PROPELLER_ROT, 0.0F);
        this.entityData.define(LEFT_PROPELLER_ROT, 0.0F);
        this.entityData.define(BACK_PROPELLER_ROT, 0.0F);
        this.entityData.define(ACCELERATION, 0.0F);
        this.entityData.define(LIGHTS, false);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public void tick() {
        super.tick();
        float leftPropellerRot = getLeftPropellerRot();
        float rightPropellerRot = getRightPropellerRot();
        float backPropellerRot = getBackPropellerRot();
        if(controlDownTicks > 0){
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            controlDownTicks--;
        }else if(controlUpTicks > 0 && getWaterHeight() > 1.5F){
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08, 0));
            controlUpTicks--;
        }
        float xRotSet = Mth.clamp(-(float) this.getDeltaMovement().y * 2F, -1.0F, 1.0F) * -(float) (180F / (float) Math.PI);
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
                this.setRot(this.getYRot(), this.getXRot());
            } else {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
            }
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();

            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(0) && controlUpTicks < 2) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 0));
                    controlUpTicks = 10;
                }
                if (AlexsCaves.PROXY.isKeyDown(1) && controlDownTicks < 2) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 1));
                    controlDownTicks = 10;
                }
                if (AlexsCaves.PROXY.isKeyDown(2) && floodlightToggleCooldown <= 0) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                    floodlightToggleCooldown = 5;
                }
            }
        } else {
            float acceleration = this.getAcceleration();
            if (acceleration < 0.0F) {
                this.setAcceleration(Math.min(0F, acceleration + 0.01F));
            }
            if (acceleration > 0.0F) {
                this.setAcceleration(Math.max(0F, acceleration - 0.01F));
            }
            float rot = 0;
            float signum = Math.signum(acceleration);
            if (Math.abs(acceleration) > 0) {
                rot = acceleration * 30 + signum * 15;
                Vec3 vec3 = this.getViewVector(1.0F).normalize().scale(Mth.clamp(acceleration, -0.25F, 0.8F) * 0.2F);
                this.setDeltaMovement(this.getDeltaMovement().add(vec3));
            }
            this.setBackPropellerRot(backPropellerRot + rot);
            this.setLeftPropellerRot(leftPropellerRot + rot + (turnLeftTicks > 0 ? 5 * turnLeftTicks : 0));
            this.setRightPropellerRot(rightPropellerRot + rot + (turnRightTicks > 0 ? 5 * turnRightTicks : 0));
            if(this.isInWaterOrBubble()){
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8F, 0.8F, 0.8F));
            }else{
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.5F, 0));
                this.move(MoverType.SELF, this.getDeltaMovement().scale(0.9F));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.1F, 0.3F, 0.1F));
            }
        }
        if(this.getWaterHeight() >= 1.5F){
            if(Math.abs(getAcceleration()) > 0.05F){
                Vec3 bubblesAt = new Vec3(0F, 0.3F, -2F).xRot((float) Math.toRadians(this.getXRot())).yRot((float) Math.toRadians(-this.getYRot()));
                for(int i = 0; i < 1 + random.nextInt(4); i++){
                    float offsetX = 0.5F - random.nextFloat();
                    float offsetY = 0.5F - random.nextFloat();
                    float offsetZ = 0.5F - random.nextFloat();
                    level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + offsetX + bubblesAt.x, this.getY(0.5F) + offsetY + bubblesAt.y, this.getZ() + offsetZ + bubblesAt.z, 0, 0, 0);
                }
            }
            if(submergedTicks < 10){
                submergedTicks++;
            }
        }else if(submergedTicks > 0){
            submergedTicks = 0;
        }
        if(floodlightToggleCooldown > 0){
            floodlightToggleCooldown--;
        }
        if(turnLeftTicks > 0){
            turnLeftTicks--;
        }
        if(turnRightTicks > 0){
            turnRightTicks--;
        }
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        prevLeftPropellerRot = leftPropellerRot;
        prevRightPropellerRot = rightPropellerRot;
        prevBackPropellerRot = backPropellerRot;
        this.setXRot(ACMath.approachRotation(this.getXRot(), Mth.clamp(xRotSet, -50, 50), 2));

    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lSteps > 0) {
            this.lSteps = 0;
            this.absMoveTo(this.lx, this.ly, this.lz, (float) this.lyr, (float) this.lxr);
        }
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

    public boolean areLightsOn() {
        return this.entityData.get(LIGHTS);
    }

    public void setLightsOn(boolean bool) {
        this.entityData.set(LIGHTS, bool);
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return type.supportsBoating(null);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        boolean prev = super.shouldRender(x, y, z);
        return prev || this.isVehicle() && this.getFirstPassenger() != null && this.getFirstPassenger().shouldRender(x, y, z);
    }

    public void positionRider(Entity passenger) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            clampRotation(living);
            if (passenger instanceof Player) {
                tickController((Player) passenger);
            }
            float f1 = -(this.getXRot() / 40F);
            Vec3 seatOffset = new Vec3(0F, -0.2F, 0.8F + f1).xRot((float) Math.toRadians(this.getXRot())).yRot((float) Math.toRadians(-this.getYRot()));
            double d0 = this.getY() + this.getBbHeight() * 0.5F + seatOffset.y + passenger.getMyRidingOffset();
            living.setPos(this.getX() + seatOffset.x, d0, this.getZ() + seatOffset.z);
            living.setAirSupply(Math.min(living.getAirSupply() + 2, living.getMaxAirSupply()));
        } else {
            super.positionRider(passenger);
        }
    }

    private void tickController(Player passenger) {
        if (passenger.xxa != 0) {
            float turn = -Math.signum(passenger.xxa);
            if(turn > 0.0F){
                turnLeftTicks = 5;
            }else{
                turnRightTicks = 5;
            }
            this.setYRot(this.getYRot() + turn * 2.5f);
        }
        if (passenger.zza != 0) {
            float back = -Math.signum(passenger.zza);
            if (back < 0.0F) {
                this.setAcceleration(Mth.approach(this.getAcceleration(), 1.0F, 0.02F));
            } else {
                this.setAcceleration(Mth.approach(this.getAcceleration(), -0.5F, 0.02F));
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else {
            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
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

    public float getLeftPropellerRot() {
        return this.entityData.get(LEFT_PROPELLER_ROT);
    }

    public void setLeftPropellerRot(float f) {
        this.entityData.set(LEFT_PROPELLER_ROT, f);
    }

    public float getLeftPropellerRot(float partialTick) {
        return prevLeftPropellerRot + (this.getLeftPropellerRot() - prevLeftPropellerRot) * partialTick;
    }

    public float getRightPropellerRot() {
        return this.entityData.get(RIGHT_PROPELLER_ROT);
    }

    public void setRightPropellerRot(float f) {
        this.entityData.set(RIGHT_PROPELLER_ROT, f);
    }

    public float getRightPropellerRot(float partialTick) {
        return prevRightPropellerRot + (this.getRightPropellerRot() - prevRightPropellerRot) * partialTick;
    }

    public float getBackPropellerRot() {
        return this.entityData.get(BACK_PROPELLER_ROT);
    }

    public void setBackPropellerRot(float f) {
        this.entityData.set(BACK_PROPELLER_ROT, f);
    }

    public float getBackPropellerRot(float partialTick) {
        return prevBackPropellerRot + (this.getBackPropellerRot() - prevBackPropellerRot) * partialTick;
    }

    public float getAcceleration() {
        return this.entityData.get(ACCELERATION);
    }

    public void setAcceleration(float f) {
        this.entityData.set(ACCELERATION, f);
    }

    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    public boolean isPushable() {
        return !this.isRemoved();
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoved();
    }

    public boolean isAttackable() {
        return !this.isRemoved();
    }

    public float getWaterHeight() {
        return (float) getFluidTypeHeight(ForgeMod.WATER_TYPE.get());
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if(keyPresser.isPassengerOfSameVehicle(this)){
            if(type == 0){
                controlUpTicks = 10;
            }
            if(type == 1){
                controlDownTicks = 10;
            }
            if(type == 2){
                this.setLightsOn(!this.areLightsOn());
                floodlightToggleCooldown = 5;
            }
        }
    }

}
