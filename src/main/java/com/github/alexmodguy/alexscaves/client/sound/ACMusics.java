package com.github.alexmodguy.alexscaves.client.sound;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class ACMusics {

    public static final ForgeMusic LUXTRUCTOSAURUS_BOSS_MUSIC = new ForgeMusic(ACSoundRegistry.LUXTRUCTOSAURUS_BOSS_MUSIC, 0, 0, true);

    private static class ForgeMusic extends Music {

        private final RegistryObject<SoundEvent> registryObject;

        public ForgeMusic(RegistryObject<SoundEvent> registryObject, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
            super(null, minDelay, maxDelay, replaceCurrentMusic);
            this.registryObject = registryObject;
        }

        @Override
        public Holder<SoundEvent> getEvent() {
            return this.registryObject.getHolder().get();
        }
    }
}
