package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.block.blockentity.MagnetBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;

public class MagnetSound extends BlockEntityTickableSound<MagnetBlockEntity> {

    private float activeAmount = 0.0F;

    public MagnetSound(MagnetBlockEntity magnetBlockEntity) {
        super(magnetBlockEntity.isAzure() ? ACSoundRegistry.AZURE_NEODYMIUM_PUSH_LOOP.get() : ACSoundRegistry.SCARLET_NEODYMIUM_PULL_LOOP.get(), magnetBlockEntity);
        this.volume = 0.0f;
    }

    public boolean canPlaySound() {
        return !this.blockEntity.isRemoved() && this.blockEntity.isLocallyActive();
    }

    public void tick() {
        if (activeAmount < 1.0F && this.blockEntity.isLocallyActive()) {
            activeAmount += 0.1F;
        }
        if (activeAmount > 0 && !this.blockEntity.isLocallyActive()) {
            activeAmount -= 0.1F;
        }
        if (this.blockEntity != null && !this.blockEntity.isRemoved() && !(activeAmount == 0 && !this.blockEntity.isLocallyActive())) {
            this.x = this.blockEntity.getBlockPos().getX() + 0.5D;
            this.y = this.blockEntity.getBlockPos().getY() + 0.5D;
            this.z = this.blockEntity.getBlockPos().getZ() + 0.5D;
            this.volume = activeAmount * 0.5F;
            this.pitch = 1.0F;
        } else {
            this.stop();
        }
    }

    @Override
    public boolean isSameBlockEntity(MagnetBlockEntity blockEntity) {
        return super.isSameBlockEntity(blockEntity) && this.blockEntity.isAzure() == blockEntity.isAzure();
    }

}
