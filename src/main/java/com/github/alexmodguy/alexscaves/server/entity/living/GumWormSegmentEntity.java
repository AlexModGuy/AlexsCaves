package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.FlyingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CandyCaneHookItem;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACLoadedMods;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GumWormSegmentEntity extends Entity implements ICustomCollisions, KeybindUsingMount, PlayerRideableJumping, FlyingMount {

    private static final EntityDataAccessor<Optional<UUID>> HEAD_ENTITY_UUID = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HEAD_ENTITY_ID = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> FRONT_ENTITY_UUID = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> FRONT_ENTITY_ID = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> BACK_ENTITY_UUID = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BACK_ENTITY_ID = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(GumWormSegmentEntity.class, EntityDataSerializers.INT);
    public boolean renderHurtFlag = false;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private float prevZRot;
    private float zRot;

    private boolean lastZRotDirection;

    private int zRotTickOffset = random.nextInt(10);
    private Vec3 surfacePosition;
    private double surfaceY;
    private Vec3 prevSurfacePosition;

    public GumWormSegmentEntity(EntityType entityType, Level level) {
        super(entityType, level);
        if(ACLoadedMods.isEntityCullingLoaded()){
            this.noCulling = true;
        }
    }

    public GumWormSegmentEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.GUM_WORM_SEGMENT.get(), level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEAD_ENTITY_UUID, Optional.empty());
        this.entityData.define(HEAD_ENTITY_ID, -1);
        this.entityData.define(FRONT_ENTITY_UUID, Optional.empty());
        this.entityData.define(FRONT_ENTITY_ID, -1);
        this.entityData.define(BACK_ENTITY_UUID, Optional.empty());
        this.entityData.define(BACK_ENTITY_ID, -1);
        this.entityData.define(INDEX, 0);
    }

    @Nullable
    public UUID getBackEntityUUID() {
        return this.entityData.get(BACK_ENTITY_UUID).orElse(null);
    }

    public void setBackEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(BACK_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getHeadUUID() {
        return this.entityData.get(HEAD_ENTITY_UUID).orElse(null);
    }

    public void setHeadUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HEAD_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getFrontEntityUUID() {
        return this.entityData.get(FRONT_ENTITY_UUID).orElse(null);
    }

    public void setFrontEntityUUID(@Nullable UUID uniqueId) {
        this.entityData.set(FRONT_ENTITY_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getHeadEntity() {
        if (!level().isClientSide) {
            UUID id = getHeadUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(HEAD_ENTITY_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public Entity getFrontEntity() {
        if (!level().isClientSide) {
            UUID id = getFrontEntityUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(FRONT_ENTITY_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public Entity getBackEntity() {
        if (!level().isClientSide) {
            UUID id = getBackEntityUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(BACK_ENTITY_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public int getIndex() {
        return this.entityData.get(INDEX);
    }

    public void setIndex(int i) {
        this.entityData.set(INDEX, i);
    }


    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("HeadUUID")) {
            this.setHeadUUID(compound.getUUID("HeadUUID"));
        }
        if (compound.hasUUID("FrontUUID")) {
            this.setFrontEntityUUID(compound.getUUID("FrontUUID"));
        }
        if (compound.hasUUID("BackUUID")) {
            this.setBackEntityUUID(compound.getUUID("BackUUID"));
        }
        this.setIndex(compound.getInt("Index"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        if (this.getHeadUUID() != null) {
            compound.putUUID("HeadUUID", this.getHeadUUID());
        }
        if (this.getFrontEntityUUID() != null) {
            compound.putUUID("FrontUUID", this.getFrontEntityUUID());
        }
        if (this.getBackEntityUUID() != null) {
            compound.putUUID("BackUUID", this.getBackEntityUUID());
        }
        compound.putInt("Index", this.getIndex());
    }

    public static void createWormSegmentsFor(GumWormEntity gumWorm, int count) {
        GumWormSegmentEntity prev = null;
        GumWormSegmentEntity ridingSegment = null;
        for (int i = 0; i < count; i++) {
            GumWormSegmentEntity current = new GumWormSegmentEntity(ACEntityRegistry.GUM_WORM_SEGMENT.get(), gumWorm.level());
            current.setHeadUUID(gumWorm.getUUID());
            current.setFrontEntityUUID(prev == null ? gumWorm.getUUID() : prev.getUUID());
            if (prev != null) {
                prev.setBackEntityUUID(current.getUUID());
            }
            current.setIndex(i);
            current.setPos(current.getIdealPosition(prev == null ? gumWorm : prev));
            gumWorm.level().addFreshEntity(current);
            prev = current;
            if(i == 3){
                ridingSegment = prev;
            }
        }
        if(ridingSegment == null){
            ridingSegment = prev;
        }
        gumWorm.setRidingSegmentUUID(ridingSegment.getUUID());
        gumWorm.setRidingSegmentId(ridingSegment.getId());
    }

    public Vec3 getIdealPosition(@Nullable Entity parent) {
        Entity head = getHeadEntity();
        Entity front = parent == null ? getFrontEntity() : parent;
        if (front != null) {
            float backStretch = -2.5F;
            float sideSwing = 0;
            if (head != null) {
                float headDelta = Mth.clamp((float) head.getDeltaMovement().length(), 0F, 1F);
                if (front == head) {
                    boolean flag = head instanceof GumWormEntity gumWorm && gumWorm.isLeaping();
                    backStretch -= flag ? 0.7F : 1.2F;
                }
                backStretch *= 1F - headDelta * 0.3F;
                sideSwing = (0.5F + getIndex() * 0.05F) * (float) Math.sin(head.tickCount * 0.2F - getIndex());
            }
            Vec3 offsetFromParent = new Vec3(sideSwing, 0F, backStretch).xRot(-(float) Math.toRadians(front.xRotO)).yRot(-(float) Math.toRadians(front.yRotO));
            return front.position().add(offsetFromParent);
        } else {
            return this.position();
        }
    }

    @Override
    public boolean isPickable() {
        Entity head = this.getHeadEntity();
        return head != null && head.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity head = this.getHeadEntity();
        if (!this.isInvulnerableTo(source) && head != null) {
            head.hurt(source, amount);
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource.getEntity() != null && this.getHeadEntity() instanceof GumWormEntity gumWorm && gumWorm.isRidingPlayer(damageSource.getEntity())){
            return true;
        }
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL) || damageSource.is(DamageTypes.FALL);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevZRot = zRot;
        prevSurfacePosition = surfacePosition;
        surfacePosition = calculateLightAbovePosition();
        surfaceY = calculateSurfaceY();
        Entity head = getHeadEntity();
        Entity front = getFrontEntity();
        Entity back = getBackEntity();
        if (level().isClientSide) {
            if (head instanceof GumWormEntity gumWorm) {
                this.renderHurtFlag = gumWorm.hurtTime > 0 || gumWorm.deathTime > 0;
                lastZRotDirection = gumWorm.getZRotDirection();
            }
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                double lerpRot = Mth.wrapDegrees(this.lyr - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) lerpRot / (float) this.lSteps);
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
            spawnDustParticles(false);
            Player clientPlayer = AlexsCaves.PROXY.getClientSidePlayer();
            if (clientPlayer != null && clientPlayer.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(4)){
                    clientPlayer.stopRiding();
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), clientPlayer.getId(), 0));
                    postDismount(clientPlayer);
                }
                if (AlexsCaves.PROXY.isKeyDown(3)) {
                    if(this.getHeadEntity() instanceof GumWormEntity gumWorm){
                        gumWorm.onRidingPlayerAttack();
                    }
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), clientPlayer.getId(), 1));
                }
            }
        } else {
            boolean riddenFlag = false;
            this.entityData.set(HEAD_ENTITY_ID, head != null ? head.getId() : -1);
            this.entityData.set(FRONT_ENTITY_ID, front != null ? front.getId() : -1);
            this.entityData.set(BACK_ENTITY_ID, back != null ? back.getId() : -1);

            if (front == null || head == null) {
                if(tickCount > 3){
                    this.discard();
                }
            } else {
                float maxDistFromFront = 2;
                Vec3 ideal = getIdealPosition(front);
                Vec3 distVec = ideal.subtract(this.position());
                float extraLength = (float) Math.max(distVec.length() - maxDistFromFront, 0F);
                Vec3 vec31 = distVec.length() > 1F ? distVec.normalize().scale(1F + extraLength) : distVec;
                Vec3 vec32 = this.position().add(vec31);
                riddenFlag = head instanceof GumWormEntity gumWorm && gumWorm.isRidingMode();
                if ((!front.isInWall() || riddenFlag) && !(head instanceof GumWormEntity gumWorm && gumWorm.isLeaping())) {
                    float f = Mth.approach((float) this.getY(), riddenFlag ? (float) Math.max(surfaceY, ideal.y) :  (float) Math.min(surfaceY, vec31.y), 1F);
                    vec32 = new Vec3(vec32.x, f, vec32.z);
                }
                this.setPos(vec32);
                Vec3 frontsBack = front.position().add(new Vec3(0F, 0F, 3F).xRot(-(float) Math.toRadians(front.getXRot())).yRot(-(float) Math.toRadians(front.getYRot())));
                double d0 = frontsBack.x - this.getX();
                double d1 = frontsBack.y - this.getY(0.0F);
                double d2 = frontsBack.z - this.getZ();
                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                float f1 = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI))));
                float f2 = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90);
                this.setXRot(Mth.approachDegrees(this.getXRot(), f1, 5));
                this.setYRot(Mth.approachDegrees(this.getYRot(), f2, 7));
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));
                if(this.hasControllingPassenger() && head instanceof GumWormEntity gumWorm){
                    if(gumWorm.getHook(true) == null && gumWorm.getHook(false) == null){
                        this.ejectPassengers();
                    }
                }
            }
            this.setNoGravity(!riddenFlag);
        }
        this.pushEntities();
        if (zRotTickOffset < 0) {
            int i = (lastZRotDirection ? -1 : 1) * (getIndex() * 5 + 15);
            if (front instanceof GumWormEntity prior) {
                this.zRot = Mth.approachDegrees(this.zRot, prior.getBodyZRot(1.0F) + i, 8);
            } else if (front instanceof GumWormSegmentEntity prior) {
                this.zRot = Mth.approachDegrees(this.zRot, prior.zRot + i, 8);
            }
        } else {
            zRotTickOffset--;
        }
    }

    private void pushEntities() {
        if (this.level().isClientSide()) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::push);
        } else {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
                if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;

                    for(int k = 0; k < list.size(); ++k) {
                        if (!list.get(k).isPassenger()) {
                            ++j;
                        }
                    }

                    if (j > i - 1) {
                        this.hurt(this.damageSources().cramming(), 6.0F);
                    }
                }

                for(int l = 0; l < list.size(); ++l) {
                    Entity entity = list.get(l);
                    this.push(entity);
                }
            }

        }
    }

    private void postDismount(Entity rider){
        if(rider instanceof Player player){
            if(player.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.CANDY_CANE_HOOK.get())){
                CandyCaneHookItem.setReelingIn(player.getItemInHand(InteractionHand.MAIN_HAND), true);
            }
            if(player.getItemInHand(InteractionHand.OFF_HAND).is(ACItemRegistry.CANDY_CANE_HOOK.get())){
                CandyCaneHookItem.setReelingIn(player.getItemInHand(InteractionHand.OFF_HAND), true);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity) && !(entity instanceof GumWormSegmentEntity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d0 = entity.getX() - this.getX();
                double d1 = entity.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= (double) 0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05F;
                    d1 *= 0.05F;
                    if (!entity.isVehicle() && (entity.isPushable() || entity instanceof KaijuMob)) {
                        entity.push(d0, 0.0D, d1);
                    }
                }
            }
        }
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockState, VoxelShape voxelShape) {
        boolean ridingMode = this.getHeadEntity() instanceof GumWormEntity gumWorm && gumWorm.isRidingMode();
        return GumWormEntity.canDigBlock(blockState) && (!ridingMode || !level().getBlockState(blockPos.above()).isSolid() || !blockState.isSuffocating(level(), blockPos));
    }

    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return GumWormEntity.canDigBlock(blockstate) && super.isColliding(pos, blockstate);
    }

    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        if (surfacePosition != null && prevSurfacePosition != null) {
            Vec3 difference = surfacePosition.subtract(prevSurfacePosition);
            return prevSurfacePosition.add(difference.scale(f)).add(0, this.getEyeHeight(), 0);
        }
        return super.getLightProbePosition(f);
    }

    private Vec3 calculateLightAbovePosition() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
        while (mutableBlockPos.getY() < level().getMaxBuildHeight() && level().getBlockState(mutableBlockPos).isSuffocating(level(), mutableBlockPos)) {
            mutableBlockPos.move(0, 1, 0);
        }
        return new Vec3(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    private double calculateSurfaceY() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(Math.round(surfacePosition.x), Math.max(surfacePosition.y, this.getY(1.0F)) + 2, Math.round(surfacePosition.z));
        while (mutableBlockPos.getY() > level().getMinBuildHeight() && !level().getBlockState(mutableBlockPos).isSuffocating(level(), mutableBlockPos)) {
            mutableBlockPos.move(0, -1, 0);
        }
        return 1D + mutableBlockPos.getY();
    }

    public void spawnDustParticles(boolean surface) {
        if(surfaceY < this.getY(1.0F) && this.getY(1.0F) - surfaceY < 4){
            BlockPos lightPos = BlockPos.containing(this.getX(), surfaceY - 1.0F, this.getZ());
            BlockState state = level().getBlockState(lightPos);
            if(!state.isAir()){
                level().addParticle(new BlockParticleOption(ACParticleRegistry.BIG_BLOCK_DUST.get(), state), true, this.getRandomX(0.8F), surfaceY + random.nextFloat(), this.getRandomZ(0.8F), (random.nextFloat() - 0.5F) * 0.2F, (random.nextFloat() - 0.5F) * 0.2F, (random.nextFloat() - 0.5F) * 0.2F);
            }
        }
    }

    public boolean isMoving() {
        float f = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        return f > 0.1F;
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

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(8.0F);
    }

    public float getBodyZRot(float partialTicks) {
        return prevZRot + (zRot - prevZRot) * partialTicks;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }
    public Vec3 getRiderPosition(Entity playerOwner) {
        float f = (float) (this.getBbHeight() + 0.25F + playerOwner.getMyRidingOffset());
        Vec3 offset = new Vec3(0.0F, f, 0.15F).xRot((float) -Math.toRadians(this.getXRot())).yRot((float) -Math.toRadians(this.getYRot()));
        Vec3 position = this.position().add(offset);
        double setY = surfaceY;
        int worldHeight = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) position.x, (int) position.z);
        if(position.y > setY || surfaceY > worldHeight - 3.0F){
            setY = position.y;
        }
        return new Vec3(position.x, setY, position.z);
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
    
    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            clampRotation(living);
            if (passenger instanceof Player && this.getHeadEntity() instanceof GumWormEntity gumWorm) {
                gumWorm.tickController((Player) passenger);
            }
            Vec3 riderPosition = getRiderPosition(passenger);
            moveFunction.accept(passenger, riderPosition.x, riderPosition.y, riderPosition.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if(type == 0){
            keyPresser.stopRiding();
            postDismount(keyPresser);
        }
        if(type == 1){
            if(this.getHeadEntity() instanceof GumWormEntity gumWorm){
                gumWorm.onRidingPlayerAttack();
            }
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean shouldBeSaved() {
        return (this.getRemovalReason() == null || this.getRemovalReason().shouldSave()) && !this.isPassenger();
    }

    public ItemStack getPickResult() {
        return new ItemStack(ACItemRegistry.getSpawnEggFor(ACEntityRegistry.GUM_WORM.get()));
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    public void onPlayerJump(int i) {
    }

    @Override
    public boolean canJump() {
        return this.getHeadEntity() instanceof GumWormEntity gumWorm && !gumWorm.isLeaping() && !gumWorm.recentlyLeapt();
    }

    @Override
    public void handleStartJump(int i) {
        if(this.getHeadEntity() instanceof GumWormEntity gumWorm){
            gumWorm.onPlayerJump(i);
        }
    }

    @Override
    public void handleStopJump() {

    }
}
