package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.message.misc.ACTagRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class TeletorEntity extends Monster {

    private static ImmutableList<Item> HELD_ITEM_POSSIBILITIES = null;
    private static final EntityDataAccessor<Optional<UUID>> WEAPON_UUID = SynchedEntityData.defineId(TeletorEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> WEAPON_ID = SynchedEntityData.defineId(TeletorEntity.class, EntityDataSerializers.INT);

    private float prevControlProgress = 0;
    private float controlProgress = 0;

    private Vec3[][] trailPositions = new Vec3[64][2];
    private int trailPointer = -1;

    public TeletorEntity(EntityType<? extends Monster> teletor, Level level) {
        super(teletor, level);
        this.moveControl = new MoveController();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WEAPON_UUID, Optional.empty());
        this.entityData.define(WEAPON_ID, -1);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 45));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Husk.class, true, false));
    }

    protected PathNavigation createNavigation(Level p_27815_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_27815_) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Nullable
    public UUID getWeaponUUID() {
        return this.entityData.get(WEAPON_UUID).orElse(null);
    }

    public void setWeaponUUID(@Nullable UUID uniqueId) {
        this.entityData.set(WEAPON_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getWeapon() {
        if (!level.isClientSide) {
            UUID id = getWeaponUUID();
            return id == null ? null : ((ServerLevel) level).getEntity(id);
        } else {
            int id = this.entityData.get(WEAPON_ID);
            return id == -1 ? null : level.getEntity(id);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("WeaponUUID")) {
            this.setWeaponUUID(compound.getUUID("WeaponUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getWeaponUUID() != null) {
            compound.putUUID("WeaponUUID", this.getWeaponUUID());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FLYING_SPEED, 1F).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MAX_HEALTH, 20.0D);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.55F * dimensions.height;
    }

    public boolean areLegsCrossed(float limbSwing) {
        return this.isAlive() && limbSwing <= 0.35F;
    }

    public void tick() {
        super.tick();
        prevControlProgress = controlProgress;
        Entity weapon = getWeapon();
        if (weapon instanceof MagneticWeaponEntity magneticWeapon) {
            this.entityData.set(WEAPON_ID, magneticWeapon.getId());
            magneticWeapon.setControllerUUID(this.getUUID());
            Entity e = magneticWeapon.getTarget();
            boolean control = e != null && e.isAlive();
            if (control && controlProgress < 5F) {
                controlProgress++;
            }
            if (!control && controlProgress > 0F) {
                controlProgress--;
            }
        }
        if (level.isClientSide) {
            tickVisual();
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.98F, 0.98F, 0.98F));
    }

    public void tickVisual() {
        Vec3 blue = getHelmetPosition(0);
        Vec3 red = getHelmetPosition(1);
        if (trailPointer == -1) {
            for (int i = 0; i < trailPositions.length; i++) {
                trailPositions[i][0] = blue;
                trailPositions[i][1] = red;
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer][0] = blue;
        this.trailPositions[this.trailPointer][1] = red;
    }

    public boolean hasTrail() {
        return trailPointer != -1;
    }

    public float getControlProgress(float partialTick) {
        return (prevControlProgress + (controlProgress - prevControlProgress) * partialTick) * 0.2F;
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public Vec3 getTrailPosition(int pointer, int side, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j][side];
        Vec3 d1 = this.trailPositions[i][side].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        MagneticWeaponEntity magneticWeapon = ACEntityRegistry.MAGNETIC_WEAPON.get().create(this.level);
        ItemStack stack = createItemStack(level.getRandom());
        float f = difficultyIn.getSpecialMultiplier();
        if(level.getRandom().nextFloat() < 0.25F * (f + 0.5F)){
            stack = EnchantmentHelper.enchantItem(level.getRandom(), stack, (int)(5.0F + f * (float)level.getRandom().nextInt(18)), false);
        }
        magneticWeapon.setItemStack(stack);
        magneticWeapon.setPos(this.getWeaponPosition());
        magneticWeapon.setControllerUUID(this.getUUID());
        this.setWeaponUUID(magneticWeapon.getUUID());
        level.addFreshEntity(magneticWeapon);
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }


    public ItemStack createItemStack(RandomSource random) {
        if (HELD_ITEM_POSSIBILITIES == null || HELD_ITEM_POSSIBILITIES.isEmpty()) {
            HELD_ITEM_POSSIBILITIES = ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(ACTagRegistry.TELETOR_SPAWNS_WITH)).collect(ImmutableList.toImmutableList());
        }
        if (HELD_ITEM_POSSIBILITIES.size() <= 0 || random.nextFloat() < 0.3F) {
            return new ItemStack(Items.IRON_SWORD);
        } else if (HELD_ITEM_POSSIBILITIES.size() == 1) {
            return new ItemStack(HELD_ITEM_POSSIBILITIES.get(0));
        } else {
            return new ItemStack(HELD_ITEM_POSSIBILITIES.get(random.nextInt(HELD_ITEM_POSSIBILITIES.size())));
        }
    }

    public Vec3 getWeaponPosition() {
        return this.getEyePosition().add(0, 1.4F - Math.sin(tickCount * 0.1F) * 0.2F, 0);
    }

    public Vec3 getHelmetPosition(int offsetFlag) {
        Vec3 helmet = new Vec3(offsetFlag == 0 ? -0.65F : 0.65F, 1.1F, 0.0F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
        return this.getEyePosition().add(helmet);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        Entity weapon = this.getWeapon();
        if(weapon instanceof MagneticWeaponEntity magneticWeapon){
            ItemStack itemstack = magneticWeapon.getItemStack();
            float f = this.getEquipmentDropChance(EquipmentSlot.MAINHAND);
            if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && this.random.nextFloat() < f) {
                if (itemstack.isDamageableItem()) {
                    itemstack.setDamageValue(itemstack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
                }
                this.spawnAtLocation(itemstack);
            }
            magneticWeapon.remove(RemovalReason.KILLED);
        }
    }

    private class MeleeGoal extends Goal {

        private int executionTime = 0;
        private BlockPos strafeOrigin = null;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = TeletorEntity.this.getTarget();
            return target != null && target.isAlive() && TeletorEntity.this.getWeapon() != null;
        }

        public void start() {
            executionTime = 0;
            strafeOrigin = null;
        }

        public void tick() {
            executionTime++;
            LivingEntity target = TeletorEntity.this.getTarget();
            double dist = TeletorEntity.this.distanceTo(target);
            if (dist < 2) {
                strafeOrigin = TeletorEntity.this.blockPosition().offset(TeletorEntity.this.random.nextInt(16) - 8, TeletorEntity.this.random.nextInt(8), TeletorEntity.this.random.nextInt(16) - 8);
            }
            if (dist < 16) {
                Vec3 lookDist = target.getEyePosition().subtract(TeletorEntity.this.getEyePosition());
                float targetXRot = (float) (-(Mth.atan2(lookDist.y, lookDist.horizontalDistance()) * (double) (180F / (float) Math.PI)));
                float targetYRot = (float) (-Mth.atan2(lookDist.x, lookDist.z) * (double) (180F / (float) Math.PI));
                TeletorEntity.this.getNavigation().stop();
                float f = executionTime * 0.1F;
                Vec3 strafe = new Vec3(Math.sin(f) * 5F, Math.cos(f) * 2F, 0).yRot(-targetYRot * ((float) Math.PI / 180F));
                if (strafeOrigin == null) {
                    strafeOrigin = TeletorEntity.this.blockPosition();
                }
                Vec3 moveTo = Vec3.atCenterOf(strafeOrigin).add(strafe);
                TeletorEntity.this.getMoveControl().setWantedPosition(moveTo.x, moveTo.y, moveTo.z, 1);
                TeletorEntity.this.setXRot(targetXRot);
                TeletorEntity.this.setYRot(targetYRot);
            } else {
                strafeOrigin = null;
                TeletorEntity.this.getNavigation().moveTo(target, 1);
            }
        }
    }
    class MoveController extends MoveControl {
        private final Mob parentEntity;


        public MoveController() {
            super(TeletorEntity.this);
            this.parentEntity = TeletorEntity.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                LivingEntity attackTarget = parentEntity.getTarget();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.025D / d0);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1));
                if(d0 < width * 0.3F){
                    this.operation = Operation.WAIT;
                }else if (d0 >= width && attackTarget == null) {
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                    if (TeletorEntity.this.getTarget() != null) {
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    }
                }
            }
        }
    }
}


