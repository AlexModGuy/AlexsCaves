package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UraniumRodBlock extends RotatedPillarBlock {

    private static final VoxelShape SHAPE_X = ACMath.buildShape(
            Block.box(2, 6, 6, 14, 10, 10),
            Block.box(14, 5, 5, 16, 11, 11),
            Block.box(0, 5, 5, 2, 11, 11)
    );

    private static final VoxelShape SHAPE_Y = ACMath.buildShape(
            Block.box(6, 2, 6, 10, 14, 10),
            Block.box(5, 0, 5, 11, 2, 11),
            Block.box(5, 14, 5, 11, 16, 11)
    );

    private static final VoxelShape SHAPE_Z = ACMath.buildShape(
            Block.box(6, 6, 2, 10, 10, 14),
            Block.box(5, 5, 14, 11, 11, 16),
            Block.box(5, 5, 0, 11, 11, 2)
    );


    public UraniumRodBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(1.5F).lightLevel((state -> 9)).emissiveRendering((state, level, pos) -> true).sound(SoundType.METAL));
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        switch (state.getValue(AXIS)) {
            case X:
                return SHAPE_X;
            case Y:
                return SHAPE_Y;
            case Z:
                return SHAPE_Z;
            default:
                return SHAPE_Y;
        }
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if(randomSource.nextInt(10) == 0){
            Vec3 center = Vec3.upFromBottomCenterOf(pos, 0.5F);
            level.addParticle(ACParticleRegistry.PROTON.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
