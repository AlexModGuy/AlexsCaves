package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RaygunSound extends AbstractTickableSoundInstance {

    private final LivingEntity user;

    public RaygunSound(LivingEntity user) {
        super(ACSoundRegistry.RAYGUN_LOOP.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.user = user;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.user.getX());
        this.y = (double)((float)this.user.getY());
        this.z = (double)((float)this.user.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.user.isSilent() && this.user.isUsingItem() && (this.user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RaygunItem  || this.user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RaygunItem );
    }

    public void tick() {
        ItemStack itemStack = ItemStack.EMPTY;
        if(this.user.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RaygunItem){
            itemStack = this.user.getItemInHand(InteractionHand.MAIN_HAND);
        }
        if(this.user.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RaygunItem){
            itemStack = this.user.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (this.user.isAlive() && itemStack != ItemStack.EMPTY) {
            this.x = (double)((float)this.user.getX());
            this.y = (double)((float)this.user.getY());
            this.z = (double)((float)this.user.getZ());
            float useAmount = RaygunItem.getLerpedUseTime(itemStack, 1.0F) / 5F;
            this.volume = useAmount;
            this.pitch = 0.2F + 0.8F * useAmount;
        } else {
            this.stop();
        }
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(LivingEntity user) {
        return this.user.isAlive() && this.user.getId() == user.getId();
    }
}
