package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class GumWormSound extends AbstractTickableSoundInstance {
    private final GumWormEntity gumWorm;

    private float moveFade = 0;

    public GumWormSound(GumWormEntity gumWorm) {
        super(ACSoundRegistry.GUM_WORM_DIG_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.gumWorm = gumWorm;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.gumWorm.getX());
        this.y = (double)((float)this.gumWorm.getY());
        this.z = (double)((float)this.gumWorm.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.gumWorm.isAlive() && !this.gumWorm.isSilent() && this.gumWorm.isDigging();
    }

    public void tick() {
        if (this.gumWorm.isAlive() && this.gumWorm.isDigging()) {
            this.x = (double)((float)this.gumWorm.getX());
            this.y = (double)((float)this.gumWorm.getY());
            this.z = (double)((float)this.gumWorm.getZ());
            float f = (float)this.gumWorm.getDeltaMovement().length();
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

    public boolean isSameEntity(GumWormEntity entity) {
        return this.gumWorm.isAlive() && this.gumWorm.getId() == entity.getId();
    }
}
