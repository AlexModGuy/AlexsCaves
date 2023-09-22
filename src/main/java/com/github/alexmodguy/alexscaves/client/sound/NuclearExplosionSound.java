package com.github.alexmodguy.alexscaves.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class NuclearExplosionSound extends AbstractTickableSoundInstance implements UnlimitedPitch {

    private int tickCount = 0;
    private final int duration;
    private final int fadesAt;
    private final float fadeInBy;

    public NuclearExplosionSound(SoundEvent soundEvent, double x, double y, double z, int duration, int fadesAt, float fadeInBy, boolean looping) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.attenuation = Attenuation.LINEAR;
        this.looping = looping;
        this.x = x;
        this.y = y;
        this.z = z;
        this.duration = duration;
        this.fadesAt = fadesAt;
        this.fadeInBy = fadeInBy;
        this.delay = 0;
        this.volume = 0;
    }

    public void tick() {
        if(tickCount < this.duration){
            if(tickCount >= fadesAt){
                float shrinkVolumeFor = 1F / Math.max(this.duration - fadesAt, 1F);
                volume = Math.max(0, volume - shrinkVolumeFor);
            }else if(volume < 1F){
                volume = Math.min(1F, volume + fadeInBy);
            }
            tickCount++;
        }else{
            stop();
        }
    }

    public boolean canStartSilent() {
        return true;
    }
}
