package com.github.alexmodguy.alexscaves.server.potion;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACEffectRegistry {

    public static final DeferredRegister<MobEffect> DEF_REG = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AlexsCaves.MODID);

    public static final RegistryObject<MobEffect> IRRADIATED = DEF_REG.register("irradiated", ()-> new IrradiatedEffect());
    public static final RegistryObject<MobEffect> BUBBLED = DEF_REG.register("bubbled", ()-> new BubbledEffect());

}
