package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MineExplosion;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class DepthChargeEntity extends ThrowableItemProjectile {

    private boolean hitGround = false;
    private int groundTime = 0;

    public DepthChargeEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public DepthChargeEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.DEPTH_CHARGE.get(), level);
    }

    public DepthChargeEntity(Level level, LivingEntity thrower) {
        super(ACEntityRegistry.DEPTH_CHARGE.get(), thrower, level);
    }

    public DepthChargeEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.DEPTH_CHARGE.get(), x, y, z, level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void handleEntityEvent(byte message) {
        if (message == 3) {
            double d0 = 0.08D;
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().hurt(damageSources().thrown(this, this.getOwner()), 2.0F + random.nextInt(2));
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        hitGround = true;
    }

    public void tick(){
        this.baseTick();
        this.checkInsideBlocks();
        if(!this.isNoGravity()){
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05F, 0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.updateRotation();
        this.setDeltaMovement(this.getDeltaMovement().scale(0.99F));
        if(this.onGround() || this.horizontalCollision || this.verticalCollision){
            hitGround = true;
        }
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, this.getX(), this.getY(0.5F), this.getZ(), 0, 0, 0);
            }
        }
        if(hitGround){
            this.setDeltaMovement(this.getDeltaMovement().multiply(0, 0, 0));
            if(groundTime++ > 30 && !level().isClientSide){
                this.explode();
            }
        }
    }

    private void explode() {
        this.remove(RemovalReason.KILLED);
        Explosion.BlockInteraction blockinteraction = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level(), this) ? level().getGameRules().getBoolean(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
        boolean inWater = this.getFeetBlockState() != null && this.getFeetBlockState().getFluidState().is(FluidTags.WATER);
        MineExplosion explosion = new MineExplosion(level(), this, this.getX(), this.getY(0.5), this.getZ(), 2.0F, inWater, blockinteraction);
        explosion.explode();
        explosion.finalizeExplosion(true);

    }

    protected Item getDefaultItem() {
        return ACItemRegistry.DEPTH_CHARGE.get();
    }
}