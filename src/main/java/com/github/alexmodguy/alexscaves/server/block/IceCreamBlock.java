package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class IceCreamBlock extends Block {

    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 2);
    public static final VoxelShape DRIPPING_SHAPE = Block.box(0, 2, 0, 16, 16, 16);
    public static final VoxelShape ABOVE_OCCLUDE_SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    private final int dripColor;

    public IceCreamBlock(int dripColor) {
        super(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().strength(3F).sound(ACSoundTypes.SQUISHY_CANDY));
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, 0));
        this.dripColor = dripColor;
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return state.getValue(TYPE) == 2 ? DRIPPING_SHAPE : super.getShape(state, getter, pos, context);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        return this.defaultBlockState().setValue(TYPE, getType(levelaccessor, context.getClickedPos()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return state.setValue(TYPE, getType(levelAccessor, blockPos));
    }

    public void onLand(Level level, BlockPos pos, BlockState blockState, BlockState blockState1, FallingBlockEntity fallingBlockEntity) {
        level.setBlock(pos, blockState.setValue(TYPE, getType(level, pos)), 3);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(TYPE);
    }

    public int getType(LevelAccessor levelAccessor, BlockPos iceCreamPos) {
        BlockState beneathState = levelAccessor.getBlockState(iceCreamPos.below());
        if (beneathState.is(this)) {
            return 0;
        } else if (beneathState.isFaceSturdy(levelAccessor, iceCreamPos.below(), Direction.UP)) {
            return 1;
        } else {
            return 2;
        }
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        boolean melting = false;
        if (melting || blockState.getValue(TYPE) == 2) {
            if (randomSource.nextInt(melting ? 1 : 10) == 0) {
                Direction direction = !melting ? Direction.DOWN : Direction.getRandom(randomSource);
                if (direction != Direction.UP) {
                    BlockPos blockpos = blockPos.relative(direction);
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (!blockState.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                        double d0 = direction.getStepX() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepX() * 0.6D;
                        double d1 = direction.getStepY() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepY() * 0.6D;
                        double d2 = direction.getStepZ() == 0 ? randomSource.nextDouble() : 0.5D + (double) direction.getStepZ() * 0.6D;
                        if (blockState.getValue(TYPE) == 2 && direction == Direction.DOWN) {
                            d1 = 0.1;
                        }
                        level.addAlwaysVisibleParticle(ACParticleRegistry.ICE_CREAM_DRIP.get(), true, (double) blockPos.getX() + d0, (double) blockPos.getY() + d1, (double) blockPos.getZ() + d2, dripColor, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return true;
    }

    public boolean useShapeForLightOcclusion(BlockState p_60576_) {
        return true;
    }

    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return state.getValue(TYPE) == 2 ? DRIPPING_SHAPE : ABOVE_OCCLUDE_SHAPE;
    }
}
