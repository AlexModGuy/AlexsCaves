package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FiddleheadBlock extends CavePlantBlock {

    protected static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D);

    public FiddleheadBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).noCollission().instabreak().randomTicks().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ), false);
    }


    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(getter, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public void randomTick(BlockState currentState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        super.randomTick(currentState, level, blockPos, randomSource);
        if (randomSource.nextInt(7) == 0 && level.getBlockState(blockPos.above()).isAir()) {
            Direction facing = Direction.fromYRot(randomSource.nextFloat() * 360);
            level.setBlockAndUpdate(blockPos, ACBlockRegistry.CURLY_FERN.get().defaultBlockState().setValue(DoublePlantWithRotationBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoublePlantWithRotationBlock.FACING, facing));
            level.setBlockAndUpdate(blockPos.above(), ACBlockRegistry.CURLY_FERN.get().defaultBlockState().setValue(DoublePlantWithRotationBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoublePlantWithRotationBlock.FACING, facing));
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter getter, BlockPos pos) {
        return blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.MOSS_BLOCK);
    }
}
