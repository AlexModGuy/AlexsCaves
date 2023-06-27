package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MagneticLevitationRailBlock;
import com.github.alexmodguy.alexscaves.server.entity.util.MinecartAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IMinecartCollisionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements MinecartAccessor {

    @Shadow private boolean flipped;

    @Shadow public abstract IMinecartCollisionHandler getCollisionHandler();

    @Shadow public abstract int getHurtTime();

    @Shadow public abstract void setHurtTime(int p_38155_);

    @Shadow public abstract void setDamage(float p_38110_);

    @Shadow public abstract float getDamage();

    @Shadow private int lSteps;
    @Shadow private double lx;
    @Shadow private double ly;
    @Shadow private double lz;
    @Shadow private double lyr;
    @Shadow private double lxr;

    @Shadow @Nullable public abstract Vec3 getPos(double p_38180_, double p_38181_, double p_38182_);

    @Shadow
    private static Pair<Vec3i, Vec3i> exits(RailShape p_38126_) {
        return null;
    }

    @Shadow protected abstract void applyNaturalSlowdown();

    @Shadow protected abstract boolean isRedstoneConductor(BlockPos p_38130_);

    @Shadow public abstract boolean isOnRails();

    private BlockPos lastMagLevCheck = null;
    private BlockPos magLevBelow = null;
    private float magLevProgress = 0F;
    private float prevMagLevProgress = 0F;


    public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/vehicle/AbstractMinecart;tick()V"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void ac_tick(CallbackInfo ci) {
        prevMagLevProgress = magLevProgress;

        if (lastMagLevCheck == null || !lastMagLevCheck.equals(this.blockPosition())) {
            lastMagLevCheck = this.blockPosition();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(this.getX(), this.getY(), this.getZ());
            boolean flag = false;
            int i = 0;
            while (i < 3) {
                if (level().getBlockState(mutableBlockPos).is(ACBlockRegistry.MAGNETIC_LEVITATION_RAIL.get())) {
                    flag = true;
                    break;
                }
                mutableBlockPos.move(0, -1, 0);
                i++;
            }
            magLevBelow = flag ? mutableBlockPos.immutable() : null;
        }
        if (magLevBelow != null) {

            if(magLevProgress < 1.0F){
                magLevProgress += 0.2F;
            }
            BlockState magLevBlockState = level().getBlockState(magLevBelow);
            if(magLevBlockState.is(ACBlockRegistry.MAGNETIC_LEVITATION_RAIL.get())){
                ci.cancel();

                if (this.getHurtTime() > 0) {
                    this.setHurtTime(this.getHurtTime() - 1);
                }

                if (this.getDamage() > 0.0F) {
                    this.setDamage(this.getDamage() - 1.0F);
                }

                this.checkBelowWorld();
                this.handleNetherPortal();
                if (this.level().isClientSide) {
                    if (this.lSteps > 0) {
                        double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                        double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                        double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                        double d2 = Mth.wrapDegrees(this.lyr - (double) this.getYRot());
                        this.setYRot(this.getYRot() + (float) d2 / (float) this.lSteps);
                        this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                        --this.lSteps;
                        this.setPos(d5, d6, d7);
                        this.setRot(this.getYRot(), this.getXRot());
                    } else {
                        this.reapplyPosition();
                        this.setRot(this.getYRot(), this.getXRot());
                    }
                } else {
                    if (!this.isNoGravity()) {
                        double d0 = this.isInWater() ? -0.005D : -0.04D;
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, d0, 0.0D));
                    }

                    this.moveAlongMagLev(magLevBelow, magLevBlockState);

                    this.checkInsideBlocks();
                    this.setXRot(0.0F);
                    double d1 = this.xo - this.getX();
                    double d3 = this.zo - this.getZ();
                    if (d1 * d1 + d3 * d3 > 0.001D) {
                        this.setYRot((float) (Mth.atan2(d3, d1) * 180.0D / Math.PI));
                        if (this.flipped) {
                            this.setYRot(this.getYRot() + 180.0F);
                        }
                    }

                    double d4 = (double) Mth.wrapDegrees(this.getYRot() - this.yRotO);
                    if (d4 < -170.0D || d4 >= 170.0D) {
                        this.setYRot(this.getYRot() + 180.0F);
                        this.flipped = !this.flipped;
                    }

                    this.setRot(this.getYRot(), this.getXRot());
                    AABB box;
                    if (getCollisionHandler() != null)
                        box = getCollisionHandler().getMinecartCollisionBox((AbstractMinecart) (Entity) this);
                    else box = this.getBoundingBox().inflate(0.2F, 0.0D, 0.2F);
                    if (((AbstractMinecart) (Entity) this).canBeRidden() && this.getDeltaMovement().horizontalDistanceSqr() > 0.01D) {
                        List<Entity> list = this.level().getEntities(this, box, EntitySelector.pushableBy(this));
                        if (!list.isEmpty()) {
                            for (int l = 0; l < list.size(); ++l) {
                                Entity entity1 = list.get(l);
                                if (!(entity1 instanceof Player) && !(entity1 instanceof IronGolem) && !(entity1 instanceof AbstractMinecart) && !this.isVehicle() && !entity1.isPassenger()) {
                                    entity1.startRiding(this);
                                } else {
                                    entity1.push(this);
                                }
                            }
                        }
                    } else {
                        for (Entity entity : this.level().getEntities(this, box)) {
                            if (!this.hasPassenger(entity) && entity.isPushable() && entity instanceof AbstractMinecart) {
                                entity.push(this);
                            }
                        }
                    }

                    this.updateInWaterStateAndDoFluidPushing();
                    if (this.isInLava()) {
                        this.lavaHurt();
                        this.fallDistance *= 0.5F;
                    }

                    this.firstTick = false;
                }
            }

            if(level().isClientSide && random.nextFloat() < 0.4F){
                Vec3 randomLightningFrom = magLevBelow.getCenter().add(random.nextFloat() - 0.5F, -0.4F, random.nextFloat() - 0.5F);
                Vec3 vec3 = this.position().add(getDeltaMovement()).add(new Vec3(random.nextFloat() - 0.5F, 0.2F, random.nextFloat() - 0.5F));
                this.level().addParticle(ACParticleRegistry.AZURE_SHIELD_LIGHTNING.get(), randomLightningFrom.x, randomLightningFrom.y, randomLightningFrom.z, vec3.x, vec3.y, vec3.z);
            }
        }else{
            if(magLevProgress > 0.0F){
                magLevProgress -= 0.2F;
                if(!this.isOnRails()){
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.5F, 1F, 1.5F).add(0, 0.1F, 0));
                }
            }
        }
    }

    private void moveAlongMagLev(BlockPos railPos, BlockState railState) {
        boolean doRailFunctions = ((AbstractMinecart) (Entity) this).shouldDoRailFunctions();
        this.resetFallDistance();
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        Vec3 vec3 = this.getPos(d0, d1, d2);
        boolean flag = true;
        boolean flag1 = true;
        double d3 = ((AbstractMinecart) (Entity) this).getSlopeAdjustment();
        if (this.isInWater()) {
            d3 *= 0.2D;
        }

        Vec3 vec31 = this.getDeltaMovement();
        RailShape railshape = ((BaseRailBlock)railState.getBlock()).getRailDirection(railState, this.level(), railPos, ((AbstractMinecart) (Entity) this));
        switch (railshape) {
            case ASCENDING_EAST:
                this.setDeltaMovement(vec31.add(-d3, 0.0D, 0.0D));
                ++d1;
                break;
            case ASCENDING_WEST:
                this.setDeltaMovement(vec31.add(d3, 0.0D, 0.0D));
                ++d1;
                break;
            case ASCENDING_NORTH:
                this.setDeltaMovement(vec31.add(0.0D, 0.0D, d3));
                ++d1;
                break;
            case ASCENDING_SOUTH:
                this.setDeltaMovement(vec31.add(0.0D, 0.0D, -d3));
                ++d1;
        }

        vec31 = this.getDeltaMovement();
        Pair<Vec3i, Vec3i> pair = exits(railshape);
        Vec3i vec3i = pair.getFirst();
        Vec3i vec3i1 = pair.getSecond();
        double d4 = (double)(vec3i1.getX() - vec3i.getX());
        double d5 = (double)(vec3i1.getZ() - vec3i.getZ());
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);
        double d7 = vec31.x * d4 + vec31.z * d5;
        if (d7 < 0.0D) {
            d4 = -d4;
            d5 = -d5;
        }

        double d8 = Math.min(2.0D, vec31.horizontalDistance());
        vec31 = new Vec3(d8 * d4 / d6, vec31.y, d8 * d5 / d6);
        this.setDeltaMovement(vec31);
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            Vec3 vec32 = entity.getDeltaMovement();
            double d9 = vec32.horizontalDistanceSqr();
            double d11 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d9 > 1.0E-4D && d11 < 0.01D) {
                this.setDeltaMovement(this.getDeltaMovement().add(vec32.x * 0.1D, 0.0D, vec32.z * 0.1D));
                flag1 = false;
            }
        }

        if (flag1 && doRailFunctions) {
            double d22 = this.getDeltaMovement().horizontalDistance();
            if (d22 < 0.03D) {
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
            }
        }

        double d23 = (double)railPos.getX() + 0.5D + (double)vec3i.getX() * 0.5D;
        double d10 = (double)railPos.getZ() + 0.5D + (double)vec3i.getZ() * 0.5D;
        double d12 = (double)railPos.getX() + 0.5D + (double)vec3i1.getX() * 0.5D;
        double d13 = (double)railPos.getZ() + 0.5D + (double)vec3i1.getZ() * 0.5D;
        d4 = d12 - d23;
        d5 = d13 - d10;
        double d14;
        if (d4 == 0.0D) {
            d14 = d2 - (double)railPos.getZ();
        } else if (d5 == 0.0D) {
            d14 = d0 - (double)railPos.getX();
        } else {
            double d15 = d0 - d23;
            double d16 = d2 - d10;
            d14 = (d15 * d4 + d16 * d5) * 2.0D;
        }

        d0 = d23 + d4 * d14;
        d2 = d10 + d5 * d14;
        this.setPos(d0, d1, d2);

        //hover
        Vec3 idealPos = new Vec3(this.getX(), magLevBelow.getY() + 1.5F + Math.sin(tickCount * 0.2) * 0.5F, this.getZ());
        this.setDeltaMovement(this.getDeltaMovement().add(idealPos.subtract(this.position()).scale(0.2F)));

        this.moveMinecartOnMagLev(railPos);
        if (vec3i.getY() != 0 && Mth.floor(this.getX()) - railPos.getX() == vec3i.getX() && Mth.floor(this.getZ()) - railPos.getZ() == vec3i.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
        } else if (vec3i1.getY() != 0 && Mth.floor(this.getX()) - railPos.getX() == vec3i1.getX() && Mth.floor(this.getZ()) - railPos.getZ() == vec3i1.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)vec3i1.getY(), this.getZ());
        }

        Vec3 vec33 = this.getPos(this.getX(), this.getY(), this.getZ());
        if (vec33 != null && vec3 != null) {
            double d17 = (vec3.y - vec33.y) * 0.05D;
            Vec3 vec34 = this.getDeltaMovement();
            double d18 = vec34.horizontalDistance();
            if (d18 > 0.0D) {
                this.setDeltaMovement(vec34.multiply((d18 + d17) / d18, 0.3F, (d18 + d17) / d18));
            }
        }

        int j = Mth.floor(this.getX());
        int i = Mth.floor(this.getZ());
        if (j != railPos.getX() || i != railPos.getZ()) {
            Vec3 vec35 = this.getDeltaMovement();
            double d26 = vec35.horizontalDistance();
            this.setDeltaMovement(d26 * (double)(j - railPos.getX()), vec35.y, d26 * (double)(i - railPos.getZ()));
        }

        if (doRailFunctions)
            ((BaseRailBlock)railState.getBlock()).onMinecartPass(railState, level(), railPos, ((AbstractMinecart) (Entity)this));

        if (flag && doRailFunctions) {
            Vec3 vec36 = this.getDeltaMovement();
            double d27 = vec36.horizontalDistance();
            if (d27 > 0.01D) {
                double d19 = 1D;
                this.setDeltaMovement(vec36.add(vec36.x / d27 * d19, 0.0D, vec36.z / d27 * d19));
            } else {
                Vec3 vec37 = this.getDeltaMovement();
                double d20 = vec37.x;
                double d21 = vec37.z;
                if (railshape == RailShape.EAST_WEST) {
                    if (this.isRedstoneConductor(railPos.west())) {
                        d20 = 0.02D;
                    } else if (this.isRedstoneConductor(railPos.east())) {
                        d20 = -0.02D;
                    }
                } else {
                    if (railshape != RailShape.NORTH_SOUTH) {
                        return;
                    }

                    if (this.isRedstoneConductor(railPos.north())) {
                        d21 = 0.02D;
                    } else if (this.isRedstoneConductor(railPos.south())) {
                        d21 = -0.02D;
                    }
                }

                this.setDeltaMovement(d20, vec37.y, d21);
            }
        }

    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/vehicle/AbstractMinecart;getPos(DDD)Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            at = @At(value = "RETURN"),
            cancellable = true
    )
    public void ac_getPos(double x, double y, double z, CallbackInfoReturnable<Vec3> cir) {
        double magLevAmount = prevMagLevProgress + (magLevProgress - prevMagLevProgress) * AlexsCaves.PROXY.getPartialTicks();
        if(magLevAmount >= 0.0F){
            double yClientSide = yOld + (this.getY() - yOld) * AlexsCaves.PROXY.getPartialTicks();
            Vec3 prev = cir.getReturnValue();
            Vec3 modified = prev == null ? this.getPosition(AlexsCaves.PROXY.getPartialTicks()) : new Vec3(prev.x, prev.y + (yClientSide - prev.y) * magLevAmount, prev.z);
            cir.setReturnValue(modified);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/vehicle/AbstractMinecart;getPosOffs(DDDD)Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            at = @At(value = "RETURN"),
            cancellable = true
    )
    public void ac_getPosOffs(double x, double y, double z, double offset, CallbackInfoReturnable<Vec3> cir) {
        double magLevAmount = prevMagLevProgress + (magLevProgress - prevMagLevProgress) * AlexsCaves.PROXY.getPartialTicks();
        if(magLevAmount >= 0.0F){
            double yClientSide = yOld + (this.getY() - yOld) * AlexsCaves.PROXY.getPartialTicks();
            Vec3 prev = cir.getReturnValue();
            Vec3 modified = prev == null ? this.getPosition(AlexsCaves.PROXY.getPartialTicks()) : new Vec3(prev.x, prev.y + (yClientSide - prev.y) * magLevAmount, prev.z);
            cir.setReturnValue(modified);
        }
    }

    public void moveMinecartOnMagLev(BlockPos pos) { //Non-default because getMaximumSpeed is protected
        double d24 = this.isVehicle() ? 0.75D : 1.0D;

        double d25 = 0.05F;
        if(magLevBelow != null){
            BlockState magLevState = level().getBlockState(magLevBelow);
            if(magLevState.getBlock() instanceof MagneticLevitationRailBlock magRailBlock){
                d25 = magRailBlock.getRailMaxSpeed(magLevState, this.level(), pos, (AbstractMinecart)(Entity)this);
            }
        }
        Vec3 vec3d1 = this.getDeltaMovement();
        this.move(MoverType.SELF, new Vec3(Mth.clamp(d24 * vec3d1.x, -d25, d25), vec3d1.y, Mth.clamp(d24 * vec3d1.z, -d25, d25)));
    }

    @Override
    public boolean isOnMagLevRail(){
        return magLevProgress >= 0.5F;
    }
}
