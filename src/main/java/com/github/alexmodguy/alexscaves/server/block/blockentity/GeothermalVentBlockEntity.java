package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.GeothermalVentBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeothermalVentBlockEntity extends BlockEntity {

    private static final double PARTICLE_DIST = 120 * 120;

    private int soundTime = 0;

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
        if(blockEntity.soundTime-- <= 0){
            blockEntity.soundTime = level.getRandom().nextInt(20) + 30;
            boolean underwater = !state.getFluidState().isEmpty() || !level.getBlockState(pos.above()).getFluidState().isEmpty();
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, underwater ? ACSoundRegistry.GEOTHERMAL_VENT_BUBBLE_UNDERWATER.get() : ACSoundRegistry.GEOTHERMAL_VENT_BUBBLE.get(), SoundSource.BLOCKS, underwater ? 2.5F : 1.5F, level.random.nextFloat() * 0.4F + 0.8F, false);
        }
    }
}
