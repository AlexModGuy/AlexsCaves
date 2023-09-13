package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.NotorEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class NotorHologramSound extends AbstractTickableSoundInstance {
    private final NotorEntity notor;

    public NotorHologramSound(NotorEntity notor) {
        super(ACSoundRegistry.HOLOGRAM_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.notor = notor;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.notor.getX());
        this.y = (double)((float)this.notor.getY());
        this.z = (double)((float)this.notor.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.notor.isAlive() && !this.notor.isSilent() && (this.notor.showingHologram() || this.notor.getScanningMob() != null);
    }

    public void tick() {
        if (this.notor.isAlive() && (this.notor.showingHologram() || this.notor.getScanningMob() != null)) {
            this.x = (double)((float)this.notor.getX());
            this.y = (double)((float)this.notor.getY());
            this.z = (double)((float)this.notor.getZ());
            float f = this.notor.getBeamProgress(1.0F);
            this.volume = 4.0F * f;
            this.pitch = 1F;
        } else {
            this.stop();
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(NotorEntity entity) {
        return this.notor.isAlive() && this.notor.getId() == entity.getId();
    }
}
