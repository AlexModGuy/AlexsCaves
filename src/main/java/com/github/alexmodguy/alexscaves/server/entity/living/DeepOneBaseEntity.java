package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ai.SemiAquaticPathNavigator;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.item.InkBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.DeepOneReaction;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class DeepOneBaseEntity extends Monster implements IAnimatedEntity {
    private boolean isLandNavigator;
    private boolean hasSwimmingSize = false;
    private float fishPitch = 0;
    private float prevFishPitch = 0;
    private Player corneringPlayer;

    private Animation currentAnimation;
    private int animationTick;
    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(DeepOneBaseEntity.class, EntityDataSerializers.BOOLEAN);

    protected final Predicate<LivingEntity> playerTargetPredicate = (player) -> {
        return player instanceof Player && DeepOneBaseEntity.this.getReactionTo((Player)player) == DeepOneReaction.AGGRESSIVE;
    };

    protected DeepOneBaseEntity(EntityType entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWIMMING, false);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new VerticalSwimmingMoveControl(this, 0.8F, 10);
            this.isLandNavigator = false;
        }
    }

    public static boolean checkDeepOneSpawnRules(EntityType entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        if (!level.getFluidState(blockPos.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            boolean flag = level.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(level, blockPos, randomSource) && (mobSpawnType == MobSpawnType.SPAWNER || level.getFluidState(blockPos).is(FluidTags.WATER));
            return randomSource.nextInt(60) == 0 && blockPos.getY() < level.getSeaLevel() - 80 && flag;
        }
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new PathNavigator(worldIn);
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    public void tick(){
        super.tick();
        prevFishPitch = fishPitch;
        boolean water = this.isInWaterOrBubble();
        if (water && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!water && !this.isLandNavigator) {
            switchNavigator(true);
        }
        float pitchTarget;
        if(isDeepOneSwimming()){
            pitchTarget = (float) this.getDeltaMovement().y * getPitchScale();
            if (!hasSwimmingSize) {
                hasSwimmingSize = true;
                refreshDimensions();
            }
            if(!level.isClientSide && this.getNavigation().isDone() && this.onGround){
                this.setDeepOneSwimming(false);
            }
        }else{
            pitchTarget = 0;
            if (hasSwimmingSize) {
                hasSwimmingSize = false;
                refreshDimensions();
            }
        }
        fishPitch = Mth.approachDegrees(fishPitch, Mth.clamp((float) pitchTarget, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI), 5);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private float getPitchScale() {
        return 2F;
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    public boolean isDeepOneSwimming() {
        return this.entityData.get(SWIMMING);
    }

    public void setDeepOneSwimming(boolean bool) {
        this.entityData.set(SWIMMING, bool);
    }


    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            if(Double.isNaN(delta.y)){
                delta = new Vec3(delta.x, 0, delta.z);
            }
            if (this.sinksWhenNotSwimming()) {
                if(!isDeepOneSwimming()){
                    delta = delta.scale(0.8D);
                    if (this.jumping || horizontalCollision) {
                        delta = delta.add(0, 0.1F, 0);
                    } else {
                        delta = delta.add(0, -0.05F, 0);
                    }
                }
            }
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.8D));
        } else {
            super.travel(travelVector);
        }
    }

    public boolean isVisuallySwimming(){
        return isDeepOneSwimming();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isDeepOneSwimming() ? getSwimmingSize() : super.getDimensions(poseIn);
    }

    public DeepOneReaction getReactionTo(Player player){
        return DeepOneReaction.fromReputation(getReputationOf(player.getUUID()));
    }

    public int getReputationOf(UUID playerUUID){
        if(true) {//TODO
            return 0;
        }
        if(!level.isClientSide){
            ACWorldData worldData = ACWorldData.get(level);
            return worldData == null ? 0 : worldData.getDeepOneReputation(playerUUID);
        }
        return 0;
    }

    public void setReputationOf(UUID playerUUID, int amount){
        if(!level.isClientSide){
            ACWorldData worldData = ACWorldData.get(level);
            if(worldData != null){
                worldData.setDeepOneReputation(playerUUID, amount);
            }
        }
    }

    public void addReputation(UUID playerUUID, int amount) {
        setReputationOf(playerUUID, amount + getReputationOf(playerUUID));
    }

    public EntityDimensions getSwimmingSize(){
        return this.getType().getDimensions().scale(this.getScale());
    }

    protected boolean sinksWhenNotSwimming() {
        return true;
    }

    public void setCorneredBy(Player player){
        corneringPlayer = player;
    }

    public Player getCorneringPlayer(){
        return corneringPlayer;
    }

    public void calculateEntityAnimation(LivingEntity living, boolean flying) {
        if(isDeepOneSwimming()){
            living.animationSpeedOld = living.animationSpeed;
            double d0 = living.getX() - living.xo;
            double d1 = living.getY() - living.yo;
            double d2 = living.getZ() - living.zo;
            float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 6.0F;
            if (f > 1.0F) {
                f = 1.0F;
            }
            living.animationSpeed += (f - living.animationSpeed) * 0.4F;
            living.animationPosition += living.animationSpeed;
        }else{
            super.calculateEntityAnimation(living, flying);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageValue) {
        boolean sup = super.hurt(damageSource, damageValue);
        if (sup && damageSource.getEntity() instanceof Player player && !level.isClientSide) {
            int decrease = getReactionTo(player) == DeepOneReaction.HELPFUL ? -1 : -5;
            if(!this.isAlive()){
                decrease *= 5;
            }
            this.addReputation(player.getUUID(), decrease);
        }
        return sup;
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

    public boolean startDisappearBehavior(Player player) {
        InkBombEntity inkBombEntity = new InkBombEntity(this.level, this);
        Vec3 vec3 = player.getDeltaMovement();
        double d0 = player.getX() + vec3.x - this.getX();
        double d1 = player.getEyeY() + vec3.y - this.getEyeY();
        double d2 = player.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        inkBombEntity.shoot(d0,d1, d2, 0.5F + 0.2F * (float) d3, 12.0F);
        this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.getRandom().nextFloat() * 0.4F);
        this.level.addFreshEntity(inkBombEntity);
        this.addReputation(player.getUUID(), -1);
        return true;
    }

    public void startAttackBehavior(LivingEntity target) {

    }

    public HumanoidArm getMainArm(){
        return HumanoidArm.RIGHT;
    }

    protected void checkAndDealMeleeDamage(LivingEntity target, float multiplier) {
        if (this.hasLineOfSight(target) && this.distanceTo(target) < this.getBbWidth() + target.getBbWidth() + 5.0D) {
            float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
            target.hurt(DamageSource.mobAttack(this), f);
            target.knockback(0.25D * multiplier, this.getX() - target.getX(), this.getZ() - target.getZ());
            Entity entity = target.getVehicle();
            if(entity != null){
                entity.setDeltaMovement(target.getDeltaMovement());
                entity.hurt(DamageSource.mobAttack(this),f);
            }
        }
    }

    public class HurtByHostileTargetGoal extends HurtByTargetGoal {

        public HurtByHostileTargetGoal() {
            super(DeepOneBaseEntity.this, DeepOneBaseEntity.class);
            this.setAlertOthers();
        }

        protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions conditions) {
            if(target instanceof Player player && DeepOneBaseEntity.this.getReactionTo(player) == DeepOneReaction.HELPFUL){
                return false;
            }
            return super.canAttack(target, conditions);
        }
    }

    private class PathNavigator extends SemiAquaticPathNavigator {
        public PathNavigator(Level worldIn) {
            super(DeepOneBaseEntity.this, worldIn);
        }

        @Override
        protected Vec3 getTempMobPos() {
            return new Vec3(this.mob.getX(), this.mob.getY(0.5D), this.mob.getZ());
        }

        @Override
        protected double getGroundY(Vec3 vec3) {
            if(isDeepOneSwimming() || !DeepOneBaseEntity.this.isInWaterOrBubble()){
                return super.getGroundY(vec3);
            }else{
                BlockPos blockpos = new BlockPos(vec3);
                return this.level.getFluidState(blockpos.below()).isEmpty() ? vec3.y : WalkNodeEvaluator.getFloorLevel(this.level, blockpos);
            }
        }
    }
}
