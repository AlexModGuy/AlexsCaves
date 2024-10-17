package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.FrostmintExplosion;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class PurpleSodaBlock extends LiquidBlock {

    public PurpleSodaBlock(RegistryObject<FlowingFluid> flowingFluid, BlockBehaviour.Properties properties) {
        super(flowingFluid, properties);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(400) == 0) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ACSoundRegistry.PURPLE_SODA_IDLE.get(), SoundSource.BLOCKS, 0.5F, randomSource.nextFloat() * 0.4F + 0.8F, false);
        }
        if (randomSource.nextInt(400) == 0 && level.getBlockState(pos.below()).isSolid()) {
            level.addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE_EMITTER.get(), pos.getX() + randomSource.nextFloat(), pos.getY(), pos.getZ() + randomSource.nextFloat(), 0F, 0F, 0F);
        } else if (randomSource.nextInt(150) == 0) {
            level.addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), pos.getX() + randomSource.nextFloat(), pos.getY(), pos.getZ() + randomSource.nextFloat(), 0F, 0F, 0F);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
        entity.fallDistance = 0.0F;
        if (entity instanceof LivingEntity && entity.moveDist > entity.nextStep && !(entity instanceof SweetishFishEntity)) {
            entity.nextStep = entity.moveDist + 1F;
            Vec3 vec3 = entity.getDeltaMovement();
            float f1 = Math.min(1.0F, (float) vec3.length());
            entity.playSound(ACSoundRegistry.PURPLE_SODA_SWIM.get(), f1, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            BlockPos offset = pos.relative(direction);
            BlockState state1 = worldIn.getBlockState(offset);
            if (state1.is(ACBlockRegistry.FROSTMINT.get())) {
                worldIn.setBlockAndUpdate(offset, Blocks.AIR.defaultBlockState());
                FrostmintExplosion explosion = new FrostmintExplosion(worldIn, null, offset.getX() + 0.5F, offset.getY() + 0.5F, offset.getZ() + 0.5F, 4.0F, Explosion.BlockInteraction.DESTROY_WITH_DECAY, false);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        }
    }

}
