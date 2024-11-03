package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class SugarGlassBlock extends GlassBlock {

    public SugarGlassBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noOcclusion().requiresCorrectToolForDrops().strength(0.3F, 0.0F).sound(SoundType.GLASS));
    }

    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!(entityIn.getType().is(ACTagRegistry.CANDY_MOBS)) && !entityIn.isInFluidType() && !level.isClientSide) {
            level.destroyBlock(pos, true);
        }
        super.fallOn(level, state, pos, entityIn, fallDistance);
    }


    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile entityIn) {
        if (!(entityIn.getType().is(ACTagRegistry.CANDY_MOBS)) && !level.isClientSide) {
            level.destroyBlock(blockHitResult.getBlockPos(), true);
        }
    }
}
