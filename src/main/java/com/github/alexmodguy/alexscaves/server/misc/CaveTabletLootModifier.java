package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class CaveTabletLootModifier extends LootModifier {

    public static final Supplier<Codec<CaveTabletLootModifier>> CODEC = () ->
            RecordCodecBuilder.create(inst ->
                    inst.group(
                                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
                                    Biome.CODEC.fieldOf("biome").forGetter((configuration) -> configuration.biome)
                            )
                            .apply(inst, CaveTabletLootModifier::new));

    private final Holder<Biome> biome;

    protected CaveTabletLootModifier(LootItemCondition[] conditionsIn, Holder<Biome> biome) {
        super(conditionsIn);
        this.biome = biome;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(context.getRandom().nextFloat() < getChance()){
            generatedLoot.add(getTablet());
        }
        return generatedLoot;
    }

    private float getChance(){
        if(biome == null){
            return 0F;
        }
        if(biome.is(ACBiomeRegistry.MAGNETIC_CAVES)){
            return AlexsCaves.COMMON_CONFIG.magneticTabletLootChance.get().floatValue();
        }
        if(biome.is(ACBiomeRegistry.PRIMORDIAL_CAVES)){
            return AlexsCaves.COMMON_CONFIG.primordialTabletLootChance.get().floatValue();
        }
        if(biome.is(ACBiomeRegistry.TOXIC_CAVES)){
            return AlexsCaves.COMMON_CONFIG.toxicTabletLootChance.get().floatValue();
        }
        if(biome.is(ACBiomeRegistry.ABYSSAL_CHASM)){
            return AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance.get().floatValue();
        }
        if(biome.is(ACBiomeRegistry.FORLORN_HOLLOWS)){
            return AlexsCaves.COMMON_CONFIG.forlornTabletLootChance.get().floatValue();
        }
        return 0F;
    }

    private ItemStack getTablet(){
        CompoundTag tag = new CompoundTag();
        ResourceKey<Biome> key = ACBiomeRegistry.MAGNETIC_CAVES;
        if(biome != null){
            key = biome.unwrapKey().orElse(ACBiomeRegistry.MAGNETIC_CAVES);
        }
        tag.putString("CaveBiome", key.location().toString());
        ItemStack stack = new ItemStack(ACItemRegistry.CAVE_TABLET.get());
        stack.setTag(tag);
        return stack;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
