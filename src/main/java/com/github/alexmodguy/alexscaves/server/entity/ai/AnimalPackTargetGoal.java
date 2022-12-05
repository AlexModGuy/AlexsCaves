package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.util.PackAnimal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class AnimalPackTargetGoal extends NearestAttackableTargetGoal {

    public PackAnimal packAnimal;
    public int packSizeMandatory;

    public AnimalPackTargetGoal(Mob mob, Class aClass, int chance, boolean sight, int packSizeMandatory) {
        super(mob, aClass, chance, sight, false, null);
        packAnimal = (PackAnimal) mob;
        this.packSizeMandatory = packSizeMandatory;
    }

    public boolean canUse() {
        if(super.canUse()){
            return packAnimal.getPackSize() >= packSizeMandatory;
        }
        return false;
    }
}
