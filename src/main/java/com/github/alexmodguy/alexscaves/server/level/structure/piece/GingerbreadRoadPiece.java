package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class GingerbreadRoadPiece extends StructurePiece {

    private final StructureTemplateManager structureTemplateManager;
    private final BlockPos origin;
    private final int length;
    private final int branchesLeft;
    private final Direction direction;
    public final List<StructurePiece> pendingChildren = Lists.newArrayList();

    public GingerbreadRoadPiece(StructureTemplateManager structureTemplateManager, BlockPos blockpos, int length, int branchesLeft, Direction direction) {
        super(ACStructurePieceRegistry.GINGERBREAD_ROAD.get(), 0, createBoundingBox(blockpos, direction, length));
        this.structureTemplateManager = structureTemplateManager;
        this.origin = blockpos;
        this.length = length;
        this.branchesLeft = branchesLeft;
        this.direction = direction;
    }

    public GingerbreadRoadPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ACStructurePieceRegistry.GINGERBREAD_ROAD.get(), tag);
        this.structureTemplateManager = manager;
        this.origin = new BlockPos(tag.getInt("OX"), tag.getInt("OY"), tag.getInt("OZ"));
        this.length = tag.getInt("Length");
        this.branchesLeft = tag.getInt("BranchesLeft");
        this.direction = Direction.from2DDataValue(tag.getInt("Direction"));
    }

    public GingerbreadRoadPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);
    }

    private static BoundingBox createBoundingBox(BlockPos origin, Direction direction, int length) {
        BoundingBox bb1 = BoundingBox.fromCorners(origin, origin.relative(direction, length));
        return new BoundingBox(bb1.minX(), -64, bb1.minZ(), bb1.maxX(), bb1.maxY() + 10, bb1.maxZ());
    }

    private BoundingBox inflateRoadBox(int by) {
        BoundingBox bb1 = this.getBoundingBox();
        int absX = Math.abs(direction.getStepX()) * by;
        int absZ = Math.abs(direction.getStepZ()) * by;
        return new BoundingBox(bb1.minX() + absX, -64, bb1.minZ() + absZ, bb1.maxX() + absX, bb1.maxY() + 10, bb1.maxZ() + absZ);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("OX", this.origin.getX());
        tag.putInt("OY", this.origin.getY());
        tag.putInt("OZ", this.origin.getZ());
        tag.putInt("Length", this.length);
        tag.putInt("BranchesLeft", this.branchesLeft);
        tag.putInt("Direction", this.direction.get2DDataValue());
    }

    @Override
    public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int built = 0;
        BlockPos building = origin;
        int prevYLevel = -1;
        while (built < length){
            int y = (int) CakeCaveStructurePiece.calculatePlateauHeight(building.getX(), building.getZ(), 7, true);
            BlockPos set = building.atY(y - 1);
            if(worldGenLevel.getBlockState(set.above()).isSolid() && set.getY() < worldGenLevel.getMaxBuildHeight()){
                set = set.above();
            }
            if(boundingBox.isInside(set)){
                BlockState replaceState = worldGenLevel.getBlockState(set);
                boolean placeFlag = false;
                if(replaceState.is(ACTagRegistry.GINGERBREAD_TOWN_GEN_REPLACEABLES)){
                    placeFlag = true;
                }else if(replaceState.canBeReplaced()){
                    placeFlag = true;
                    set = set.below();
                }
                if(placeFlag){
                    worldGenLevel.setBlock(set, ACBlockRegistry.GINGERBREAD_BRICKS.get().defaultBlockState(), 2);
                    if(prevYLevel > set.getY() && built > 0){
                        worldGenLevel.setBlock(set.above(), ACBlockRegistry.GINGERBREAD_BRICK_SLAB.get().defaultBlockState(), 2);
                    }
                }
            }
            building = building.relative(direction);
            built++;
            prevYLevel = set.getY();
        }
    }


    @Override
    public void addChildren(StructurePiece startPiece, StructurePieceAccessor piecesBuilder, RandomSource randomSource) {
        if(startPiece instanceof GingerbreadRoadPiece startRoadPiece && branchesLeft > 0){
            for(int childrenIndex = 0; childrenIndex < this.branchesLeft; childrenIndex++){
                Direction childDir = selectAnyOther(randomSource, true);
                int i = Math.max(length / 5, 2);
                int j = i + randomSource.nextInt(Math.max(length - i, 1));
                BlockPos offsetPos = origin.relative(direction, j).relative(direction);
                StructurePiece childPiece;
                boolean isRoad = false;
                if(randomSource.nextFloat() < 0.5F){
                    ResourceLocation templateResLoc = Util.getRandom(GingerbreadHousePiece.HOUSE_TEMPLATES, randomSource);
                    StructureTemplate houseTemplate = structureTemplateManager.getOrCreate(templateResLoc);
                    Rotation rotation = getRotationFromDirectionDefaultNorth(childDir);
                    Vec3i centerSize = houseTemplate.getSize(rotation);
                    BlockPos houseCenterPos1 = offsetPos.offset(-centerSize.getX() / 2, 0, -centerSize.getZ() / 2);
                    BlockPos houseCenterPos2 = houseTemplate.getZeroPositionWithTransform(houseCenterPos1, Mirror.NONE, rotation);
                    BlockPos houseCenterPos3 = houseCenterPos2.offset(childDir.getStepX() * -(centerSize.getX() / 2 + 1), 0, childDir.getStepZ() * -(centerSize.getZ() / 2 + 1));
                    childPiece = new GingerbreadHousePiece(structureTemplateManager, templateResLoc, houseCenterPos3, rotation);
                }else{
                    childPiece = new GingerbreadRoadPiece(structureTemplateManager, offsetPos, Math.max(4, length / 2), branchesLeft - 1, childDir);
                    isRoad = true;
                }
                if(!intersectsWithAnyPendingChildren(startRoadPiece, childPiece, isRoad)){
                    startRoadPiece.pendingChildren.add(childPiece);
                    piecesBuilder.addPiece(childPiece);
                }
            }
        }
    }

    private static boolean intersectsWithAnyPendingChildren(StructurePiece startPiece, StructurePiece newPiece, boolean inflateRoads){
        if(startPiece instanceof GingerbreadRoadPiece startRoadPiece) {
            for(StructurePiece pendingChild : startRoadPiece.pendingChildren){
                BoundingBox box = pendingChild.getBoundingBox();
                if(inflateRoads && pendingChild instanceof GingerbreadRoadPiece otherRoad && newPiece instanceof GingerbreadRoadPiece ourRoad && ourRoad.direction == otherRoad.direction){
                    box = otherRoad.inflateRoadBox(2);
                }
                if(box.intersects(newPiece.getBoundingBox())){
                    return true;
                }
            }
        }
        return false;
    }

    private Direction selectAnyOther(RandomSource randomSource, boolean limitToAxis){
        int tries = 0;
        Direction direction1 = direction;
        while ((limitToAxis ? direction1.getAxis() == direction.getAxis() : direction1 == direction) && tries < 256){
            direction1 = Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, randomSource);
            tries++;
        }
        return direction1;
    }

    private static Rotation getRotationFromDirectionDefaultNorth(Direction direction){
        switch (direction){
            case EAST:
                return Rotation.CLOCKWISE_90;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            default:
                return Rotation.NONE;
        }
    }

    public BlockPos getRoadEndPos() {
        return origin.relative(direction, length);
    }
}
