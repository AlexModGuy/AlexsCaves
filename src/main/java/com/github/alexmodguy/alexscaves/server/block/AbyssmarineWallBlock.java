package com.github.alexmodguy.alexscaves.server.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class AbyssmarineWallBlock extends WallBlock implements ActivatedByAltar {

    private final Map<BlockState, VoxelShape> shapeByIndex;
    private final Map<BlockState, VoxelShape> collisionShapeByIndex;

    public AbyssmarineWallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.valueOf(true)).setValue(NORTH_WALL, WallSide.NONE).setValue(EAST_WALL, WallSide.NONE).setValue(SOUTH_WALL, WallSide.NONE).setValue(WEST_WALL, WallSide.NONE).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(ACTIVE, Boolean.valueOf(false)).setValue(DISTANCE, MAX_DISTANCE));
        this.shapeByIndex = this.makeAbyssalShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeByIndex = this.makeAbyssalShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource randomSource) {
        super.tick(state, serverLevel, pos, randomSource);
        serverLevel.setBlock(pos, updateDistance(state, serverLevel, pos), 3);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        BlockState newState = super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
        int i = ActivatedByAltar.getDistanceAt(state1) + 1;
        if (i != 1 || newState.getValue(DISTANCE) != i) {
            levelAccessor.scheduleTick(blockPos, this, 2);
        }
        return newState;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateDistance(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, ACTIVE, UP, NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
    }

    private Map<BlockState, VoxelShape> makeAbyssalShapes(float p_57966_, float p_57967_, float p_57968_, float p_57969_, float p_57970_, float p_57971_) {
        float f = 8.0F - p_57966_;
        float f1 = 8.0F + p_57966_;
        float f2 = 8.0F - p_57967_;
        float f3 = 8.0F + p_57967_;
        VoxelShape voxelshape = Block.box((double) f, 0.0D, (double) f, (double) f1, (double) p_57968_, (double) f1);
        VoxelShape voxelshape1 = Block.box((double) f2, (double) p_57969_, 0.0D, (double) f3, (double) p_57970_, (double) f3);
        VoxelShape voxelshape2 = Block.box((double) f2, (double) p_57969_, (double) f2, (double) f3, (double) p_57970_, 16.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, (double) p_57969_, (double) f2, (double) f3, (double) p_57970_, (double) f3);
        VoxelShape voxelshape4 = Block.box((double) f2, (double) p_57969_, (double) f2, 16.0D, (double) p_57970_, (double) f3);
        VoxelShape voxelshape5 = Block.box((double) f2, (double) p_57969_, 0.0D, (double) f3, (double) p_57971_, (double) f3);
        VoxelShape voxelshape6 = Block.box((double) f2, (double) p_57969_, (double) f2, (double) f3, (double) p_57971_, 16.0D);
        VoxelShape voxelshape7 = Block.box(0.0D, (double) p_57969_, (double) f2, (double) f3, (double) p_57971_, (double) f3);
        VoxelShape voxelshape8 = Block.box((double) f2, (double) p_57969_, (double) f2, 16.0D, (double) p_57971_, (double) f3);
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (Boolean obool : UP.getPossibleValues()) {
            for (WallSide wallside : EAST_WALL.getPossibleValues()) {
                for (WallSide wallside1 : NORTH_WALL.getPossibleValues()) {
                    for (WallSide wallside2 : WEST_WALL.getPossibleValues()) {
                        for (WallSide wallside3 : SOUTH_WALL.getPossibleValues()) {
                            VoxelShape voxelshape9 = Shapes.empty();
                            voxelshape9 = applyWallShape(voxelshape9, wallside, voxelshape4, voxelshape8);
                            voxelshape9 = applyWallShape(voxelshape9, wallside2, voxelshape3, voxelshape7);
                            voxelshape9 = applyWallShape(voxelshape9, wallside1, voxelshape1, voxelshape5);
                            voxelshape9 = applyWallShape(voxelshape9, wallside3, voxelshape2, voxelshape6);
                            if (obool) {
                                voxelshape9 = Shapes.or(voxelshape9, voxelshape);
                            }

                            BlockState blockstate = this.defaultBlockState().setValue(UP, obool).setValue(EAST_WALL, wallside).setValue(WEST_WALL, wallside2).setValue(NORTH_WALL, wallside1).setValue(SOUTH_WALL, wallside3);
                            for (int i = 1; i <= MAX_DISTANCE; i++) {
                                builder.put(blockstate.setValue(DISTANCE, i).setValue(ACTIVE, false).setValue(WATERLOGGED, Boolean.valueOf(false)), voxelshape9);
                                builder.put(blockstate.setValue(DISTANCE, i).setValue(ACTIVE, false).setValue(WATERLOGGED, Boolean.valueOf(true)), voxelshape9);
                                builder.put(blockstate.setValue(DISTANCE, i).setValue(ACTIVE, true).setValue(WATERLOGGED, Boolean.valueOf(false)), voxelshape9);
                                builder.put(blockstate.setValue(DISTANCE, i).setValue(ACTIVE, true).setValue(WATERLOGGED, Boolean.valueOf(true)), voxelshape9);
                            }
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    private static VoxelShape applyWallShape(VoxelShape p_58034_, WallSide p_58035_, VoxelShape p_58036_, VoxelShape p_58037_) {
        if (p_58035_ == WallSide.TALL) {
            return Shapes.or(p_58034_, p_58037_);
        } else {
            return p_58035_ == WallSide.LOW ? Shapes.or(p_58034_, p_58036_) : p_58034_;
        }
    }

    public VoxelShape getShape(BlockState p_58050_, BlockGetter p_58051_, BlockPos p_58052_, CollisionContext p_58053_) {
        return this.shapeByIndex.get(p_58050_);
    }

    public VoxelShape getCollisionShape(BlockState p_58055_, BlockGetter p_58056_, BlockPos p_58057_, CollisionContext p_58058_) {
        return this.collisionShapeByIndex.get(p_58055_);
    }

}
