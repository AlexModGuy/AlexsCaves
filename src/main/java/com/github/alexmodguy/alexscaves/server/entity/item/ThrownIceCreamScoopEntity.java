package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class ThrownIceCreamScoopEntity extends ThrowableItemProjectile {

    public ThrownIceCreamScoopEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public ThrownIceCreamScoopEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP.get(), level);
    }

    public ThrownIceCreamScoopEntity(Level level, LivingEntity thrower) {
        super(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP.get(), thrower, level);
    }

    public ThrownIceCreamScoopEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP.get(), x, y, z, level);
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
        hitResult.getEntity().hurt(damageSources().thrown(this, this.getOwner()), 0.0F);
        if(hitResult.getEntity() instanceof LivingEntity living){
            living.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }

    }

    protected Item getDefaultItem() {
        return ACItemRegistry.VANILLA_ICE_CREAM_SCOOP.get();
    }
}