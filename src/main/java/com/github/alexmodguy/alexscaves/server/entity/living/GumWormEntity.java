package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.*;
import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class GumWormEntity extends Monster implements ICustomCollisions, KaijuMob, ShakesScreen {

    private static final EntityDataAccessor<Boolean> Z_ROT_DIRECTION = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BITING = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TARGET_DIG_PITCH = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> TEMP_SUMMON = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> GOBTHUMPER_POS = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> RIDING_SEGMENT_ID = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> RIDING_SEGMENT_UUID = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> LEFT_HOOK_ID = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RIGHT_HOOK_ID = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RIDER_LEAP_TIME = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RIDER_LEAP_TIME_MAX = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> VALID_RIDER = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RETURNING_TO_GROUND = SynchedEntityData.defineId(GumWormEntity.class, EntityDataSerializers.BOOLEAN);
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private double surfaceY;
    private float prevZRot;
    private float zRot;
    private float prevMouthOpenProgress;
    private float mouthOpenProgress;
    private Vec3 surfacePosition;
    private Vec3 prevSurfacePosition;
    public int timeBetweenAttacks = 0;
    public int leapAttackCooldown = 0;
    private float prevDigPitch;
    private float digPitch;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private int ridingModeTicks;
    private int recentlyLeaptTicks;
    private int forceMouthOpenTicks;
    private Player ridingPlayer;
    private int attackNoiseCooldown;
    private int stopDiggingNoiseCooldown;
    private boolean wasDiggingLastTick;
    private int outOfGroundTime = 0;
    private int postRidingDigDownTicks = 0; // Time after riding ends to force digging down
    private int stuckAboveGobthumperTicks = 0; // Fallback counter when stuck above gobthumper position
    private int stuckInAirTicks = 0; // Fallback counter when stuck motionless in air
    private Vec3 lastTickPosition = null; // Track position to detect if stuck
    // returningToGround is now synced via RETURNING_TO_GROUND EntityDataAccessor
    private boolean returnNeedsInitialBurst = false; // Whether we need to give initial strong push
    private double returnTargetX = 0; // Target X to return to (20+ blocks from gobthumper)
    private double returnTargetY = 0; // Target Y level to return to
    private double returnTargetZ = 0; // Target Z to return to (20+ blocks from gobthumper)
    private Vec3 returnMoveDirection = null; // Fixed movement direction during return mode
    private boolean detectedStuckState = false; // Once we detect stuck state, don't reset counter

    public GumWormEntity(EntityType type, Level level) {
        super(type, level);
        this.moveControl = new MoveController();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GumWormRidingGoal(this));
        this.goalSelector.addGoal(1, new GumWormAttackGoal(this));
        this.goalSelector.addGoal(2, new GumWormDestroyGobthumperGoal(this));
        this.goalSelector.addGoal(3, new GumWormLeapRandomlyGoal(this));
        this.goalSelector.addGoal(4, new GumWormDigRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.GRAVITY, 0.15D).add(Attributes.MAX_HEALTH, 150.0D).add(Attributes.ARMOR, 10.0D).add(Attributes.ATTACK_DAMAGE, 9.0D).add(Attributes.FOLLOW_RANGE, 128.0D);
    }

    public static boolean checkGumWormSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(6) == 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(Z_ROT_DIRECTION, false);
        builder.define(LEAPING, false);
        builder.define(BITING, false);
        builder.define(TARGET_DIG_PITCH, 0.0F);
        builder.define(TEMP_SUMMON, false);
        builder.define(GOBTHUMPER_POS, Optional.empty());
        builder.define(RIDING_SEGMENT_ID, -1);
        builder.define(RIDING_SEGMENT_UUID, Optional.empty());
        builder.define(LEFT_HOOK_ID, -1);
        builder.define(RIGHT_HOOK_ID, -1);
        builder.define(RIDER_LEAP_TIME_MAX, 1);
        builder.define(RIDER_LEAP_TIME, 0);
        builder.define(DIGGING, false);
        builder.define(VALID_RIDER, false);
        builder.define(RETURNING_TO_GROUND, false);
    }

    public EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDefaultDimensions(pose);
        return dimensions.withEyeHeight(0.4F * dimensions.height());
    }

    protected PathNavigation createNavigation(Level level) {
        return new Navigator(this, level);
    }

    public float getMouthOpenProgress(float partialTicks) {
        return (prevMouthOpenProgress + (mouthOpenProgress - prevMouthOpenProgress) * partialTicks) * 0.1F;
    }

    public float getBodyZRot(float partialTicks) {
        return prevZRot + (zRot - prevZRot) * partialTicks;
    }

    public float getXRot() {
        return this.digPitch;
    }

    public boolean removeWhenFarAway(double distanceSq) {
        return distanceSq < 65536;
    }

    @Override
    public void tick() {
        super.tick();
        prevSurfacePosition = surfacePosition;
        prevScreenShakeAmount = screenShakeAmount;
        prevMouthOpenProgress = mouthOpenProgress;

        // Check if the riding player is no longer riding the segment - if so, immediately clear riding mode
        if (ridingPlayer != null && ridingModeTicks > 0) {
            Entity ridingSegment = this.getRidingSegment();
            if (ridingSegment == null || ridingPlayer.getVehicle() != ridingSegment) {
                ridingPlayer = null;
                ridingModeTicks = 0;
                postRidingDigDownTicks = 40; // Force digging down for 2 seconds after dismount
            }
        }

        if (isMoving() || surfacePosition == null) {
            surfacePosition = calculateLightAbovePosition();
        }
        if (this.isMouthOpen() && mouthOpenProgress < 10F) {
            mouthOpenProgress++;
        }
        if (!this.isMouthOpen() && mouthOpenProgress > 0F) {
            mouthOpenProgress--;
        }
        surfaceY = calculateSurfaceY();
        prevZRot = zRot;
        prevDigPitch = digPitch;
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        Entity target = this.getTarget();
        if(!level().isClientSide && (!this.isLeaping() || (target == null || !target.isAlive())) && !this.isRidingMode() && !isReturningToGround()){
            this.setTargetDigPitch((float) (-(Mth.atan2(this.getDeltaMovement().y, this.getDeltaMovement().horizontalDistance()) * (180F / (float) Math.PI))));
        }
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        // During return mode, rotate pitch faster (20 degrees/tick instead of 5)
        float pitchSpeed = isReturningToGround() ? 20 : 5;
        this.digPitch = Mth.approachDegrees(digPitch, getTargetDigPitch(), pitchSpeed);
        if (this.isMoving()) {
            this.zRot += (this.entityData.get(Z_ROT_DIRECTION) ? -10 : 10);
            if (random.nextInt(300) == 0 && !level().isClientSide) {
                this.entityData.set(Z_ROT_DIRECTION, random.nextBoolean());
            }
            screenShakeAmount = 1.5F;
        } else {
            this.zRot = Mth.approachDegrees(this.zRot, 0, 2);
        }
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
            if(this.isDigging() && isAlive()){
                AlexsCaves.PROXY.playWorldSound(this, (byte) 17);
            }
            spawnDustParticles(false);
        }else{
            Entity ridingSegment = this.getRidingSegment();
            this.entityData.set(RIDING_SEGMENT_ID, ridingSegment == null ? -1 : ridingSegment.getId());
            this.entityData.set(DIGGING, isDigLogic() && !isLeaping());
        }
        boolean flag = false;
        BlockState centralState = level().getBlockState(this.blockPosition());
        BlockState centralStateBelow = level().getBlockState(this.blockPosition().below());
        
        // Fallback mechanism: detect if GumWorm is stuck and needs to return to ground
        // When triggered, GumWorm will phase through all blocks until reaching target Y level
        boolean forcingReturnToGobthumper = false;
        
        // Handle active "return to ground" mode - phase through blocks until reaching underground
        // Head leads, body follows - like a diving worm
        if (isReturningToGround()) {
            forcingReturnToGobthumper = true;
            // Enable phasing through blocks
            this.noPhysics = true;
            // Clear leaping state when returning to ground
            this.setLeaping(false);
            
            // CLIENT SIDE: Force digPitch to match the synced targetDigPitch immediately
            // Don't use local deltaMovement as it may not match server's direction
            if (level().isClientSide) {
                // Directly set digPitch to targetDigPitch (synced from server)
                this.digPitch = getTargetDigPitch();
            }
            
            // SERVER SIDE: Handle the actual movement logic
            if (!level().isClientSide) {
                // Target is FIXED at returnTargetX/Y/Z - calculated when return mode started
                int targetWorldHeight = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) returnTargetX, (int) returnTargetZ);
                double horizontalDistance = Math.sqrt((returnTargetX - this.getX()) * (returnTargetX - this.getX()) + (returnTargetZ - this.getZ()) * (returnTargetZ - this.getZ()));
                
                // INITIAL BURST: Calculate and LOCK the movement direction at this moment
                if (returnNeedsInitialBurst) {
                    returnNeedsInitialBurst = false;
                    
                    // Calculate FIXED direction from current position to target - this will be used for entire return
                    Vec3 targetPos = new Vec3(returnTargetX, returnTargetY, returnTargetZ);
                    Vec3 directionToTarget = targetPos.subtract(this.position());
                    double distanceToTarget = directionToTarget.length();
                    returnMoveDirection = distanceToTarget > 0.1 ? directionToTarget.normalize() : new Vec3(0, -1, 0);
                    
                    // Calculate and LOCK target pitch and yaw based on fixed direction
                    float targetPitch = (float) (-(Mth.atan2(returnMoveDirection.y, returnMoveDirection.horizontalDistance()) * (180F / (float) Math.PI)));
                    float targetYaw = -((float) Mth.atan2(returnMoveDirection.x, returnMoveDirection.z)) * (180F / (float) Math.PI);
                    
                    // Strong initial velocity - directly SET the movement
                    this.setDeltaMovement(returnMoveDirection.scale(1.8F));
                    // Immediately face the target - SET directly, no gradual approach
                    this.setYRot(targetYaw);
                    // Directly set digPitch for instant head rotation
                    this.digPitch = targetPitch;
                    this.setTargetDigPitch(targetPitch);
                }
                
                // CONTINUOUS MOVEMENT: Use the FIXED direction calculated at initial burst
                // Don't recalculate direction - keep heading in the same direction
                if (returnMoveDirection != null) {
                    // DIRECT velocity set using FIXED direction
                    double speed = 1.2; // Fast constant speed
                    this.setDeltaMovement(returnMoveDirection.scale(speed));
                    
                    // FORCE the head pitch to stay locked - override the approachDegrees that ran earlier in tick()
                    float lockedPitch = (float) (-(Mth.atan2(returnMoveDirection.y, returnMoveDirection.horizontalDistance()) * (180F / (float) Math.PI)));
                    this.digPitch = lockedPitch;
                    this.setTargetDigPitch(lockedPitch);
                }
                
                // Check if we've reached the target (close enough horizontally AND underground)
                if (horizontalDistance <= 5 && this.getY() <= targetWorldHeight - 5) {
                    // Successfully reached target underground, exit return mode and let normal AI take over
                    setReturningToGround(false);
                    returnMoveDirection = null; // Clear fixed direction
                    this.noPhysics = false;
                    stuckAboveGobthumperTicks = 0;
                    stuckInAirTicks = 0;
                    forcingReturnToGobthumper = false;
                    detectedStuckState = false;
                    // Set postRidingDigDownTicks to ensure we stay underground initially
                    postRidingDigDownTicks = 60; // 3 seconds of staying underground before AI can move
                }
            }
            
            flag = true;
        } else if (!level().isClientSide && !isRidingMode() && postRidingDigDownTicks <= 0 && !isLeaping()) {
            // Only detect stuck state when NOT leaping - leaping worms are under Goal control and will naturally fall
            // IMPORTANT: Don't detect stuck state during postRidingDigDownTicks - worm is intentionally staying underground
            BlockPos gobthumperPos = getGobthumperPos();
            
            // Check if stuck motionless (position hasn't changed significantly)
            Vec3 currentPos = this.position();
            boolean isMotionless = false;
            if (lastTickPosition != null) {
                double movement = currentPos.distanceToSqr(lastTickPosition);
                // Consider motionless if moved less than 0.01 blocks squared
                isMotionless = movement < 0.01;
            }
            lastTickPosition = currentPos;
            
            // Check world surface height at current position
            int worldHeight = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) this.getX(), (int) this.getZ());
            
            // Determine if GumWorm is in an abnormal position
            boolean isAboveSurface = this.getY() > worldHeight;
            boolean isInUnsafePosition = !isSafeDig(level(), this.blockPosition());
            
            // Calculate target Y for return (gobthumper Y or 10 blocks below surface)
            double targetY = gobthumperPos != null ? gobthumperPos.getY() : (worldHeight - 10);
            
            if (gobthumperPos != null) {
                double heightAboveGobthumper = this.getY() - gobthumperPos.getY();
                // Detect stuck state if above gobthumper AND either:
                // 1. Above the world surface (in open air above ground)
                // 2. Inside a solid block (stuck in ceiling in a cave)
                // This ensures worms stuck on cave ceilings above the gobthumper are also detected
                boolean isInsideSolidBlock = centralState.isSolid();
                if (heightAboveGobthumper > 8.0 && (isAboveSurface || isInsideSolidBlock)) {
                    detectedStuckState = true;
                }
                
                // Keep counting if we've ever detected stuck state (don't reset when worm moves down)
                if (detectedStuckState) {
                    stuckAboveGobthumperTicks++;
                    // After 3 seconds (60 ticks) of being in stuck state, activate return to ground mode
                    if (stuckAboveGobthumperTicks > 60) {
                        // Calculate return target: 20-25 blocks away from gobthumper in random direction
                        double angle = random.nextDouble() * Math.PI * 2;
                        double distance = 20 + random.nextDouble() * 5; // 20-25 blocks
                        returnTargetX = gobthumperPos.getX() + Math.cos(angle) * distance;
                        returnTargetZ = gobthumperPos.getZ() + Math.sin(angle) * distance;
                        // Calculate target Y: 10 blocks below ground at target location
                        int targetGroundHeight = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) returnTargetX, (int) returnTargetZ);
                        returnTargetY = targetGroundHeight - 10;
                        
                        setReturningToGround(true);
                        returnNeedsInitialBurst = true; // Give strong initial push
                        forcingReturnToGobthumper = true;
                        this.setLeaping(false);
                    }
                }
            } else {
                // No gobthumper position - check if above surface
                if (isAboveSurface) {
                    detectedStuckState = true;
                }
                
                if (detectedStuckState) {
                    stuckAboveGobthumperTicks++;
                    if (stuckAboveGobthumperTicks > 60) {
                        // No gobthumper, just go straight down at current position
                        returnTargetX = this.getX();
                        returnTargetZ = this.getZ();
                        // Target: 10 blocks below ground at current location
                        returnTargetY = worldHeight - 10;
                        
                        setReturningToGround(true);
                        returnNeedsInitialBurst = true; // Give strong initial push
                        forcingReturnToGobthumper = true;
                        this.setLeaping(false);
                    }
                }
            }
            
            // Additional fallback: if stuck motionless anywhere for too long (regardless of position)
            // This is the ultimate safety net
            if (isMotionless && !forcingReturnToGobthumper) {
                stuckInAirTicks++;
                // After 3 seconds (60 ticks) of being completely stuck, activate return mode
                if (stuckInAirTicks > 60) {
                    if (gobthumperPos != null) {
                        // Calculate return target: 20-25 blocks away from gobthumper
                        double angle = random.nextDouble() * Math.PI * 2;
                        double distance = 20 + random.nextDouble() * 5;
                        returnTargetX = gobthumperPos.getX() + Math.cos(angle) * distance;
                        returnTargetZ = gobthumperPos.getZ() + Math.sin(angle) * distance;
                    } else {
                        returnTargetX = this.getX();
                        returnTargetZ = this.getZ();
                    }
                    // Target: 10 blocks below ground at target location
                    int targetGroundHeight = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) returnTargetX, (int) returnTargetZ);
                    returnTargetY = targetGroundHeight - 10;
                    
                    setReturningToGround(true);
                    returnNeedsInitialBurst = true; // Give strong initial push
                    forcingReturnToGobthumper = true;
                    detectedStuckState = true;
                    // Also clear leaping state
                    this.setLeaping(false);
                }
            } else if (!isMotionless) {
                // Don't fully reset counter - reduce by half so occasional small movements don't prevent trigger
                stuckInAirTicks = Math.max(0, stuckInAirTicks - 10);
            }
        } else {
            stuckAboveGobthumperTicks = 0;
            stuckInAirTicks = 0;
            detectedStuckState = false;
        }
        
        // After dismounting, force dig down into the ground instead of bouncing up
        if (postRidingDigDownTicks > 0) {
            postRidingDigDownTicks--;
            if (canDigBlock(centralStateBelow) || centralStateBelow.isAir()) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8).add(0, -0.6, 0));
                flag = true;
            }
        } else if (!forcingReturnToGobthumper && (!isSafeDig(level(), this.blockPosition())) && !this.isLeaping()) {
            // Skip the bounce-up logic if we're forcing return to gobthumper
            if (!canDigBlock(centralStateBelow)) {
                this.setDeltaMovement(random.nextFloat() - 0.5F, 0.8F, random.nextFloat() - 0.5F);
                flag = true;
            } else if((surfaceY < this.getEyeY() || centralStateBelow.isAir() || this.isInFluidType()) && isSafeDig(level(), this.blockPosition().below()) && !isRidingMode()){
                if(outOfGroundTime++ > 10){
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.5, 0));
                }
            }
        }else if (!forcingReturnToGobthumper) {
            outOfGroundTime = 0;
        }
        if(isRidingMode()){
            boolean flag1 = false;
            int worldHeight = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) this.getX(), (int) this.getZ());
            if(isTouchingBedrock()){
                noPhysics = false;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9).add(0, this.getY() < worldHeight - 6.0F ? 0.2F : -0.2F, 0));
            }else if((centralState.isSolid() || this.horizontalCollision || ridingPlayer != null && ridingPlayer.getY() > this.getY() + this.getBbHeight() + 4.0F) && !this.isLeaping()){
                float upOrDown = 0.4F;
                if(surfaceY < worldHeight + 1.0F){
                    upOrDown = 0.8F;
                }else if(this.getY() > level().getMinBuildHeight() + 3.0F){
                    upOrDown = -0.1F;
                }
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9).add(0, upOrDown, 0));
                flag = true;
                flag1 = !this.horizontalCollision;
            }
            if(!this.isLeaping() && !level().getBlockState(this.blockPosition().below()).isSolid() && !this.horizontalCollision){
                flag = true;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9).add(0, -0.8F, 0));
            }
            noPhysics = flag1;
        }
        if(wasDiggingLastTick != isDigging()){
            wasDiggingLastTick = isDigging();
            if(!isDigging()){
                attemptPlayStopDiggingNoise();
            }
        }
        // When forcing return to gobthumper or flag is set, always enable gravity to allow falling
        // Also enable gravity if above the ground surface (in the air) to prevent floating after leap ends
        boolean isAboveGroundLevel = this.getY() > surfaceY;
        this.setNoGravity(!forcingReturnToGobthumper && !isAboveGroundLevel && !this.getNavigation().isDone() && !this.isLeaping() && !flag && !this.isInWall());
        if (timeBetweenAttacks > 0) {
            timeBetweenAttacks--;
        }
        if (leapAttackCooldown > 0) {
            leapAttackCooldown--;
        }
        if(ridingModeTicks > 0){
            ridingModeTicks--;
        }
        if(recentlyLeaptTicks > 0 && !this.isLeaping()){
            recentlyLeaptTicks--;
        }
        if(forceMouthOpenTicks > 0){
            forceMouthOpenTicks--;
        }
        if(attackNoiseCooldown > 0){
            attackNoiseCooldown--;
        }
        if(stopDiggingNoiseCooldown > 0){
            stopDiggingNoiseCooldown--;
        }
    }

    private boolean isTouchingBedrock() {
        float f = 1.0F;
        float f1 = 1.0F;
        AABB aabb = this.getBoundingBox().inflate(f, f1, f);
        return BlockPos.betweenClosedStream(aabb).anyMatch((collisionShape) -> {
            BlockState blockstate = this.level().getBlockState(collisionShape);
            return blockstate.is(Blocks.BEDROCK);
        });
    }

    public boolean isDigLogic() {
        float f = 4.0F;
        float f1 = 4.0F;
        AABB aabb = this.getBoundingBox().inflate(f, f1, f);
        return BlockPos.betweenClosedStream(aabb).anyMatch((collisionShape) -> {
            BlockState blockstate = this.level().getBlockState(collisionShape);
            return blockstate.isSolid() && canDigBlock(blockstate);
        });
    }

    public void travel(Vec3 vec3d) {
        if (this.isEffectiveAi() || this.isVehicle()) {
            this.moveRelative(this.getSpeed(), vec3d);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            this.calculateEntityAnimation(false);
            this.setDeltaMovement(delta.scale(0.8D));
        } else {
            super.travel(vec3d);
        }
    }

    public void onMounted(){
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
        while (!level().getBlockState(mutableBlockPos).is(Blocks.BEDROCK) && !level().getBlockState(mutableBlockPos).isAir() && mutableBlockPos.getY() < level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos.getX(), mutableBlockPos.getZ())) {
            mutableBlockPos.move(0, 1, 0);
        }
        this.setPos(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public boolean isDigging() {
        return this.entityData.get(DIGGING);
    }

    @Override
    public float maxUpStep() {
        return isRidingMode() ? 5.0F : super.maxUpStep();
    }

    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel serverLevel, DamageSource damageSource) {
        this.captureDrops(new java.util.ArrayList<>());

        boolean flag = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropLoot() && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable(damageSource, flag);
            this.dropCustomDeathLoot(serverLevel, damageSource, flag);
        }

        this.dropEquipment();
        this.dropExperience(damageSource.getEntity());

        Collection<ItemEntity> drops = captureDrops(null);
        if (!net.neoforged.neoforge.common.CommonHooks.onLivingDrops(this, damageSource, drops, lastHurtByPlayerTime > 0)){
            drops.forEach(e -> dropItemAtSurface(e));
        }
    }

    private void dropItemAtSurface(ItemEntity itementity) {
        BlockPos pos = itementity.blockPosition();
        while (level().getBlockState(pos).isSolid() && pos.getY() < level().getMaxBuildHeight()){
            pos = pos.above();
        }
        itementity.setPos(itementity.getX(), pos.getY() + 0.2F, itementity.getZ());
        itementity.setGlowingTag(true);
        itementity.setDefaultPickUpDelay();
        level().addFreshEntity(itementity);
    }

    public boolean attackAllAroundMouth(float damageAmount, float knockbackAmount) {
        boolean attackedMainTarget = false;
        AABB hurtBox = this.getBoundingBox().inflate(this.isLeaping() ? 3.0D : 1.0D);
        Entity target = this.getTarget();
        DamageSource damageSource = this.damageSources().mobAttack(this);
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, hurtBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (!living.is(this) && !living.isAlliedTo(this) && !isRidingPlayer(living) && living.getType() != this.getType()) {
                if (living.hurt(damageSource, damageAmount)) {
                    living.knockback(knockbackAmount, this.getX() - living.getX(), this.getZ() - living.getZ());
                }
                if (target != null && living.is(target)) {
                    attackedMainTarget = true;
                }
            }
        }
        return attackedMainTarget;
    }

    public boolean isRidingPlayer(Entity player){
        Entity owner1 = getHook(true) instanceof CandyCaneHookEntity hookEntity ? hookEntity.getOwner() : null;
        Entity owner2 = getHook(false) instanceof CandyCaneHookEntity hookEntity ? hookEntity.getOwner() : null;
        return owner1 != null && owner2 != null && player.is(owner1);
    }

    public boolean hasARidingHook(){
        return getHook(true) != null || getHook(false) != null;
    }

    public boolean isRidingMode(){
        return ridingModeTicks > 0;
    }

    public Player getRidingPlayer(){
        return ridingPlayer;
    }

    public void spawnDustParticles(boolean surface) {
        if (surfaceY < this.getY(1.0F) && this.getY(1.0F) - surfaceY < 4) {
            BlockPos lightPos = BlockPos.containing(this.getX(), surfaceY - 1.0F, this.getZ());
            BlockState state = level().getBlockState(lightPos);
            if (!state.isAir()) {
                level().addParticle(new BlockParticleOption(ACParticleRegistry.BIG_BLOCK_DUST.get(), state), true, this.getRandomX(0.8F), surfaceY + random.nextFloat(), this.getRandomZ(0.8F), (random.nextFloat() - 0.5F) * 0.2F, (random.nextFloat() - 0.5F) * 0.2F, (random.nextFloat() - 0.5F) * 0.2F);
            }
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps) {
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
    public boolean getZRotDirection() {
        return this.entityData.get(Z_ROT_DIRECTION);
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean leaping) {
        this.entityData.set(LEAPING, leaping);
    }

    /**
     * Set a forced dig-down period to prevent bounce-up behavior.
     * Used after gobthumper destruction or dismounting.
     */
    public void setForceDigDownTicks(int ticks) {
        this.postRidingDigDownTicks = ticks;
    }

    public boolean isBiting() {
        return this.entityData.get(BITING);
    }

    public void setBiting(boolean biting) {
        this.entityData.set(BITING, biting);
    }

    public boolean isMoving() {
        return this.getDeltaMovement().length() > 0.1F;
    }

    public boolean isMouthOpen() {
        return this.isLeaping() || this.isBiting();
    }

    public void setTargetDigPitch(float pitch) {
        this.entityData.set(TARGET_DIG_PITCH, pitch);
    }

    public float getTargetDigPitch() {
        return this.entityData.get(TARGET_DIG_PITCH);
    }

    public boolean isReturningToGround() {
        return this.entityData.get(RETURNING_TO_GROUND);
    }

    public void setReturningToGround(boolean returning) {
        this.entityData.set(RETURNING_TO_GROUND, returning);
    }

    public BlockPos getGobthumperPos() {
        return this.entityData.get(GOBTHUMPER_POS).orElse(null);
    }

    public void setGobthumperPos(BlockPos gobthumperPos) {
        this.entityData.set(GOBTHUMPER_POS, Optional.ofNullable(gobthumperPos));
    }

    public boolean isTempSummon() {
        return this.entityData.get(TEMP_SUMMON);
    }

    public void setTempSummon(boolean tempSummon) {
        this.entityData.set(TEMP_SUMMON, tempSummon);
    }


    public boolean isValidRider() {
        return this.entityData.get(VALID_RIDER);
    }

    public Entity getRidingSegment() {
        if (!level().isClientSide) {
            final UUID id = getRidingSegmentUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(RIDING_SEGMENT_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (LEAPING.equals(dataAccessor)) {
            attemptPlayStopDiggingNoise();
            recentlyLeaptTicks = 15;
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    public void setRidingSegmentId(int id){
        this.entityData.set(RIDING_SEGMENT_ID, id);
    }
    
    @Nullable
    public UUID getRidingSegmentUUID() {
        return this.entityData.get(RIDING_SEGMENT_UUID).orElse(null);
    }

    public void setRidingSegmentUUID(@Nullable UUID uniqueId) {
        this.entityData.set(RIDING_SEGMENT_UUID, Optional.ofNullable(uniqueId));
    }

    public void setHookId(boolean left, int id){
        this.entityData.set(left ? LEFT_HOOK_ID : RIGHT_HOOK_ID, id);
    }

    public Entity getHook(boolean left){
        int id = this.entityData.get(left ? LEFT_HOOK_ID : RIGHT_HOOK_ID);
        return id == -1 ? null : level().getEntity(id);
    }

    public void setRidingLeapTime(int time){
        this.entityData.set(RIDER_LEAP_TIME, time);
    }

    public void setMaxRidingLeapTime(int time){
        this.entityData.set(RIDER_LEAP_TIME_MAX, time);
    }

    public int getRidingLeapTime(){
        return this.entityData.get(RIDER_LEAP_TIME);
    }

    public int getMaxRidingLeapTime(){
        return this.entityData.get(RIDER_LEAP_TIME_MAX);
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

    public double getSurfaceY() {
        return surfaceY;
    }


    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(level);
        }
        GumWormSegmentEntity.createWormSegmentsFor(this, 15 + random.nextInt(5));
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn);
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition().below();
        int downCount = 0;
        while (!world.getBlockState(down).isAir()  && downCount < 10 && down.getY() > world.getMinBuildHeight()) {
            down = down.below();
            downCount++;
        }
        this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        BlockPos gobthumperPos = getGobthumperPos();
        if (gobthumperPos != null) {
            compoundTag.putInt("GobthumperX", gobthumperPos.getX());
            compoundTag.putInt("GobthumperY", gobthumperPos.getY());
            compoundTag.putInt("GobthumperZ", gobthumperPos.getZ());
        }
        if (this.getRidingSegmentUUID() != null) {
            compoundTag.putUUID("RidingSegmentUUID", this.getRidingSegmentUUID());
        }
        compoundTag.putBoolean("TempSummon", this.isTempSummon());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("GobthumperX") && compoundTag.contains("GobthumperY") && compoundTag.contains("GobthumperZ")) {
            this.setGobthumperPos(new BlockPos(compoundTag.getInt("GobthumperX"), compoundTag.getInt("GobthumperY"), compoundTag.getInt("GobthumperZ")));
        }
        if (compoundTag.hasUUID("RidingSegmentUUID")) {
            this.setRidingSegmentUUID(compoundTag.getUUID("RidingSegmentUUID"));
        }
        this.setTempSummon(compoundTag.getBoolean("TempSummon"));
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockState, VoxelShape voxelShape) {
        return canDigBlock(blockState) && (!isRidingMode() || !level().getBlockState(blockPos.above()).isSolid() || !blockState.isSuffocating(level(), blockPos));
    }

    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return (!this.isDigging() || canDigBlock(blockstate)) && super.isColliding(pos, blockstate);
    }

    @Override
    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    public static boolean isSafeDig(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());
        return canDigBlock(state) && canDigBlock(below);
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

    public static boolean canDigBlock(BlockState state) {
        return !state.is(ACTagRegistry.GUM_WORM_BLOCKS_DIGGING) && state.getFluidState().isEmpty() && state.canOcclude();
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL) || damageSource.is(DamageTypes.CACTUS) || damageSource.is(DamageTypes.DROWN) || damageSource.is(DamageTypes.FALL) || damageSource.getEntity() != null && isRidingPlayer(damageSource.getEntity());
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        if (surfacePosition != null && prevSurfacePosition != null) {
            Vec3 difference = surfacePosition.subtract(prevSurfacePosition);
            return prevSurfacePosition.add(difference.scale(f)).add(0, this.getEyeHeight(), 0);
        }
        return super.getLightProbePosition(f);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public float getViewXRot(float partialTick) {
        return (prevDigPitch + (digPitch - prevDigPitch) * partialTick);
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(6);
    }

    public boolean canReach(BlockPos target) {
        Path path = this.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - target.getX();
                int j = node.y - target.getY();
                int k = node.z - target.getZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {

    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    public float getScreenShakeAmount(float partialTicks) {
        if (this.isLeaping() || !this.isMoving()) {
            return 0;
        }
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    public double getShakeDistance() {
        return 64F;
    }

    public Vec3 getHookPosition(int i) {
        Vec3 offset = new Vec3(i * -1.0F, -0.5F, -1.15F).xRot((float) -Math.toRadians(this.getXRot())).yRot((float) -Math.toRadians(this.yBodyRot));
        return this.position().add(0, 0.5F * this.getBbWidth(), 0).add(offset);
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GUM_WORM_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUM_WORM_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUM_WORM_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() * 3.0F;
    }

    public void tickController(Player passenger) {
        ridingPlayer = passenger;
        this.entityData.set(VALID_RIDER, isRidingPlayer(ridingPlayer));
        if(hasARidingHook()){
            ridingModeTicks = 10;
        }
    }

    public void onPlayerJump(int i) {
        int leapFor = (int) Math.ceil((float)i * 0.2F) + 10;
        this.setRidingLeapTime(leapFor);
        this.setMaxRidingLeapTime(leapFor);
    }

    public boolean recentlyLeapt(){
        return recentlyLeaptTicks > 0;
    }

    public void onRidingPlayerAttack() {
        forceMouthOpenTicks = 40;
        attemptPlayAttackNoise();
    }

    public boolean isMouthForcedOpen() {
        return forceMouthOpenTicks > 0;
    }

    public void attemptPlayAttackNoise() {
        if(attackNoiseCooldown == 0){
            this.playSound(ACSoundRegistry.GUM_WORM_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
            attackNoiseCooldown = 70;
        }
    }

    public void attemptPlayStopDiggingNoise() {
        if(stopDiggingNoiseCooldown == 0){
            this.playSound(ACSoundRegistry.GUM_WORM_DIG_STOP.get(), this.getSoundVolume(), this.getVoicePitch());
            stopDiggingNoiseCooldown = 10;
        }
    }

    class MoveController extends MoveControl {

        public MoveController() {
            super(GumWormEntity.this);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
                double d0 = vector3d.length();
                double width = mob.getBoundingBox().getSize();
                float digSpeed = 0.25F;
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * digSpeed / d0);
                boolean safeDig = isSafeDig(level(), BlockPos.containing(wantedX, Mth.clamp(this.wantedY, this.mob.getY() - 1.0, this.mob.getY() + 1.0), wantedZ));
                // In riding mode, allow movement even if destination is not safe to dig (surface travel)
                if (isSafeDig(level(), BlockPos.containing(wantedX, wantedY, wantedZ)) || GumWormEntity.this.isRidingMode()) {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(vector3d1).scale(0.9F));
                } else {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(0, 0.1, 0).scale(0.7F));
                    this.operation = Operation.WAIT;
                    this.mob.getNavigation().stop();
                }
                if (d0 < width * 0.15F) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width && !GumWormEntity.this.isLeaping()) {
                    mob.setYRot(Mth.approachDegrees(mob.getYRot(), -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI), 25));
                }
            }
        }
    }

    class Navigator extends FlyingPathNavigation {

        public Navigator(Mob mob, Level world) {
            super(mob, world);
        }

        public boolean isStableDestination(BlockPos blockPos) {
            return !this.level.isEmptyBlock(blockPos) && CorrodentEntity.isSafeDig(level, blockPos);
        }

        protected PathFinder createPathFinder(int i) {
            this.nodeEvaluator = new CorrodentEntity.DiggingNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, i);
        }

        protected double getGroundY(Vec3 vec3) {
            return vec3.y + this.mob.getBbHeight();
        }

        protected boolean canUpdatePath() {
            return true;
        }

        protected void followThePath() {
            Vec3 vector3d = this.getTempMobPos();
            this.maxDistanceToWaypoint = this.mob.getBbWidth();
            Vec3i vector3i = this.path.getNextNodePos();
            double d0 = Math.abs(this.mob.getX() - ((double) vector3i.getX() + 0.5D));
            double d1 = Math.abs(this.mob.getY() - (double) vector3i.getY());
            double d2 = Math.abs(this.mob.getZ() - ((double) vector3i.getZ() + 0.5D));
            boolean flag = d0 < (double) this.maxDistanceToWaypoint && d2 < (double) this.maxDistanceToWaypoint && d1 <= (double) this.maxDistanceToWaypoint;
            if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
                this.path.advance();
            }

            this.doStuckDetection(vector3d);
        }

        protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec31) {
            Vec3 vector3d = new Vec3(vec31.x, vec31.y + (double) this.mob.getBbHeight() * 0.5D, vec31.z);
            BlockHitResult result = this.level.clip(new ClipContext(vec3, vector3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
            return isSafeDig(level, result.getBlockPos());
        }

        private boolean shouldTargetNextNodeInDirection(Vec3 currentPosition) {
            if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
                return false;
            } else {
                Vec3 vector3d = Vec3.atBottomCenterOf(this.path.getNextNodePos());
                if(level.getBlockState(this.path.getNextNodePos()).isAir()){
                    return true;
                }
                if (!currentPosition.closerThan(vector3d, 2.0D)) {
                    return false;
                } else {
                    Vec3 vector3d1 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                    Vec3 vector3d2 = vector3d1.subtract(vector3d);
                    Vec3 vector3d3 = currentPosition.subtract(vector3d);
                    return vector3d2.dot(vector3d3) > 0.0D;
                }
            }
        }

    }
}
