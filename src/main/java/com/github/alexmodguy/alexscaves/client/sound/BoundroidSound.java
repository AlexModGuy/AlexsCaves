package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.BoundroidEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.BoundroidWinchEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class BoundroidSound extends AbstractTickableSoundInstance {
    private final BoundroidEntity boundroid;

    private float moveFade = 0;

    private float prevChainLength = 0;

    public BoundroidSound(BoundroidEntity boundroid) {
        super(ACSoundRegistry.BOUNDROID_CHAIN_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.boundroid = boundroid;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.boundroid.getX());
        this.y = (double)((float)this.boundroid.getY());
        this.z = (double)((float)this.boundroid.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.boundroid.isAlive() && !this.boundroid.isSilent();
    }

    public void tick() {
        if (this.boundroid.isAlive()) {
            this.x = (double)((float)this.boundroid.getX());
            this.y = (double)((float)this.boundroid.getY());
            this.z = (double)((float)this.boundroid.getZ());
            float f = 0.0F;
            if(this.boundroid.getWinch() instanceof BoundroidWinchEntity winchEntity){
                f = winchEntity.distanceTo(this.boundroid);
            }
            float f1 = Math.min(Math.abs(prevChainLength - f) * 20F, 1.0F);
            if (f1 <= 0.3F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            }else{
                this.moveFade = Math.max(0F, this.moveFade - 0.25F);
            }
            this.volume = 1.0F - moveFade;
            this.pitch = 1.0F + f1;
            this.prevChainLength = f;
        } else {
            this.volume = 0.0F;
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(BoundroidEntity entity) {
        return this.boundroid.isAlive() && this.boundroid.getId() == entity.getId();
    }
}
