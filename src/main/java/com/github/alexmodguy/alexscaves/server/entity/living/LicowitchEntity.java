package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.LicowitchAttackGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.LicowitchUseCrucibleGoal;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessedByLicowitch;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.ACStructureRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LicowitchEntity extends Monster implements IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> CROSSED_ARMS = SynchedEntityData.defineId(LicowitchEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> POSSESSED_UUID_0 = SynchedEntityData.defineId(LicowitchEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> POSSESSED_UUID_1 = SynchedEntityData.defineId(LicowitchEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> POSSESSED_UUID_2 = SynchedEntityData.defineId(LicowitchEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<BlockPos>> CRUCIBLE_POS = SynchedEntityData.defineId(LicowitchEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static EntityDataAccessor<Optional<Vec3>> TELEPORTING_TO_POS = SynchedEntityData.defineId(TremorzillaEntity.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());
    public static final Animation ANIMATION_SWING_LEFT = Animation.create(20);
    public static final Animation ANIMATION_SWING_RIGHT = Animation.create(20);
    public static final Animation ANIMATION_EAT = Animation.create(100);
    public static final Animation ANIMATION_SPELL_0 = Animation.create(45);
    public static final Animation ANIMATION_SPELL_1 = Animation.create(50);
    private Animation currentAnimation;
    private int animationTick;

    private float prevUncrossedArmsProgress;
    private float uncrossedArmsProgress;
    private float prevTeleportingProgress;
    private float teleportingProgress;

    public boolean updateHeldItems = true;
    public boolean updateFoldedArms = true;

    private int eatCooldown = 0;
    private int teleportCooldown = 0;
    public static ItemStack hungerPotion = ACEffectRegistry.createSplashPotion(ACEffectRegistry.STRONG_HUNGER_POTION.get());

    public LicowitchEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveHelper();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.ATTACK_DAMAGE, 3.0F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    public static boolean checkLicowitchSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        if (LicowitchEntity.isWithinTowerSpawnBounds(levelAccessor, blockPos)) {
            return checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(2) == 0 && getWitchCountInStructure(levelAccessor, blockPos, true) < 4;
        } else {
            return checkMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(2) == 0;
        }
    }

    public static boolean isWithinTowerSpawnBounds(ServerLevelAccessor level, BlockPos pos) {
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ACStructureRegistry.LICOWITCH_TOWER.getId());
        StructureStart structureStart = level.getLevel().structureManager().getStructureAt(pos, structure);
        if (structure != null && structureStart.isValid()) {
            //stop spawning on the roof and floor
            return pos.getY() < structureStart.getBoundingBox().maxY() - 9 && pos.getY() > structureStart.getBoundingBox().minY() + 2;
        }
        return false;
    }

    public static int getWitchCountInStructure(ServerLevelAccessor level, BlockPos pos, boolean licowitches) {
        AABB aabb = new AABB(pos).inflate(32.0D);
        if (licowitches) {
            return level.getEntitiesOfClass(LicowitchEntity.class, aabb).size();
        } else {
            return level.getEntitiesOfClass(Witch.class, aabb).size();
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LicowitchAttackGoal(this));
        this.goalSelector.addGoal(2, new LicowitchUseCrucibleGoal(this));
        this.goalSelector.addGoal(3, new RandomlyTeleportGoal());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, LicowitchEntity.class, Witch.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CROSSED_ARMS, true);
        this.entityData.define(POSSESSED_UUID_0, Optional.empty());
        this.entityData.define(POSSESSED_UUID_1, Optional.empty());
        this.entityData.define(POSSESSED_UUID_2, Optional.empty());
        this.entityData.define(CRUCIBLE_POS, Optional.empty());
        this.entityData.define(TELEPORTING_TO_POS, Optional.empty());
    }

    public float getArmsUncrossedProgress(float partialTicks) {
        return (prevUncrossedArmsProgress + (uncrossedArmsProgress - prevUncrossedArmsProgress) * partialTicks) * 0.2F;
    }

    public float getTeleportingProgress(float partialTicks) {
        return (prevTeleportingProgress + (teleportingProgress - prevTeleportingProgress) * partialTicks) * 0.025F;
    }

    public boolean areArmsVisuallyCrossed(float partialTicks) {
        return getArmsUncrossedProgress(partialTicks) <= 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        prevUncrossedArmsProgress = uncrossedArmsProgress;
        prevTeleportingProgress = teleportingProgress;
        if (!this.areArmsCrossed() && uncrossedArmsProgress < 5F) {
            uncrossedArmsProgress++;
        }
        if (this.areArmsCrossed() && uncrossedArmsProgress > 0F) {
            uncrossedArmsProgress--;
        }
        if (this.getTeleportingToPos() != null && teleportingProgress < 40F) {
            teleportingProgress++;
        }
        if (this.getTeleportingToPos() == null && teleportingProgress > 0F) {
            teleportingProgress = Math.max(0, teleportingProgress - 10);
        }
        if (this.getAnimation() == ANIMATION_SPELL_1) {
            float f = this.getAnimationTick() < 10 ? 0.2F : this.getAnimationTick() > 40 ? -0.2F : 0;
            float backAmount = this.getAnimationTick() > 40 ? 0.0F : 0.1F;
            this.setDeltaMovement(new Vec3(0, f, 0).add(new Vec3(0, 0, -backAmount).yRot((float) -Math.toRadians(this.getYRot()))));
            this.fallDistance = 0.0F;
        }
        if (updateHeldItems && !level().isClientSide) {
            ItemStack main = ItemStack.EMPTY;
            ItemStack offhand = ItemStack.EMPTY;
            if (this.getAnimation() == ANIMATION_SPELL_0 || this.getAnimation() == ANIMATION_SPELL_1) {
                main = new ItemStack(ACItemRegistry.SUGAR_STAFF.get());
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() < 90) {
                main = new ItemStack(ACBlockRegistry.CANDY_CANE.get());
            }
            if (!main.isEmpty() || this.areArmsVisuallyCrossed(1.0F)) {
                this.setItemInHand(InteractionHand.MAIN_HAND, main);
            }
            if (!offhand.isEmpty() || this.areArmsVisuallyCrossed(1.0F)) {
                this.setItemInHand(InteractionHand.OFF_HAND, offhand);
            }
        }
        if (updateFoldedArms) {
            boolean unfold = this.teleportingProgress > 0 || this.getAnimation() == ANIMATION_SPELL_0 || this.getAnimation() == ANIMATION_SPELL_1 || this.getAnimation() == ANIMATION_SWING_LEFT || this.getAnimation() == ANIMATION_SWING_RIGHT;
            this.setArmsCrossed(!unfold);
        }
        if (!level().isClientSide) {
            List<UUID> possessedIds = getPossessedUUIDs();
            if (level() instanceof ServerLevel) {
                for (UUID possessedId : possessedIds) {
                    Entity possessedEntity = ((ServerLevel) level()).getEntity(possessedId);
                    if (possessedEntity != null) {
                        this.tickPossessedEntity(possessedEntity);
                    }
                }
            }
            if (this.getHealth() < this.getMaxHealth() && this.getAnimation() == NO_ANIMATION && tickCount % 20 == 8 && eatCooldown == 0) {
                this.setAnimation(ANIMATION_EAT);
                eatCooldown = 200;
            }
            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 90) {
                this.heal(5);
            }
            if (eatCooldown > 0) {
                eatCooldown--;
            }
        } else {
            if (this.getTeleportingToPos() != null && this.getTeleportingProgress(1.0F) < 1.0F) {
                Vec3 angle = new Vec3(random.nextBoolean() ? 0.5F : -0.5F, 2.0F, 0.0F).yRot((float) -Math.toRadians(this.yBodyRot));
                Vec3 delta = new Vec3(random.nextGaussian() * 2.0F, 4.0F, random.nextGaussian()).yRot((float) -Math.toRadians(this.yBodyRot)).add(this.position());
                this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_MAGIC.get(), this.getX() + angle.x, this.getY() + angle.y, this.getZ() + angle.z, delta.x, delta.y, delta.z);
            } else if (this.random.nextFloat() < 0.003F) {
                for (int i = 0; i < this.random.nextInt(2) + 2; ++i) {
                    this.level().addParticle(ACParticleRegistry.WITCH_COOKIE.get(), this.getX() + this.random.nextGaussian() * (double) 0.13F, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double) 0.13F, this.getZ() + this.random.nextGaussian() * (double) 0.13F, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (this.teleportingProgress >= 40 && this.getTeleportingToPos() != null) {
            Vec3 vec3 = this.getTeleportingToPos();
            this.setPos(vec3.x, vec3.y, vec3.z);
            if (!level().isClientSide) {
                this.setTeleportingToPos(null);
            } else {
                for (int i = 0; i < this.random.nextInt(8) + 8; ++i) {
                    this.level().addParticle(ACParticleRegistry.PURPLE_WITCH_EXPLOSION.get(), this.getX() + this.random.nextGaussian() * (double) 0.3F, this.getY() + this.random.nextGaussian() * (double) 1.5F, this.getZ() + this.random.nextGaussian() * (double) 0.3F, 0.0D, 0.0D, 0.0D);
                }
            }
            this.setOldPosAndRot();
            this.postTeleport();
        }
        if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() < 90 && this.getAnimationTick() % 4 == 0) {
            this.triggerItemUseEffects(this.getItemInHand(InteractionHand.MAIN_HAND), 2);
        }
        if (teleportCooldown > 0) {
            teleportCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void postTeleport() {
        this.teleportCooldown = 200 + random.nextInt(300);
    }

    public boolean canTeleport() {
        return this.teleportCooldown <= 0;
    }

    public boolean isUsingItem() {
        return this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() > 10 && this.getAnimationTick() < 90 || super.isUsingItem();
    }

    private void tickPossessedEntity(Entity possessedEntity) {
        if (possessedEntity instanceof PossessedByLicowitch possessedByLicowitch) {
            possessedByLicowitch.setPossessedByLicowitchId(this.getId());
        }
        if (possessedEntity instanceof Mob mob) {
            mob.setTarget(this.getTarget());
        }
        if (!possessedEntity.isAlive() || !(possessedEntity instanceof PossessedByLicowitch)) {
            removePossessedUUID(possessedEntity.getUUID());
        }
    }

    public void killAllPossessedEntities() {
        if (!level().isClientSide) {
            List<UUID> possessedIds = getPossessedUUIDs();
            if (level() instanceof ServerLevel) {
                for (UUID possessedId : possessedIds) {
                    Entity possessedEntity = ((ServerLevel) level()).getEntity(possessedId);
                    if (possessedEntity != null) {
                        possessedEntity.hurt(this.damageSources().magic(), 300);
                    }
                }
            }
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        killAllPossessedEntities();
        super.remove(removalReason);
    }


    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        List<UUID> list = this.getPossessedUUIDs();
        ListTag listtag = new ListTag();
        for (UUID uuid : list) {
            if (uuid != null) {
                listtag.add(NbtUtils.createUUID(uuid));
            }
        }
        compoundTag.put("Possessed", listtag);

        BlockPos cruciblePos = getLastCruciblePos();
        if (cruciblePos != null) {
            compoundTag.putInt("CrucibleX", cruciblePos.getX());
            compoundTag.putInt("CrucibleY", cruciblePos.getY());
            compoundTag.putInt("CrucibleZ", cruciblePos.getZ());
        }
        compoundTag.putInt("TeleportCooldown", teleportCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ListTag listtag = compoundTag.getList("Possessed", 11);
        for (int i = 0; i < listtag.size(); ++i) {
            this.addPossessedUUID(NbtUtils.loadUUID(listtag.get(i)));
        }
        if (compoundTag.contains("CrucibleX") && compoundTag.contains("CrucibleY") && compoundTag.contains("CrucibleZ")) {
            this.setLastCruciblePos(new BlockPos(compoundTag.getInt("CrucibleX"), compoundTag.getInt("CrucibleY"), compoundTag.getInt("CrucibleZ")));
        }
        teleportCooldown = compoundTag.getInt("TeleportCooldown");
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return slot.isArmor() ? super.getEquipmentDropChance(slot) : 0.0F;
    }

    protected int calculateFallDamage(float f, float f1) {
        return super.calculateFallDamage(f, f1) - 2;
    }

    public boolean areArmsCrossed() {
        return this.entityData.get(CROSSED_ARMS);
    }

    public void setArmsCrossed(boolean crossed) {
        this.entityData.set(CROSSED_ARMS, crossed);
    }

    public BlockPos getLastCruciblePos() {
        return this.entityData.get(CRUCIBLE_POS).orElse(null);
    }

    public void setLastCruciblePos(BlockPos lastAltarPos) {
        this.entityData.set(CRUCIBLE_POS, Optional.ofNullable(lastAltarPos));
    }

    public Vec3 getTeleportingToPos() {
        return this.entityData.get(TELEPORTING_TO_POS).orElse(null);
    }

    public void setTeleportingToPos(Vec3 lastAltarPos) {
        this.entityData.set(TELEPORTING_TO_POS, Optional.ofNullable(lastAltarPos));
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
        return new Animation[]{ANIMATION_SWING_LEFT, ANIMATION_SWING_RIGHT, ANIMATION_EAT, ANIMATION_SPELL_0, ANIMATION_SPELL_1};
    }

    public boolean isFriendlyFire(LivingEntity entity) {
        if (entity instanceof PossessedByLicowitch possessed) {
            int id = possessed.getPossessedByLicowitchId();
            return id == this.getId();
        }
        return entity instanceof Witch || entity.is(this);
    }

    public Vec3 getStaffPosition() {
        Vec3 angle = new Vec3(this.getMainArm() == HumanoidArm.LEFT ? 0.18F : -0.18F, 1.625F, 1.625F).yRot((float) -Math.toRadians(this.yBodyRot));
        return this.position().add(angle);
    }

    public Vec3 getSwingArmPosition() {
        Vec3 angle = new Vec3(this.getAnimation() == ANIMATION_SWING_LEFT ? 0.25F : -0.25F, 1.45F, 0.45F).yRot((float) -Math.toRadians(this.yBodyRot));
        return this.position().add(angle);
    }

    public List<UUID> getPossessedUUIDs() {
        List<UUID> list = Lists.newArrayList();
        UUID uuid0 = this.entityData.get(POSSESSED_UUID_0).orElse(null);
        UUID uuid1 = this.entityData.get(POSSESSED_UUID_1).orElse(null);
        UUID uuid2 = this.entityData.get(POSSESSED_UUID_2).orElse(null);
        if (uuid0 != null) {
            list.add(uuid0);
        }
        if (uuid1 != null) {
            list.add(uuid1);
        }
        if (uuid2 != null) {
            list.add(uuid2);
        }
        return list;
    }

    public void addPossessedUUID(@Nullable UUID uuid) {
        if (this.entityData.get(POSSESSED_UUID_0).isPresent()) {
            if (this.entityData.get(POSSESSED_UUID_1).isPresent()) {
                this.entityData.set(POSSESSED_UUID_2, Optional.ofNullable(uuid));
            } else {
                this.entityData.set(POSSESSED_UUID_1, Optional.ofNullable(uuid));
            }
        } else {
            this.entityData.set(POSSESSED_UUID_0, Optional.ofNullable(uuid));
        }
    }

    public void removePossessedUUID(@Nullable UUID uuid) {
        if (this.entityData.get(POSSESSED_UUID_0).isPresent() && uuid.equals(this.entityData.get(POSSESSED_UUID_0).get())) {
            this.entityData.set(POSSESSED_UUID_0, Optional.empty());
        } else if (this.entityData.get(POSSESSED_UUID_1).isPresent() && uuid.equals(this.entityData.get(POSSESSED_UUID_1).get())) {
            this.entityData.set(POSSESSED_UUID_1, Optional.empty());
        } else if (this.entityData.get(POSSESSED_UUID_2).isPresent() && uuid.equals(this.entityData.get(POSSESSED_UUID_2).get())) {
            this.entityData.set(POSSESSED_UUID_2, Optional.empty());
        }
    }

    public Mob createRandomPossessedMob() {
        float chance = random.nextFloat();
        if (chance < 0.1F) {
            return ACEntityRegistry.CANDICORN.get().create(level());
        } else if (chance < 0.2F) {
            return ACEntityRegistry.GUMMY_BEAR.get().create(level());
        } else if (chance < 0.5F) {
            return ACEntityRegistry.GUMBEEPER.get().create(level());
        } else if (chance < 0.65F) {
            return ACEntityRegistry.CARAMEL_CUBE.get().create(level());
        } else if (chance < 0.75F) {
            return ACEntityRegistry.GINGERBREAD_MAN.get().create(level());
        } else {
            return ACEntityRegistry.CANIAC.get().create(level());
        }
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    @Override
    public void awardKillScore(Entity entity, int deathScore, DamageSource damageSource) {
        super.awardKillScore(entity, deathScore, damageSource);
        this.level().playSound((Player) null, this.blockPosition(), ACSoundRegistry.LICOWITCH_CELEBRATE.get(), SoundSource.HOSTILE, 0.3F, 0.9F + this.level().random.nextFloat() * 0.2F);
    }

        public boolean canReach(BlockPos pos) {
        Path path = this.getNavigation().createPath(pos, 0);
        if (path == null) {
            return false;
        } else {
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            } else {
                int i = node.x - pos.getX();
                int j = node.y - pos.getY();
                int k = node.z - pos.getZ();
                return (double) (i * i + j * j + k * k) <= 3D;
            }
        }
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.getTeleportingToPos() != null) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    private class MoveHelper extends MoveControl {

        public MoveHelper() {
            super(LicowitchEntity.this);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.STRAFE) {
                speedModifier = 0.5F;
            }
            super.tick();
        }
    }


    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.LICOWITCH_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.LICOWITCH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LICOWITCH_DEATH.get();
    }

    private class RandomlyTeleportGoal extends Goal {

        private Vec3 teleportVec;

        public RandomlyTeleportGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = LicowitchEntity.this.getTarget();
            if (LicowitchEntity.this.random.nextInt(1000) == 0 && LicowitchEntity.this.canTeleport() && LicowitchEntity.this.getTeleportingToPos() == null && (target == null || !target.isAlive())) {
                teleportVec = LandRandomPos.getPos(LicowitchEntity.this, 16, 16);
                if (teleportVec != null) {
                    teleportVec = teleportVec.add(0, 1, 0);
                    AABB aabb = LicowitchEntity.this.getBoundingBox().move(teleportVec.subtract(LicowitchEntity.this.position()));
                    return LicowitchEntity.this.level().isUnobstructed(LicowitchEntity.this, Shapes.create(aabb));
                }
            }
            return false;
        }

        public void start() {
            LicowitchEntity.this.setTeleportingToPos(teleportVec);
        }

        public boolean canContinueToUse() {
            return LicowitchEntity.this.canTeleport() || LicowitchEntity.this.getTeleportingToPos() != null;
        }
    }
}
