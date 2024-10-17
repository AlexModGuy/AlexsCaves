package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.LicorootVineBlock;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

import java.util.Map;

public class LicorootTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final Map<Direction, Direction[]> SPIRAL_UP_MAP = Util.make(Maps.newHashMap(), (map) -> {
        map.put(Direction.NORTH, new Direction[]{Direction.NORTH, Direction.UP, Direction.SOUTH, Direction.DOWN});
        map.put(Direction.SOUTH, new Direction[]{Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.DOWN});
        map.put(Direction.EAST, new Direction[]{Direction.EAST, Direction.UP, Direction.WEST, Direction.DOWN});
        map.put(Direction.WEST, new Direction[]{Direction.WEST, Direction.UP, Direction.EAST, Direction.DOWN});
    });

    private static final Map<Direction, Direction[]> SPIRAL_DOWN_MAP = Util.make(Maps.newHashMap(), (map) -> {
        map.put(Direction.NORTH, new Direction[]{Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.UP});
        map.put(Direction.SOUTH, new Direction[]{Direction.SOUTH, Direction.DOWN, Direction.NORTH, Direction.UP});
        map.put(Direction.EAST, new Direction[]{Direction.EAST, Direction.DOWN, Direction.WEST, Direction.UP});
        map.put(Direction.WEST, new Direction[]{Direction.WEST, Direction.DOWN, Direction.EAST, Direction.UP});
    });

    public LicorootTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos treeGround = context.origin();
        int centerAboveGround = 5 + randomsource.nextInt(5);
        int height = centerAboveGround + 4 + randomsource.nextInt(7);
        if (!checkCanTreePlace(level, treeGround, height)) {
            return false;
        }
        BlockPos centerPos = treeGround.above(centerAboveGround);
        //create a thicker random trunk
        for(Direction direction : ACMath.HORIZONTAL_DIRECTIONS){
            if(randomsource.nextBoolean()){
                BlockPos extraTrunkPos = centerPos.relative(direction).relative(Direction.DOWN, randomsource.nextInt(1) + 1);
                int trunkLengths = 0;
                while(canReplace(level.getBlockState(extraTrunkPos)) && trunkLengths < centerAboveGround + 2){
                    level.setBlock(extraTrunkPos, ACBlockRegistry.LICOROOT.get().defaultBlockState(), 3);
                    extraTrunkPos = extraTrunkPos.below();
                    trunkLengths++;
                }
            }
        }
        //fill in the center
        int centerHeight = 0;
        while(centerHeight < height){
            level.setBlock(treeGround, ACBlockRegistry.LICOROOT.get().defaultBlockState(), 3);
            treeGround = treeGround.above();
            centerHeight++;
        }
        boolean flag = false;
        BlockPos branchFrom = treeGround;
        for(Direction direction : ACMath.HORIZONTAL_DIRECTIONS){
            if(randomsource.nextBoolean()){
               int branchLength = randomsource.nextInt(8) + 8;
                buildBranchFrom(level, branchFrom.below(1 + randomsource.nextInt(2)), randomsource, branchLength, direction, randomsource.nextInt(2) == 0, randomsource.nextBoolean());
                flag = true;
            }
        }
        if(!flag){
            int branchLength = randomsource.nextInt(8) + 8;
            buildBranchFrom(level, branchFrom.below(1 + randomsource.nextInt(2)), randomsource, branchLength, ACMath.HORIZONTAL_DIRECTIONS[randomsource.nextInt(3)], randomsource.nextInt(2) == 0, randomsource.nextBoolean());
        }
        return true;
    }
    private void buildBranchFrom(WorldGenLevel level, BlockPos startBranchPos, RandomSource random, int length, Direction originalBranchDirection, boolean spiralUp, boolean propogate){
        Direction[] directions = spiralUp ? SPIRAL_UP_MAP.get(originalBranchDirection) : SPIRAL_DOWN_MAP.get(originalBranchDirection);
        int currentBranchDirectionOrdinal = 0;
        int i = 0;
        Direction moveDirection = directions[0];
        BlockPos branchPos = startBranchPos.relative(moveDirection);
        int movesInDirection = 0;
        int movesNeededToSpiral = (int) Math.floor((float)length / 4F);
        boolean split = false;
        while (i < length && canReplace(level.getBlockState(branchPos))){
            if(propogate && moveDirection.getAxis().isVertical() && !split && random.nextFloat() < 0.6F){
                split = true;
                buildBranchFrom(level, branchPos, random, Math.max(2, length - 2), originalBranchDirection, !spiralUp, false);
            }
            int j = movesNeededToSpiral;
            if(currentBranchDirectionOrdinal == 0){
                j += 1;
            }
            if(random.nextFloat() < 0.8F && movesInDirection >= j){
                movesInDirection = 0;
                currentBranchDirectionOrdinal = (currentBranchDirectionOrdinal + 1) % 4;
                moveDirection = directions[currentBranchDirectionOrdinal];
            }else{
                movesInDirection++;
                dropVinesFrom(level, branchPos, random, 0.8F);
                level.setBlock(branchPos, ACBlockRegistry.LICOROOT.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, moveDirection.getAxis()), 3);
            }
            if(movesInDirection > 0 && currentBranchDirectionOrdinal > 3){
                continue;
            }
            i++;
            branchPos = branchPos.relative(moveDirection);
        }
    }

    private boolean checkCanTreePlace(WorldGenLevel level, BlockPos treeBottom, int height) {
        BlockState below = level.getBlockState(treeBottom.below());
        if (!below.is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) && !below.is(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get()) && !below.is(ACBlockRegistry.CAKE_LAYER.get())) {
            return false;
        }
        for (int i = 0; i < height; i++) {
            if (!canReplace(level.getBlockState(treeBottom.above(i)))) {
                return false;
            }
        }
        BlockPos treeTop = treeBottom.above(height).immutable();
        for (BlockPos checkLeaf : BlockPos.betweenClosed(treeTop.offset(-2, -1, -2), treeTop.offset(2, 1, 2))) {
            if (!canReplace(level.getBlockState(checkLeaf))) {
                return false;
            }
        }
        return true;
    }

    protected static void dropVinesFrom(WorldGenLevel level, BlockPos from, RandomSource random, float vineChance) {
        if (random.nextFloat() < vineChance) {
            BlockPos vinePos = from.immutable().relative(Direction.DOWN);
            int vineLength = 1 + random.nextInt(4);
            int i = 0;
            while (i < vineLength && level.getBlockState(vinePos).canBeReplaced()) {
                BlockState vine = ACBlockRegistry.LICOROOT_VINE.get().defaultBlockState().setValue(LicorootVineBlock.FACING, Direction.DOWN).setValue(LicorootVineBlock.WATERLOGGED, level.getFluidState(vinePos).is(Fluids.WATER));
                level.setBlock(vinePos, vine, 3);
                vinePos = vinePos.relative(Direction.DOWN);
                i++;
            }
            if (i > 0) {
                BlockState vine = ACBlockRegistry.LICOROOT_VINE.get().defaultBlockState().setValue(LicorootVineBlock.FACING, Direction.DOWN).setValue(LicorootVineBlock.END, true).setValue(LicorootVineBlock.WATERLOGGED, level.getFluidState(vinePos).is(Fluids.WATER));
                level.setBlock(vinePos.above(), vine, 3);
            }
        }
    }


    private static boolean canReplace(BlockState state) {
        return (state.isAir() || state.canBeReplaced() || state.is(ACBlockRegistry.LICOROOT_SPROUT.get()) || state.is(BlockTags.DIRT) || state.is(ACBlockRegistry.BLOCK_OF_CHOCOLATE.get()) || state.is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get()) || state.is(ACBlockRegistry.LICOROOT_VINE.get())) && !state.is(ACTagRegistry.UNMOVEABLE);
    }
}
