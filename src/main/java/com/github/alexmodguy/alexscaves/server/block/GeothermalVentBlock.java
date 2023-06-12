package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.GeothermalVentBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GeothermalVentBlock extends BaseEntityBlock {

    public static final IntegerProperty SMOKE_TYPE = IntegerProperty.create("smoke_type", 0, 3);
    public static final BooleanProperty SPAWNING_PARTICLES = BooleanProperty.create("spawning_particles");
    public GeothermalVentBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2F, 5.0F).sound(SoundType.TUFF));
        this.registerDefaultState(this.stateDefinition.any().setValue(SMOKE_TYPE, Integer.valueOf(0)).setValue(SPAWNING_PARTICLES, true));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(SMOKE_TYPE, getSmokeType(levelaccessor, blockpos)).setValue(SPAWNING_PARTICLES, isSpawningParticles(blockpos, levelaccessor));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return state.setValue(SMOKE_TYPE, getSmokeType(levelAccessor, blockPos)).setValue(SPAWNING_PARTICLES, isSpawningParticles(blockPos, levelAccessor));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SMOKE_TYPE, SPAWNING_PARTICLES);
    }

    public int getSmokeType(LevelAccessor level, BlockPos blockpos) {
        BlockState state = level.getBlockState(blockpos.below());
        if(state.getBlock() instanceof GeothermalVentBlock){
            return state.getValue(SMOKE_TYPE);
        }
        if(state.getFluidState().getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get()){
            return 3;
        }else if(state.getFluidState().is(FluidTags.WATER)){
            return 1;
        }else if(state.getFluidState().is(FluidTags.LAVA)){
            return 2;
        }
        return 0;
    }

    public boolean isSpawningParticles(BlockPos pos, LevelAccessor level){
        BlockState above = level.getBlockState(pos.above());
        return (above.isAir() || !above.blocksMotion());
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {

    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack heldItem = player.getItemInHand(hand);
        if(heldItem.is(Items.GLASS_BOTTLE) && blockState.getValue(SMOKE_TYPE) == 3 && blockState.getValue(SPAWNING_PARTICLES)){
            ItemStack bottle = new ItemStack(ACItemRegistry.RADON_BOTTLE.get());
            if(!player.addItem(bottle)){
                player.drop(bottle, false);
            }
            if(!player.isCreative()){
                heldItem.shrink(1);
            }
            player.playSound(SoundEvents.BOTTLE_FILL);
            return InteractionResult.SUCCESS;
        }
        return super.use(blockState, level, blockPos, player, hand, result);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        if (level.isClientSide) {
            return state.getValue(SMOKE_TYPE) > 0 && state.getValue(SPAWNING_PARTICLES) ? createTickerHelper(entityType, ACBlockEntityRegistry.GEOTHERMAL_VENT.get(), GeothermalVentBlockEntity::particleTick) : null;
        } else {
            return null;
        }
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeothermalVentBlockEntity(pos, state);
    }

}
