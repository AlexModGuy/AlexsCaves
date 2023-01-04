package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class IrradiatedEffect extends MobEffect {

    protected IrradiatedEffect() {
        super(MobEffectCategory.HARMFUL, 0X77D60E);
    }

    public void applyEffectTick(LivingEntity entity, int tick) {
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(0.4F);
        }
        entity.hurt(ACDamageTypes.RADIATION, 1.0F);
    }

    public boolean isDurationEffectTick(int tick1, int level) {
        if(level <= 0){
            return false;
        }
        int j = 100 / level;
        if (j > 0) {
            return tick1 % j == 0;
        } else {
            return true;
        }
    }

    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

}
