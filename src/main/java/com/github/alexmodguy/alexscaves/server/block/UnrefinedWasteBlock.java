package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UnrefinedWasteBlock extends FallingBlockWithColor {
    protected static final VoxelShape SHAPE = Block.box(0.1D, 0.1D, 0.1D, 15.9D, 14.0D, 15.9D);
    public UnrefinedWasteBlock() {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.LIME).strength(0.5F).sound(SoundType.FROGSPAWN).lightLevel(state -> 3).emissiveRendering((state, level, pos) -> true), 0X00EE00);
    }

    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if(entity instanceof LivingEntity living && !entity.getType().is(ACTagRegistry.RESISTS_RADIATION)){
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9D, 1.0D, 0.9D));
            living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 4000));
        }
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return SHAPE;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos blockPos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.block();
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(2) == 0) {
            Direction direction = Direction.getRandom(randomSource);
            BlockPos blockpos = blockPos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            if (!blockState.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                double d0 = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepX() * 0.6D;
                double d1 = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepY() * 0.6D;
                double d2 = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepZ() * 0.6D;
                level.addParticle(randomSource.nextBoolean() ? ACParticleRegistry.GAMMAROACH.get() : ACParticleRegistry.HAZMAT_BREATHE.get(), (double) blockPos.getX() + d0, (double) blockPos.getY() + d1, (double) blockPos.getZ() + d2, 0.0D, 0.1D + level.random.nextFloat() * 0.1F, 0.0D);
            }
        }
    }
}
