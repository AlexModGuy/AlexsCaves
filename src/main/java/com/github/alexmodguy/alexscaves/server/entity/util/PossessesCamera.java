package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.world.entity.Entity;

public interface PossessesCamera {

    float getPossessionStrength(float f);

    boolean instant();

    boolean isPossessionBreakable();

    void onPossessionKeyPacket(Entity keyPresser, int type);
}
