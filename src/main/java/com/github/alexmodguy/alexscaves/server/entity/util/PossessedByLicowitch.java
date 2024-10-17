package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface PossessedByLicowitch {

    void setPossessedByLicowitchId(int entityId);
    int getPossessedByLicowitchId();

    default void setPossessedByLicowitch(LicowitchEntity licowitch){
        this.setPossessedByLicowitchId(licowitch.getId());
    }

    @Nullable
    default LicowitchEntity getPossessingLicowitch(Level level){
        int i = this.getPossessedByLicowitchId();
        if(i != -1){
            Entity entity = level.getEntity(i);
            return entity instanceof LicowitchEntity licowitch ? licowitch : null;
        }
        return null;
    }

    default void spawnPossessedParticles(double randomX, double randomY, double randomZ, Level level){
        if(getPossessedByLicowitchId() != -1 && level.random.nextFloat() < 0.2F){
            LicowitchEntity possessing = getPossessingLicowitch(level);
            if(possessing != null && possessing.isAlive()){
                Vec3 particleTo = possessing.position().add(0.0F, possessing.getBbHeight() * 0.5F, 0);
                level.addParticle(ACParticleRegistry.PURPLE_WITCH_MAGIC.get(), randomX, randomY, randomZ, particleTo.x, particleTo.y, particleTo.z);
            }
        }

    }
}
