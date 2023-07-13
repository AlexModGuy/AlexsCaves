package com.github.alexmodguy.alexscaves.server.potion;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class MagnetizedEffect extends MobEffect {

    protected MagnetizedEffect() {
        super(MobEffectCategory.NEUTRAL, 0X53556C);
    }

    public void applyEffectTick(LivingEntity entity, int tick) {
        if(!entity.level().isClientSide && entity.tickCount % 20 == 0){
            MobEffectInstance instance = entity.getEffect(this);
            if(instance != null){
                AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntity(entity.getId(), entity.getId(), 2, instance.getDuration()));
            }
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
        if(!entity.level().isClientSide){
            MobEffectInstance instance = entity.getEffect(this);
            if(instance != null){
                AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntity(entity.getId(), entity.getId(), 2, instance.getDuration()));
            }
        }
        super.addAttributeModifiers(entity, map, i);
    }

}
