package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class CandyCanePoleBlock extends CrossCollisionBlock {

    public CandyCanePoleBlock() {
        super(2.0F, 2.0F, 16.0F, 16.0F, 16.0F, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).noOcclusion().noCollission().pushReaction(PushReaction.DESTROY).instabreak().sound(ACSoundTypes.HARD_CANDY).dynamicShape());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
    }

    @Override
    protected VoxelShape[] makeShapes(float poleWidth, float connectorWidth, float poleHeight, float connectorYMin, float connectorYMax) {
        float f = 8.0F - poleWidth;
        float f1 = 8.0F + poleWidth;
        float f2 = 8.0F - connectorWidth;
        float f3 = 8.0F + connectorWidth;
        float upper = connectorYMax - 4.0F;
        VoxelShape voxelshape = Block.box((double)f, 0.0D, (double)f, (double)f1, (double)poleHeight, (double)f1);
        VoxelShape voxelshape1 = Block.box((double)f2, (double)upper, 0.0D, (double)f3, (double)connectorYMax, (double)f3);
        VoxelShape voxelshape2 = Block.box((double)f2, (double)upper, (double)f2, (double)f3, (double)connectorYMax, 16.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, (double)upper, (double)f2, (double)f3, (double)connectorYMax, (double)f3);
        VoxelShape voxelshape4 = Block.box((double)f2, (double)upper, (double)f2, 16.0D, (double)connectorYMax, (double)f3);
        VoxelShape voxelshape5 = Shapes.or(voxelshape1, voxelshape4);
        VoxelShape voxelshape6 = Shapes.or(voxelshape2, voxelshape3);
        VoxelShape[] avoxelshape = new VoxelShape[]{Shapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, Shapes.or(voxelshape2, voxelshape1), Shapes.or(voxelshape3, voxelshape1), Shapes.or(voxelshape6, voxelshape1), voxelshape4, Shapes.or(voxelshape2, voxelshape4), Shapes.or(voxelshape3, voxelshape4), Shapes.or(voxelshape6, voxelshape4), voxelshape5, Shapes.or(voxelshape2, voxelshape5), Shapes.or(voxelshape3, voxelshape5), Shapes.or(voxelshape6, voxelshape5)};
        for(int i = 0; i < 16; ++i) {
            avoxelshape[i] = Shapes.or(voxelshape, avoxelshape[i]);
        }
        return avoxelshape;
    }

    public boolean connectsTo(BlockGetter level, BlockPos ourPos, BlockPos posInQuestion) {
        BlockState stateAbove = level.getBlockState(ourPos.above());
        BlockState theirState = level.getBlockState(posInQuestion);
        BlockState stateAboveThem = level.getBlockState(posInQuestion.above());
        return theirState.getBlock() instanceof CandyCanePoleBlock && !(stateAbove.getBlock() instanceof CandyCanePoleBlock) && !(stateAboveThem.getBlock() instanceof CandyCanePoleBlock);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        return super.getStateForPlacement(context).setValue(NORTH, this.connectsTo(blockgetter, blockpos, blockpos1)).setValue(EAST, this.connectsTo(blockgetter, blockpos, blockpos2)).setValue(SOUTH, this.connectsTo(blockgetter, blockpos, blockpos3)).setValue(WEST, this.connectsTo(blockgetter, blockpos, blockpos4)).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos ourPos, BlockPos updatePos) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(ourPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return direction.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? blockState.setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(levelAccessor, ourPos, updatePos)) : super.updateShape(blockState, direction, blockState1, levelAccessor, ourPos, updatePos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

    public static BooleanProperty getPropertyByDirection(Direction direction){
        return PROPERTY_BY_DIRECTION.get(direction);
    }

    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        ItemStack itemStack = context.getItemInHand();
        if (!itemStack.canPerformAction(toolAction))
            return null;
        if (ToolActions.AXE_STRIP == toolAction && this == ACBlockRegistry.CANDY_CANE_POLE.get()) {
            return ACBlockRegistry.STRIPPED_CANDY_CANE_POLE.get().defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)).setValue(NORTH, state.getValue(NORTH)).setValue(EAST, state.getValue(EAST)).setValue(WEST, state.getValue(WEST)).setValue(SOUTH, state.getValue(SOUTH));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
