package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACLootTableRegistry {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> DEF_REG = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AlexsCaves.MODID);

    public static final RegistryObject<Codec<CaveTabletLootModifier>> CAVE_TABLET_LOOT_MODIFIER = DEF_REG.register("cave_tablet", CaveTabletLootModifier.CODEC);

    public static final ResourceLocation UNDERGROUND_CABIN_CHEST = new ResourceLocation("alexscaves:chests/abyssal_ruins");
    public static final ResourceLocation ABYSSAL_RUINS_CHEST = new ResourceLocation("alexscaves:chests/abyssal_ruins");



}
