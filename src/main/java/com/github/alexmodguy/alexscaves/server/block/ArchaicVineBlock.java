package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArchaicVineBlock extends GrowingPlantHeadBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public ArchaicVineBlock() {
      super(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.NETHER).randomTicks().noCollission().instabreak().sound(SoundType.ROOTS), Direction.DOWN, SHAPE, false, 0.1D);
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource randomSource) {
      return NetherVines.getBlocksToGrowWhenBonemealed(randomSource);
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
      //no natural growth
   }


   protected Block getBodyBlock() {
      return ACBlockRegistry.ARCHAIC_VINE_PLANT.get();
   }

   protected boolean canGrowInto(BlockState state) {
      return state.isAir();
   }
}