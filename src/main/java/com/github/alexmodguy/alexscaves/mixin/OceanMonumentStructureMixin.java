package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(OceanMonumentStructure.class)
public class OceanMonumentStructureMixin {

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/OceanMonumentStructure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        int i = context.chunkPos().getBlockX(9);
        int j = context.chunkPos().getBlockZ(9);

        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(i, context.chunkGenerator().getSeaLevel() - 80, j, 29, context.randomState().sampler())) {
            if (holder.is(ACBiomeRegistry.ABYSSAL_CHASM)) {
                cir.setReturnValue(Optional.empty());
            }
        }
    }

}
