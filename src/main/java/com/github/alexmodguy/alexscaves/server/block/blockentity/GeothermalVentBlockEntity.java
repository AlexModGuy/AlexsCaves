package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.GeothermalVentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeothermalVentBlockEntity extends BlockEntity {

    private static final double PARTICLE_DIST = 120 * 120;

    public GeothermalVentBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.GEOTHERMAL_VENT.get(), pos, state);
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, GeothermalVentBlockEntity blockEntity) {
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > PARTICLE_DIST || level.random.nextBoolean()) {
            return;
        }
        int smokeType = state.getValue(GeothermalVentBlock.SMOKE_TYPE);
        ParticleOptions particle = ParticleTypes.SMOKE;
        switch (smokeType) {
            case 1:
                particle = level.random.nextInt(3) == 0 ? ParticleTypes.POOF : ACParticleRegistry.WHITE_VENT_SMOKE.get();
                break;
            case 2:
                particle = level.random.nextInt(3) == 0 ? ParticleTypes.SQUID_INK : ACParticleRegistry.BLACK_VENT_SMOKE.get();
                break;
            case 3:
                particle = level.random.nextInt(3) == 0 ? ACParticleRegistry.ACID_BUBBLE.get() : ACParticleRegistry.GREEN_VENT_SMOKE.get();
                break;
        }
        float x = (level.random.nextFloat() - 0.5F) * 0.25F;
        float z = (level.random.nextFloat() - 0.5F) * 0.25F;
        level.addAlwaysVisibleParticle(particle, true, pos.getX() + 0.5F + x, pos.getY() + 1.0F, pos.getZ() + 0.5F + z, x * 0.15F, 0.03F + level.random.nextFloat() * 0.2F, z * 0.15F);
    }
}
