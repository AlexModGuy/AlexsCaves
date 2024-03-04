package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PewenBranchBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AtlatitanEntity extends SauropodBaseEntity implements KeybindUsingMount {

    private static final EntityDataAccessor<Optional<BlockPos>> EATING_POS = SynchedEntityData.defineId(AtlatitanEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockState>> LAST_EATEN_BLOCK = SynchedEntityData.defineId(AtlatitanEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Integer> RIDEABLE_FOR = SynchedEntityData.defineId(AtlatitanEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> METER_AMOUNT = SynchedEntityData.defineId(AtlatitanEntity.class, EntityDataSerializers.FLOAT);

    public AtlatitanEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AtlatitanMeleeGoal(this));
        this.goalSelector.addGoal(2, new AnimalBreedEggsGoal(this, 1));
        this.goalSelector.addGoal(3, new AnimalLayEggGoal(this, 100, 1));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(ACBlockRegistry.TREE_STAR.get()), false));
        this.goalSelector.addGoal(5, new AtlatitanNibbleTreesGoal(this, 30));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0D, 50) {
            protected Vec3 getPosition() {
                return DefaultRandomPos.getPos(this.mob, 30, 7);
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 30.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(10, new LookForwardsGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AtlatitanEntity.class)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EATING_POS, Optional.empty());
        this.entityData.define(LAST_EATEN_BLOCK, Optional.empty());
        this.entityData.define(RIDEABLE_FOR, 0);
        this.entityData.define(METER_AMOUNT, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.325D).add(Attributes.MAX_HEALTH, 400.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 8);
    }

    @Override
    protected void onStep() {
        if(!this.isBaby()){
            if (screenShakeAmount <= 1.0F) {
                this.playSound(ACSoundRegistry.ATLATITAN_STEP.get(), 2, 1);
            }
            if (screenShakeAmount <= 1.0F) {
                screenShakeAmount = 1.0F;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (this.getAnimation() == ANIMATION_EAT_LEAVES && this.getAnimationTick() > 35 && this.getAnimationTick() < 90) {
                BlockState lastEatenBlock = getLastEatenBlock();
                if (lastEatenBlock != null) {
                    Vec3 crumbPos = this.headPart.position().add((random.nextFloat() - 0.5F) * 2.0F * this.getScale(), (0.5F + (random.nextFloat() - 0.5F) * 0.2F) * this.getScale(), (random.nextFloat() - 0.5F) * 2.0F * this.getScale());
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, lastEatenBlock), crumbPos.x, crumbPos.y, crumbPos.z, ((double) this.random.nextFloat() - 0.5D) * 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);
                }
            }
            if(this.getRideableFor() > 0 && level().random.nextInt(2) == 0 && !this.isDancing()){
                Vec3 particlePos = this.headPart.position().add((random.nextFloat() - 0.5F) * 2.0F * this.getScale(), random.nextFloat() * 2.0F * this.getScale(), (random.nextFloat() - 0.5F) * 2.0F * this.getScale()).add(this.getDeltaMovement());
                this.level().addParticle(ACParticleRegistry.HAPPINESS.get(), particlePos.x, particlePos.y, particlePos.z, ((double) this.random.nextFloat() - 0.5D) * 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);
            }
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && getMeterAmount() >= 1.0F) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
            }
        }else{
            if(this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 30){
                this.playSound(ACSoundRegistry.ATLATITAN_STOMP.get(), 3.0F, 1.0F);
                if(this.screenShakeAmount < 4.0F){
                    this.screenShakeAmount = 4.0F;
                }
                crushBlocksInRing(15, this.getBlockX(), this.getBlockZ(), 1.0F);
                if(this.isVehicle() && !this.level().isClientSide){
                    for(Entity passenger : this.getPassengers()){
                        ACAdvancementTriggerRegistry.ATLATITAN_STOMP.triggerForEntity(passenger);
                    }
                }
            }
            if(this.getRideableFor() > 0){
                this.setRideableFor(this.getRideableFor() - 1);
            }
            if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.heal(2);
            }
            if(this.getAnimation() == ANIMATION_RIGHT_KICK && this.getAnimationTick() == 8){
                Vec3 armPos = this.position().add(rotateOffsetVec(new Vec3(-2, 0, 2.5F), 0, this.yBodyRot));
                this.hurtEntitiesAround(armPos, 5.0F,  (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 1.0F, false, false);
            }
            if(this.getAnimation() == ANIMATION_LEFT_KICK && this.getAnimationTick() == 8){
                Vec3 armPos = this.position().add(rotateOffsetVec(new Vec3(2, 0, 2.5F), 0, this.yBodyRot));
                this.hurtEntitiesAround(armPos, 5.0F,  (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 1.0F, false, false);
            }
            if((this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) && this.getAnimationTick() > 20 && this.getAnimationTick() < 30){
                this.hurtEntitiesAround(this.tailPart2.position(), 12.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE), 1.0F, false, false);
            }
        }
        if (this.isVehicle()) {
            if (this.getMeterAmount() < 1.0F) {
                this.setMeterAmount(Math.min(this.getMeterAmount() + 0.0025F, 1.0F));
            }
        } else {
            this.setMeterAmount(0.0F);
        }
    }

    public BlockPos getEatingPos() {
        return this.entityData.get(EATING_POS).orElse(null);
    }

    public void setEatingPos(BlockPos eatingPos) {
        this.entityData.set(EATING_POS, Optional.ofNullable(eatingPos));
    }

    public BlockState getLastEatenBlock() {
        return this.entityData.get(LAST_EATEN_BLOCK).orElse(null);
    }

    public void setLastEatenBlock(BlockState eatingPos) {
        this.entityData.set(LAST_EATEN_BLOCK, Optional.ofNullable(eatingPos));
    }

    public void setRideableFor(int time) {
        this.entityData.set(RIDEABLE_FOR, time);
    }

    public int getRideableFor() {
        return this.entityData.get(RIDEABLE_FOR);
    }

    @Override
    public boolean onFeedMixture(ItemStack itemStack, Player player) {
        if (itemStack.is(ACItemRegistry.SERENE_SALAD.get())) {
            this.setRideableFor(12000);
            return true;
        }
        return false;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(ACBlockRegistry.TREE_STAR.get().asItem());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setRideableFor(compound.getInt("RideableTime"));
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RideableTime", this.getRideableFor());
    }


    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2) {
                if (this.getMeterAmount() >= 1.0F && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    this.yBodyRot = keyPresser.getYHeadRot();
                    this.setYRot(keyPresser.getYHeadRot());
                    this.setAnimation(ANIMATION_STOMP);
                    this.setMeterAmount(0.0F);
                }
            }
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult prev = super.mobInteract(player, hand);
        ItemStack itemstack = player.getItemInHand(hand);
        if (!prev.consumesAction() && itemstack.is(ACItemRegistry.SERENE_SALAD.get()) && !this.isBaby()) {
            if (!itemstack.getCraftingRemainingItem().isEmpty()) {
                this.spawnAtLocation(itemstack.getCraftingRemainingItem().copy());
            }
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }else if(!prev.consumesAction() && this.getRideableFor() > 0 && !this.isBaby() && this.canAddPassenger(player)){
            player.startRiding(this);
        }
        return prev;
    }

    @Override
    public float getTargetNeckXRot() {
        if (this.getAnimation() == ANIMATION_EAT_LEAVES && this.getAnimationTick() <= 35) {
            BlockPos eatingPos = getEatingPos();
            if (eatingPos != null) {
                float peckDist = (float) Mth.clamp(eatingPos.getY() + 0.5F - this.getY(), -6.0F, 12.0F) - 6.0F;
                return peckDist * 1.2F / 6.0F * -90F;
            }
        }
        return super.getTargetNeckXRot();
    }

    public BlockPos getStandAtTreePos(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        BlockState state = level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        if (level().getBlockState(target.below()).isAir()) {
            target = target.relative(dir);
        }
        return target.relative(dir.getOpposite(), (int) Math.floor(13 * this.getScale())).atY((int) this.getY());
    }

    public boolean lockTreePosition(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        int headDistToBody = (int) Math.floor(14 * this.getScale());
        BlockState state = level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        float targetRot = Mth.approachDegrees(this.getYRot(), dir.toYRot(), 20);
        this.setYRot(targetRot);
        this.setYHeadRot(targetRot);
        this.yBodyRot = Mth.approachDegrees(yBodyRot, targetRot, 10);
        this.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
        if (level().getBlockState(target.below()).isAir()) {
            target = target.relative(dir);
        }
        Vec3 vec31 = Vec3.atCenterOf(target.relative(dir.getOpposite(), headDistToBody - 1));
        if (vec31.distanceToSqr(this.getX(), vec31.y, this.getZ()) > 1.0F) {
            this.getMoveControl().setWantedPosition(vec31.x, this.getY(), vec31.z, 1.0D);
        }
        return this.distanceToSqr(vec31.x, this.getY(), vec31.z) < headDistToBody && Mth.degreesDifferenceAbs(this.yBodyRot, dir.toYRot()) < 7;
    }

    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0 || player.xxa != 0) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setTarget(null);
            this.entityData.set(WALKING, true);
        }else{
            this.entityData.set(WALKING, false);
        }
    }


    protected float getRiddenSpeed(Player rider) {
        float f1 = 0.0F;
        if(this.areLegsMoving()){
            float f = this.getLegSlamAmount(2.0F, 0.66F);
            float threshold = 0.65F;
            if (f >= threshold) {
                f1 = (f - threshold) / (1F - threshold);
            }
        }
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED)) * f1;
    }

    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        } else {
            return null;
        }
    }

    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            float seatY = 0.5F;
            float seatZ = 0.5F;
            if(this.getAnimation() == ANIMATION_STOMP){
                float animationIntensity = ACMath.cullAnimationTick(this.getAnimationTick(), 1F, ANIMATION_STOMP, 1.0F, 0, 30);
                seatY += animationIntensity * 1.5F;
                seatZ += animationIntensity * -4.5F;
            }
            Vec3 seatOffset = new Vec3(0F, seatY, seatZ).yRot((float) Math.toRadians(-this.yBodyRot));
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            clampRotation(living, 105);
            moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset() - this.getLegSolverBodyOffset(), this.getZ() + seatOffset.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity p_20123_) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    public int getExperienceReward() {
        return 30;
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float roarPower) {
        this.entityData.set(METER_AMOUNT, roarPower);
    }

    @Override
    public BlockState createEggBlockState() {
        return ACBlockRegistry.ATLATITAN_EGG.get().defaultBlockState();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ACEntityRegistry.ATLATITAN.get().create(level);
    }

    public float getScale() {
        return this.isBaby() ? 0.15F : 1.0F;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.ATLATITAN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.ATLATITAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.ATLATITAN_DEATH.get();
    }
}
