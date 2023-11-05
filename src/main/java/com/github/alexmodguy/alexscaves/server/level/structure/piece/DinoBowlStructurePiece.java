package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.PrimalMagmaBlock;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DinoBowlStructurePiece extends AbstractCaveGenerationStructurePiece {

    private VoronoiGenerator voronoiGenerator;



    public DinoBowlStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius);
    }

    public DinoBowlStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), tag);
    }

    public DinoBowlStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        if(voronoiGenerator == null){
            voronoiGenerator = new VoronoiGenerator(level.getSeed());
            voronoiGenerator.setOffsetAmount(0.6F);
        }
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        boolean flag = false;
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveAbove = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(cornerX, cornerY, cornerZ);
        carveAbove.set(cornerX, cornerY, cornerZ);
        carveBelow.set(cornerX, cornerY, cornerZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                MutableBoolean doFloor = new MutableBoolean(false);
                for (int y = 15; y >= 0; y--) {
                    carve.set(cornerX + x, Mth.clamp(cornerY + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), cornerZ + z);
                    if (!generateVolcanoes(level, random, carve) && inCircle(carve) && !checkedGetBlock(level, carve).is(Blocks.BEDROCK)) {
                        flag = true;
                        checkedSetBlock(level, carve, Blocks.CAVE_AIR.defaultBlockState());
                        surroundCornerOfLiquid(level, carve);
                        carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                        doFloor.setTrue();
                    }
                }
                if (doFloor.isTrue()) {
                    BlockState floor = checkedGetBlock(level, carveBelow);
                    if(!floor.isAir() && !floor.is(ACTagRegistry.VOLCANO_BLOCKS)){
                        decorateFloor(level, random, carveBelow.immutable());
                    }
                    doFloor.setFalse();
                }
            }
        }
        if (flag) {
            replaceBiomes(level, ACBiomeRegistry.PRIMORDIAL_CAVES, 32);
        }
    }

    private boolean generateVolcanoes(WorldGenLevel level, RandomSource randomSource, BlockPos carve) {
        //TODO: Volcano generation
        if(true){
            return false;
        }
        float volcanoSize = 35;
        float seperationDistance = volcanoSize + 180;
        double sampleX = carve.getX() / seperationDistance;
        double sampleZ = carve.getZ() / seperationDistance;
        double sampleOffsetX = sampleX + ACMath.sampleNoise3D((float) (sampleX + 1230), carve.getY() * 0.1F, (float) (sampleZ - 1230), 90F) * 0.8F;
        double sampleOffsetZ = sampleZ + ACMath.sampleNoise3D((float) (sampleX + 4321), carve.getY() * 0.1F, (float) (sampleZ - 4123), 90F) * 0.8F;
        VoronoiGenerator.VoronoiInfo info = voronoiGenerator.get2(sampleOffsetX, sampleOffsetZ);
        if (info.distance() < (volcanoSize / seperationDistance)) {
            double dist = info.distance() / (volcanoSize / seperationDistance);
            double invDist = 1F - dist;
            int maxVolcanoHeight = Math.max(40, height - 30);
            int holeBottomY = (int) (holeCenter.getY() - height * 0.5F);
            double volcanoCurve = calcVolcanoCurve(invDist) + ACMath.sampleNoise3D((float) (sampleX + 5000), 0F, (float) (sampleZ - 5000), 40F) * 0.1F;
            int lavaSink = volcanoCurve < 1.0F && dist < 0.15F ? 3 : 0;
            double volcanoHeight = maxVolcanoHeight * volcanoCurve;
            int generateHeight = (int) (holeBottomY + volcanoHeight - lavaSink);
            if(carve.getY() < generateHeight){
                BlockState checkedBlockstate = checkedGetBlock(level, carve);
                if(checkedBlockstate.is(Blocks.BEDROCK)){
                    return false;
                }
                int beneathAmount = generateHeight - carve.getY();
                int aboveAmount = carve.getY() - holeBottomY;
                float calderaAmount = (float) Math.sin(invDist * Math.PI);
                if(dist <= 1F/maxVolcanoHeight && carve.getY() == (int)(generateHeight - maxVolcanoHeight * 0.5) && canPlaceVolcanicCoreAt(level, carve)){
                    checkedSetBlock(level, carve, ACBlockRegistry.VOLCANIC_CORE.get().defaultBlockState());
                }else if(beneathAmount > 3 + calderaAmount * 20 && aboveAmount > 3 + calderaAmount * 10){
                    checkedSetBlock(level, carve, Blocks.LAVA.defaultBlockState());
                }else if((lavaSink > 0 || beneathAmount > 6 && aboveAmount > 3)){
                    checkedSetBlock(level, carve, ACBlockRegistry.PRIMAL_MAGMA.get().defaultBlockState().setValue(PrimalMagmaBlock.ACTIVE, true).setValue(PrimalMagmaBlock.PERMANENT, true));
                }else{
                    float magmaVeinNoise = ACMath.sampleNoise3D(carve.getX() - 19000, carve.getY() * 0.5F + 120, carve.getZ() + 19000, 20);
                    if(volcanoCurve < 0.035F){
                        if(randomSource.nextFloat() * 0.08F < volcanoCurve){
                            checkedSetBlock(level, carve, Blocks.PACKED_MUD.defaultBlockState());
                        }else{
                            checkedSetBlock(level, carve, ACBlockRegistry.LIMESTONE.get().defaultBlockState());
                        }
                    }else if(magmaVeinNoise < 0.075F && magmaVeinNoise > -0.075F){
                        checkedSetBlock(level, carve, ACBlockRegistry.PRIMAL_MAGMA.get().defaultBlockState());
                    }else{
                        checkedSetBlock(level, carve, ACBlockRegistry.FLOOD_BASALT.get().defaultBlockState());
                    }
                }
                return true;
            }
        }
        return false;
    }

    private double calcVolcanoCurve(double dist) {
        double off = 2 * dist - 1;
        double d0 = 0.5F * (0.85F * Math.sin(off * Mth.PI) + Math.pow(off, 3) + 0.5F);
        return dist > 0.85F ? d0 : Math.max(d0, dist * dist);
    }

    private void surroundCornerOfLiquid(WorldGenLevel level, Vec3i center) {
        BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            offset.set(center);
            offset.move(dir);
            BlockState state = checkedGetBlock(level, offset);
            if (!state.getFluidState().isEmpty()) {
                checkedSetBlock(level, offset, Blocks.SANDSTONE.defaultBlockState());
            }
        }
    }

    private boolean canPlaceVolcanicCoreAt(WorldGenLevel level, Vec3i center) {
        BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            offset.set(center);
            offset.move(dir);
            BlockState state = checkedGetBlock(level, offset);
            if (state.is(ACBlockRegistry.VOLCANIC_CORE.get())) {
                return false;
            }
        }
        return true;
    }

    private boolean inCircle(BlockPos carve) {
        float wallNoise = (ACMath.sampleNoise3D(carve.getX(), (int) (carve.getY() * 0.1F), carve.getZ(), 40) + 1.0F) * 0.5F;
        double yDist = ACMath.smin(1F - Math.abs(this.holeCenter.getY() - carve.getY()) / (float) (height * 0.5F), 1.0F, 0.3F);
        double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY(), this.holeCenter.getZ());
        double targetRadius = yDist * (radius * wallNoise) * radius;
        return distToCenter < targetRadius;
    }

    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos carveBelow) {
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        checkedSetBlock(level, carveBelow, grass);
        for (int i = 0; i < 1 + rand.nextInt(2); i++) {
            carveBelow = carveBelow.below();
            checkedSetBlock(level, carveBelow, dirt);
        }
    }
}