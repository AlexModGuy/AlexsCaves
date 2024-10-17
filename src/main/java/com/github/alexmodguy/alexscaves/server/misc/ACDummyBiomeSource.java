package com.github.alexmodguy.alexscaves.server.misc;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ACDummyBiomeSource extends BiomeSource {


    @Override
    protected Codec<? extends BiomeSource> codec() {
        return null;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        List<Holder<Biome>> biomes = new ArrayList<>();
        for(Biome biome : ForgeRegistries.BIOMES.getValues()){
            biomes.add(Holder.direct(biome));
        }
        return biomes.stream();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int p_204238_, int p_204239_, int p_204240_, Climate.Sampler p_204241_) {
        return null;
    }
}
