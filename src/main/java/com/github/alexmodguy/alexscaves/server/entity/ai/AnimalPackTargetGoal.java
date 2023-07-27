package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import net.minecraft.world.entity.TamableAnimal;

public class AnimalPackTargetGoal extends MobTargetUntamedGoal {

    public PackAnimal packAnimal;
    public int packSizeMandatory;

    public AnimalPackTargetGoal(TamableAnimal mob, Class aClass, int chance, boolean sight, int packSizeMandatory) {
        super(mob, aClass, chance, sight, false, null);
        packAnimal = (PackAnimal) mob;
        this.packSizeMandatory = packSizeMandatory;
    }

    public boolean canUse() {
        if (super.canUse()) {
            return packAnimal.getPackSize() >= packSizeMandatory;
        }
        return false;
    }
}
