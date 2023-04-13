package com.github.alexmodguy.alexscaves.server.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class DeepsightEffect extends MobEffect {

    private int lastDuration = -1;
    private int firstDuration = -1;
    protected DeepsightEffect() {
        super(MobEffectCategory.BENEFICIAL, 0X002972);
    }

    public int getActiveTime(){
        return firstDuration - lastDuration;
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        if(duration <= 0){
            lastDuration = -1;
            firstDuration = -1;
        }
        if(firstDuration == -1){
            firstDuration = duration;
        }
        return duration > 0;
    }

    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
        lastDuration = -1;
        firstDuration = -1;
        super.removeAttributeModifiers(entity, map, i);
    }

    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
        lastDuration = -1;
        firstDuration = -1;
        super.addAttributeModifiers(entity, map, i);
    }


    public static float getIntensity(Player player, float partialTicks){
        MobEffectInstance instance = player.getEffect(ACEffectRegistry.DEEPSIGHT.get());
        if(instance == null){
            return 0.0F;
        }else{
            DeepsightEffect deepsightEffect = (DeepsightEffect) instance.getEffect();
            float j = instance.isInfiniteDuration() ? 0 : deepsightEffect.getActiveTime() + partialTicks;
            int duration = instance.getDuration();
            return Math.min(20, (Math.min(j, duration + partialTicks))) * 0.05F;
        }
    }
}
