package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class MultipleDinosaurEggsBlock extends DinosaurEggBlock {

    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    private final int maxEggs;

    private static final VoxelShape ONE_EGG_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private static final VoxelShape MULTI_EGG_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);

    public MultipleDinosaurEggsBlock(Properties properties, RegistryObject births, int maxEggs) {
        super(properties, births, Shapes.block());
        this.maxEggs = maxEggs;
        this.registerDefaultState(this.defaultBlockState().setValue(HATCH, Integer.valueOf(0)).setValue(EGGS, 1).setValue(NEEDS_PLAYER, false));
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return useContext.getItemInHand().getItem() == this.asItem() && state.getValue(EGGS) < 4 || super.canBeReplaced(state, useContext);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.getBlock() == this ? blockstate.setValue(EGGS, Integer.valueOf(Math.min(maxEggs, blockstate.getValue(EGGS) + 1))) : super.getStateForPlacement(context);
    }

    protected void removeOneEgg(Level worldIn, BlockPos pos, BlockState state) {
        worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.random.nextFloat() * 0.2F);
        int i = state.getValue(EGGS);
        if (i <= 1) {
            worldIn.destroyBlock(pos, false);
        } else {
            worldIn.setBlock(pos, state.setValue(EGGS, Integer.valueOf(i - 1)), 2);
            worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
            worldIn.levelEvent(2001, pos, Block.getId(state));
        }

    }

    protected int getDinosaursBornFrom(BlockState state) {
        return state.getValue(EGGS);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EGGS);
    }
}
