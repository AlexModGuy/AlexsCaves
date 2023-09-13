package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class NucleeperSound extends AbstractTickableSoundInstance implements UnlimitedPitch {
    private final NucleeperEntity nucleeper;

    public NucleeperSound(NucleeperEntity nucleeper) {
        super(ACSoundRegistry.NUCLEEPER_CHARGE.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.nucleeper = nucleeper;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.nucleeper.getX());
        this.y = (double)((float)this.nucleeper.getY());
        this.z = (double)((float)this.nucleeper.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.nucleeper.isSilent() && this.nucleeper.isTriggered();
    }

    public void tick() {
        if (this.nucleeper.isAlive() && this.nucleeper.isTriggered()) {
            this.x = (double)((float)this.nucleeper.getX());
            this.y = (double)((float)this.nucleeper.getY());
            this.z = (double)((float)this.nucleeper.getZ());
            float f = this.nucleeper.getCloseProgress(0.0F);
            float f1 = (float) Math.pow(f, 0.5F);
            this.volume = 1.0F + f * 2.0F;
            this.pitch = 1F + 6F * f1;
        } else {
            this.stop();
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(NucleeperEntity nucleeper) {
        return this.nucleeper.isAlive() && this.nucleeper.getId() == nucleeper.getId();
    }
}
