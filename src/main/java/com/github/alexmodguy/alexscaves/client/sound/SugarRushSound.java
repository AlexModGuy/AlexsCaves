package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class SugarRushSound extends AbstractTickableSoundInstance implements UnlimitedPitch{

    protected final LivingEntity user;

    public SugarRushSound(LivingEntity user) {
        super(ACSoundRegistry.SUGAR_RUSH_LOOP.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.user = user;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.x = (float) this.user.getX();
        this.y = (float) this.user.getY();
        this.z = (float) this.user.getZ();
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.user.isSilent() && this.user.hasEffect(ACEffectRegistry.SUGAR_RUSH.get());
    }

    public void tick() {
        if (this.user.isAlive() && user.hasEffect(ACEffectRegistry.SUGAR_RUSH.get())) {
            this.x = (float) this.user.getX();
            this.y = (float) this.user.getY();
            this.z = (float) this.user.getZ();
            this.volume = Math.min(1F, this.volume + 0.1F);
            this.pitch = 1.0F;
        } else {
            this.volume = Math.max(this.volume - 0.1F, 0.0F);
        }
    }


    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(LivingEntity user) {
        return this.user.isAlive() && this.user.getId() == user.getId();
    }

}