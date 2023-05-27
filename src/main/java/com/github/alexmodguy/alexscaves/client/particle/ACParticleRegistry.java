package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AlexsCaves.MODID);

    public static final RegistryObject<SimpleParticleType> SCARLET_MAGNETIC_ORBIT = DEF_REG.register("scarlet_magnetic_orbit", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> AZURE_MAGNETIC_ORBIT = DEF_REG.register("azure_magnetic_orbit", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SCARLET_MAGNETIC_FLOW = DEF_REG.register("scarlet_magnetic_flow", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> AZURE_MAGNETIC_FLOW = DEF_REG.register("azure_magnetic_flow", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GALENA_DEBRIS = DEF_REG.register("galena_debris", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TESLA_BULB_LIGHTNING = DEF_REG.register("tesla_bulb_lightning", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MAGNET_LIGHTNING = DEF_REG.register("magnet_lightning", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MAGNETIC_CAVES_AMBIENT = DEF_REG.register("magnetic_caves_ambient", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FERROUSLIME = DEF_REG.register("ferrouslime", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FLY = DEF_REG.register("fly", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WATER_TREMOR = DEF_REG.register("water_tremor", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ACID_BUBBLE = DEF_REG.register("acid_bubble", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BLACK_VENT_SMOKE = DEF_REG.register("black_vent_smoke", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WHITE_VENT_SMOKE = DEF_REG.register("white_vent_smoke", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GREEN_VENT_SMOKE = DEF_REG.register("green_vent_smoke", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MUSHROOM_CLOUD = DEF_REG.register("mushroom_cloud", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MUSHROOM_CLOUD_SMOKE = DEF_REG.register("mushroom_cloud_smoke", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MUSHROOM_CLOUD_EXPLOSION = DEF_REG.register("mushroom_cloud_explosion", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> PROTON = DEF_REG.register("proton", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FALLOUT = DEF_REG.register("fallout", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GAMMAROACH = DEF_REG.register("gammaroach", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RADGILL_SPLASH = DEF_REG.register("radgill_splash", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ACID_DROP = DEF_REG.register("acid_drop", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> TUBE_WORM = DEF_REG.register("tube_worm", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DEEP_ONE_MAGIC = DEF_REG.register("deep_one_magic", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WATER_FOAM = DEF_REG.register("water_foam", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BIG_SPLASH = DEF_REG.register("big_splash", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BIG_SPLASH_EFFECT = DEF_REG.register("big_splash_effect", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MINE_EXPLOSION = DEF_REG.register("mine_explosion", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WATCHER_APPEARANCE = DEF_REG.register("watcher_appearance", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> VOID_BEING_CLOUD = DEF_REG.register("void_being_cloud", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> VOID_BEING_TENDRIL = DEF_REG.register("void_being_tendril", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> VOID_BEING_EYE = DEF_REG.register("void_being_eye", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> UNDERZEALOT_MAGIC = DEF_REG.register("underzealot_magic", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> UNDERZEALOT_EXPLOSION = DEF_REG.register("underzealot_explosion", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FALLING_GUANO = DEF_REG.register("falling_guano", ()-> new SimpleParticleType(false));

}
