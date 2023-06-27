package com.github.alexmodguy.alexscaves.server.entity.util;

public interface UnderzealotSacrifice {

    void triggerSacrificeIn(int time);

    boolean isValidSacrifice(int distanceFromGround);

}
