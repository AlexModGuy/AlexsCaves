package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GuanoBlock extends FallingBlockWithColor {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public GuanoBlock() {
        super(Properties.of(Material.CLAY, MaterialColor.COLOR_BROWN).strength(0.3F).sound(SoundType.FROGSPAWN), 0X402721);
    }

    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getFeetBlockState().is(this)) {
            if (isForlornEntity(entity)) {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9D, 1.0D, 0.9D));
            }
        }
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() != null && (isForlornEntity(entityCollisionContext.getEntity()) || entityCollisionContext.getEntity() instanceof FallingBlockEntity) ? Shapes.block() : SHAPE;
    }

    public static boolean isForlornEntity(Entity entity) {
        return entity instanceof Bat;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos blockPos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.block();
    }

    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos blockPos) {
        return 0.2F;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if(randomSource.nextInt(20) == 0){
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 1).add(randomSource.nextFloat() - 0.5F, randomSource.nextFloat() * 0.5F + 0.2F, randomSource.nextFloat() - 0.5F);
            level.addParticle(ACParticleRegistry.FLY.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
