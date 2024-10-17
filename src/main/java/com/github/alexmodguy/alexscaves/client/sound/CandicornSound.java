package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.CandicornEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class CandicornSound extends AbstractTickableSoundInstance {
    private final CandicornEntity candicorn;

    private float moveFade = 0;

    public CandicornSound(CandicornEntity candicorn) {
        super(ACSoundRegistry.CANDICORN_CHARGE_LOOP.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.candicorn = candicorn;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.candicorn.getX());
        this.y = (double)((float)this.candicorn.getY());
        this.z = (double)((float)this.candicorn.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.candicorn.isAlive() && !this.candicorn.isSilent() && this.candicorn.isCharging();
    }

    public void tick() {
        if (this.candicorn.isAlive() && this.candicorn.isCharging()) {
            this.x = (double)((float)this.candicorn.getX());
            this.y = (double)((float)this.candicorn.getY());
            this.z = (double)((float)this.candicorn.getZ());
            float f = (float)this.candicorn.getDeltaMovement().length();
            this.pitch = 1.0F;
            if (f <= 0.01F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            }else{
                this.moveFade = Math.max(0F, this.moveFade - 0.05F);
            }
            this.volume = (1F - moveFade);
        } else {
            this.volume = 0.0F;
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(CandicornEntity entity) {
        return this.candicorn.isAlive() && this.candicorn.getId() == entity.getId();
    }
}
