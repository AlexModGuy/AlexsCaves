package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Monster.class)
public class MonsterMixin {

    @Inject(
            method = {"Lnet/minecraft/world/entity/monster/Monster;checkMonsterSpawnRules(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private static void ac_checkMonsterSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource, CallbackInfoReturnable<Boolean> cir) {
        if(entityType == EntityType.WITCH && LicowitchEntity.isWithinTowerSpawnBounds(serverLevelAccessor, blockPos)){
            cir.setReturnValue(Monster.checkAnyLightMonsterSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource) && randomSource.nextInt(4) == 0 && LicowitchEntity.getWitchCountInStructure(serverLevelAccessor, blockPos, false) < 4);
        }
    }
}
