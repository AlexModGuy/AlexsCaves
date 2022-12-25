package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ACDamageTypes {

    public static final DamageSource ACID = new DamageSourceRandomMessages("acid", 1).bypassArmor();

    private static class DamageSourceRandomMessages extends DamageSource {

        private int messageCount;

        public DamageSourceRandomMessages(String message, int messageCount) {
            super(message);
            this.messageCount = messageCount;
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(this.messageCount);
            String s = "death.attack." + this.msgId + "_" + type;
            return Component.translatable(s, attacked.getDisplayName());
        }
    }
}
