package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class RadioactiveOnDestroyedBlockItem extends RadioactiveBlockItem {

    public RadioactiveOnDestroyedBlockItem(RegistryObject<Block> blockSupplier, Properties props, float randomChanceOfRadiation) {
        super(blockSupplier, props, randomChanceOfRadiation);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource){
        super.onDestroyed(itemEntity, damageSource);
        if(!damageSource.isCreativePlayer() && !itemEntity.isRemoved()){
            itemEntity.discard();
            AreaEffectCloud cloud = new AreaEffectCloud(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            cloud.setParticle(ACParticleRegistry.GAMMAROACH.get());
            cloud.setFixedColor(0X77D60E);
            cloud.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 9600, 1));
            cloud.setRadius(2.0F);
            cloud.setDuration(12000);
            cloud.setWaitTime(0);
            cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
            itemEntity.level().addFreshEntity(cloud);
            itemEntity.level().explode(null, itemEntity.getX(), itemEntity.getY() + 0.5F, itemEntity.getZ(), 1.8F, Level.ExplosionInteraction.BLOCK);
        }
    }
}
