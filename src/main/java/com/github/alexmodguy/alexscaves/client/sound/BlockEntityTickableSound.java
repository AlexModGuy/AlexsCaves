package com.github.alexmodguy.alexscaves.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityTickableSound<T extends BlockEntity> extends AbstractTickableSoundInstance implements UnlimitedPitch {
    protected final T blockEntity;

    public BlockEntityTickableSound(SoundEvent soundEvent, T blockEntity) {
        super(soundEvent, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.blockEntity = blockEntity;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.looping = true;
        this.x = this.blockEntity.getBlockPos().getX() + 0.5D;
        this.y = this.blockEntity.getBlockPos().getY() + 0.5D;
        this.z = this.blockEntity.getBlockPos().getZ() + 0.5D;
        this.delay = 0;
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameBlockEntity(T blockEntity) {
        return this.blockEntity.getBlockPos().equals(blockEntity.getBlockPos());
    }
}
