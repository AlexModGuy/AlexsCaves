package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DonutArchStructurePiece extends StructurePiece {
    protected final BlockPos centerPos;
    protected final BlockPos chunkCorner;

    protected final Direction direction;
    private double width;
    private int frostingType;

    private static Block[] SPRINKLES_BLOCKS = new Block[]{
            ACBlockRegistry.RED_ROCK_CANDY.get(),
            ACBlockRegistry.PURPLE_ROCK_CANDY.get(),
            ACBlockRegistry.YELLOW_ROCK_CANDY.get(),
            ACBlockRegistry.LIGHT_BLUE_ROCK_CANDY.get(),
            ACBlockRegistry.ORANGE_ROCK_CANDY.get(),
            ACBlockRegistry.LIME_ROCK_CANDY.get(),
            ACBlockRegistry.GREEN_ROCK_CANDY.get(),
            ACBlockRegistry.BLUE_ROCK_CANDY.get(),
            ACBlockRegistry.MAGENTA_ROCK_CANDY.get(),
            ACBlockRegistry.PINK_ROCK_CANDY.get(),
    };

    private VoronoiGenerator voronoiGenerator;

    public DonutArchStructurePiece(BlockPos centerPos, BlockPos chunkCorner, Direction direction, double width, int frostingType) {
        super(ACStructurePieceRegistry.DONUT_ARCH.get(), 0, createBoundingBox(chunkCorner, direction));
        this.centerPos = centerPos;
        this.chunkCorner = chunkCorner;
        this.direction = direction;
        this.width = width;
        this.frostingType = frostingType;
    }

    public DonutArchStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.DONUT_ARCH.get(), tag);
        this.centerPos = new BlockPos(tag.getInt("CX"), tag.getInt("CY"), tag.getInt("CZ"));
        this.chunkCorner = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
        this.direction = Direction.from2DDataValue(tag.getInt("Direction"));
        this.width = tag.getDouble("Width");
        this.frostingType = tag.getInt("FrostingType");
    }

    public DonutArchStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    private static BoundingBox createBoundingBox(BlockPos origin, Direction direction) {
        ChunkPos chunkPos = new ChunkPos(origin);
        return new BoundingBox(chunkPos.getMinBlockX(), origin.getY() - 2, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), origin.getY() + 16, chunkPos.getMaxBlockZ());
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("CX", this.centerPos.getX());
        tag.putInt("CY", this.centerPos.getY());
        tag.putInt("CZ", this.centerPos.getZ());
        tag.putInt("TPX", this.chunkCorner.getX());
        tag.putInt("TPY", this.chunkCorner.getY());
        tag.putInt("TPZ", this.chunkCorner.getZ());
        tag.putInt("Direction", this.direction.get2DDataValue());
        tag.putDouble("Width", this.width);
        tag.putInt("FrostingType", this.frostingType);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        if (voronoiGenerator == null) {
            voronoiGenerator = new VoronoiGenerator(level.getSeed());
            voronoiGenerator.setOffsetAmount(1.0F);
            voronoiGenerator.setDistanceType(VoronoiGenerator.DistanceType.euclidean);
        }
        BlockState frosting = ACBlockRegistry.BLOCK_OF_FROSTING.get().defaultBlockState();
        if(frostingType == 1){
            frosting = ACBlockRegistry.BLOCK_OF_VANILLA_FROSTING.get().defaultBlockState();
        }else if(frostingType == 2){
            frosting = ACBlockRegistry.BLOCK_OF_CHOCOLATE_FROSTING.get().defaultBlockState();
        }
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos centeredCenter = centerPos.offset(8, 0, 8);
        double outerWidth = Math.pow(width, 2);
        double innerWidth = Math.pow(width * 0.6, 2);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 15; y >= 0; y--) {
                    pos.set(cornerX + x, Mth.clamp(cornerY + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), cornerZ + z);
                    double forwardsNoise1 = (ACMath.sampleNoise2D(direction.getAxis() == Direction.Axis.X ? pos.getZ() : pos.getX(), pos.getY(), 20) + 1.0D) * 0.5D;
                    double length = 15 - 4 * forwardsNoise1;
                    double frostingNoise = (ACMath.sampleNoise2D(direction.getAxis() == Direction.Axis.X ? pos.getZ() : pos.getX(), pos.getY(), 10) + 1.0D) * 0.5D;
                    double distSides = pos.distToCenterSqr(direction.getAxis() == Direction.Axis.X ? centeredCenter.getX() : pos.getX(), centeredCenter.getY(), direction.getAxis() == Direction.Axis.Z ? centeredCenter.getZ() : pos.getZ());
                    double distForwards = direction.getAxis() == Direction.Axis.X ? centeredCenter.getZ() - pos.getZ() : centeredCenter.getX() - pos.getX();
                    double distForwardsAbs = Math.abs(distForwards);
                    if(distForwardsAbs < length){
                        boolean frosted = calculateFrostingDepth(direction, centeredCenter, pos) >= 2 + frostingNoise * 4;
                        double widthShrink = frosted ? 0 : 25;
                        double distForwardsClamped = distForwardsAbs / length;
                        float distForwardsSmin = ACMath.smin((float)(distForwardsClamped * distForwardsClamped), 1.0F, 0.3F);
                        float outerForwardsSmooth = 1F - distForwardsSmin;
                        float innerForwardsSmooth = distForwardsSmin + 0.5F;
                        if(distSides < (outerWidth - widthShrink) * outerForwardsSmooth && distSides > (innerWidth + widthShrink) * innerForwardsSmooth && !level.getBlockState(pos).is(Blocks.BEDROCK)){
                            if(frosted){
                                if(!placeSprinklesAt(level, pos)){
                                    checkedSetBlock(level, pos, frosting);
                                }
                            }else{
                                checkedSetBlock(level, pos, ACBlockRegistry.DOUGH_BLOCK.get().defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean placeSprinklesAt(WorldGenLevel level, BlockPos.MutableBlockPos pos) {
        float voronoiSampleSize = 0.3F;
        VoronoiGenerator.VoronoiInfo info = voronoiGenerator.get3(pos.getX() * voronoiSampleSize, pos.getY() * voronoiSampleSize, pos.getZ() * voronoiSampleSize);
        if(info.distance() < 0.5F){
            float sprinkleNoise = Math.abs(ACMath.sampleNoise3D(pos.getX() + 3334, pos.getY() - 200, pos.getZ() + 22223, 1.0F));
            if(Math.abs(sprinkleNoise) < 0.1){
                int index = (int) Mth.clamp((info.hash() + 1.0F) * 0.5F * SPRINKLES_BLOCKS.length, 0, SPRINKLES_BLOCKS.length - 1);
                BlockState block = SPRINKLES_BLOCKS[index].defaultBlockState();
                checkedSetBlock(level, pos, block);
                return true;
            }
        }
        return false;
    }

    private float calculateFrostingDepth(Direction direction, Vec3i centeredCenter, Vec3i pos){
        switch (direction){
            case NORTH:
                return centeredCenter.getX() - pos.getX();
            case EAST:
                return pos.getZ() - centeredCenter.getZ();
            case SOUTH:
                return pos.getX() - centeredCenter.getX();
            case WEST:
                return centeredCenter.getZ() - pos.getZ();
        }
        return 0;
    }


    public void checkedSetBlock(WorldGenLevel level, BlockPos position, BlockState state) {
        if (this.getBoundingBox().isInside(position)) {
            level.setBlock(position, state, 128);
        }
    }

    public BlockState checkedGetBlock(WorldGenLevel level, BlockPos position) {
        if (this.getBoundingBox().isInside(position)) {
            return level.getBlockState(position);
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

}
