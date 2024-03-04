package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexmodguy.alexscaves.server.entity.item.CrushedBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingTreeBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.LuxtructosaurusLegSolver;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class SauropodBaseEntity extends DinosaurEntity implements ShakesScreen, IAnimatedEntity, KaijuMob, ITallWalker {

    protected static final EntityDataAccessor<Boolean> WALKING = SynchedEntityData.defineId(SauropodBaseEntity.class, EntityDataSerializers.BOOLEAN);
    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(60);
    public static final Animation ANIMATION_EPIC_DEATH = Animation.create(120);
    public static final Animation ANIMATION_SUMMON = Animation.create(120);
    public static final Animation ANIMATION_STOMP = Animation.create(50);
    public static final Animation ANIMATION_SPEW_FLAMES = Animation.create(80);
    public static final Animation ANIMATION_JUMP = Animation.create(45);
    public static final Animation ANIMATION_LEFT_KICK = Animation.create(20);
    public static final Animation ANIMATION_RIGHT_KICK = Animation.create(20);
    public static final Animation ANIMATION_LEFT_WHIP = Animation.create(40);
    public static final Animation ANIMATION_RIGHT_WHIP = Animation.create(40);
    public static final Animation ANIMATION_EAT_LEAVES = Animation.create(100);
    private static final int STOMP_CRUSH_HEIGHT = 6;
    public LuxtructosaurusLegSolver legSolver = new LuxtructosaurusLegSolver(0.2F, 2F, 1.2F, 1.9F, 2);
    private Animation currentAnimation;
    private int animationTick;
    private final SauropodPartEntity[] allParts;
    public final SauropodPartEntity neckPart1;
    public final SauropodPartEntity neckPart2;
    public final SauropodPartEntity neckPart3;
    public final SauropodPartEntity headPart;
    public final SauropodPartEntity tailPart1;
    public final SauropodPartEntity tailPart2;
    public final SauropodPartEntity tailPart3;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private float prevWalkAnimPosition;
    private float walkAnimPosition;
    private float prevWalkAnimSpeed;
    private float walkAnimSpeed;
    private double lastStompX;
    private double lastStompZ;
    private float prevLegBackAmount = 0;
    private float legBackAmount = 0;
    private float prevRaiseArmsAmount = 0;
    private float raiseArmsAmount = 0;
    protected float neckXRot;
    protected float neckYRot;
    protected float tailXRot;
    protected float tailYRot;
    private float prevScreenShakeAmount;
    protected float screenShakeAmount;
    private float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    private float lastYawBeforeWhip;
    public boolean turningFast;
    private boolean wasPreviouslyChild;
    private int stepSoundCooldown = 0;

    public SauropodBaseEntity(EntityType entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.moveControl = new SauropodMoveHelper();
        this.neckPart1 = new SauropodPartEntity(this, this, 3F, 3F);
        this.neckPart2 = new SauropodPartEntity(this, neckPart1, 2F, 2F);
        this.neckPart3 = new SauropodPartEntity(this, neckPart2, 2F, 1.5F);
        this.headPart = new SauropodPartEntity(this, neckPart3, 2F, 2F);
        this.tailPart1 = new SauropodPartEntity(this, this, 3F, 2F);
        this.tailPart2 = new SauropodPartEntity(this, tailPart1, 2.5F, 1.5F);
        this.tailPart3 = new SauropodPartEntity(this, tailPart2, 2F, 1F);
        this.allParts = new SauropodPartEntity[]{neckPart1, neckPart2, neckPart3, headPart, tailPart1, tailPart2, tailPart3};
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WALKING, false);
    }

    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    public boolean isFakeEntity() {
        return this.firstTick;
    }

    @Override
    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.prevRaiseArmsAmount = raiseArmsAmount;
        this.prevScreenShakeAmount = screenShakeAmount;
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        if (shouldRaiseArms() && raiseArmsAmount < 5F) {
            raiseArmsAmount++;
        }
        if (!shouldRaiseArms() && raiseArmsAmount > 0F) {
            raiseArmsAmount--;
        }
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.3F);
        }
        if (this.getAnimation() != ANIMATION_LEFT_WHIP && this.getAnimation() != ANIMATION_RIGHT_WHIP) {
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.getYRot(), turningFast ? 10 : 2);
            lastYawBeforeWhip = this.getYRot();
        } else {
            float negative = this.getAnimation() == ANIMATION_RIGHT_WHIP ? -1 : 1;
            float target = 0;
            if (this.getAnimationTick() > 10) {
                float f = (this.getAnimationTick() - 10) / 30F;
                target = f * 230;
            }
            if (this.getAnimationTick() > 30F) {
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, lastYawBeforeWhip, 15);
            } else {
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, lastYawBeforeWhip + negative * target, 90);
            }
        }
        tickMultipart();
        tickWalking();
        if (level().isClientSide) {
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
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() > 25 && this.getAnimationTick() < 35 || this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() > 5 && this.getAnimationTick() < 45) {
            if (screenShakeAmount <= 2.0F) {
                screenShakeAmount = 2.0F;
            }
        }
        if(wasPreviouslyChild != this.isBaby()){
            wasPreviouslyChild = this.isBaby();
            this.refreshDimensions();
            for(SauropodPartEntity sauropodPartEntity : this.allParts){
                sauropodPartEntity.refreshDimensions();
            }
        }
    }

    private void tickWalking() {
        this.prevWalkAnimPosition = walkAnimPosition;
        this.prevWalkAnimSpeed = walkAnimSpeed;
        this.prevLegBackAmount = legBackAmount;
        float f = getLegSlamAmount(2.0F, 0.333F);
        float f1 = getLegSlamAmount(2.0F, 0.666F);
        Vec3 movement = this.getDeltaMovement();
        float speed = (float) movement.length();
        if (this.areLegsMoving()) {
            this.walkAnimPosition += walkAnimSpeed;
            if (this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) {
                walkAnimSpeed = Mth.approach(walkAnimSpeed, 4.0F, 0.66F);
            } else {
                walkAnimSpeed = Mth.approach(walkAnimSpeed, this.isBaby() ? 2.0F : 1.0F, this.isBaby() ? 0.2F : 0.1F);
            }
        } else {
            if (f > 0.05) {
                this.walkAnimPosition += Math.min(f - 0.05F, walkAnimSpeed);
            }
            if (walkAnimSpeed > 0.0F) {
                walkAnimSpeed = Math.max(0, walkAnimSpeed - 0.025F);
            }
        }
        if (f <= 0.05 && walkAnimSpeed > 0.0F && this.onGround() && (speed > 0.003F || this.isVehicle()) && stepSoundCooldown <= 0) {
            this.onStep();
            stepSoundCooldown = 5;
        }
        if (f1 < 0.65F) {
            this.lastStompX = this.getX();
            this.lastStompZ = this.getZ();
        }
        if(stepSoundCooldown > 0){
            stepSoundCooldown--;
        }
        double stompX = (this.getX() - this.lastStompX);
        double stompZ = (this.getZ() - this.lastStompZ);
        float stompDist = Mth.sqrt((float) (stompX * stompX + stompZ * stompZ));
        if (speed <= 0.003 || !areLegsMoving()) {
            stompDist = 0;
        }
        if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 2) {
            actuallyPlayAmbientSound();
        }
        this.legBackAmount = Mth.clamp(Mth.approach(this.legBackAmount, stompDist, speed), -1, 1);
    }

    protected abstract void onStep();

    public boolean shouldRaiseArms() {
        return this.isDancing() || this.getAnimation() == ANIMATION_LEFT_KICK || this.getAnimation() == ANIMATION_RIGHT_KICK || this.getAnimation() == ANIMATION_STOMP || this.getAnimation() == ANIMATION_JUMP;
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
        float neckRotateSpeed = getNeckRotateSpeed();
        float tailRotateSpeed = getTailRotateSpeed();
        if(turningFast){
            neckRotateSpeed += 30;
            tailRotateSpeed += 30;
        }
        this.neckXRot = wrapNeckDegrees(Mth.approachDegrees(this.neckXRot, getTargetNeckXRot(), neckRotateSpeed));
        this.neckYRot = wrapNeckDegrees(Mth.approachDegrees(this.neckYRot, getTargetNeckYRot(), neckRotateSpeed));
        this.tailXRot = wrapNeckDegrees(Mth.approachDegrees(this.tailXRot, getTargetTailXRot(), tailRotateSpeed));
        this.tailYRot = wrapNeckDegrees(Mth.approachDegrees(this.tailYRot, getTargetTailYRot(), tailRotateSpeed));
        Vec3 center = this.position().add(0, this.getBbHeight() * 0.5F - getLegSolverBodyOffset(), 0);
        float headXStep = neckXRot / 4F;
        float headYStep = neckYRot / 4F;
        float tailXStep = tailXRot / 3F;
        float tailYStep = tailYRot / 3F;
        float neckAdditionalY = 0F;
        float neckAdditionalZ = 0F;
        if (this.getAnimation() == ANIMATION_STOMP) {
            float f = ACMath.cullAnimationTick(this.getAnimationTick(), 2, ANIMATION_STOMP, 1.0F, 0, 30);
            neckAdditionalY = 4 * f;
            neckAdditionalZ = -4 * f;
        }else if(this.isDancing()){
            float f = this.getDanceProgress(1.0F);
            neckAdditionalY = 4 * f;
            neckAdditionalZ = -4 * f;
            headYStep *= (1F - f);
        }
        this.neckPart1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 2F + neckAdditionalY, 5F + neckAdditionalZ).scale(this.getScale()), headXStep, (yBodyRot + headYStep)).add(center));
        this.neckPart2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, 2.5F).scale(this.getScale()), headXStep, (yBodyRot + headYStep * 2F)).add(this.neckPart1.centeredPosition()));
        this.neckPart3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, 2F).scale(this.getScale()), headXStep, (yBodyRot + headYStep * 3F)).add(this.neckPart2.centeredPosition()));
        this.headPart.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, 2.5F).scale(this.getScale()), headXStep, (yBodyRot + headYStep * 4F)).add(this.neckPart3.centeredPosition()));
        this.tailPart1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, -0.5F, -3.5F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep)).add(center));
        this.tailPart2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, -0.25F, -3.25F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep * 2F)).add(this.tailPart1.centeredPosition()));
        this.tailPart3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, -2.5F).scale(this.getScale()), tailXStep, (yBodyRot + tailYStep * 3F)).add(this.tailPart2.centeredPosition()));

        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    private float wrapNeckDegrees(float f) {
        return f % 360.0F;
    }


    protected void crushBlocksInRing(int width, int ringStartX, int ringStartZ, float dropChance) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        float lowestFoot = 0.0F;
        for(LuxtructosaurusLegSolver.Leg leg : legSolver.legs){
            float height = leg.getHeight(1.0F);
            if(height > lowestFoot){
                lowestFoot = height;
            }
        }
        int feetY = this.blockPosition().getY() - (int)lowestFoot;
        BlockPos center = new BlockPos(ringStartX, feetY, ringStartZ);
        if(net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this) || this.isVehicle() && this.getControllingPassenger() instanceof Player){
            for (int y = 0; y <= STOMP_CRUSH_HEIGHT; y++) {
                List<MovingBlockData> dataPerYLevel = new ArrayList<>();
                int currentBlocksInChunk = 0;
                for (int i = -width - 1; i <= width + 1; i++) {
                    for (int j = -width - 1; j <= width + 1; j++) {
                        mutableBlockPos.set(this.getBlockX() + i, feetY + y, this.getBlockZ() + j);
                        double dist = Math.sqrt(mutableBlockPos.distSqr(center));
                        if (dist <= width && level().isLoaded(mutableBlockPos)) {
                            BlockState state = level().getBlockState(mutableBlockPos);
                            if (state.is(ACTagRegistry.UNMOVEABLE) || state.isAir() || state.canBeReplaced() || state.getBlock().getExplosionResistance() > AlexsCaves.COMMON_CONFIG.atlatitanMaxExplosionResistance.get()) {
                                continue;
                            } else {
                                BlockEntity te = level().getBlockEntity(mutableBlockPos);
                                BlockPos offset = mutableBlockPos.immutable().subtract(center);
                                MovingBlockData data = new MovingBlockData(state, state.getShape(level(), mutableBlockPos), offset, te == null ? null : te.saveWithoutMetadata());
                                dataPerYLevel.add(data);

                                if (currentBlocksInChunk < 16) {
                                    currentBlocksInChunk++;
                                } else {
                                    CrushedBlockEntity crushed = ACEntityRegistry.CRUSHED_BLOCK.get().create(level());
                                    crushed.moveTo(Vec3.atCenterOf(center.above(y)));
                                    crushed.setAllBlockData(FallingTreeBlockEntity.createTagFromData(dataPerYLevel));
                                    crushed.setPlacementCooldown(10);
                                    crushed.setDropChance(dropChance);
                                    level().addFreshEntity(crushed);
                                    dataPerYLevel.clear();
                                    currentBlocksInChunk = 0;
                                }
                                level().setBlockAndUpdate(mutableBlockPos, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
                if (!dataPerYLevel.isEmpty()) {
                    CrushedBlockEntity crushed = ACEntityRegistry.CRUSHED_BLOCK.get().create(level());
                    crushed.moveTo(Vec3.atCenterOf(center.above(y)));
                    crushed.setAllBlockData(FallingTreeBlockEntity.createTagFromData(dataPerYLevel));
                    crushed.setDropChance(dropChance);
                    crushed.setPlacementCooldown(1);
                    level().addFreshEntity(crushed);
                }
            }
        }

    }

    protected Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * ((float) Math.PI / 180F)).yRot(-yRot * ((float) Math.PI / 180F));
    }

    public float getLegSolverBodyOffset() {
        float heightBackLeft = legSolver.backLeft.getHeight(1.0F);
        float heightBackRight = legSolver.backRight.getHeight(1.0F);
        float heightFrontLeft = legSolver.frontLeft.getHeight(1.0F);
        float heightFrontRight = legSolver.frontRight.getHeight(1.0F);
        float armsWalkAmount = 1F - (raiseArmsAmount / 5F);
        return Math.max(Math.max(heightBackLeft, heightBackRight), armsWalkAmount * Math.max(heightFrontLeft, heightFrontRight)) * 0.8F;
    }

    public int getMaxHeadYRot() {
        return 90;
    }

    public int getHeadRotSpeed() {
        return 3;
    }

    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !level().isClientSide) {
            this.setAnimation(ANIMATION_SPEAK);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
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


    public float getYawFromBuffer(int pointer, float partialTick) {
        int i = this.yawPointer - pointer & 127;
        int j = this.yawPointer - pointer - 1 & 127;
        float d0 = this.yawBuffer[j];
        float d1 = this.yawBuffer[i] - d0;
        return d0 + d1 * partialTick;
    }

    public float getTargetNeckXRot() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return -140F;
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH && this.getAnimationTick() < 110) {
            return -140F;
        }
        if (this.getAnimation() == ANIMATION_SUMMON && this.getAnimationTick() < 70) {
            return -60F;
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() <= 30) {
            return 30F;
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() < 70) {
            return 60 + (float) (Math.sin(animationTick * 0.4F) * 10F);
        }
        if (this.isDancing()) {
            return 30F;
        }
        return -30F;
    }

    public float getTargetNeckYRot() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return (float) (Math.sin(animationTick * 0.2F) * 40F);
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH && this.getAnimationTick() < 110) {
            return (float) (Math.sin(animationTick * 0.1F) * 20F);
        }
        if (this.getAnimation() == ANIMATION_SUMMON && this.getAnimationTick() < 50) {
            return 110;
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() < 70) {
            return (float) (Math.sin(animationTick * 0.15F) * 40F);
        }
        float buffered = getYawFromBuffer(10, 1.0F) - this.yBodyRot;
        return this.getYHeadRot() - this.yBodyRot + buffered;
    }

    private float getNeckRotateSpeed() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return 30F;
        }
        if (this.getAnimation() == ANIMATION_SUMMON) {
            return 2;
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() < 70) {
            return 40F;
        }
        if (this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return 30F;
        }
        return 10F;
    }

    public float getTargetTailXRot() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return -20F;
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH && this.getAnimationTick() < 110) {
            return -20F;
        }
        if (this.getAnimation() == ANIMATION_SUMMON) {
            return -100F;
        }
        if (this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return this.getAnimationTick() > 20 ? -20F : 20F;
        }
        return 0;
    }

    public float getTargetTailYRot() {
        if (this.getAnimation() == ANIMATION_LEFT_WHIP) {
            return this.getAnimationTick() > 24 ? 70F : -70;
        }
        if (this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return this.getAnimationTick() > 24 ? -70F : 70;
        }
        return getYawFromBuffer(20, 1.0F) - this.yBodyRot;
    }

    private float getTailRotateSpeed() {
        return this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP ? 30F : 10F;
    }

    public boolean areLegsMoving() {
        return (this.entityData.get(WALKING) || this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) && !this.isImmobile() && !this.isNoAi();
    }

    public float getLegSlamAmount(float speed, float offset) {
        float walkSpeed = 0.05F;
        return Math.abs((float) Math.cos(getWalkAnimPosition(1.0F) * walkSpeed * speed - (Math.PI * offset)));
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public float getStepHeight() {
        return 3.2F;
    }

    public boolean hurtEntitiesAround(Vec3 center, float radius, float damageAmount, float knockbackAmount, boolean setsOnFire, boolean disablesShields){
        AABB aabb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        boolean flag = false;
        DamageSource damageSource = this.damageSources().mobAttack(this);
        for(LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR)){
            if(!living.is(this) && !living.isAlliedTo(this) && living.getType() != this.getType() && living.distanceToSqr(center.x, center.y, center.z) <= radius * radius){
                if(living.isDamageSourceBlocked(damageSource) && disablesShields && living instanceof Player player){
                    player.disableShield(true);
                }
                if(living.hurt(damageSource, damageAmount)){
                    flag = true;
                    if(setsOnFire){
                        living.setSecondsOnFire(10);
                    }
                    living.knockback(knockbackAmount, center.x - living.getX(), center.z - living.getZ());
                }
            }
        }
        return flag;
    }

    public boolean isImmobile(){
        return super.isImmobile() || this.getAnimation() == ANIMATION_SUMMON;
    }

    public float getScreenShakeAmount(float partialTicks) {
        if(!this.isAlive()){
            return 0;
        }
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    public float getRaiseArmsAmount(float partialTicks) {
        return (prevRaiseArmsAmount + (raiseArmsAmount - prevRaiseArmsAmount) * partialTicks) * 0.2F;
    }

    public float getWalkAnimPosition(float partialTicks) {
        return prevWalkAnimPosition + (walkAnimPosition - prevWalkAnimPosition) * partialTicks;
    }

    public float getWalkAnimSpeed(float partialTicks) {
        return prevWalkAnimSpeed + (walkAnimSpeed - prevWalkAnimSpeed) * partialTicks;
    }

    public float getLegBackAmount(float partialTicks) {
        return prevLegBackAmount + (legBackAmount - prevLegBackAmount) * partialTicks;
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
        return new Animation[]{ANIMATION_SPEAK, ANIMATION_ROAR, ANIMATION_EPIC_DEATH, ANIMATION_SUMMON, ANIMATION_STOMP, ANIMATION_SPEW_FLAMES, ANIMATION_JUMP, ANIMATION_LEFT_KICK, ANIMATION_RIGHT_KICK, ANIMATION_LEFT_WHIP, ANIMATION_RIGHT_WHIP, ANIMATION_EAT_LEAVES};
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= getProjectileDamageReduction();
        }
        return super.hurt(source, amount);
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2);
    }

    public float getProjectileDamageReduction() {
        return 0.75F;
    }

    @Override
    public int getMaxNavigableDistanceToGround() {
        return 3;
    }

    private class SauropodMoveHelper extends MoveControl {

        public SauropodMoveHelper() {
            super(SauropodBaseEntity.this);
        }

        @Override
        public void tick() {
            if (this.operation == Operation.WAIT) {
                SauropodBaseEntity.this.entityData.set(WALKING, false);
                speedModifier = 0.0F;
            } else {
                SauropodBaseEntity.this.entityData.set(WALKING, true);
                float f = SauropodBaseEntity.this.getLegSlamAmount(2.0F, 0.66F);
                boolean flag = true;
                if (this.operation == Operation.MOVE_TO) {
                    double d0 = this.wantedX - this.mob.getX();
                    double d1 = this.wantedZ - this.mob.getZ();
                    float moveToRot = (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    float difference = Mth.degreesDifferenceAbs(SauropodBaseEntity.this.yBodyRot, moveToRot);
                    flag = difference < 15.0F;
                }
                if (SauropodBaseEntity.this.getAnimation() == ANIMATION_LEFT_WHIP || SauropodBaseEntity.this.getAnimation() == ANIMATION_RIGHT_WHIP) {
                    flag = true;
                }
                float threshold = 0.65F;
                if (f >= threshold && flag) {
                    float f1 = (f - threshold) / (1F - threshold);
                    if(SauropodBaseEntity.this.isBaby()){
                        f1 *= 0.5F;
                    }
                    speedModifier = f1;
                } else {
                    speedModifier = 0.0F;

                }
            }
            super.tick();
        }
    }

}
