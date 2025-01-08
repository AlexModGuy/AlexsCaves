package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.item.PrimordialArmorItem;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DinosaurChopBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public final Map<BlockState, VoxelShape> shapeMap = new HashMap<>();

    private final int foodAmount;
    private final float saturationAmount;

    public DinosaurChopBlock(int foodAmount, float saturationAmount) {
        super(Properties.of().mapColor(MapColor.COLOR_RED).strength(1F, 1.0F).sound(SoundType.CANDLE).noOcclusion().dynamicShape().randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(BITES, Integer.valueOf(0)).setValue(WATERLOGGED, false));
        this.foodAmount = foodAmount;
        this.saturationAmount = saturationAmount;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getChopShape(state);
    }

    protected VoxelShape getChopShape(BlockState state) {
        if (shapeMap.containsKey(state)) {
            return shapeMap.get(state);
        } else {
            int bites = state.getValue(BITES);
            VoxelShape shape;
            if (bites == 0) {
                shape = Shapes.block();
            } else {
                Direction facing = state.getValue(FACING);
                VoxelShape merge = ThinBoneBlock.SHAPE_Y;
                switch (facing.getAxis()) {
                    case X:
                        merge = ThinBoneBlock.SHAPE_X;
                        break;
                    case Y:
                        merge = ThinBoneBlock.SHAPE_Y;
                        break;
                    case Z:
                        merge = ThinBoneBlock.SHAPE_Z;
                        break;
                }
                shape = Shapes.join(merge, calculateShapeForRotation(facing, bites), BooleanOp.OR);
            }
            shapeMap.put(state, shape);
            return shape;
        }
    }

    private static VoxelShape calculateShapeForRotation(Direction facing, int bites) {
        float minHeight = 4 * bites;
        float height = 16 - minHeight;
        switch (facing) {
            case UP:
                return Block.box(0, 0, 0, 16, height, 16);
            case DOWN:
                return Block.box(0, minHeight, 0, 16, 16, 16);
            case NORTH:
                return Block.box(0, 0, minHeight, 16, 16, 16);
            case SOUTH:
                return Block.box(0, 0, 0, 16, 16, height);
            case EAST:
                return Block.box(0, 0, 0, height, 16, 16);
            case WEST:
                return Block.box(minHeight, 0, 0, 16, 16, 16);
        }
        return Shapes.empty();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, context.getNearestLookingDirection().getOpposite());

    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(WATERLOGGED, BITES, FACING);
    }

    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide) {
            if (eat(level, blockPos, blockState, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (itemstack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return eat(level, blockPos, blockState, player);
    }

    protected InteractionResult eat(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            int extraShanksFromArmor = this == ACBlockRegistry.DINOSAUR_CHOP.get() ? PrimordialArmorItem.getExtraSaturationFromArmor(player) : 0;
            player.getFoodData().eat(this.foodAmount + extraShanksFromArmor, this.saturationAmount + (extraShanksFromArmor * 0.125F));
            int i = blockState.getValue(BITES);
            levelAccessor.gameEvent(player, GameEvent.EAT, blockPos);
            if (i < 3) {
                levelAccessor.setBlock(blockPos, blockState.setValue(BITES, Integer.valueOf(i + 1)), 3);
            } else {
                levelAccessor.removeBlock(blockPos, false);
                levelAccessor.setBlock(blockPos, ACBlockRegistry.THIN_BONE.get().defaultBlockState().setValue(ThinBoneBlock.AXIS, blockState.getValue(FACING).getAxis()), 4);
                levelAccessor.gameEvent(player, GameEvent.BLOCK_DESTROY, blockPos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    public void randomTick(BlockState currentState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(4) == 0 && this == ACBlockRegistry.DINOSAUR_CHOP.get() && isFireBelow(level, blockPos.below())) {
            BlockState blockstate1 = ACBlockRegistry.COOKED_DINOSAUR_CHOP.get().defaultBlockState().setValue(FACING, currentState.getValue(FACING)).setValue(BITES, currentState.getValue(BITES));
            level.setBlockAndUpdate(blockPos, blockstate1);
        }
    }

    private boolean isFireBelow(Level level, BlockPos pos) {
        while (level.getBlockState(pos).isAir() && pos.getY() > level.getMinBuildHeight()) {
            pos = pos.below();
        }
        BlockState fireState = level.getBlockState(pos);
        if (fireState.getBlock() instanceof CampfireBlock){
            return fireState.getValue(CampfireBlock.LIT) && fireState.is(ACTagRegistry.COOKS_MEAT_BLOCKS);
        } else {
            return fireState.is(ACTagRegistry.COOKS_MEAT_BLOCKS);
        }
    }

    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return getOutputSignal(blockState.getValue(BITES));
    }

    public static int getOutputSignal(int i) {
        return (7 - i) * 2;
    }

    public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos pos, BlockState blockState, Fluid fluid) {
        return blockState.getValue(BITES) != 0 && fluid == Fluids.WATER;
    }

    public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(BITES) != 0 && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            levelAccessor.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
            if (!blockState.canSurvive(levelAccessor, blockPos)) {
                levelAccessor.destroyBlock(blockPos, true);
            }

            return new ItemStack(Items.WATER_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
    }

}
