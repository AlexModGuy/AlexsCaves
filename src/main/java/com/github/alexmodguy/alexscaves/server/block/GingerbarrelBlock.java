package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.GingerbarrelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class GingerbarrelBlock extends BarrelBlock {

    public static final VoxelShape SHAPE_X = Block.box(3, 0, 4, 13, 8, 12);
    public static final VoxelShape SHAPE_Y_UP = Block.box(4, 0, 4, 12, 10, 12);
    public static final VoxelShape SHAPE_Y_DOWN = Block.box(4, 6, 4, 12, 16, 12);
    public static final VoxelShape SHAPE_Z = Block.box(4, 0, 3, 12, 8, 13);
    public GingerbarrelBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F).sound(ACSoundTypes.DENSE_CANDY).noOcclusion());
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof GingerbarrelBlockEntity) {
                player.openMenu((GingerbarrelBlockEntity) blockentity);
                player.awardStat(Stats.OPEN_BARREL);
                PiglinAi.angerNearbyPiglins(player, true);
            }
            return InteractionResult.CONSUME;
        }
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof GingerbarrelBlockEntity) {
            ((GingerbarrelBlockEntity) blockentity).recheckOpen();
        }
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new GingerbarrelBlockEntity(pos, blockState);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState blockState, @Nullable LivingEntity entity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof GingerbarrelBlockEntity) {
                ((GingerbarrelBlockEntity) blockentity).setCustomName(stack.getHoverName());
            }
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean force) {
        if (state.hasBlockEntity() && (!(newState.getBlock() instanceof GingerbarrelBlock) || !newState.hasBlockEntity())) {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, blockPos, (Container)blockentity);
                level.updateNeighbourForOutputSignal(blockPos, this);
            }

            level.removeBlockEntity(blockPos);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING).getAxis()){
            case X:
                return SHAPE_X;
            case Y:
                return state.getValue(FACING) == Direction.UP ? SHAPE_Y_UP : SHAPE_Y_DOWN;
            case Z:
                return SHAPE_Z;
            default:
                return SHAPE_Y_UP;
        }
    }
}
