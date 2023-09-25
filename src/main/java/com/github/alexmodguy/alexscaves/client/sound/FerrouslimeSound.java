package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.FerrouslimeEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class FerrouslimeSound extends AbstractTickableSoundInstance {
    private final FerrouslimeEntity ferrouslime;

    private float moveFade = 0;

    public FerrouslimeSound(FerrouslimeEntity ferrouslime) {
        super(ACSoundRegistry.FERROUSLIME_MOVE_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.ferrouslime = ferrouslime;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.ferrouslime.getX());
        this.y = (double)((float)this.ferrouslime.getY());
        this.z = (double)((float)this.ferrouslime.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.ferrouslime.isAlive() && !this.ferrouslime.isSilent();
    }

    public void tick() {
        if (this.ferrouslime.isAlive()) {
            this.x = (double)((float)this.ferrouslime.getX());
            this.y = (double)((float)this.ferrouslime.getY());
            this.z = (double)((float)this.ferrouslime.getZ());
            float f = (float)this.ferrouslime.getDeltaMovement().length();
            if (f <= 0.01F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            }else{
                this.moveFade = Math.max(0F, this.moveFade - 0.25F);
            }
            float f1 = (3.0F - Mth.clamp(this.ferrouslime.getSlimeSize(1.0F), 1.0F, 3.0F)) / 3.0F;
            this.volume = 1F - moveFade;
            this.pitch = 1F - f1;
        } else {
            this.volume = 0.0F;
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(FerrouslimeEntity entity) {
        return this.ferrouslime.isAlive() && this.ferrouslime.getId() == entity.getId();
    }
}
