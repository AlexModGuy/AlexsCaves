package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class SodaBottleRocketEntity extends FireworkRocketEntity {

    private int phageAge = 0;

    public SodaBottleRocketEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public SodaBottleRocketEntity(Level worldIn, double x, double y, double z, ItemStack givenItem) {
        super(ACEntityRegistry.SODA_BOTTLE_ROCKET.get(), worldIn);
        this.setPos(x, y, z);
        if (!givenItem.isEmpty() && givenItem.hasTag()) {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, givenItem.copy());
        }

        this.setDeltaMovement(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
        this.lifetime = 18 + this.random.nextInt(14);
    }

    public SodaBottleRocketEntity(Level level, @Nullable Entity entity, double x, double y, double z, ItemStack stack) {
        this(level, x, y, z, stack);
        this.setOwner(entity);
    }

    public SodaBottleRocketEntity(Level level, ItemStack stack, LivingEntity livingEntity) {
        this(level, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), stack);
        this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(livingEntity.getId()));
    }

    public SodaBottleRocketEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ACEntityRegistry.SODA_BOTTLE_ROCKET.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        ++this.phageAge;
        if (this.level().isClientSide) {
            for(int i = 0; i < 5; i++){
                this.level().addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), this.getX(), this.getY() - 0.3D, this.getZ(), this.random.nextGaussian() * 0.25D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.25D);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 17) {
            this.level().addParticle(ACParticleRegistry.FROSTMINT_EXPLOSION.get(), this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
            for(int i = 0; i < this.random.nextInt(15) + 30; ++i) {
                this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D);
            }
            for(int i = 0; i < this.random.nextInt(15) + 15; ++i) {
                this.level().addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), this.getX() + this.random.nextGaussian() * 0.95D, this.getY() + this.random.nextGaussian() * 0.95D, this.getZ() + this.random.nextGaussian() * 0.95D, this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D);
            }
            SoundEvent soundEvent = AlexsCaves.PROXY.isFarFromCamera(this.getX(), this.getY(), this.getZ()) ? SoundEvents.FIREWORK_ROCKET_BLAST : SoundEvents.FIREWORK_ROCKET_BLAST_FAR;
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);


        }else{
            super.handleEntityEvent(id);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE_ROCKET.get());
    }

}