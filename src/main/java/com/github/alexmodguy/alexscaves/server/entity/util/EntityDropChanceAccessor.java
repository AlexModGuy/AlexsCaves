package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;

public interface EntityDropChanceAccessor {

    float ac_getEquipmentDropChance(EquipmentSlot equipmentSlot);

    void ac_setDropChance(EquipmentSlot equipmentSlot, float chance);

    void ac_dropCustomDeathLoot(DamageSource damageSource, int i1, boolean idk);
}
