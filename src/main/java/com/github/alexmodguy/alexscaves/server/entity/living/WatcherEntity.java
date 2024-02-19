package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTarget3DGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.WatcherAttackGoal;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import com.github.alexmodguy.alexscaves.server.entity.util.WatcherPossessionAccessor;
import com.github.alexmodguy.alexscaves.server.message.PossessionKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class WatcherEntity extends Monster implements IAnimatedEntity, PossessesCamera {

    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(WatcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHADE_MODE = SynchedEntityData.defineId(WatcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> POSSESSED_ENTITY_UUID = SynchedEntityData.defineId(WatcherEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> POSSESSED_ENTITY_ID = SynchedEntityData.defineId(WatcherEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> POSSESSION_STRENGTH = SynchedEntityData.defineId(WatcherEntity.class, EntityDataSerializers.FLOAT);
    public static final Animation ANIMATION_ATTACK_0 = Animation.create(15);
    public static final Animation ANIMATION_ATTACK_1 = Animation.create(15);
    private Animation currentAnimation;
    private int animationTick;
    private float runProgress;
    private float prevRunProgress;
    private float shadeProgress;
    private float prevShadeProgress;
    private boolean isLandNavigator;
    private final PathNavigation groundNavigator;
    private final PathNavigation airNavigator;
    private BlockPos lastPossessionSite = null;
    private int lastPossessionTimestamp;
    private Entity prevPossessedEntity;
    private boolean isPossessionBreakable;
    private int possessedTimeout = 0;
    private static final String LAST_POSSESSED_TIME_IDENTIFIER = "alexscaves_last_possessed_time";

    private UUID previousPossessionUUID;

    public WatcherEntity(EntityType entityType, Level level) {
        super(entityType, level);
        groundNavigator = createNavigation(level);
        airNavigator = createShadeNavigation(level);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 4).add(Attributes.FOLLOW_RANGE, 256);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WatcherAttackGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 100));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, UnderzealotEntity.class, WatcherEntity.class, ForsakenEntity.class).setAlertOthers()));
        this.targetSelector.addGoal(2, new MobTarget3DGoal(this, Player.class, false, 10, this::canPossessTargetEntity));
    }

    protected PathNavigation createShadeNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = groundNavigator;
            this.isLandNavigator = true;
        } else {
            this.moveControl = new MoveController();
            this.navigation = airNavigator;
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(SHADE_MODE, false);
        this.entityData.define(POSSESSED_ENTITY_UUID, Optional.empty());
        this.entityData.define(POSSESSED_ENTITY_ID, -1);
        this.entityData.define(POSSESSION_STRENGTH, 0.0F);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public boolean isShadeMode() {
        return this.entityData.get(SHADE_MODE);
    }

    public void setShadeMode(boolean bool) {
        this.entityData.set(SHADE_MODE, bool);
    }

    public float getPossessionStrength(float partialTicks) {
        return this.entityData.get(POSSESSION_STRENGTH);
    }

    @Override
    public boolean instant() {
        return false;
    }

    public void setPossessionStrength(float possessionStrength) {
        this.entityData.set(POSSESSION_STRENGTH, possessionStrength);
    }

    @javax.annotation.Nullable
    public UUID getPossessedEntityUUID() {
        return this.entityData.get(POSSESSED_ENTITY_UUID).orElse(null);
    }


    public void setPossessedEntityUUID(@javax.annotation.Nullable UUID hologram) {
        this.entityData.set(POSSESSED_ENTITY_UUID, Optional.ofNullable(hologram));
    }

    public boolean isPossessionBreakable() {
        return isPossessionBreakable;
    }

    public Entity getPossessedEntity() {
        if (!level().isClientSide) {
            UUID id = getPossessedEntityUUID();
            return id == null ? null : ((ServerLevel) level()).getEntity(id);
        } else {
            int id = this.entityData.get(POSSESSED_ENTITY_ID);
            return id == -1 ? null : level().getEntity(id);
        }
    }

    public boolean canReach(LivingEntity target, boolean flying) {
        Path path = (flying ? airNavigator : groundNavigator).createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - target.getBlockX();
                int j = node.y - target.getBlockY();
                int k = node.z - target.getBlockZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

    public void tick() {
        super.tick();
        prevRunProgress = runProgress;
        prevShadeProgress = shadeProgress;
        Entity possessedEntity = getPossessedEntity();
        if (isRunning() && runProgress < 5F) {
            runProgress++;
        }
        if (!isRunning() && runProgress > 0F) {
            runProgress--;
        }
        if (isShadeMode() && shadeProgress < 5F) {
            shadeProgress++;
        }
        if (!isShadeMode() && shadeProgress > 0F) {
            shadeProgress--;
        }
        if (isShadeMode()) {
            this.fallDistance = 0;
            if (this.isLandNavigator) {
                switchNavigator(false);
            }
        } else {
            if (!this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if (possessedEntity instanceof Player) {
            this.yHeadRot = Mth.approachDegrees(this.yHeadRotO, yHeadRot, 3);
        }
        if (!level().isClientSide) {
            if (possessedEntity != null && possessedEntity.isAlive()) {
                double dist = possessedEntity.distanceTo(this);
                if (this.entityData.get(POSSESSED_ENTITY_ID) != possessedEntity.getId()) {
                    this.entityData.set(POSSESSED_ENTITY_ID, possessedEntity.getId());
                    this.setPossessionStrength(1.0F);
                }
                lastPossessionTimestamp = this.tickCount;
                if (possessedTimeout++ > 140) {
                    this.setPossessionStrength(Math.max(0, this.getPossessionStrength(1.0F) + 0.1F));
                }
                if (dist < 1 || stopPossession(possessedEntity) || !this.isAlive()) {
                    if(possessedEntity instanceof WatcherPossessionAccessor possessionAccessor){
                        possessionAccessor.setPossessedByWatcher(false);
                    }
                    if(possessedEntity instanceof Player player){
                        setLastPossessedTimeFor(player);
                    }
                    this.level().broadcastEntityEvent(this, (byte) 78);
                    this.setPossessedEntityUUID(null);
                    this.entityData.set(POSSESSED_ENTITY_ID, -1);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 77);
                    if (possessedEntity instanceof LivingEntity living) {
                        living.zza = 0;
                        living.yya = 0;
                        living.xxa = 0;
                    }
                }
            } else {
                if(possessedEntity != null || this.entityData.get(POSSESSED_ENTITY_ID) != -1){
                    this.level().broadcastEntityEvent(this, (byte) 78);
                }
                possessedTimeout = 0;
                this.entityData.set(POSSESSED_ENTITY_ID, -1);
            }
        } else if (possessedEntity instanceof LivingEntity living) {
            living.zza = 0;
            living.yya = 0;
            living.xxa = 0;
            if (this.getPossessionStrength(1.0F) == 0) {
                isPossessionBreakable = true;
            }
            if (living instanceof Player player && isPossessionBreakable) {
                player.jumping = false;
                Player clientSidePlayer = AlexsCaves.PROXY.getClientSidePlayer();
                if (AlexsCaves.PROXY.isKeyDown(-1) && player == clientSidePlayer) {
                    AlexsCaves.sendMSGToServer(new PossessionKeyMessage(this.getId(), player.getId(), 0));
                }
            }
            if (prevPossessedEntity != living) {
                isPossessionBreakable = false;
            }
            prevPossessedEntity = living;
        } else {
            prevPossessedEntity = null;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    private boolean stopPossession(Entity possessed) {
        float possessionStrength = getPossessionStrength(1.0F);
        this.setPossessionStrength(Math.max(0, possessionStrength - 0.05F));
        return possessionStrength >= 1.0F && possessedTimeout > 40;
    }

    public boolean canPossessTargetEntity(Entity entity) {
        if (entity instanceof Player player) {
            CompoundTag playerData = player.getPersistentData();
            CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (data != null) {
                long timeElapsed = level().getGameTime() - data.getLong(LAST_POSSESSED_TIME_IDENTIFIER);
                return timeElapsed >= AlexsCaves.COMMON_CONFIG.watcherPossessionCooldown.get();
            }
        }
        return true;
    }

    public void handleEntityEvent(byte b) {
        if (b == 77 || b == 78) {
            Entity possessedEntity = getPossessedEntity();
            if(possessedEntity == null && getPossessedEntityUUID() != null){
                possessedEntity = level().getPlayerByUUID(getPossessedEntityUUID());
            }
            if (possessedEntity instanceof Player player && player == AlexsCaves.PROXY.getClientSidePlayer()) {
                if (b == 77) {
                    if(AlexsCaves.COMMON_CONFIG.watcherPossession.get()){
                        AlexsCaves.PROXY.setRenderViewEntity(player, this);
                    }
                } else {
                    level().addParticle(ACParticleRegistry.WATCHER_APPEARANCE.get(), player.getX(), player.getEyeY(), player.getZ(), 0, 0, 0);
                    player.level().playSound(player, player.getX(), player.getY(), player.getZ(), ACSoundRegistry.WATCHER_SCARE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                    if(AlexsCaves.COMMON_CONFIG.watcherPossession.get()) {
                        AlexsCaves.PROXY.resetRenderViewEntity(player);
                    }
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 6.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public float getShadeAmount(float partialTick) {
        return (prevShadeProgress + (shadeProgress - prevShadeProgress) * partialTick) * 0.2F;
    }

    public float getRunAmount(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public boolean attemptPossession(LivingEntity living) {
        if (tickCount - lastPossessionTimestamp > 100 && (lastPossessionSite == null || lastPossessionSite.distSqr(this.blockPosition()) > 10) && canPossessTargetEntity(living)) {
            lastPossessionSite = this.blockPosition();
            lastPossessionTimestamp = tickCount;
            if (living instanceof Player player) {
                setLastPossessedTimeFor(player);
                ((WatcherPossessionAccessor)player).setPossessedByWatcher(true);
            }
            return true;
        }
        return false;
    }

    public static void setLastPossessedTimeFor(Player player){
        CompoundTag playerData = player.getPersistentData();
        CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
        if (data != null) {
            data.putLong(LAST_POSSESSED_TIME_IDENTIFIER, player.level().getGameTime());
            playerData.put(Player.PERSISTED_NBT_TAG, data);
        }
    }

    public void onPossessionKeyPacket(Entity keyPresser, int type) {
        Entity possessed = this.getPossessedEntity();
        if (possessed.equals(keyPresser)) {
            this.setPossessionStrength(this.getPossessionStrength(1.0F) + 0.07F);
        }
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
        return new Animation[]{ANIMATION_ATTACK_0, ANIMATION_ATTACK_1};
    }

    public static boolean checkWatcherSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(20) == 0;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.WATCHER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.WATCHER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.WATCHER_DEATH.get();
    }


    class MoveController extends MoveControl {
        private final Mob parentEntity;


        public MoveController() {
            super(WatcherEntity.this);
            this.parentEntity = WatcherEntity.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 ed = this.mob.getNavigation().getTargetPos().getCenter();
                // ((ServerLevel)mob.level).sendParticles(ParticleTypes.HEART, ed.x, ed.y, ed.z, 0, 0, 0, 0, 1);
                //((ServerLevel)mob.level).sendParticles(ParticleTypes.SNEEZE, wantedX, wantedY, wantedZ, 0, 0, 0, 0, 1);
                double d1 = this.wantedY - this.mob.getY();
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                LivingEntity attackTarget = parentEntity.getTarget();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.025D / d0).add(0.0D, 0.08 + (double) (d1 / d0) * 0.1D, 0.0D);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1));
                if (d0 < width * 0.8F) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width && attackTarget == null) {
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                    if (WatcherEntity.this.getTarget() != null) {
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    }
                }
            }
        }
    }
}
