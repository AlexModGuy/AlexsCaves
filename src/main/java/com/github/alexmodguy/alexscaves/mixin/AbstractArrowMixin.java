package com.github.alexmodguy.alexscaves.mixin;


import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/projectile/AbstractArrow;<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    private void ac_playerConstructor(EntityType arrowEntityType, LivingEntity shooter, Level level, CallbackInfo ci) {
        if(MagnetUtil.getEntityMagneticDirection(shooter) != Direction.DOWN){
            this.setPos(shooter.getEyePosition().add(0, -0.1, 0));
        }
    }
}
