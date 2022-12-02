package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Frog.class)
public class FrogMixin {

    @Inject(
            method = {"Lnet/minecraft/world/entity/animal/frog/Frog;checkFrogSpawnRules(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z"},
            remap = true,
            cancellable = true,
            at = @At(value = "TAIL")
    )
    private static void ac_spawnMobsForChunkGeneration(EntityType<? extends Animal> type, LevelAccessor levelAccessor, MobSpawnType mobType, BlockPos pos, RandomSource randomSource, CallbackInfoReturnable<Boolean> cir) {
        if(levelAccessor.getBiome(pos).is(ACBiomeRegistry.PRIMORDIAL_CAVES)){
            cir.setReturnValue(levelAccessor.getBlockState(pos.below()).is(ACTagRegistry.DINOSAURS_SPAWNABLE_ON) && levelAccessor.getFluidState(pos).isEmpty() && levelAccessor.getFluidState(pos.below()).isEmpty());
        }
    }
}
