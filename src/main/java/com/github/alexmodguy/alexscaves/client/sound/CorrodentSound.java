package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class CorrodentSound extends AbstractTickableSoundInstance {
    private final CorrodentEntity corrodent;

    private float moveFade = 0;

    public CorrodentSound(CorrodentEntity corrodent) {
        super(ACSoundRegistry.CORRODENT_DIG_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.corrodent = corrodent;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.corrodent.getX());
        this.y = (double)((float)this.corrodent.getY());
        this.z = (double)((float)this.corrodent.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.corrodent.isAlive() && !this.corrodent.isSilent() && this.corrodent.isDigging();
    }

    public void tick() {
        if (this.corrodent.isAlive() && this.corrodent.isDigging()) {
            this.x = (double)((float)this.corrodent.getX());
            this.y = (double)((float)this.corrodent.getY());
            this.z = (double)((float)this.corrodent.getZ());
            float f = (float)this.corrodent.getDeltaMovement().length();
            this.pitch = 1.0F;
            if (f <= 0.01F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            }else{
                this.moveFade = Math.max(0F, this.moveFade - 0.25F);
            }
            this.volume = 1F - moveFade;
        } else {
            this.volume = 0.0F;
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(CorrodentEntity entity) {
        return this.corrodent.isAlive() && this.corrodent.getId() == entity.getId();
    }
}
