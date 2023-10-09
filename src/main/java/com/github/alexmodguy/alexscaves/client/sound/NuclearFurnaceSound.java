package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearFurnaceBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

public class NuclearFurnaceSound extends BlockEntityTickableSound<NuclearFurnaceBlockEntity> {

    private final int criticality;
    private int fade = 0;

    public NuclearFurnaceSound(NuclearFurnaceBlockEntity furnace) {
        super(getSoundFromFurnaceCriticality(furnace.getCriticality()), furnace);
        this.volume = 0.1f;
        this.criticality = furnace.getCriticality();

    }

    private static SoundEvent getSoundFromFurnaceCriticality(int criticality) {
        return criticality >= 3 ? ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE_SUPERCRITICAL.get() : criticality == 2 ? ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE_CRITICAL.get() : criticality > 0 ? ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE_SUBCRITICAL.get() : ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE.get();
    }

    public boolean canPlaySound() {
        return !this.blockEntity.isRemoved();
    }

    @Override
    public boolean isSameBlockEntity(NuclearFurnaceBlockEntity blockEntity) {
        return super.isSameBlockEntity(blockEntity) && criticality == blockEntity.getCriticality();
    }


    public void tick() {
        if ((this.blockEntity.isUndergoingFission() || criticality > 0) && criticality == this.blockEntity.getCriticality()) {
            this.x = this.blockEntity.getBlockPos().getX() + 1D;
            this.y = this.blockEntity.getBlockPos().getY() + 1D;
            this.z = this.blockEntity.getBlockPos().getZ() + 1D;
            this.pitch = 1.0F;
            if(fade > 0){
                fade--;
            }
        } else {
            fade++;
        }
        this.volume = Mth.clamp(1F - fade / 40F, 0F, 1F);
        if(fade > 40){
            this.stop();
        }
    }
}
