package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;

public class NuclearSirenSound extends BlockEntityTickableSound<NuclearSirenBlockEntity> {

    public NuclearSirenSound(NuclearSirenBlockEntity siren) {
        super(ACSoundRegistry.NUCLEAR_SIREN.get(), siren);
        this.volume = 0.1f;
    }

    public boolean canPlaySound() {
        return !this.blockEntity.isRemoved() && this.blockEntity.isActivated(this.blockEntity.getBlockState()) && this.blockEntity.getVolume(1.0F) > 0F;
    }

    public void tick() {
        if (this.blockEntity != null && !this.blockEntity.isRemoved()) {
            this.x = this.blockEntity.getBlockPos().getX() + 0.5D;
            this.y = this.blockEntity.getBlockPos().getY() + 0.5D;
            this.z = this.blockEntity.getBlockPos().getZ() + 0.5D;
            this.volume = this.blockEntity.getVolume(1.0F);
        } else {
            this.stop();
        }
    }
}
