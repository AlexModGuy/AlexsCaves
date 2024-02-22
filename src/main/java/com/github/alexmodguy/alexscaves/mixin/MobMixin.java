package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.EntityDropChanceAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements EntityDropChanceAccessor {

    @Shadow protected abstract float getEquipmentDropChance(EquipmentSlot p_21520_);

    @Shadow public abstract void setDropChance(EquipmentSlot p_21410_, float p_21411_);

    @Shadow private boolean canPickUpLoot;

    @Shadow protected abstract void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_);

    public MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public float ac_getEquipmentDropChance(EquipmentSlot equipmentSlot){
        return this.getEquipmentDropChance(equipmentSlot);
    }

    public void ac_setDropChance(EquipmentSlot equipmentSlot, float chance){
        this.setDropChance(equipmentSlot, chance);
    }

    public void ac_dropCustomDeathLoot(DamageSource damageSource, int i1, boolean idk){
        this.dropCustomDeathLoot(damageSource, i1, idk);
    }
}
