package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class DesolateDaggerEntity extends Entity {

    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(DesolateDaggerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> STAB = SynchedEntityData.defineId(DesolateDaggerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> PLAYER_ID = SynchedEntityData.defineId(DesolateDaggerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(DesolateDaggerEntity.class, EntityDataSerializers.ITEM_STACK);

    protected final RandomSource orbitRandom = RandomSource.create();
    private float orbitOffset = 0;
    private float prevStab = 0;
    public int orbitFor = 20;
    public ItemStack daggerRenderStack = new ItemStack(ACItemRegistry.DESOLATE_DAGGER.get());
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private boolean playedSummonNoise = false;

    public DesolateDaggerEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        orbitFor = 20 + level.random.nextInt(10);
    }

    public DesolateDaggerEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.DESOLATE_DAGGER.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        prevStab = this.getStab();
        Entity entity = getTargetEntity();
        if (level().isClientSide) {
            level().addParticle(DustParticleOptions.REDSTONE, (double) this.getRandomX(0.75F), (double) this.getRandomY(), (double) this.getRandomZ(0.75F), 0.0D, 0.0D, 0.0D);
        }
        if (!playedSummonNoise) {
            this.playSound(ACSoundRegistry.DESOLATE_DAGGER_SUMMON.get());
            playedSummonNoise = true;
        }
        if (entity != null) {
            this.noPhysics = true;
            float invStab = 1F - getStab();
            Vec3 orbitAround = entity.position().add(0, entity.getBbHeight() * 0.25F, 0);
            orbitRandom.setSeed(this.getId());
            if (orbitOffset == 0) {
                orbitOffset = orbitRandom.nextInt(360);
            }
            Vec3 orbitAdd = new Vec3(0, (orbitRandom.nextFloat() + entity.getBbHeight()) * invStab, (orbitRandom.nextFloat() + entity.getBbWidth()) * invStab).yRot((float) Math.toRadians((orbitOffset)));
            this.setDeltaMovement(orbitAround.add(orbitAdd).subtract(this.position()));
            if (!level().isClientSide) {
                if (orbitFor > 0 && entity.isAlive()) {
                    orbitFor--;
                } else {
                    this.setStab(Math.min(this.getStab() + 0.2F, 1F));
                }
                if (this.getStab() >= 1F) {
                    Entity player = getPlayer();
                    Entity damageFrom = player == null ? this : player;
                    float damage = 2 + this.getItemStack().getEnchantmentLevel(ACEnchantmentRegistry.IMPENDING_STAB.get()) * 2F;
                    if (entity.hurt(ACDamageTypes.causeDesolateDaggerDamage(this.level().registryAccess(), damageFrom), damage)) {
                        this.playSound(ACSoundRegistry.DESOLATE_DAGGER_HIT.get());
                        int healBy = this.getItemStack().getEnchantmentLevel(ACEnchantmentRegistry.SATED_BLADE.get());
                        if(healBy > 0 && damageFrom instanceof Player healPlayer && healPlayer.getFoodData().getSaturationLevel() < 5F){
                            healPlayer.getFoodData().setSaturation(healPlayer.getFoodData().getSaturationLevel() + healBy * 0.1F);
                        }
                    }
                    this.discard();
                }
            }
            double d1 = entity.getZ() - this.getZ();
            double d3 = entity.getEyeY() - this.getEyeY();
            double d2 = entity.getX() - this.getX();
            float f = Mth.sqrt((float) (d2 * d2 + d1 * d1));
            this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            this.setXRot(-(float) (Mth.atan2(d3, f) * (double) (180F / (float) Math.PI)));
        } else if (tickCount > 3) {
            this.noPhysics = false;
            this.discard();
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));

        if (this.level().isClientSide) {
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
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
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

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(PLAYER_ID, -1);
        this.entityData.define(STAB, 0F);
        this.entityData.define(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    private int getTargetId() {
        return this.entityData.get(TARGET_ID);
    }

    public void setTargetId(int id) {
        this.entityData.set(TARGET_ID, id);
    }

    private int getPlayerId() {
        return this.entityData.get(PLAYER_ID);
    }

    public void setPlayerId(int id) {
        this.entityData.set(PLAYER_ID, id);
    }

    public float getStab() {
        return this.entityData.get(STAB);
    }

    public float getStab(float partialTicks) {
        return prevStab + (getStab() - prevStab) * partialTicks;
    }

    public void setStab(float stab) {
        this.entityData.set(STAB, stab);
    }

    private Entity getTargetEntity() {
        int id = getTargetId();
        return id == -1 ? null : level().getEntity(id);
    }

    private Entity getPlayer() {
        int id = getPlayerId();
        return id == -1 ? null : level().getEntity(id);
    }
}
