package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class NeodymiumOreBlock extends Block {

    private boolean azure;

    public NeodymiumOreBlock(boolean azure) {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).requiresCorrectToolForDrops().strength(3.5F, 10.0F).sound(ACSoundTypes.NEODYMIUM).lightLevel((i) -> 3).emissiveRendering((state, level, pos) -> true));
        this.azure = azure;
    }


    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        Vec3 center = Vec3.atCenterOf(pos);
        if (randomSource.nextInt(3) == 0) {
            level.addParticle(azure ? ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get() : ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
