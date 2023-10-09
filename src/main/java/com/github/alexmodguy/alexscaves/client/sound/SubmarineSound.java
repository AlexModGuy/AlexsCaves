package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class SubmarineSound extends AbstractTickableSoundInstance {
    private final SubmarineEntity submarine;

    private float moveFade = 0;

    public SubmarineSound(SubmarineEntity submarine) {
        super(ACSoundRegistry.SUBMARINE_MOVE_LOOP.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.submarine = submarine;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.submarine.getX());
        this.y = (double)((float)this.submarine.getY());
        this.z = (double)((float)this.submarine.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.submarine.isRemoved() && !this.submarine.isSilent();
    }

    public void tick() {
        if (this.submarine.isAlive()) {
            this.x = (double)((float)this.submarine.getX());
            this.y = (double)((float)this.submarine.getY());
            this.z = (double)((float)this.submarine.getZ());
            float f = submarine.getAcceleration();
            if (f <= 0.1F || !this.submarine.isVehicle() || !this.submarine.isInWaterOrBubble()) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            }else{
                this.moveFade = Math.max(0F, this.moveFade - 0.25F);
            }
            float f1 = 1F - moveFade;
            this.volume = f1;
            this.pitch = 0.8F + f1 * 0.4F;
        } else {
            this.volume = 0.0F;
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(SubmarineEntity entity) {
        return this.submarine.isAlive() && this.submarine.getId() == entity.getId();
    }
}
