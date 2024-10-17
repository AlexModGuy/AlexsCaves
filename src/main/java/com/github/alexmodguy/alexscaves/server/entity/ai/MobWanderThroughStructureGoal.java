package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.CakeCaveStructurePiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.GingerbreadRoadPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MobWanderThroughStructureGoal extends RandomStrollGoal {

    private final TagKey<Structure> structureTagKey;
    private final double maximumDistance;
    private final double maximumYDistance;

    private int errorCooldown = 0;

    public MobWanderThroughStructureGoal(PathfinderMob mob, double speed, int chance, TagKey<Structure> structureTagKey, double maximumDistance, double maximumYDistance) {
        super(mob, speed, chance, false);
        this.structureTagKey = structureTagKey;
        this.maximumDistance = maximumDistance;
        this.maximumYDistance = maximumYDistance;
    }

    public boolean canUse() {
        if (errorCooldown > 0) {
            errorCooldown--;
        }
        return super.canUse();
    }

    public void tick() {
        super.tick();
        if (errorCooldown > 0) {
            errorCooldown--;
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        StructureStart start = getNearestStructure(mob.blockPosition());
        if (start != null && start.isValid() || errorCooldown > 0) {
            List<BlockPos> validPieceCenters = new ArrayList<>();
            for (StructurePiece piece : start.getPieces()) {
                BoundingBox boundingbox = piece.getBoundingBox();
                BlockPos blockpos = boundingbox.getCenter().atY(boundingbox.minY() + 1);
                if(piece instanceof GingerbreadRoadPiece gingerbreadRoadPiece){
                    BlockPos roadEnd = gingerbreadRoadPiece.getRoadEndPos();
                    blockpos = blockpos.atY((int) CakeCaveStructurePiece.calculatePlateauHeight(roadEnd.getX(), roadEnd.getZ(), 7, true));
                }
                double yDist = Math.abs(blockpos.getY() - mob.blockPosition().getY());
                if (this.mob.distanceToSqr(Vec3.atCenterOf(blockpos)) <= this.maximumDistance * this.maximumDistance && yDist < maximumYDistance) {
                    validPieceCenters.add(blockpos);
                }
            }
            if (!validPieceCenters.isEmpty()) {
                BlockPos randomCenter = validPieceCenters.size() > 1 ? validPieceCenters.get(mob.getRandom().nextInt(validPieceCenters.size() - 1)) : validPieceCenters.get(0);
                while(mob.level().getBlockState(randomCenter).isAir() && randomCenter.getY() > mob.level().getMinBuildHeight()){
                    randomCenter = randomCenter.below();
                }
                while(!mob.level().getBlockState(randomCenter).isAir() && randomCenter.getY() < mob.level().getMaxBuildHeight()){
                    randomCenter = randomCenter.above();
                }
                return Vec3.atCenterOf(randomCenter.offset(mob.getRandom().nextInt(2) - 1, 0, mob.getRandom().nextInt(2) - 1));
            }
        }
        return null;
    }

    @Nullable
    private StructureStart getNearestStructure(BlockPos pos) {
        ServerLevel serverlevel = (ServerLevel) this.mob.level();
        try {
            StructureStart start = serverlevel.structureManager().getStructureWithPieceAt(pos, structureTagKey);
            if (start.isValid()) {
                return start;
            } else {
                BlockPos nearestOf = serverlevel.findNearestMapStructure(structureTagKey, pos, (int) (this.maximumDistance / 16), false);
                if (nearestOf == null || nearestOf.distToCenterSqr(this.mob.getX(), this.mob.getY(), this.mob.getZ()) > 256 || !serverlevel.isLoaded(nearestOf)) {
                    return null;
                }
                return serverlevel.structureManager().getStructureWithPieceAt(nearestOf, structureTagKey);
            }
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn(this.mob + " encountered an issue searching for a nearby structure.");
            errorCooldown = 2000 + this.mob.getRandom().nextInt(2000);
            return null;
        }
    }
}

