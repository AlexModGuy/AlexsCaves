package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.ai.SemiAquaticPathNavigator;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.item.InkBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.DeepOneReaction;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public abstract class DeepOneBaseEntity extends PathfinderMob implements IAnimatedEntity {
    protected boolean isLandNavigator;
    private boolean hasSwimmingSize = false;
    private float fishPitch = 0;
    private float prevFishPitch = 0;
    private Player corneringPlayer;
    private int tradingLockedTime = 0;
    private Animation currentAnimation;
    private int animationTick;
    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(DeepOneBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> ALTAR_POS = SynchedEntityData.defineId(DeepOneBaseEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> SUMMONED = SynchedEntityData.defineId(DeepOneBaseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SUMMON_TIME = SynchedEntityData.defineId(DeepOneBaseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SOUNDS_ANGRY = SynchedEntityData.defineId(DeepOneBaseEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID summonerUUID = null;
    private ItemStack swappedItem = ItemStack.EMPTY;
    private float summonedProgress = 0;
    private float prevSummonedProgress = 0;

    private boolean spawnedLootItem = false;


    protected DeepOneBaseEntity(EntityType entityType, Level level) {
        super(entityType, level);
        this.xpReward = 8;
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWIMMING, false);
        this.entityData.define(SUMMONED, false);
        this.entityData.define(SUMMON_TIME, 0);
        this.entityData.define(SOUNDS_ANGRY, false);
        this.entityData.define(ALTAR_POS, Optional.empty());
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new VerticalSwimmingMoveControl(this, 0.8F, 10);
            this.navigation = createNavigation(level());
            this.isLandNavigator = false;
        }
    }

    public static boolean checkDeepOneSpawnRules(EntityType entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        if (!level.getFluidState(blockPos.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            boolean flag = level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, blockPos, randomSource) && (mobSpawnType == MobSpawnType.SPAWNER || level.getFluidState(blockPos).is(FluidTags.WATER));
            return randomSource.nextInt(110) == 0 && blockPos.getY() < level.getSeaLevel() - 80 && flag;
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

    public void tick() {
        super.tick();
        prevFishPitch = fishPitch;
        prevSummonedProgress = summonedProgress;
        boolean water = this.isInWaterOrBubble();
        if (water && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!water && !this.isLandNavigator) {
            switchNavigator(true);
        }
        float pitchTarget;
        if (isDeepOneSwimming()) {
            pitchTarget = (float) this.getDeltaMovement().y * getPitchScale();
            if (!level().isClientSide && this.getNavigation().isDone() && this.onGround()) {
                this.setDeepOneSwimming(false);
            }
        } else {
            pitchTarget = 0;
        }
        if (isSummoned()) {
            if (this.getSummonTime() > 0) {
                if (summonedProgress < 20.0F) {
                    if (summonedProgress == 0) {
                        if (!level().isClientSide) {
                            this.level().broadcastEntityEvent(this, (byte) 61);
                        }
                        this.playSound(ACSoundRegistry.MAGIC_CONCH_SUMMON.get());
                    }
                    summonedProgress++;
                }
            } else {
                if (summonedProgress > 0.0F) {
                    summonedProgress--;
                }
            }
            if (getSummonTime() > 0) {
                if (!level().isClientSide) {
                    setSummonTime(getSummonTime() - 1);
                }
                if (getSummonTime() == 0) {
                    if (!level().isClientSide) {
                        this.level().broadcastEntityEvent(this, (byte) 61);
                    }
                    this.playSound(ACSoundRegistry.MAGIC_CONCH_SUMMON.get());
                }
            } else {
                if (!level().isClientSide && this.summonedProgress <= 0) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
        if (!isSummoned() && summonedProgress > 0.0F) {
            summonedProgress--;
        }
        if (hasSwimmingBoundingBox()) {
            if (!hasSwimmingSize) {
                hasSwimmingSize = true;
                refreshDimensions();
            }
        } else {
            if (hasSwimmingSize) {
                hasSwimmingSize = false;
                refreshDimensions();
            }
        }
        if (tradingLockedTime > 0) {
            tradingLockedTime--;
        }
        if (!level().isClientSide && this.getAnimation() == getTradingAnimation() && this.getMainHandItem().is(ACTagRegistry.DEEP_ONE_BARTERS)) {
            BlockPos altarPos = getLastAltarPos();
            if (altarPos != null) {
                Vec3 center = Vec3.atCenterOf(altarPos);
                if (this.getAnimationTick() > getTradingAnimation().getDuration() - 10) {
                    if (level().getBlockEntity(altarPos) instanceof AbyssalAltarBlockEntity altar && !spawnedLootItem) {
                        List<ItemStack> possibles = generateBarterLoot();
                        ItemStack stack = possibles.isEmpty() ? ItemStack.EMPTY : possibles.get(0);
                        if (altar.getItem(0).isEmpty()) {
                            altar.setItem(0, stack);
                            this.level().broadcastEntityEvent(this, (byte) 68);
                            altar.onEntityInteract(this, false);
                        } else {
                            Vec3 vec3 = center.add(0, 0.5F, 0);
                            ItemEntity itemEntity = new ItemEntity(level(), vec3.x, vec3.y, vec3.z, stack);
                            level().addFreshEntity(itemEntity);
                        }
                        double advancementRange = 20.0D;
                        for (Player player : level().getNearbyPlayers(TargetingConditions.forNonCombat().range(advancementRange), this, this.getBoundingBox().inflate(advancementRange))) {
                            if (player.distanceTo(this) < advancementRange) {
                                ACAdvancementTriggerRegistry.DEEP_ONE_TRADE.triggerForEntity(player);
                            }
                        }
                        spawnedLootItem = true;
                    }
                    restoreSwappedItem();
                }
                this.lookAt(EntityAnchorArgument.Anchor.EYES, center);
            }
        }
        if (spawnedLootItem && this.getAnimation() != getTradingAnimation()) {
            spawnedLootItem = false;
        }
        fishPitch = Mth.approachDegrees(fishPitch, Mth.clamp((float) pitchTarget, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI), 5);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        this.updateNoActionTime();
        super.aiStep();
    }

    protected void updateNoActionTime() {
        float f = this.getLightLevelDependentMagicValue();
        if (f > 0.5F) {
            this.noActionTime += 2;
        }
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEvents.HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return -levelReader.getPathfindingCostFromLightLevels(blockPos);
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    private List<ItemStack> generateBarterLoot() {
        LootTable loottable = this.level().getServer().getLootData().getLootTable(getBarterLootTable());
        return loottable.getRandomItems((new LootParams.Builder((ServerLevel) this.level())).withParameter(LootContextParams.THIS_ENTITY, this).create(LootContextParamSets.PIGLIN_BARTER));
    }

    protected abstract ResourceLocation getBarterLootTable();

    protected boolean hasSwimmingBoundingBox() {
        return this.isDeepOneSwimming();
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

    public BlockPos getLastAltarPos() {
        return this.entityData.get(ALTAR_POS).orElse(null);
    }

    public boolean isSummoned() {
        return this.entityData.get(SUMMONED);
    }

    public void setSummoned(boolean bool) {
        this.entityData.set(SUMMONED, bool);
    }


    private int getSummonTime() {
        return this.entityData.get(SUMMON_TIME);
    }

    private void setSummonTime(int i) {
        this.entityData.set(SUMMON_TIME, i);
    }

    public void setLastAltarPos(BlockPos lastAltarPos) {
        this.entityData.set(ALTAR_POS, Optional.ofNullable(lastAltarPos));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        BlockPos lastAltarPos = getLastAltarPos();
        if (lastAltarPos != null) {
            compound.putInt("AltarX", lastAltarPos.getX());
            compound.putInt("AltarY", lastAltarPos.getY());
            compound.putInt("AltarZ", lastAltarPos.getZ());
        }
        if (!swappedItem.isEmpty()) {
            compound.put("SwappedItem", swappedItem.save(new CompoundTag()));
        }
        compound.putBoolean("ConchSummoned", this.isSummoned());
        if (summonerUUID != null) {
            compound.putUUID("ConchUUID", summonerUUID);
            compound.putInt("ConchTime", getSummonTime());
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AltarX") && compound.contains("AltarY") && compound.contains("AltarZ")) {
            this.setLastAltarPos(new BlockPos(compound.getInt("AltarX"), compound.getInt("AltarY"), compound.getInt("AltarZ")));
        }
        if (compound.contains("SwappedWeapon")) {
            swappedItem = ItemStack.of(compound.getCompound("SwappedWeapon"));
        }
        this.setSummoned(compound.getBoolean("ConchSummoned"));
        if (compound.contains("ConchUUID")) {
            this.summonerUUID = compound.getUUID("ConchUUID");
            this.setSummonTime(compound.getInt("ConchTime"));
        }
    }

    public void handleEntityEvent(byte b) {
        if (b == 61) {
            this.level().addParticle(ACParticleRegistry.BIG_SPLASH.get(), this.getX(), this.getY() + 0.5F, this.getZ(), this.getBbWidth() + 0.2F, 3, 0);

        } else if (b == 68) {
            BlockPos pos = getLastAltarPos();
            if (pos != null && level().getBlockEntity(pos) instanceof AbyssalAltarBlockEntity altarBlockEntity) {
                altarBlockEntity.onEntityInteract(this, false);
            }
        } else if (b == 69) {
            BlockPos pos = getLastAltarPos();
            if (pos != null && level().getBlockEntity(pos) instanceof AbyssalAltarBlockEntity altarBlockEntity) {
                altarBlockEntity.onEntityInteract(this, true);
                altarBlockEntity.setItem(0, ItemStack.EMPTY);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    public void setSummonedBy(LivingEntity player, int time) {
        this.setSummoned(true);
        summonerUUID = player.getUUID();
        setSummonTime(time);
    }

    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && (!this.isSummoned() || summonedProgress >= 20.0F);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            if (Double.isNaN(delta.y)) {
                delta = new Vec3(delta.x, 0, delta.z);
            }
            if (this.sinksWhenNotSwimming()) {
                if (!isDeepOneSwimming()) {
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

    public boolean isVisuallySwimming() {
        return isDeepOneSwimming();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isDeepOneSwimming() ? getSwimmingSize() : super.getDimensions(poseIn);
    }

    public DeepOneReaction getReactionTo(Player player) {
        if (isSummoned() && summonerUUID != null && summonerUUID.equals(player.getUUID())) {
            return DeepOneReaction.HELPFUL;
        }
        return DeepOneReaction.fromReputation(getReputationOf(player.getUUID()));
    }

    public int getReputationOf(UUID playerUUID) {
        if (!level().isClientSide) {
            ACWorldData worldData = ACWorldData.get(level());
            return worldData == null ? 0 : worldData.getDeepOneReputation(playerUUID);
        }
        return 0;
    }

    public void setReputationOf(UUID playerUUID, int amount) {
        if (!level().isClientSide) {
            ACWorldData worldData = ACWorldData.get(level());
            if (worldData != null) {
                worldData.setDeepOneReputation(playerUUID, amount);
            }
        }
    }

    public void addReputation(UUID playerUUID, int amount) {
        int prev = getReputationOf(playerUUID);
        DeepOneReaction newReaction = DeepOneReaction.fromReputation(amount + prev);
        setReputationOf(playerUUID, amount + prev);
        if (DeepOneReaction.fromReputation(prev) != newReaction) {
            Player player = level().getPlayerByUUID(playerUUID);
            if (player != null) {
                if (newReaction == DeepOneReaction.NEUTRAL) {
                    ACAdvancementTriggerRegistry.DEEP_ONE_NEUTRAL.triggerForEntity(player);
                }
                if (newReaction == DeepOneReaction.HELPFUL) {
                    ACAdvancementTriggerRegistry.DEEP_ONE_HELPFUL.triggerForEntity(player);
                }
                player.displayClientMessage(Component.translatable("entity.alexscaves.deep_one.reaction_" + newReaction.toString().toLowerCase(Locale.ROOT)), true);
            }
        }
    }

    public void setTradingLockedTime(int i) {
        tradingLockedTime = Math.max(i, tradingLockedTime);
    }

    public boolean isTradingLocked() {
        return tradingLockedTime > 0;
    }

    public EntityDimensions getSwimmingSize() {
        return this.getType().getDimensions().scale(this.getScale());
    }

    protected boolean sinksWhenNotSwimming() {
        return true;
    }

    public void setCorneredBy(Player player) {
        corneringPlayer = player;
    }

    public Player getCorneringPlayer() {
        return corneringPlayer;
    }

    public void calculateEntityAnimation(boolean flying) {
        if (isDeepOneSwimming()) {
            float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
            float f2 = Math.min(f1 * 6.0F, 1.0F);
            this.walkAnimation.update(f2, 0.4F);
        } else {
            super.calculateEntityAnimation(flying);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageValue) {
        boolean sup = super.hurt(damageSource, damageValue);
        if (sup && damageSource.getEntity() instanceof Player player && !level().isClientSide && !damageSource.is(ACTagRegistry.DEEP_ONE_IGNORES)) {
            int decrease = -5;
            if (!this.isAlive()) {
                decrease = -15;
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
        InkBombEntity inkBombEntity = new InkBombEntity(this.level(), this);
        Vec3 vec3 = player.getDeltaMovement();
        double d0 = player.getX() + vec3.x - this.getX();
        double d1 = player.getEyeY() + vec3.y - this.getEyeY();
        double d2 = player.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        inkBombEntity.shoot(d0, d1, d2, 0.5F + 0.2F * (float) d3, 12.0F);
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.getRandom().nextFloat() * 0.4F);
        this.level().addFreshEntity(inkBombEntity);
        this.addReputation(player.getUUID(), -1);
        return true;
    }

    public void startAttackBehavior(LivingEntity target) {

    }

    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    protected void checkAndDealMeleeDamage(LivingEntity target, float multiplier) {
        checkAndDealMeleeDamage(target, multiplier, 0.25F);
    }

    protected void checkAndDealMeleeDamage(LivingEntity target, float multiplier, float knockback) {
        if (this.hasLineOfSight(target) && this.distanceTo(target) < this.getBbWidth() + target.getBbWidth() + 5.0D) {
            float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
            target.hurt(damageSources().mobAttack(this), f);
            target.knockback(knockback * multiplier, this.getX() - target.getX(), this.getZ() - target.getZ());
            Entity entity = target.getVehicle();
            if (entity != null) {
                entity.setDeltaMovement(target.getDeltaMovement());
                entity.hurt(damageSources().mobAttack(this), f);
            }
        }
    }

    public abstract Animation getTradingAnimation();

    public boolean soundsAngry() {
        return this.entityData.get(SOUNDS_ANGRY);
    }

    public void setSoundsAngry(boolean angrySounding) {
        this.entityData.set(SOUNDS_ANGRY, angrySounding);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (entityIn instanceof DeepOneBaseEntity) {
            return true;
        } else if (entityIn instanceof Player) {
            return getReactionTo((Player) entityIn) == DeepOneReaction.HELPFUL || super.isAlliedTo(entityIn);
        } else {
            return super.isAlliedTo(entityIn);
        }
    }

    public void swapItemsForAnimation(ItemStack item) {
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            swappedItem = this.getItemInHand(InteractionHand.MAIN_HAND).copy();
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, item);

    }

    public void restoreSwappedItem() {
        this.setItemInHand(InteractionHand.MAIN_HAND, swappedItem);
    }

    public float getSummonProgress(float partialTicks) {
        return (prevSummonedProgress + (summonedProgress - prevSummonedProgress) * partialTicks) / 20F;
    }

    public void copyTarget(LivingEntity player) {
        LivingEntity priorTarget = this.getTarget();
        if (priorTarget == null || !priorTarget.isAlive()) {
            LivingEntity target = null;
            if (player.getLastHurtMob() != null) {
                target = player.getLastHurtMob();
            } else if (player.getLastHurtByMob() != null) {
                target = player.getLastHurtByMob();
            }
            if (target != null && target.isAlive() && !target.isAlliedTo(player) && !target.is(player) && !target.isAlliedTo(this) && !(target instanceof DeepOneBaseEntity)) {
                this.setTarget(target);
            }
        }

    }

    public class HurtByHostileTargetGoal extends HurtByTargetGoal {

        public HurtByHostileTargetGoal() {
            super(DeepOneBaseEntity.this, DeepOneBaseEntity.class);
            this.setAlertOthers();
        }

        protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions conditions) {
            if (target instanceof Player player && DeepOneBaseEntity.this.getReactionTo(player) == DeepOneReaction.HELPFUL) {
                return false;
            }
            return super.canAttack(target, conditions);
        }

        @Override
        protected void alertOthers() {
            double d0 = this.getFollowDistance();
            AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
            List<? extends Mob> list = this.mob.level().getEntitiesOfClass(DeepOneBaseEntity.class, aabb, EntitySelector.NO_SPECTATORS);
            Iterator iterator = list.iterator();

            while (true) {
                Mob mob;
                while (true) {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    mob = (Mob) iterator.next();
                    if (this.mob != mob && mob.getTarget() == null && (!(this.mob instanceof TamableAnimal) || ((TamableAnimal) this.mob).getOwner() == ((TamableAnimal) mob).getOwner()) && !mob.isAlliedTo(this.mob.getLastHurtByMob())) {

                        boolean flag = false;

                        if (DeepOneBaseEntity.class.isAssignableFrom(mob.getClass())) {
                            flag = true;
                            break;
                        }

                        if (!flag) {
                            break;
                        }
                    }
                }

                this.alertOther(mob, this.mob.getLastHurtByMob());
            }
        }

    }

    public abstract SoundEvent getAdmireSound();

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
            if (isDeepOneSwimming() || !DeepOneBaseEntity.this.isInWaterOrBubble()) {
                return super.getGroundY(vec3);
            } else {
                BlockPos blockpos = BlockPos.containing(vec3);
                return this.level.getFluidState(blockpos.below()).isEmpty() ? vec3.y : WalkNodeEvaluator.getFloorLevel(this.level, blockpos);
            }
        }
    }
}
