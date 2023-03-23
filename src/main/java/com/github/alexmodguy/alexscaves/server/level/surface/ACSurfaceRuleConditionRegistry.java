package com.github.alexmodguy.alexscaves.server.level.surface;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSimplexNoise;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ACSurfaceRuleConditionRegistry {

    public static final DeferredRegister<Codec<? extends SurfaceRules.ConditionSource>> DEF_REG = DeferredRegister.create(Registries.MATERIAL_CONDITION, AlexsCaves.MODID);

    public static final RegistryObject<Codec<? extends SurfaceRules.ConditionSource>> AC_SIMPLEX_CONDITION = DEF_REG.register("ac_simplex", () -> SimplexConditionSource.CODEC.codec());

    public static SurfaceRules.ConditionSource simplexCondition(float noiseMin, float noiseMax, float noiseScale, int offsetType) {
        return new SimplexConditionSource(noiseMin, noiseMax, noiseScale, offsetType);
    }

    private record SimplexConditionSource(float noiseMin, float noiseMax, float noiseScale,
                                          int offsetType) implements SurfaceRules.ConditionSource {
        private static final KeyDispatchDataCodec<SimplexConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((group) -> {
            return group.group(Codec.floatRange(-1F, 1F).fieldOf("noise_min").forGetter(SimplexConditionSource::noiseMin), Codec.floatRange(-1F, 1F).fieldOf("noise_max").forGetter(SimplexConditionSource::noiseMax), Codec.floatRange(1F, 10000F).fieldOf("noise_scale").forGetter(SimplexConditionSource::noiseScale), Codec.intRange(0, 128).fieldOf("offset_type").forGetter(SimplexConditionSource::offsetType)).apply(group, SimplexConditionSource::new);
        }));

        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(final SurfaceRules.Context contextIn) {
            class NoiseCondition implements SurfaceRules.Condition {

                private SurfaceRules.Context context;

                NoiseCondition(SurfaceRules.Context context) {
                    this.context = context;
                }

                public boolean test() {
                    double f = ACMath.sampleNoise3D(context.blockX + (offsetType * 1000), context.blockY + (offsetType * 2000), context.blockZ - (offsetType * 3000), SimplexConditionSource.this.noiseScale);
                    return f > SimplexConditionSource.this.noiseMin && f <= SimplexConditionSource.this.noiseMax;
                }
            }
            return new NoiseCondition(contextIn);
        }
    }
}
