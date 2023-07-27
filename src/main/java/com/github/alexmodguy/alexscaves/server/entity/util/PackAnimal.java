package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.world.entity.LivingEntity;

public interface PackAnimal {

    default boolean isPackFollower() {
        return this.getPriorPackMember() != null;
    }

    default boolean hasPackFollower() {
        return this.getAfterPackMember() != null;
    }

    default PackAnimal getPackLeader() {
        PackAnimal leader = this;
        while (leader.getPriorPackMember() != null && leader.getPriorPackMember() != this) {
            leader = leader.getPriorPackMember();
        }
        return leader;
    }

    default int getPackSize() {
        PackAnimal leader = getPackLeader();
        int i = 1;
        while (leader.getAfterPackMember() != null) {
            leader = leader.getAfterPackMember();
            i++;
        }
        return i;
    }

    default boolean isInPack(PackAnimal packAnimal) {
        PackAnimal leader = getPackLeader();
        while (leader.getAfterPackMember() != null) {
            if (packAnimal.equals(leader)) {
                return true;
            }
            leader = leader.getAfterPackMember();
        }
        return false;
    }

    default boolean isValidLeader(PackAnimal packLeader) {
        return !packLeader.isPackFollower() && ((LivingEntity) packLeader).isAlive();
    }

    PackAnimal getPriorPackMember();

    PackAnimal getAfterPackMember();

    void setPriorPackMember(PackAnimal animal);

    void setAfterPackMember(PackAnimal animal);

    default void joinPackOf(PackAnimal caravanHeadIn) {
        setPriorPackMember(caravanHeadIn);
        caravanHeadIn.setAfterPackMember(this);
        resetPackFlags();
    }

    default void leavePack() {
        if (this.getPriorPackMember() != null) {
            this.getPriorPackMember().setAfterPackMember(null);
        }
        this.setPriorPackMember(null);
        resetPackFlags();
    }

    default void resetPackFlags() {
    }
}
