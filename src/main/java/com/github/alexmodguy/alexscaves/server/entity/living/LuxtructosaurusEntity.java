package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.FissurePrimalMagmaBlock;
import com.github.alexmodguy.alexscaves.server.entity.ai.LookForwardsGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.LuxtructosaurusMeleeGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTarget3DGoal;
import com.github.alexmodguy.alexscaves.server.entity.item.TephraEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.ACBossEvent;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.message.UpdateBossEruptionStatus;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LuxtructosaurusEntity extends SauropodBaseEntity implements Enemy {

    private static final EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.defineId(LuxtructosaurusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final VoronoiGenerator VORONOI_GENERATOR = new VoronoiGenerator(42L);
    private float prevEnragedProgress = 0;
    private float enragedProgress = 0;
    public Vec3 jumpTarget = null;
    public int enragedFor = 0;
    private BlockPos lastStompPos;
    private int postStopTicks;
    private boolean stompMakesFissures;
    private boolean prevOnGround;
    private final ACBossEvent bossEvent = (ACBossEvent) new ACBossEvent(this.getDisplayName(), 0).setPlayBossMusic(true);
    private int reducedDamageTicks;
    private boolean collectedLoot = false;
    private List<ItemStack> deathItems = new ArrayList<>();
    private int lastScareTimestamp;

    public LuxtructosaurusEntity(EntityType entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        VORONOI_GENERATOR.setOffsetAmount(1.0F);
        VORONOI_GENERATOR.setDistanceType(VoronoiGenerator.DistanceType.euclidean);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LuxtructosaurusMeleeGoal(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 10) {
            protected Vec3 getPosition() {
                return DefaultRandomPos.getPos(this.mob, 30, 7);
            }
        });
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 30.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Mob.class, 10.0F));
        this.goalSelector.addGoal(6, new LookForwardsGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, LuxtructosaurusEntity.class)));
        this.targetSelector.addGoal(2, new MobTarget3DGoal(this, Player.class, false));
        this.targetSelector.addGoal(3, new MobTarget3DGoal(this, DinosaurEntity.class, false, 200, dinosaur -> !(dinosaur instanceof LuxtructosaurusEntity)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENRAGED, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.325D).add(Attributes.MAX_HEALTH, 600.0D).add(Attributes.ARMOR, 20.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 12).add(Attributes.FOLLOW_RANGE, 256D);
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
    }

    public void setCustomName(@javax.annotation.Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public BlockState createEggBlockState() {
        return Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mob) {
        return null;
    }


    public void tick() {
        super.tick();

        this.prevEnragedProgress = enragedProgress;
        if (this.isEnraged() && enragedProgress < 20.0F) {
            enragedProgress++;
        }
        if (!this.isEnraged() && enragedProgress > 0.0F) {
            enragedProgress--;
        }

        if (level().isClientSide) {
            if (this.isAlive()) {
                if (this.isEnraged()) {
                    if (random.nextInt(8) == 0) {
                        level().addParticle(ACParticleRegistry.LUXTRUCTOSAURUS_SPIT.get(), this.getX(), this.getY() + 0.5F, this.getZ(), this.getId(), 0, 0);
                    }
                    if (this.getAnimation() == ANIMATION_RIGHT_WHIP || this.getAnimation() == ANIMATION_LEFT_WHIP) {
                        float tailPitch = tailPart1.calculateAnimationAngle(1.0F, true) + tailPart2.calculateAnimationAngle(1.0F, true) + tailPart3.calculateAnimationAngle(1.0F, true);
                        float tailYaw = this.yBodyRot + tailPart1.calculateAnimationAngle(1.0F, false) + tailPart2.calculateAnimationAngle(1.0F, false) + tailPart3.calculateAnimationAngle(1.0F, false);
                        Vec3 tailOffset = rotateOffsetVec(new Vec3((random.nextFloat() - 0.5F) * 0.1F, 0.5F + (random.nextFloat() - 0.5F) * 0.2F, -2 + (random.nextFloat() - 0.5F) * 2), tailPitch, tailYaw);
                        Vec3 tailCenter = this.tailPart3.centeredPosition().add(tailOffset);
                        level().addParticle(ACParticleRegistry.TEPHRA_FLAME.get(), tailCenter.x, tailCenter.y, tailCenter.z, (random.nextFloat() - 0.5F) * 0.1F, random.nextFloat() * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
                    }
                }
                if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() > 10F && this.getAnimationTick() < 70F) {
                    Vec3 headPos = this.headPart.centeredPosition().add((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
                    float flameRot = this.yBodyRot + (neckPart1.calculateAnimationAngle(1.0F, false) + neckPart2.calculateAnimationAngle(1.0F, false) + neckPart3.calculateAnimationAngle(1.0F, false)) / 3F;
                    for (int i = -3; i <= 3; i++) {
                        Vec3 flameDelta = rotateOffsetVec(new Vec3(0, random.nextFloat() * 0.2F - 0.1F, random.nextFloat() * 0.5F + 0.5F), (random.nextFloat() - 0.5F) * 5F, 180 + flameRot + (i * 10));
                        level().addParticle(ACParticleRegistry.TEPHRA_FLAME.get(), headPos.x, headPos.y, headPos.z, flameDelta.x, flameDelta.y, flameDelta.z);
                    }
                }
                if (this.getAnimation() == ANIMATION_JUMP && this.getAnimationTick() > 25 && this.onGround()) {
                    if (this.getAnimationTick() > 25 && this.screenShakeAmount < 3.0F) {
                        this.screenShakeAmount = 3.0F;
                    }
                }
            }
        } else {
            if (this.getAnimation() == ANIMATION_JUMP) {
                if (this.getAnimationTick() >= 15 && this.getAnimationTick() <= 25 && jumpTarget != null) {
                    Vec3 vec3 = this.getDeltaMovement();
                    Vec3 vec31 = new Vec3(this.jumpTarget.x - this.getX(), 0.0D, this.jumpTarget.z - this.getZ());
                    if (vec31.length() > 250) {
                        vec31 = vec3.normalize().scale(250);
                    }
                    if (vec31.lengthSqr() > 1.0E-7D) {
                        vec31 = vec31.scale(0.155F).add(vec3.scale(0.2D));
                    }
                    this.setDeltaMovement(vec31.x, (10 - Math.min(this.getAnimationTick() - 10, 10)) * 0.2F + (double) vec31.length() * 0.3F, vec31.z);
                } else {
                    if (this.onGround() && !prevOnGround) {
                        this.hurtEntitiesAround(this.position(), 10.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F, 2.0F, false, false);
                    }
                    this.setDeltaMovement(this.getDeltaMovement().subtract(0, 0.2, 0));
                }
            }
            if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() >= 10 && this.getAnimationTick() < 55 && this.getAnimationTick() % 5 == 0 && this.isAlive()) {
                BlockPos tephraSpawnAt = this.blockPosition().offset(random.nextInt(20) - 10, 2, random.nextInt(20) - 10);
                while (tephraSpawnAt.getY() < Math.min(level().getMaxBuildHeight(), this.getBlockY() + 100) && !level().getBlockState(tephraSpawnAt).isSolid()) {
                    tephraSpawnAt = tephraSpawnAt.above();
                }
                tephraSpawnAt = tephraSpawnAt.below();
                TephraEntity tephra = new TephraEntity(level(), this);
                tephra.setPos(tephraSpawnAt.getCenter());
                tephra.setMaxScale(1F + 2F * level().random.nextFloat());
                Vec3 targetVec = new Vec3(level().random.nextFloat() - 0.5F, -1, level().random.nextFloat() - 0.5F).normalize().scale(level().random.nextInt(20) + 20);
                tephra.shoot(targetVec.x, targetVec.y, targetVec.z, 5 + level().random.nextFloat() * 2F, 1 + level().random.nextFloat() * 0.5F);
                level().addFreshEntity(tephra);
            }
            if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 30 && postStopTicks <= 0) {
                stompMakesFissures = this.isEnraged();
                postStopTicks = stompMakesFissures ? 15 : 50;
                this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_STOMP.get(), 3.0F, 1.0F);
                lastStompPos = this.blockPosition();
                this.hurtEntitiesAround(this.position(), 10.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.2F, 2.0F, false, false);
            }
            if (this.getAnimation() == ANIMATION_RIGHT_KICK && this.getAnimationTick() == 8) {
                Vec3 armPos = this.position().add(rotateOffsetVec(new Vec3(-2, 0, 2.5F), 0, this.yBodyRot));
                this.hurtEntitiesAround(armPos, 5.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 2.0F, false, false);
            }
            if (this.getAnimation() == ANIMATION_LEFT_KICK && this.getAnimationTick() == 8) {
                Vec3 armPos = this.position().add(rotateOffsetVec(new Vec3(2, 0, 2.5F), 0, this.yBodyRot));
                this.hurtEntitiesAround(armPos, 5.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 2.0F, false, false);
            }
            if ((this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) && this.getAnimationTick() > 20 && this.getAnimationTick() < 30) {
                this.hurtEntitiesAround(this.tailPart2.position(), 12.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE), 2.0F, this.isEnraged(), true);
            }
            if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() > 10 && this.getAnimationTick() < 70) {
                burnWithBreath(13);
            }
            if (postStopTicks > 0) {
                postStopTicks--;
                if (this.screenShakeAmount < 3.0F) {
                    this.screenShakeAmount = 3.0F;
                }
                tickStompAttack();
            }
            if (reducedDamageTicks > 0) {
                reducedDamageTicks--;
            }
            if (!this.isNoAi() && tickCount % 10 == 0) {
                LivingEntity target = this.getTarget();
                Player closestPlayer = level().getNearestPlayer(this, 150.0D);
                if (target != null && target.isAlive() && !(target instanceof Player) && closestPlayer == null) {
                    this.heal(6);
                }
            }
        }
        if (!this.isNoAi()) {
            if (tickCount % 60 == 0 && this.getAnimation() != ANIMATION_ROAR && this.isAlive()) {
                if (this.level().isClientSide) {
                    Vec3 headCenter = this.headPart.centeredPosition();
                    Vec3 nostilRightDelta = rotateOffsetVec(new Vec3(-0.25F, 0.5F, 0.75F), this.getXRot(), this.getYHeadRot());
                    Vec3 nostilRight = headCenter.add(nostilRightDelta);
                    Vec3 nostilLeftDelta = rotateOffsetVec(new Vec3(0.25F, 0.5F, 0.75F), this.getXRot(), this.getYHeadRot());
                    Vec3 nostilLeft = headCenter.add(nostilLeftDelta);
                    nostilRightDelta = nostilRightDelta.scale(0.1F);
                    nostilLeftDelta = nostilLeftDelta.scale(0.1F);
                    ParticleOptions types = ACParticleRegistry.TEPHRA_SMALL.get();
                    level().addParticle(types, nostilRight.x, nostilRight.y, nostilRight.z, nostilRightDelta.x, nostilRightDelta.y, nostilRightDelta.z);
                    level().addParticle(types, nostilLeft.x, nostilLeft.y, nostilLeft.z, nostilLeftDelta.x, nostilLeftDelta.y, nostilLeftDelta.z);
                }
                this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_SNORT.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() > 10 && this.getAnimationTick() < 50) {
                if (!level().isClientSide) {
                    scareMobs();
                }
            }
        }
        if (this.getAnimation() == ANIMATION_SUMMON && this.getAnimationTick() > 5 && !this.hasEffect(MobEffects.INVISIBILITY)) {
            this.setInvisible(false);
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_SUMMON.get(), 3.0F, 1.0F);
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 2 && this.isAlive()) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ROAR.get(), 5.0F, this.getVoicePitch());
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() == 10) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_BREATH.get(), 5.0F, this.getVoicePitch());
        }
        prevOnGround = this.onGround();
    }

    protected void tickDeath() {
        if(deathTime <= 0){
            this.toggleServerEruptionStatus(false);
        }
        this.deathTime++;
        this.setAnimation(ANIMATION_EPIC_DEATH);
        this.setEnraged(true);
        screenShakeAmount = 0;
        this.setXRot(0.0F);
        this.setYHeadRot(this.getYRot());
        if (!level().isClientSide) {
            if (!collectedLoot) {
                populateDeathLootForLuxtructosaurus();
            }
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH) {
            if (this.getAnimationTick() >= 100 && this.getAnimationTick() <= 110 && level().isClientSide) {
                for (int i = 0; i < 50; i++) {
                    level().addAlwaysVisibleParticle(ACParticleRegistry.LUXTRUCTOSAURUS_ASH.get(), true, this.getX(), this.getY(), this.getZ(), this.getId(), 0, 0);
                }
            }
            if (this.getAnimationTick() > 110 && !this.level().isClientSide() & !this.isRemoved()) {
                if (!deathItems.isEmpty()) {
                    ItemStack currentStack = ItemStack.EMPTY;
                    while (!deathItems.isEmpty() || !currentStack.isEmpty()) {
                        if (currentStack.isEmpty()) {
                            currentStack = deathItems.remove(0);
                        }
                        if (currentStack.getCount() > 0) {
                            ItemStack one = currentStack.copy();
                            one.setCount(1);
                            currentStack.shrink(1);
                            spawnAtLocation(one);
                        }
                    }

                }
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }

    }

    private void populateDeathLootForLuxtructosaurus() {
        ResourceLocation resourcelocation = this.getLootTable();
        DamageSource damageSource = getLastDamageSource();
        if (damageSource != null) {
            LootTable loottable = this.level().getServer().getLootData().getLootTable(resourcelocation);
            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) this.level())).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, damageSource).withOptionalParameter(LootContextParams.KILLER_ENTITY, damageSource.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, damageSource.getDirectEntity());
            if (this.lastHurtByPlayer != null) {
                lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
            }
            LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
            loottable.getRandomItems(lootparams, this.getLootTableSeed(), deathItems::add);
        }
        collectedLoot = true;
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack) {
        ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY() + (double) 1, this.getZ(), stack);
        if (itementity != null) {
            Vec3 centerOfMob = this.position().add(3.0F - 6.0F * random.nextFloat(), 4, 3.0F - 6.0F * random.nextFloat());
            Vec3 randomDelta = new Vec3(1.0F - random.nextFloat(), 0, 1.0F - random.nextFloat()).normalize().scale(random.nextFloat() * 0.4F + 0.4F).add(0, 0.2, 0);
            itementity.setPos(centerOfMob);
            itementity.setDeltaMovement(randomDelta);
            itementity.setGlowingTag(true);
            itementity.setDefaultPickUpDelay();
            itementity.setUnlimitedLifetime();
        }
        level().addFreshEntity(itementity);
        return itementity;
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean b) {

    }

    protected float getSoundVolume() {
        return 3.0F;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            if (enragedFor > 0) {
                enragedFor--;
            } else if (this.isEnraged()) {
                if (this.getHealth() >= Math.ceil(this.getMaxHealth() * 0.25F)) {
                    this.setEnraged(false);
                }
            }
            if ((this.horizontalCollision || this.isInWater()) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                AABB aabb = this.getBoundingBox().inflate(0.2D);
                if (this.getAnimation() == ANIMATION_JUMP && this.getAnimationTick() > 24 && this.onGround()) {
                    return;
                }
                for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX - 1), Mth.floor(aabb.minY - 1), Mth.floor(aabb.minZ - 1), Mth.ceil(aabb.maxX + 1), Mth.ceil(aabb.maxY + 2.0F), Mth.ceil(aabb.maxZ + 1))) {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (blockstate.is(ACTagRegistry.LUXTRUCTOSAURUS_BREAKS)) {
                        this.level().destroyBlock(blockpos, true, this);
                    }
                    if (blockstate.getFluidState().is(FluidTags.WATER)) {
                        level().setBlock(blockpos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(level(), blockpos, blockpos, Blocks.STONE.defaultBlockState()), 3);
                        level().levelEvent(1501, blockpos, 0);
                    }
                }
            }
        }
    }

    @Override
    protected void onStep() {
        if (screenShakeAmount <= 1.0F) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_STEP.get(), 4, 1);
        }
        if (screenShakeAmount <= 3.0F) {
            screenShakeAmount = 3.0F;
        }
    }


    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
        this.toggleServerEruptionStatus(true);
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
        if (bossEvent.getPlayers().isEmpty()) {
            this.toggleServerEruptionStatus(false);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (!this.isPassengerOfSameVehicle(entity)) {
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
                    d0 *= (double) 0.05F;
                    d1 *= (double) 0.05F;
                    if (!entity.isVehicle() && (entity.isPushable() || entity instanceof KaijuMob)) {
                        entity.push(d0, 0.0D, d1);
                    }
                }
            }
        }
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    public boolean canFeelShake(Entity player) {
        return player.onGround() || this.getAnimation() == ANIMATION_ROAR && this.isAlive();
    }

    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        toggleServerEruptionStatus(false);
    }

    private void toggleServerEruptionStatus(boolean erupting) {
        if (!level().isClientSide) {
            ACWorldData worldData = ACWorldData.get(level());
            if (worldData != null) {
                worldData.trackPrimordialBoss(this.getId(), erupting);
                AlexsCaves.sendMSGToAll(new UpdateBossEruptionStatus(this.getId(), worldData.isPrimordialBossActive(level())));
            }
        }
    }

    public boolean isLoadedInWorld() {
        return this.level().hasChunk(SectionPos.blockToSectionCoord(this.getX()), SectionPos.blockToSectionCoord(this.getZ()));
    }

    public boolean isEnraged() {
        return this.entityData.get(ENRAGED);
    }

    public void setEnraged(boolean enraged) {
        this.entityData.set(ENRAGED, enraged);
    }

    public float getEnragedProgress(float partialTicks) {
        return (prevEnragedProgress + (enragedProgress - prevEnragedProgress) * partialTicks) * 0.05F;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isDancing() {
        return false;
    }

    private void tickStompAttack() {
        BlockPos pos = this.lastStompPos == null ? this.blockPosition() : this.lastStompPos;
        if (stompMakesFissures) {
            int fissureProgress = Math.max(0, (int) Math.ceil((20 - postStopTicks) * 2.3F));
            placeFissureRing(1 + fissureProgress, pos.getX(), pos.getZ());
        } else if (postStopTicks >= 15) {
            crushBlocksInRing(12, pos.getX(), pos.getZ(), 0.2F);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        if (reducedDamageTicks > 0) {
            damageAmount *= 0.35F;
        }
        if (damageSource.getDirectEntity() instanceof DinosaurEntity && !(damageSource.getDirectEntity() instanceof TremorzillaEntity)) {
            damageAmount *= 0.65D;
        }
        if (damageSource.getEntity() instanceof AbstractGolem) {
            damageAmount *= 0.5F;
        }
        if (damageSource.getEntity() instanceof Warden) {
            damageAmount *= 0.25F;
        }
        boolean prev = super.hurt(damageSource, damageAmount);
        if (prev && reducedDamageTicks == 0) {
            reducedDamageTicks = 10;
        }
        return prev;
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL) | super.isInvulnerableTo(damageSource);
    }

    private void scareMobs() {
        if (this.tickCount - lastScareTimestamp > 5) {
            lastScareTimestamp = this.tickCount;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(64, 20, 64));
        for (LivingEntity e : list) {
            if (!e.getType().is(ACTagRegistry.RESISTS_TREMORSAURUS_ROAR) && !isAlliedTo(e)) {
                if (e instanceof PathfinderMob mob && (!(mob instanceof TamableAnimal) || !((TamableAnimal) mob).isInSittingPose())) {
                    mob.setTarget(null);
                    mob.setLastHurtByMob(null);
                    if (mob.onGround()) {
                        Vec3 randomShake = new Vec3(random.nextFloat() - 0.5F, 0, random.nextFloat() - 0.5F).scale(0.1F);
                        mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.7F, 1, 0.7F).add(randomShake));
                    }
                    if (lastScareTimestamp == tickCount) {
                        mob.getNavigation().stop();
                    }
                    if (mob.getNavigation().isDone()) {
                        Vec3 vec = LandRandomPos.getPosAway(mob, 30, 7, this.position());
                        if (vec != null) {
                            mob.getNavigation().moveTo(vec.x, vec.y, vec.z, 2D);
                        }
                    }
                }
                if (this.isTame()) {
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, true, true));
                }
            }

        }
    }

    private void placeFissureRing(int width, int fissureStartX, int fissureStartZ) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int eyeBlockY = (int) this.getY(0.5F);
        BlockPos center = new BlockPos(fissureStartX, eyeBlockY, fissureStartZ);
        for (int i = -width - 1; i <= width + 1; i++) {
            for (int j = -width - 1; j <= width + 1; j++) {
                mutableBlockPos.set(this.getBlockX() + i, eyeBlockY, this.getBlockZ() + j);
                double dist = Math.sqrt(mutableBlockPos.distSqr(center));
                if (dist <= width + 2 && dist > width - 2 && level().isLoaded(mutableBlockPos)) {
                    while (canFissureMoveThrough(mutableBlockPos)) {
                        mutableBlockPos.move(0, -1, 0);
                    }
                    mutableBlockPos.move(0, 1, 0);
                    if (placeFissureBlock(mutableBlockPos)) {
                        ((ServerLevel) level()).sendParticles(ACParticleRegistry.MUSHROOM_CLOUD_EXPLOSION.get(), mutableBlockPos.getX() + random.nextFloat(), mutableBlockPos.getY() + 0.5F + random.nextFloat(), mutableBlockPos.getZ() + random.nextFloat(), 0, 0, 0, 0, 1D);
                    }
                }
            }
        }
    }

    private boolean placeFissureBlock(BlockPos.MutableBlockPos blockPos) {
        float sampleScale = 0.08F;
        int depth = 4;
        VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(blockPos.getX() * sampleScale, blockPos.getZ() * sampleScale);
        boolean flag = false;
        if (info.distance1() - sampleScale * 4 < info.distance()) {
            if(net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)){
                int y = blockPos.getY();
                for (int i = 0; i <= depth; i++) {
                    BlockState state = level().getBlockState(blockPos);
                    if (blockPos.getY() <= level().getMinBuildHeight() || state.is(ACTagRegistry.UNMOVEABLE) || state.is(ACBlockRegistry.FISSURE_PRIMAL_MAGMA.get())) {
                        break;
                    } else {
                        if (i < depth && !state.is(ACTagRegistry.REGENERATES_AFTER_PRIMORDIAL_BOSS_FIGHT)) {
                            level().destroyBlock(blockPos, true);
                        } else {
                            level().setBlockAndUpdate(blockPos, i == depth ? ACBlockRegistry.FISSURE_PRIMAL_MAGMA.get().defaultBlockState().setValue(FissurePrimalMagmaBlock.REGEN_HEIGHT, Mth.clamp(i - 1, 0, 4)) : Blocks.AIR.defaultBlockState());
                        }
                        flag = true;
                    }
                    blockPos.move(0, -1, 0);
                }
                blockPos.setY(y);
            }else{
                return true;
            }
        }
        return flag;
    }

    private boolean canFissureMoveThrough(BlockPos.MutableBlockPos blockPos) {
        if (blockPos.getY() <= level().getMinBuildHeight()) {
            return false;
        } else {
            BlockState state = level().getBlockState(blockPos);
            return !state.isSolid() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || !state.isCollisionShapeFullBlock(level(), blockPos);
        }
    }

    @Override
    public float getProjectileDamageReduction() {
        return 0.55F;
    }

    @Override
    public int getAltSkinForItem(ItemStack stack) {
        return 0;
    }

    private void burnWithBreath(float maxDistance) {
        float distanceBurned = 0.0F;
        float burnWidth = 1.0F;
        Vec3 headPos = this.headPart.centeredPosition();
        float burnAngle = this.yBodyRot + this.neckYRot;
        while (distanceBurned < maxDistance) {
            burnWidth++;
            Vec3 burnPos = headPos.add(rotateOffsetVec(new Vec3(0, 0, distanceBurned), 0, burnAngle));
            if (random.nextFloat() < 0.5F * (1F - (maxDistance - distanceBurned) / maxDistance)) {
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                pos.set(burnPos.x + (random.nextFloat() - 0.5F) * 2F * distanceBurned, burnPos.y, burnPos.z + (random.nextFloat() - 0.5F) * 2F * distanceBurned);
                while (canFissureMoveThrough(pos)) {
                    pos.move(0, -1, 0);
                }
                pos.move(0, 1, 0);
                if (level().getBlockState(pos).canBeReplaced()) {
                    level().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                }
            }
            this.hurtEntitiesAround(burnPos, burnWidth, 3.0F, 0.3F, true, false);
            distanceBurned += burnWidth;
        }
    }

    @Override
    protected void createWitherRose(@javax.annotation.Nullable LivingEntity living) {
        if (living != null) {
            ACWorldData worldData = ACWorldData.get(level());
            if (worldData != null) {
                boolean prev = worldData.isPrimordialBossDefeatedOnce();
                worldData.setPrimordialBossDefeatedOnce(true);
                if (!prev) {
                    worldData.setFirstPrimordialBossDefeatTimestamp(level().getGameTime());
                    if (level() instanceof ServerLevel serverLevel) {
                        serverLevel.getPlayers(EntitySelector.NO_SPECTATORS).forEach(serverPlayer -> serverPlayer.displayClientMessage(Component.translatable("entity.alexscaves.luxtructosaurus.slain_message").withStyle(ChatFormatting.GOLD), true));
                    }
                }
            }
        }
        super.createWitherRose(living);
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.LUXTRUCTOSAURUS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.LUXTRUCTOSAURUS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LUXTRUCTOSAURUS_DEATH.get();
    }

    public int getExperienceReward() {
        return 100;
    }

}
