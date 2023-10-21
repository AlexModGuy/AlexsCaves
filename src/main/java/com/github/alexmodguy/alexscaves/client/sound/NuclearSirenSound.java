package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class NuclearSirenSound extends BlockEntityTickableSound<NuclearSirenBlockEntity> {

    public NuclearSirenSound(NuclearSirenBlockEntity siren) {
        super(ACSoundRegistry.NUCLEAR_SIREN.get(), siren);
    }

    public boolean canPlaySound() {
        return !this.blockEntity.isRemoved() && this.blockEntity.isActivated(this.blockEntity.getBlockState()) && this.blockEntity.getVolume(1.0F) > 0F;
    }

    public void tick() {
        if (this.blockEntity != null && !this.blockEntity.isRemoved()) {
            this.x = this.blockEntity.getBlockPos().getX() + 0.5D;
            this.y = this.blockEntity.getBlockPos().getY() + 0.5D;
            this.z = this.blockEntity.getBlockPos().getZ() + 0.5D;
            this.volume = this.blockEntity.getVolume(1.0F) * (1F - ClientProxy.masterVolumeNukeModifier);
        } else {
            this.stop();
        }
    }
}
