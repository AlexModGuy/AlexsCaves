package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {

    @Shadow @Final private Optional<ResourceLocation> startJigsawName;

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if(this.startJigsawName.isPresent() && this.startJigsawName.get().toString().equals("minecraft:city_anchor")){ // limit to only ancient cities
            int i = context.chunkPos().getBlockX(9);
            int j = context.chunkPos().getBlockZ(9);

            for (Holder<Biome> holder : ACMath.getBiomesWithinAtY(context.biomeSource(), i, context.chunkGenerator().getSeaLevel() - 80, j, 80, context.randomState().sampler())) {
                if (holder.is(ACTagRegistry.HAS_NO_ANCIENT_CITIES_IN)) {
                    cir.setReturnValue(Optional.empty());
                }
            }

        }
    }

}
