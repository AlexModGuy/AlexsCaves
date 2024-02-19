package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.VolcanicCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;

public class VolcanicCoreBlock extends BaseEntityBlock {
    public VolcanicCoreBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().lightLevel((state) -> {
            return 8;
        }).strength(55.0F, 1200.0F).isValidSpawn((state, getter, pos, entityType) -> {
            return entityType.fireImmune();
        }).hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true).sound(ACSoundTypes.FLOOD_BASALT));
    }

    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
            entity.setSecondsOnFire(3);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (!this.scanForLava(levelAccessor, blockPos)) {
            levelAccessor.scheduleTick(blockPos, this, 60 + levelAccessor.getRandom().nextInt(40));
        }

        return super.updateShape(state, direction, blockState, levelAccessor, blockPos, blockPos1);
    }

    public void tick(BlockState state, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!this.scanForLava(serverLevel, blockPos)) {
            serverLevel.setBlock(blockPos, ACBlockRegistry.FLOOD_BASALT.get().defaultBlockState(), 2);
        }
    }

    protected boolean scanForLava(BlockGetter blockGetter, BlockPos blockPos) {
        int adjacent = 0;
        for(Direction direction : Direction.values()) {
            FluidState fluidstate = blockGetter.getFluidState(blockPos.relative(direction));
            if (fluidstate.is(FluidTags.LAVA)) {
                adjacent++;
            }
        }
        return adjacent > 3;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!this.scanForLava(context.getLevel(), context.getClickedPos())) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 60 + context.getLevel().getRandom().nextInt(40));
        }
        return this.defaultBlockState();
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return  p_152180_.isClientSide ? null : createTickerHelper(p_152182_, ACBlockEntityRegistry.VOLCANIC_CORE.get(), VolcanicCoreBlockEntity::tick);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolcanicCoreBlockEntity(pos, state);
    }

}
