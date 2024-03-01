package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.HologramProjectorBlockEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HologramProjectorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);

    public HologramProjectorBlock() {
        super(Properties.of().mapColor(DyeColor.WHITE).strength(1.0F, 5.0F).sound(SoundType.METAL).lightLevel((i) -> 10));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return state.canSurvive(levelAccessor, blockPos) ? super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1) : Blocks.AIR.defaultBlockState();
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if (worldIn.getBlockEntity(pos) instanceof HologramProjectorBlockEntity projectorBlockEntity && !player.isShiftKeyDown() && heldItem.is(ACItemRegistry.HOLOCODER.get())) {
            CompoundTag entityTag = null;
            EntityType entityType = null;
            boolean flag = false;
            if (heldItem.getTag() != null) {
                CompoundTag entity = heldItem.getTag().getCompound("BoundEntityTag");
                Optional<EntityType<?>> optional = EntityType.by(entity);
                if (optional.isPresent()) {
                    entityType = optional.get();
                    entityTag = entity;
                    flag = true;
                }
            }
            if (!flag) {
                entityType = EntityType.PLAYER;
                CompoundTag playerTag = new CompoundTag();
                playerTag.putUUID("UUID", player.getUUID());
                String s = player.getEncodeId();
                if (s != null) {
                    playerTag.putString("id", s);
                }
                entityTag = playerTag;
            }
            projectorBlockEntity.setEntity(entityType, entityTag, player.getYHeadRot());
            worldIn.playSound((Player) null, pos, ACSoundRegistry.HOLOGRAM_STOP.get(), SoundSource.BLOCKS);
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HologramProjectorBlockEntity(pos, state);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, ACBlockEntityRegistry.HOLOGRAM_PROJECTOR.get(), HologramProjectorBlockEntity::tick);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(WATERLOGGED);
    }
}
