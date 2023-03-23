package com.github.alexmodguy.alexscaves.server.entity.util;

public enum DeepOneReaction {
    STALKING(0, 80),
    AGGRESSIVE(0,40),
    NEUTRAL(10, 25),
    HELPFUL(8, 30);

    private double minDistance;
    private double maxDistance;
    DeepOneReaction(double minDistance, double maxDistance) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public double getMinDistance(){
        return this.minDistance;
    }

    public double getMaxDistance(){
        return this.maxDistance;
    }

    public static DeepOneReaction fromReputation(int rep){
        if(rep <= -10){
            return AGGRESSIVE;
        }
        if(rep <= 10){
            return STALKING;
        }
        if(rep <= 30){
            return NEUTRAL;
        }
        return HELPFUL;
    }
}
