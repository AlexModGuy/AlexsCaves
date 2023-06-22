package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GloomothFindLightGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.GloomothFlightGoal;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GloomothEntity extends PathfinderMob implements UnderzealotSacrifice {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(GloomothEntity.class, EntityDataSerializers.BOOLEAN);
    private float flyProgress;
    private float prevFlyProgress;
    private float flapAmount;
    private float prevFlapAmount;
    private float flightPitch = 0;
    private float prevFlightPitch = 0;
    private float flightRoll = 0;
    private float prevFlightRoll = 0;
    private boolean isLandNavigator;

    public BlockPos lightPos;

    private int refreshLightPosIn = 0;

    private boolean isBeingSacrificed = false;
    private int sacrificeTime = 0;

    public GloomothEntity(EntityType entityType, Level level) {
        super(entityType, level);
        switchNavigator(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveHelper(this);
            this.navigation = createNavigation(level());
            this.isLandNavigator = false;
        }
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new FlyingPathNavigation(this, worldIn) {
            public boolean isStableDestination(BlockPos blockPos) {
                return this.level.getBlockState(blockPos).isAir();
            }
        };
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GloomothFindLightGoal(this, 32));
        this.goalSelector.addGoal(2, new GloomothFlightGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.MAX_HEALTH, 4.0D);
    }

    @Override
    public void tick() {
        super.tick();
        prevFlyProgress = flyProgress;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        prevFlapAmount = flapAmount;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        float yMov = (float) this.getDeltaMovement().y;
        float xzMov = (float) this.getDeltaMovement().horizontalDistance();
        if (xzMov > 0) {
            if (flapAmount < 5F) {
                flapAmount += 1F;
            }
        } else if (xzMov <= 0.05F) {
            if (flapAmount > 0) {
                flapAmount -= 0.5F;
            }
        }
        if (isFlying()) {
            if (this.isLandNavigator) {
                switchNavigator(false);
            }
        } else {
            if (!this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if (lightPos != null && this.isAlive() && !level().isClientSide) {
            if (refreshLightPosIn-- < 0) {
                refreshLightPosIn = 40 + random.nextInt(100);
                if (this.distanceToSqr(Vec3.atCenterOf(lightPos)) >= 256 || !level().getBlockState(lightPos).is(ACTagRegistry.GLOOMOTH_LIGHT_SOURCES) || level().getLightEmission(lightPos) <= 0) {
                    lightPos = null;
                }
            }
        }
        tickRotation(yMov * 2.5F * -(float) (180F / (float) Math.PI));
        if (isBeingSacrificed && !level().isClientSide) {
            sacrificeTime--;
            if(sacrificeTime < 10){
                this.level().broadcastEntityEvent(this, (byte) 61);
            }
            if (sacrificeTime < 0) {
                if(this.isPassenger() && this.getVehicle() instanceof UnderzealotEntity underzealot){
                    underzealot.postSacrifice(this);
                    underzealot.triggerIdleDigging();
                }
                this.stopRiding();
                WatcherEntity watcherEntity = this.convertTo(ACEntityRegistry.WATCHER.get(), true);
                if(watcherEntity != null){
                    net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, watcherEntity);
                    watcherEntity.stopRiding();
                }
            }
        }
    }

    public void handleEntityEvent(byte b) {
        if (b == 61) {
            for(int i = 0; i < 1 + random.nextInt(4); i++){
                this.level().addParticle(ACParticleRegistry.UNDERZEALOT_EXPLOSION.get(), this.getRandomX(1), this.getRandomY(), this.getRandomZ(1), 0, 0, 0);
            }
        }else{
            super.handleEntityEvent(b);
        }
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

    public float getFlapAmount(float partialTick) {
        return (prevFlapAmount + (flapAmount - prevFlapAmount) * partialTick) * 0.2F;
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

    public float getFlightPitch(float partialTick) {
        return (prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick);
    }

    public float getFlightRoll(float partialTick) {
        return (prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2, 2, 2);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public static boolean isValidLightLevel(ServerLevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource) {
        if (levelAccessor.getBrightness(LightLayer.SKY, blockPos) > randomSource.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = levelAccessor.getLevel().isThundering() ? levelAccessor.getMaxLocalRawBrightness(blockPos, 10) : levelAccessor.getMaxLocalRawBrightness(blockPos);
            return lvt_3_1_ <= randomSource.nextInt(8);
        }
    }

    public static boolean canMonsterSpawnInLight(EntityType<GloomothEntity> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return isValidLightLevel(levelAccessor, blockPos, randomSource) && checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
    }

    public static <T extends Mob> boolean checkGloomothSpawnRules(EntityType<GloomothEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if(canMonsterSpawnInLight(entityType, iServerWorld, reason, pos, random)){
            BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
            above.set(pos);
            int k = 0;
            while(iServerWorld.isEmptyBlock(above) && above.getY() < iServerWorld.getMaxBuildHeight()){
                above.move(0, 1, 0);
                k++;
                if(k > 4){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void triggerSacrificeIn(int time) {
        isBeingSacrificed = true;
        sacrificeTime = time;
    }

    @Override
    public boolean isValidSacrifice(int distanceFromGround) {
        return distanceFromGround < 4;
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos above = this.blockPosition();
        int upBy = 3 + random.nextInt(5);
        int k = 0;
        while(world.isEmptyBlock(above) && above.getY() < level().getMaxBuildHeight() && k < upBy){
            above = above.above();
            k++;
        }
        this.setFlying(true);
        this.setPos(above.getX() + 0.5F, above.getY(), above.getZ() + 0.5F);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    class FlightMoveHelper extends MoveControl {

        public FlightMoveHelper(GloomothEntity gloomoth) {
            super(gloomoth);
        }

        public void tick() {
            int maxRotChange = 10;
            boolean flag = false;
            if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
                //Vec3 ed = this.mob.getNavigation().getTargetPos().getCenter();
                //((ServerLevel)mob.level).sendParticles(ParticleTypes.HEART, ed.x, ed.y, ed.z, 0, 0, 0, 0, 1);
                //((ServerLevel)mob.level).sendParticles(ParticleTypes.SNEEZE, wantedX, wantedY, wantedZ, 0, 0, 0, 0, 1);
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                double d4 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                d1 /= d3;
                this.mob.yBodyRot = this.mob.getYRot();
                float f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * 3);
                float rotBy = maxRotChange;
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, (double) f1 * d1 * 0.025D, 0.0D));
                if (d4 < this.mob.getBbWidth() + 1.4F) {
                    f1 *= 0.7F;
                    if (d4 < 0.3F) {
                        rotBy = 0;
                    } else {
                        rotBy = Math.max(40, maxRotChange);
                    }
                } else {
                    flag = true;
                }
                float f = (float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, rotBy));
                if (d3 > 0.3) {
                    this.mob.setSpeed(f1);
                    flag = true;
                } else {
                    this.mob.setSpeed(0.0F);
                }
            } else {
                this.mob.setSpeed(0.0F);
            }
            this.mob.setNoGravity(flag);
        }
    }


}
