package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.UnderzealotEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class UnderzealotSound extends AbstractTickableSoundInstance implements UnlimitedPitch {
    private final UnderzealotEntity underzealot;

    public UnderzealotSound(UnderzealotEntity underzealot) {
        super(ACSoundRegistry.UNDERZEALOT_CHANT.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.underzealot = underzealot;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.underzealot.getX());
        this.y = (double)((float)this.underzealot.getY());
        this.z = (double)((float)this.underzealot.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.underzealot.isSilent() && this.underzealot.isPraying() && this.underzealot.getWorshipTime() < UnderzealotEntity.MAX_WORSHIP_TIME;
    }

    public void tick() {
        if (this.underzealot.isAlive() && this.underzealot.isPraying()) {
            this.x = (double)((float)this.underzealot.getX());
            this.y = (double)((float)this.underzealot.getY());
            this.z = (double)((float)this.underzealot.getZ());
            int time = this.underzealot.getWorshipTime();
            float f = time < 60 ? time / 60F : 1F;
            float f1 = 1F - (time / (float)UnderzealotEntity.MAX_WORSHIP_TIME);
            this.volume = f * 5F;
            this.pitch = 0.3F + f1 * 0.7F;
        } else {
            this.stop();
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(UnderzealotEntity nucleeper) {
        return this.underzealot.isAlive() && this.underzealot.getId() == nucleeper.getId();
    }
}
