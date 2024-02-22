package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

import java.util.Optional;
import java.util.UUID;

public class DinosaurSpiritEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> PLAYER_ID = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DINOSAUR_TYPE = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACKING_ENTITY_ID = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DELAY_SPAWN = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FADING = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> USING_ABILITY = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ROTATE_OFFSET = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ENCHANTMENT_LEVEL = SynchedEntityData.defineId(DinosaurSpiritEntity.class, EntityDataSerializers.INT);
    private float fadeIn = 0;
    private float prevFadeIn = 0;
    private int duration = 0;
    private float abilityProgress = 0;
    private float prevAbilityProgress = 0;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private boolean dealtDamage = false;

    public DinosaurSpiritEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public DinosaurSpiritEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.BEHOLDER_EYE.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PLAYER_ID, Optional.empty());
        this.entityData.define(DINOSAUR_TYPE, 0);
        this.entityData.define(ATTACKING_ENTITY_ID, -1);
        this.entityData.define(DELAY_SPAWN, 0);
        this.entityData.define(FADING, false);
        this.entityData.define(USING_ABILITY, false);
        this.entityData.define(ENCHANTMENT_LEVEL, 0);
        this.entityData.define(ROTATE_OFFSET, 0F);
    }

    public void tick() {
        super.tick();
        Player player = this.getUsingPlayer();
        this.prevFadeIn = fadeIn;
        this.prevAbilityProgress = abilityProgress;
        if (this.getDelaySpawn() > 0) {
            this.setDelaySpawn(this.getDelaySpawn() - 1);
            this.fadeIn = 0;
            if(this.getDelaySpawn() == 0){
                this.playSound(ACSoundRegistry.EXTINCTION_SPEAR_SUMMON.get(), 1.0F, 1.0F);
            }
            return;
        }
        if (this.isFading() && fadeIn > 0) {
            fadeIn--;
        }
        if (!this.isFading() && fadeIn < 10) {
            fadeIn++;
        }
        if (this.isUsingAbility() && abilityProgress < 5) {
            abilityProgress++;
        }
        if (!this.isUsingAbility() && abilityProgress > 0) {
            abilityProgress--;
        }
        if (this.isFading() && fadeIn <= 0) {
            this.discard();
        }
        if (level().isClientSide) {
            this.level().addParticle(ParticleTypes.FLAME, this.getRandomX(1.0F), this.getRandomY(), this.getRandomZ(1.0F), 0, 0, 0);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));
        if (player == null) {
            this.setFading(true);
        } else {
            switch (getDinosaurType()) {
                case SUBTERRANODON:
                    tickSubterranodon(player);
                    break;
                case GROTTOCERATOPS:
                    tickGrottoceratops(player);
                    break;
                case TREMORSAURUS:
                    tickTremorsaurus(player);
                    break;
            }
        }
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
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

    private void tickSubterranodon(Player player) {
        Entity target = getAttackingEntity();
        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.03 + 0.005F * getEnchantmentLevel(), 0));
        if (target != null && duration < (40 + getEnchantmentLevel() * 5)) {
            Vec3 targetMovePos = this.position().subtract(0, target.getBbHeight(), 0);
            target.setDeltaMovement(Vec3.ZERO);
            target.setPos(targetMovePos.x, targetMovePos.y, targetMovePos.z);
            duration++;
        } else {
            this.setFading(true);
        }
    }

    private void tickGrottoceratops(Player player) {
        float rot = this.getRotateOffset() + player.tickCount * 5;
        Vec3 orbitBy = new Vec3(0, 1, 2).yRot((float) -Math.toRadians(rot));
        Vec3 orbitTarget = player.position().add(orbitBy).subtract(this.position());
        this.setXRot(10);
        this.setDeltaMovement(orbitTarget.scale(0.25F));
        this.noPhysics = true;
        if(!level().isClientSide && !player.getUseItem().is(ACItemRegistry.EXTINCTION_SPEAR.get())){
            this.setFading(true);
        }
    }

    private void tickTremorsaurus(Player player) {
        Entity target = getAttackingEntity();
        if (target != null) {
            this.noPhysics = true;
            this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            boolean inRange = this.distanceTo(target) < target.getBbWidth() + 3.5D;
            if (!inRange) {
                Vec3 targetPos = target.position().subtract(this.position());
                if (targetPos.length() > 1F) {
                    targetPos = targetPos.normalize();
                }
                this.setDeltaMovement(targetPos.scale(0.15F));
            }
            this.setUsingAbility(true);
            if (inRange && this.abilityProgress >= 5) {
                if(!dealtDamage && target.hurt(ACDamageTypes.causeSpiritDinosaurDamage(level().registryAccess(), player), 3 + 2 * getEnchantmentLevel())){
                    dealtDamage = true;
                }
                this.setFading(true);
            }
        }
        if (duration++ > 20) {
            this.setFading(true);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("UsingPlayerUUID")) {
            this.setPlayerUUID(tag.getUUID("UsingPlayerUUID"));
        }
        this.setDinosaurTypeInt(tag.getInt("DinosaurType"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid1 = getPlayerUUID();
        if (uuid1 != null) {
            tag.putUUID("UsingPlayerUUID", uuid1);
        }
        tag.putInt("DinosaurType", this.getDinosaurTypeInt());
    }

    public void setPlayerUUID(UUID uuid) {
        this.entityData.set(PLAYER_ID, Optional.ofNullable(uuid));
    }

    public UUID getPlayerUUID() {
        return this.entityData.get(PLAYER_ID).orElse(null);
    }

    public Player getUsingPlayer() {
        UUID id = getPlayerUUID();
        if (id == null) {
            return null;
        } else {
            if (level().isClientSide) {
                return level().getPlayerByUUID(id);
            } else {
                return level().getServer().getPlayerList().getPlayer(id);
            }
        }
    }

    private int getDinosaurTypeInt() {
        return this.entityData.get(DINOSAUR_TYPE);
    }

    private void setDinosaurTypeInt(int type) {
        this.entityData.set(DINOSAUR_TYPE, type);
    }

    public int getDelaySpawn() {
        return this.entityData.get(DELAY_SPAWN);
    }

    public void setDelaySpawn(int type) {
        this.entityData.set(DELAY_SPAWN, type);
    }

    public int getEnchantmentLevel() {
        return this.entityData.get(ENCHANTMENT_LEVEL);
    }

    public void setEnchantmentLevel(int type) {
        this.entityData.set(ENCHANTMENT_LEVEL, type);
    }

    public void setAttackingEntityId(int id) {
        this.entityData.set(ATTACKING_ENTITY_ID, id);
    }

    public Entity getAttackingEntity() {
        int id = this.entityData.get(ATTACKING_ENTITY_ID);
        if (id == -1) {
            return null;
        } else {
            return level().getEntity(id);
        }
    }

    private float getRotateOffset() {
        return this.entityData.get(ROTATE_OFFSET);
    }

    public void setRotateOffset(float rotateOffset) {
        this.entityData.set(ROTATE_OFFSET, rotateOffset);
    }
    public DinosaurType getDinosaurType() {
        return DinosaurType.values()[Mth.clamp(getDinosaurTypeInt(), 0, DinosaurType.values().length)];
    }

    public void setDinosaurType(DinosaurType type) {
        this.entityData.set(DINOSAUR_TYPE, type.ordinal());
    }

    public boolean isFading() {
        return this.entityData.get(FADING);
    }

    public void setFading(boolean bool) {
        this.entityData.set(FADING, bool);
    }

    public boolean isUsingAbility() {
        return this.entityData.get(USING_ABILITY);
    }

    public void setUsingAbility(boolean bool) {
        this.entityData.set(USING_ABILITY, bool);
    }

    public float getFadeIn(float partialTicks) {
        return (prevFadeIn + (fadeIn - prevFadeIn) * partialTicks) * 0.1F;
    }

    public float getAbilityProgress(float partialTicks) {
        return (prevAbilityProgress + (abilityProgress - prevAbilityProgress) * partialTicks) * 0.2F;
    }

    public enum DinosaurType {
        SUBTERRANODON,
        GROTTOCERATOPS,
        TREMORSAURUS;
    }

}
