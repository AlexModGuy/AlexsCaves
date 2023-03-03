package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;

public class UraniumOreBlock extends RotatedPillarBlock {

    public UraniumOreBlock() {
        super(Properties.of(Material.METAL).strength(3.5F).lightLevel((state -> 4)).emissiveRendering((state, level, pos) -> true).sound(SoundType.COPPER));
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if(randomSource.nextInt(13) == 0){
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 0.5F);
            level.addParticle(ACParticleRegistry.PROTON.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
