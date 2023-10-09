package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class QuarrySmasherSound extends AbstractTickableSoundInstance {
    private final QuarrySmasherEntity quarrySmasherEntity;

    private float moveFade = 0;

    private float prevChainLength = 0;

    public QuarrySmasherSound(QuarrySmasherEntity quarrySmasherEntity) {
        super(ACSoundRegistry.BOUNDROID_CHAIN_LOOP.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.quarrySmasherEntity = quarrySmasherEntity;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.quarrySmasherEntity.getX());
        this.y = (double)((float)this.quarrySmasherEntity.getY());
        this.z = (double)((float)this.quarrySmasherEntity.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return this.quarrySmasherEntity.isAlive() && !this.quarrySmasherEntity.isSilent();
    }

    public void tick() {
        if (this.quarrySmasherEntity.isAlive()) {
            this.x = (double)((float)this.quarrySmasherEntity.getX());
            this.y = (double)((float)this.quarrySmasherEntity.getY());
            this.z = (double)((float)this.quarrySmasherEntity.getZ());
            float f = 0.0F;
            if(this.quarrySmasherEntity.headPart != null){
                f = this.quarrySmasherEntity.distanceTo(this.quarrySmasherEntity.headPart);
            }
            float f1 = Math.min(Math.abs(prevChainLength - f) * 20F, 1.0F);
            if (f1 <= 0.3F) {
                this.moveFade = Math.min(1F, this.moveFade + 0.1F);
            }else{
                this.moveFade = Math.max(0F, this.moveFade - 0.25F);
            }
            this.volume = 1.0F - moveFade;
            this.pitch = 1.0F + f1;
            this.prevChainLength = f;
        } else {
            this.volume = 0.0F;
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(QuarrySmasherEntity entity) {
        return this.quarrySmasherEntity.isAlive() && this.quarrySmasherEntity.getId() == entity.getId();
    }
}
