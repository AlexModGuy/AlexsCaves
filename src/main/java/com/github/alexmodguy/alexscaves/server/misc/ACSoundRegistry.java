package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACSoundRegistry {
    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AlexsCaves.MODID);

    public static final RegistryObject<SoundEvent> MAGNETIC_CAVES_MUSIC = createSoundEvent("magnetic_caves_music");
    public static final RegistryObject<SoundEvent> PRIMORDIAL_CAVES_MUSIC = createSoundEvent("primordial_caves_music");
    public static final RegistryObject<SoundEvent> TOXIC_CAVES_MUSIC = createSoundEvent("toxic_caves_music");
    public static final RegistryObject<SoundEvent> ABYSSAL_CHASM_MUSIC = createSoundEvent("abyssal_chasm_music");
    public static final RegistryObject<SoundEvent> FORLORN_HOLLOWS_MUSIC = createSoundEvent("forlorn_hollows_music");

    public static final RegistryObject<SoundEvent> AMBER_STEP = createSoundEvent("amber_step");
    public static final RegistryObject<SoundEvent> AMBER_PLACE = createSoundEvent("amber_place");
    public static final RegistryObject<SoundEvent> AMBER_BREAK = createSoundEvent("amber_break");
    public static final RegistryObject<SoundEvent> AMBER_BREAKING = createSoundEvent("amber_breaking");

    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_IDLE = createSoundEvent("vallumraptor_idle");
    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_HURT = createSoundEvent("vallumraptor_hurt");
    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_DEATH = createSoundEvent("vallumraptor_death");
    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_CALL = createSoundEvent("vallumraptor_call");
    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_ATTACK = createSoundEvent("vallumraptor_attack");
    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_SCRATCH = createSoundEvent("vallumraptor_scratch");
    public static final RegistryObject<SoundEvent> VALLUMRAPTOR_SLEEP = createSoundEvent("vallumraptor_sleep");
    public static final RegistryObject<SoundEvent> VESPER_IDLE = createSoundEvent("vesper_idle");
    public static final RegistryObject<SoundEvent> VESPER_HURT = createSoundEvent("vesper_hurt");
    public static final RegistryObject<SoundEvent> VESPER_DEATH = createSoundEvent("vesper_death");
    public static final RegistryObject<SoundEvent> VESPER_QUIET_IDLE = createSoundEvent("vesper_quiet_idle");
    public static final RegistryObject<SoundEvent> VESPER_FLAP = createSoundEvent("vesper_flap");
    public static final RegistryObject<SoundEvent> VESPER_SCREAM = createSoundEvent("vesper_scream");

    public static final RegistryObject<SoundEvent> DISAPPOINTMENT = createSoundEvent("disappointment");
    public static final RegistryObject<SoundEvent> NUCLEAR_SIREN = createSoundEvent("nuclear_siren");

    private static RegistryObject<SoundEvent> createSoundEvent(final String soundName) {
        return DEF_REG.register(soundName, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AlexsCaves.MODID, soundName)));
    }
}
