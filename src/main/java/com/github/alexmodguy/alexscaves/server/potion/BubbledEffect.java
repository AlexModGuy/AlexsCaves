package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BubbledEffect extends MobEffect {

    protected BubbledEffect() {
        super(MobEffectCategory.HARMFUL, 0X21B5FF);
    }

    public void applyEffectTick(LivingEntity entity, int tick) {
        if(entity.canBreatheUnderwater() || entity.getMobType() == MobType.WATER){
            if(!entity.getType().is(ACTagRegistry.RESISTS_BUBBLED)){
                entity.setAirSupply(entity.getMaxAirSupply());
                if(!entity.isOnGround() ){
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.08, 0));
                }
            }
        }else if(!MobEffectUtil.hasWaterBreathing(entity) && !(entity instanceof Player player && player.getAbilities().invulnerable)){
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.8F, 1.0F, 0.8F));
            entity.setAirSupply(Math.max(entity.getAirSupply() - 2, -20));
            if(entity.getAirSupply() <= -20){
                entity.setAirSupply(0);
                Vec3 vec3 = entity.getDeltaMovement();

                for(int i = 0; i < 8; ++i) {
                    double d2 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d3 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d4 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    entity.level.addParticle(ParticleTypes.BUBBLE, entity.getX() + d2, entity.getY() + d3, entity.getZ() + d4, vec3.x, vec3.y, vec3.z);
                }
                entity.hurt(entity.damageSources().drown(), 2.0F);
            }
        }
    }

    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }

    public List<ItemStack> getCurativeItems() {
        return List.of();
    }
}
