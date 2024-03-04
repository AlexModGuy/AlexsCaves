package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.server.entity.util.DarknessIncarnateUserAccessor;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;

import java.util.List;

public class DarknessIncarnateEffect extends MobEffect {

    private int lastDuration = -1;
    private int firstDuration = -1;

    protected DarknessIncarnateEffect() {
        super(MobEffectCategory.BENEFICIAL, 0X510E0E);
    }


    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
        toggleFlight(entity, true);
        if (entity.onGround()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1, 0));
        }
        if ((entity.tickCount + entity.getId() * 5) % 50 == 0 && entity.getRandom().nextInt(2) == 0) {
            entity.playSound(ACSoundRegistry.DARKNESS_INCARNATE_IDLE.get());
        }
    }

    public List<ItemStack> getCurativeItems() {
        return List.of();
    }

    public void removeAttributeModifiers(LivingEntity living, AttributeMap attributeMap, int i) {
        lastDuration = -1;
        firstDuration = -1;
        super.removeAttributeModifiers(living, attributeMap, i);
        toggleFlight(living, false);
    }

    public int getActiveTime() {
        return firstDuration - lastDuration;
    }


    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
        lastDuration = -1;
        firstDuration = -1;
        super.addAttributeModifiers(entity, map, i);
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        if (duration <= 0) {
            lastDuration = -1;
            firstDuration = -1;
        }
        if (firstDuration == -1) {
            firstDuration = duration;
        }
        return duration > 0;
    }


    public void toggleFlight(LivingEntity living, boolean flight) {
        if (!living.level().isClientSide && living instanceof ServerPlayer player) {
            boolean prevFlying = player.getAbilities().flying;
            boolean trueFlight = isCreativePlayer(living) || flight;
            player.getAbilities().mayfly = trueFlight;
            player.getAbilities().flying = trueFlight;
            float defaultFlightSpeed = 0.05F;
            if (flight) {
                player.getAbilities().setFlyingSpeed(defaultFlightSpeed * 4.0F);
            } else {
                player.getAbilities().setFlyingSpeed(defaultFlightSpeed);
                if (!player.isSpectator()) {
                    player.getAbilities().flying = false;
                    if(!player.isCreative()){
                        player.getAbilities().mayfly = false;
                    }
                    if(player instanceof DarknessIncarnateUserAccessor darknessIncarnateUserAccessor){
                        darknessIncarnateUserAccessor.setSlowFallingFlag(true);
                    }
                }
            }
            if (prevFlying != flight) {
                player.onUpdateAbilities();
            }
        }
        living.fallDistance = 0.0F;
    }

    public static float getIntensity(LivingEntity player, float partialTicks, float scaleBy) {
        MobEffectInstance instance = player.getEffect(ACEffectRegistry.DARKNESS_INCARNATE.get());
        if (instance == null) {
            return 0.0F;
        } else if (instance.isInfiniteDuration()) {
            return scaleBy;
        } else {
            DarknessIncarnateEffect effect = (DarknessIncarnateEffect) instance.getEffect();
            float j = effect.getActiveTime() + partialTicks;
            int duration = instance.getDuration();
            return Math.min(scaleBy, (Math.min(j, duration + partialTicks))) / scaleBy;
        }
    }

    public static boolean isInLight(LivingEntity living, int threshold) {
        BlockPos samplePos = living.getRootVehicle().blockPosition();
        int lightLevel = living.level().getBrightness(LightLayer.BLOCK, samplePos);
        float timeOfDay = living.level().getTimeOfDay(1.0F); //night starts at 0.259 and ends at 0.74
        if (living.level().canSeeSky(samplePos) && (timeOfDay < 0.259 || timeOfDay > 0.74)) {
            lightLevel = 15;
        }
        return lightLevel >= threshold;
    }

    private boolean isCreativePlayer(LivingEntity living) {
        return living instanceof Player player && (player.isCreative() || player.isSpectator());
    }
}
