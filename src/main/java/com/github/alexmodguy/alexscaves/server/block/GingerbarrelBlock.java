package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.GingerbarrelBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Iterator;

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
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof GingerbarrelBlockEntity gingerbarrelBlockEntity) {
                if (!player.isSpectator() && !player.isCreative()) {
                    angerGingerbreadMen(level, player);
                }
                player.openMenu((GingerbarrelBlockEntity) gingerbarrelBlockEntity);
                player.awardStat(Stats.OPEN_BARREL);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Unique
    private void angerGingerbreadMen(Level level, Entity opener) {
        if (opener instanceof Player player) {
            Iterator<GingerbreadManEntity> var4 = level.getEntitiesOfClass(GingerbreadManEntity.class, player.getBoundingBox().inflate(10, 5, 10)).iterator();
            while (var4.hasNext()) {
                LivingEntity entity = var4.next();
                if (entity instanceof GingerbreadManEntity gingerbreadMan && !gingerbreadMan.isOvenSpawned()) {
                    gingerbreadMan.setTarget(player);
                }
            }
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
