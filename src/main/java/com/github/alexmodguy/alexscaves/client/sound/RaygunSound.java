package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.RaygunItem;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RaygunSound extends ItemTickableSound {

    public RaygunSound(LivingEntity user) {
        super(user, ACSoundRegistry.RAYGUN_LOOP.get());
    }

    public void tickVolume(ItemStack itemStack) {
        float useAmount = RaygunItem.getLerpedUseTime(itemStack, 1.0F) / 5F;
        this.volume = useAmount;
        this.pitch = 0.2F + 0.8F * useAmount;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return itemStack.is(ACItemRegistry.RAYGUN.get());
    }
}
