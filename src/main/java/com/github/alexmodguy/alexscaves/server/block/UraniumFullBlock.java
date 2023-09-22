package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

public class UraniumFullBlock extends RotatedPillarBlock {

    public UraniumFullBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(3.5F).lightLevel((state -> 4)).emissiveRendering((state, level, pos) -> true).sound(ACSoundTypes.URANIUM));
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(80) == 0) {
            level.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, ACSoundRegistry.URANIUM_HUM.get(), SoundSource.BLOCKS, 0.5F, randomSource.nextFloat() * 0.4F + 0.8F, false);
        }
        if (randomSource.nextInt(13) == 0) {
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 0.5F);
            level.addParticle(ACParticleRegistry.PROTON.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
