package com.github.alexmodguy.alexscaves.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class ItemTickableSound extends AbstractTickableSoundInstance {

    protected final LivingEntity user;

    public ItemTickableSound(LivingEntity user, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.user = user;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.x = (double)((float)this.user.getX());
        this.y = (double)((float)this.user.getY());
        this.z = (double)((float)this.user.getZ());
        this.delay = 0;
    }

    public boolean canPlaySound() {
        return !this.user.isSilent() && this.user.isUsingItem() && (isValidItem(this.user.getItemInHand(InteractionHand.MAIN_HAND)) || isValidItem(this.user.getItemInHand(InteractionHand.OFF_HAND)));
    }

    public void tick() {
        ItemStack itemStack = ItemStack.EMPTY;
        if(user.isUsingItem()){
            if(isValidItem(this.user.getItemInHand(InteractionHand.MAIN_HAND))){
                itemStack = this.user.getItemInHand(InteractionHand.MAIN_HAND);
            }
            if(isValidItem(this.user.getItemInHand(InteractionHand.OFF_HAND))){
                itemStack = this.user.getItemInHand(InteractionHand.OFF_HAND);
            }
        }
        if (this.user.isAlive() && !itemStack.isEmpty()) {
            this.x = (double)((float)this.user.getX());
            this.y = (double)((float)this.user.getY());
            this.z = (double)((float)this.user.getZ());
            tickVolume(itemStack);
        } else {
            this.stop();
        }
    }

    protected abstract void tickVolume(ItemStack itemStack);

    public abstract boolean isValidItem(ItemStack itemStack);

    public boolean canStartSilent() {
        return true;
    }

    public boolean isSameEntity(LivingEntity user) {
        return this.user.isAlive() && this.user.getId() == user.getId();
    }
}
