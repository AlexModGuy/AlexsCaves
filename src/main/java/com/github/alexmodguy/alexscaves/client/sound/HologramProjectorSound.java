package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.block.blockentity.HologramProjectorBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.Minecraft;

public class HologramProjectorSound extends BlockEntityTickableSound<HologramProjectorBlockEntity> {

    public HologramProjectorSound(HologramProjectorBlockEntity hologramProjectorBlockEntity) {
        super(ACSoundRegistry.HOLOGRAM_LOOP.get(), hologramProjectorBlockEntity);
        this.volume = 0.1f;
    }

    public boolean canPlaySound() {
        return !this.blockEntity.isRemoved() && (this.blockEntity.isPlayerRender() || this.blockEntity.getDisplayEntity(Minecraft.getInstance().level) != null) && this.blockEntity.getSwitchAmount(1.0F) > 0;
    }

    public void tick() {
        if (this.blockEntity != null && !this.blockEntity.isRemoved()) {
            this.x = this.blockEntity.getBlockPos().getX() + 0.5D;
            this.y = this.blockEntity.getBlockPos().getY() + 0.5D;
            this.z = this.blockEntity.getBlockPos().getZ() + 0.5D;
            float f = this.blockEntity.getSwitchAmount(1.0F);
            this.volume = f * 0.5F;
            this.pitch = 1.0F;
        } else {
            this.stop();
        }
    }
}
