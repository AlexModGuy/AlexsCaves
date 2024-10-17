package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;

public class FrostedChocolateBlock extends Block {

    private static final VoronoiGenerator VORONOI_GENERATOR = new VoronoiGenerator(42L);
    private static final float COLORIZER_SAMPLE_SCALE = 0.0175F;
    private static final double COLORIZER_BLUR_RADIUS = 0.45F;
    private static final int COLORIZER_EDGE_SIZE_BLOCKS = 8;
    private static final int COLORIZER_R_DIFFERENCE = 5;
    private static final int COLORIZER_G_DIFFERENCE = 20;
    private static final int COLORIZER_B_DIFFERENCE = 50;
    private static final int MIN_SPIRAL_BY = 2;
    private static final int DOUBLE_SPIRAL_BY = 3;

    static {
        VORONOI_GENERATOR.setOffsetAmount(0.35F);
        VORONOI_GENERATOR.setDistanceType(VoronoiGenerator.DistanceType.euclidean);
    }

    public FrostedChocolateBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(1.0F, 2.0F).sound(ACSoundTypes.DENSE_CANDY).instrument(NoteBlockInstrument.BASEDRUM).randomTicks());
    }

    private boolean shouldRemain(LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockpos = blockPos.above();
        BlockState blockstate = levelReader.getBlockState(blockpos);
        return !blockstate.isFaceSturdy(levelReader, blockPos, Direction.DOWN, SupportType.FULL) || blockstate.is(ACBlockRegistry.BLOCK_OF_FROSTING.get()) || !blockstate.canOcclude() || blockstate.getFluidState().getAmount() == 8;
    }


    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return direction == Direction.UP && !shouldRemain(levelAccessor, blockPos) ? ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState() : super.updateShape(blockState, direction, blockState1, levelAccessor, blockPos, blockPos1);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return shouldRemain(context.getLevel(), context.getClickedPos()) ? super.getStateForPlacement(context) : ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState();
    }

    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!shouldRemain(serverLevel, blockPos)) {
            serverLevel.setBlockAndUpdate(blockPos, ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState());
        }
    }

    public static int calculateFrostingColor(@Nullable BlockPos blockPos) {
        if(blockPos == null){
            return 0XFFFFFF;
        }
        VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(blockPos.getX() * COLORIZER_SAMPLE_SCALE, blockPos.getZ() * COLORIZER_SAMPLE_SCALE);
        double distance = info.distance();
        if (distance < 0.5F) {
            double closestDist = Math.min(info.distance(), info.distance1());
            double rotateDir = info.hash() < 0 ? -1F : 1F;
            double spiralCount = MIN_SPIRAL_BY + DOUBLE_SPIRAL_BY * (1D + info.hash());
            double angle = rotateDir * distance * 360 * spiralCount;
            double targetSpiralX = (Math.sin(0.017453292519943295 * angle));
            double targetSpiralZ = (Math.cos(0.017453292519943295 * angle));
            double d0 = (targetSpiralX - info.localPos().x);
            double d1 = (targetSpiralZ - info.localPos().z);
            double distToTarget = Math.pow(d0 * d0 + d1 * d1, 2);
            double distToCenter = closestDist * 2F > COLORIZER_BLUR_RADIUS ? 1F - ACMath.smin((float) ((closestDist * 2F - COLORIZER_BLUR_RADIUS) / COLORIZER_BLUR_RADIUS), 1.0F, 0.2F) : 1;
            double edgeDistScaled = COLORIZER_SAMPLE_SCALE * COLORIZER_EDGE_SIZE_BLOCKS;
            if (info.distance1() < info.distance() + edgeDistScaled) {
                double lessBy = (info.distance() + edgeDistScaled - info.distance1()) / (edgeDistScaled);
                distToCenter *= 1F - lessBy;
            }
            int rDec = (int) ACMath.smin((float) (COLORIZER_R_DIFFERENCE * (distToTarget * distToCenter)), COLORIZER_R_DIFFERENCE, 0.1F);
            int gDec = (int) ACMath.smin((float) (COLORIZER_G_DIFFERENCE * (distToTarget * distToCenter)), COLORIZER_G_DIFFERENCE, 0.1F);
            int bDec = (int) ACMath.smin((float) (COLORIZER_B_DIFFERENCE * (distToTarget * distToCenter)), COLORIZER_B_DIFFERENCE, 0.1F);
            return FastColor.ARGB32.color(255 - rDec, 255 - gDec, 255 - bDec, 255);
        } else {
            return 0XFFFFFF;
        }
    }
}
