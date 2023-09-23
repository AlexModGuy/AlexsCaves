package com.github.alexmodguy.alexscaves.client.sound;
import com.github.alexmodguy.alexscaves.server.item.ResistorShieldItem;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ResistorShieldSound extends ItemTickableSound {

    private boolean azure;

    public ResistorShieldSound(LivingEntity user, boolean azure) {
        super(user, azure ? ACSoundRegistry.RESITOR_SHIELD_AZURE_LOOP.get() : ACSoundRegistry.RESITOR_SHIELD_SCARLET_LOOP.get());
        this.azure = azure;
    }

    public void tickVolume(ItemStack itemStack) {
        float useAmount = ResistorShieldItem.getLerpedUseTime(itemStack, 1.0F) / 5F;
        this.volume = useAmount;
        this.pitch = 0.2F + 0.8F * useAmount;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        if(itemStack.getItem() instanceof ResistorShieldItem){
            return ResistorShieldItem.isScarlet(itemStack) == !azure;
        }
        return false;
    }

    public boolean isAzure(){
        return azure;
    }
}
