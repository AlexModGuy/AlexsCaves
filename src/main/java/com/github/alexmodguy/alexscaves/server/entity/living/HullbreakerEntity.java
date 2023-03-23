package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ai.HullbreakerInspectMobGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.HullbreakerMeleeGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.RandomlySwimGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.PartEntity;

import java.util.function.Predicate;

public class HullbreakerEntity extends WaterAnimal implements IAnimatedEntity {

    public static final Animation ANIMATION_PUZZLE = Animation.create(60);
    public static final Animation ANIMATION_BITE = Animation.create(20);
    public static final Animation ANIMATION_BASH = Animation.create(25);

    private static final EntityDataAccessor<Integer> INTEREST_LEVEL = SynchedEntityData.defineId(HullbreakerEntity.class, EntityDataSerializers.INT);

    public static final Predicate<LivingEntity> GLOWING_TARGET = (mob) -> {
        return mob.isInWaterOrBubble() && (mob.hasEffect(MobEffects.GLOWING) || mob.getType().is(ACTagRegistry.GLOWING_ENTITIES) || mob.isPassenger() && mob.getVehicle() instanceof SubmarineEntity sub && sub.areLightsOn());
    };
    public final HullbreakerPartEntity headPart;
    public final HullbreakerPartEntity tail1Part;
    public final HullbreakerPartEntity tail2Part;
    public final HullbreakerPartEntity tail3Part;
    public final HullbreakerPartEntity tail4Part;
    private final HullbreakerPartEntity[] allParts;
    private Animation currentAnimation;
    private int animationTick;
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch = 0;
    private float prevFishPitch = 0;

    private float pulseAmount;
    private float prevPulseAmount;
    private float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    private int blockBreakCooldown = 0;

    public HullbreakerEntity(EntityType entityType, Level level) {
        super(entityType, level);
        headPart = new HullbreakerPartEntity(this, this,3, 2);
        tail1Part = new HullbreakerPartEntity(this, this,2, 2);
        tail2Part = new HullbreakerPartEntity(this, tail1Part,2, 1.5F);
        tail3Part = new HullbreakerPartEntity(this, tail2Part, 2.5F, 1.5F);
        tail4Part = new HullbreakerPartEntity(this, tail3Part,1.5F, 1F);
        allParts = new HullbreakerPartEntity[]{headPart, tail1Part, tail2Part, tail3Part, tail4Part};
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.7F, 30);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(INTEREST_LEVEL, 0);
    }

    public static boolean checkHullbreakerSpawnRules(EntityType<? extends LivingEntity> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return level.getFluidState(pos).is(FluidTags.WATER) && pos.getY() < level.getSeaLevel() - 25;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new HullbreakerMeleeGoal(this));
        this.goalSelector.addGoal(1, new HullbreakerInspectMobGoal(this));
        this.goalSelector.addGoal(2, new RandomlySwimGoal(this, 10, 35, 15, 1.0D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    protected void playSwimSound(float f) {

    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.45F * dimensions.height;
    }


    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.MAX_HEALTH, 350.0D).add(Attributes.ATTACK_DAMAGE, 16.0D);
    }

    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        if (allParts != null) {
            for (PartEntity part : allParts) {
                part.remove(RemovalReason.KILLED);
            }
        }
    }

    public void tick(){
        tickMultipart();
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        prevPulseAmount = pulseAmount;
        fishPitch = Mth.approachDegrees(fishPitch, Mth.clamp((float) this.getDeltaMovement().y * 2F, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI), 2.5F);
        boolean grounded = this.isOnGround() && !isInWaterOrBubble();
        if (grounded && landProgress < 5F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0F) {
            landProgress--;
        }
        float pulseBy = getInterestLevel() * 0.45F;
        pulseAmount += pulseBy;
        if(!level.isClientSide){
            double waterHeight = getFluidTypeHeight(ForgeMod.WATER_TYPE.get());
            if(waterHeight > 0 && waterHeight < this.getBbHeight() - 1.0F){
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0));
            }
        }
        if(this.getAnimation() == HullbreakerEntity.ANIMATION_BASH && this.getAnimationTick() > 10 && this.getAnimationTick() <= 20){
            breakBlock();
        }
        if(blockBreakCooldown > 0){
            blockBreakCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }
    public float getPulseAmount(float partialTicks) {
        return (prevPulseAmount + (pulseAmount - prevPulseAmount) * partialTicks) * 0.2F;
    }

    public int getInterestLevel() {
        return this.entityData.get(INTEREST_LEVEL);
    }

    public void setInterestLevel(int level) {
        this.entityData.set(INTEREST_LEVEL, level);
    }

    public int getHeadRotSpeed() {
        return 5;
    }

    public void breakBlock() {
        if(blockBreakCooldown-- > 0){
            return;
        }
        boolean flag = false;
        AABB damageBox = this.headPart.getBoundingBox().inflate(1.2F).move(this.calculateViewVector(this.getXRot(), this.getYRot()));
        if (!level.isClientSide && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, this) && this.getTarget() instanceof Player) {
            for (int a = (int) Math.round(damageBox.minX); a <= (int) Math.round(damageBox.maxX); a++) {
                for (int b = (int) Math.round(damageBox.minY) - 1; (b <= (int) Math.round(damageBox.maxY) + 1) && (b <= 127); b++) {
                    for (int c = (int) Math.round(damageBox.minZ); c <= (int) Math.round(damageBox.maxZ); c++) {
                        final BlockPos pos = new BlockPos(a, b, c);
                        final BlockState state = level.getBlockState(pos);
                        if (!state.isAir() && !state.getShape(level, pos).isEmpty() && !state.is(ACTagRegistry.UNMOVEABLE) && state.getBlock().getExplosionResistance() <= 15) {
                            final Block block = state.getBlock();
                            if (block != Blocks.AIR) {
                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
                                flag = true;
                                level.destroyBlock(pos, true);
                                if (state.is(BlockTags.ICE)) {
                                    level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
        if(flag){
            blockBreakCooldown = 3;
        }
    }

    private void tickMultipart() {
        if (yawPointer == -1) {
            for (int i = 0; i < yawBuffer.length; i++) {
                yawBuffer[i] = this.yBodyRot;
            }
        }
        if (++this.yawPointer == this.yawBuffer.length) {
            this.yawPointer = 0;
        }
        this.yawBuffer[this.yawPointer] = this.yBodyRot;

        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        Vec3 center = this.position().add(0, this.getBbHeight() * 0.5F, 0);
        this.headPart.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, 3.5F), fishPitch + this.getXRot(), this.getYHeadRot()).add(center));
        this.tail1Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(1F, 4), 0, -3.5F), fishPitch, this.getYawFromBuffer(2, 1.0F)).add(center));
        this.tail2Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(1F, 3), 0, -2), fishPitch, this.getYawFromBuffer(4, 1.0F)).add(this.tail1Part.centeredPosition()));
        this.tail3Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(2F, 2), 0, -2.65F), fishPitch, this.getYawFromBuffer(6, 1.0F)).add(this.tail2Part.centeredPosition()));
        this.tail4Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(1.5F, 1), 0, -3), fishPitch, this.getYawFromBuffer(8, 1.0F)).add(this.tail3Part.centeredPosition()));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    private double swimDegree(float width, float sinOffset) {
        double move = Math.cos(animationPosition * 0.45F + sinOffset) * animationSpeed * width;
        double idle = Math.sin((tickCount + AlexsCaves.PROXY.getPartialTicks()) * 0.05F + sinOffset) * width * 0.5F;
        return (move + idle * (1 - animationSpeed)) * (1 - getLandProgress(AlexsCaves.PROXY.getPartialTicks()));
    }

    private Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * ((float) Math.PI / 180F)).yRot(-yRot * ((float) Math.PI / 180F));
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public float getYawFromBuffer(int pointer, float partialTick) {
        int i = this.yawPointer - pointer & 127;
        int j = this.yawPointer - pointer - 1 & 127;
        float d0 = this.yawBuffer[j];
        float d1 = this.yawBuffer[i] - d0;
        return d0 + d1 * partialTick;
    }

    public void calculateEntityAnimation(LivingEntity living, boolean flying) {
        living.animationSpeedOld = living.animationSpeed;
        double d0 = living.getX() - living.xo;
        double d1 = living.getY() - living.yo;
        double d2 = living.getZ() - living.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        living.animationSpeed += (f - living.animationSpeed) * 0.4F;
        living.animationPosition += living.animationSpeed;
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
        return new Animation[]{ANIMATION_PUZZLE, ANIMATION_BITE, ANIMATION_BASH};
    }

}
