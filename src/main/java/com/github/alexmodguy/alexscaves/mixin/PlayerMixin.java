package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.server.entity.IModifiesTime;
import com.github.alexthe666.citadel.server.tick.modifier.LocalEntityTickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IModifiesTime {

    @Shadow public abstract float getSpeed();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }


    @Inject(
            method = {"Lnet/minecraft/world/entity/player/Player;getSpeed()F"},
            remap = true,
            cancellable = true,
            at = @At(value = "RETURN")
    )
    public void ac_getSpeed(CallbackInfoReturnable<Float> cir) {
        if (AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get() && this.hasEffect(ACEffectRegistry.SUGAR_RUSH.get()) && AlexsCaves.PROXY.isTickRateModificationActive(this.level())) {
            cir.setReturnValue(cir.getReturnValue() * 3.0F);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/player/Player;getFlyingSpeed()F"},
            remap = true,
            cancellable = true,
            at = @At(value = "RETURN")
    )
    public void ac_getFlyingSpeed(CallbackInfoReturnable<Float> cir) {
        if (AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get() && this.hasEffect(ACEffectRegistry.SUGAR_RUSH.get()) && AlexsCaves.PROXY.isTickRateModificationActive(this.level())) {
            cir.setReturnValue(this.getSpeed() * 0.5F);
        }
    }

    @Override
    public boolean isTimeModificationValid(TickRateModifier tickRateModifier){
        return !(tickRateModifier instanceof LocalEntityTickRateModifier) || this.hasEffect(ACEffectRegistry.SUGAR_RUSH.get());
    }
}
