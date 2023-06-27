package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.QuarryBlock;
import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class QuarryBlockEntity extends BlockEntity {

    private static int FURTHEST_TORCH_DISTANCE = 20;
    private float previousRotation;
    private float rotation;

    private BlockPos bottomLeftTorch;
    private BlockPos bottomRightTorch;
    private BlockPos topLeftTorch;
    private BlockPos topRightTorch;
    private boolean complete;
    private int checkTimer;
    public int spinFor;
    private AABB miningBox;
    private QuarrySmasherEntity serverSmasher = null;

    private BlockPos lastMineablePos;

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.QUARRY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, QuarryBlockEntity entity) {
        entity.previousRotation = entity.rotation;
        if (entity.checkTimer-- < 0) {
            entity.checkTimer = 20 + level.random.nextInt(20);
            if (entity.searchForTorches(level, blockPos, state.getValue(QuarryBlock.FACING))) {
                entity.complete = true;
                AABB aabb1 = new AABB(entity.bottomLeftTorch, entity.bottomRightTorch);
                AABB aabb2 = new AABB(entity.topLeftTorch, entity.topRightTorch);
                entity.miningBox = aabb1.minmax(aabb2);
                if (!level.isClientSide) {
                    entity.lastMineablePos = entity.findMinableBlock(level, blockPos.getY() + 3).orElse(null);
                    if (entity.serverSmasher == null) {
                        QuarrySmasherEntity closest = null;
                        Vec3 center = Vec3.atCenterOf(blockPos);
                        for (QuarrySmasherEntity quarrySmasher : level.getEntitiesOfClass(QuarrySmasherEntity.class, entity.miningBox.inflate(0, 100, 0))) {
                            if (closest == null || quarrySmasher.distanceToSqr(center) < closest.distanceToSqr(center)) {
                                closest = quarrySmasher;
                            }
                        }
                        entity.serverSmasher = closest;
                    }
                }
            } else {
                entity.complete = false;
            }
        }
        if (level.isClientSide) {
            entity.spawnLightningBetween(level, entity.bottomLeftTorch, entity.bottomRightTorch);
            entity.spawnLightningBetween(level, entity.bottomLeftTorch, entity.topLeftTorch);
            entity.spawnLightningBetween(level, entity.bottomRightTorch, entity.topRightTorch);
            entity.spawnLightningBetween(level, entity.topLeftTorch, entity.topRightTorch);
        } else if (entity.serverSmasher != null) {
            entity.serverSmasher.setQuarryPos(blockPos);
            if (entity.serverSmasher.isRemoved()) {
                entity.serverSmasher = null;
            } else if (entity.complete && entity.lastMineablePos != null) {
                entity.serverSmasher.setInactive(false);
            } else {
                entity.serverSmasher.setInactive(true);
                entity.serverSmasher = null;
            }
        }
        if (entity.spinFor > 0) {
            entity.spinFor--;
            entity.rotation += Math.min(10, entity.spinFor) * 0.1F;
        }
    }

    private boolean searchForTorches(Level level, BlockPos blockPos, Direction blockFacing) {
        BlockPos directlyBehind = blockPos.relative(blockFacing.getOpposite());
        BlockPos.MutableBlockPos mutableTorchPos = directlyBehind.mutable();

        int dist = 0;
        Direction leftTorchDir = blockFacing.getOpposite().getCounterClockWise();
        Direction rightTorchDir = blockFacing.getOpposite().getClockWise();
        while (dist < FURTHEST_TORCH_DISTANCE && !isMinable(level, mutableTorchPos) && level.isLoaded(mutableTorchPos)) {
            mutableTorchPos.move(leftTorchDir);
            dist++;
        }
        if (level.getBlockState(mutableTorchPos).is(ACBlockRegistry.MAGNETIC_LIGHT.get())) {
            bottomLeftTorch = mutableTorchPos.immutable();
        } else {
            bottomLeftTorch = null;
            return false;
        }

        dist = 0;
        mutableTorchPos.set(directlyBehind);
        mutableTorchPos.move(rightTorchDir);
        while (dist < FURTHEST_TORCH_DISTANCE && !isMinable(level, mutableTorchPos) && level.isLoaded(mutableTorchPos)) {
            mutableTorchPos.move(rightTorchDir);
            dist++;
        }
        if (level.getBlockState(mutableTorchPos).is(ACBlockRegistry.MAGNETIC_LIGHT.get())) {
            bottomRightTorch = mutableTorchPos.immutable();
        } else {
            bottomRightTorch = null;
            return false;
        }

        dist = 0;
        mutableTorchPos.set(bottomLeftTorch);
        mutableTorchPos.move(blockFacing.getOpposite());
        while (dist < FURTHEST_TORCH_DISTANCE && !isMinable(level, mutableTorchPos) && level.isLoaded(mutableTorchPos)) {
            mutableTorchPos.move(blockFacing.getOpposite());
            dist++;
        }
        if (level.getBlockState(mutableTorchPos).is(ACBlockRegistry.MAGNETIC_LIGHT.get())) {
            topLeftTorch = mutableTorchPos.immutable();
        } else {
            topLeftTorch = null;
            return false;
        }


        dist = 0;
        mutableTorchPos.set(bottomRightTorch);
        mutableTorchPos.move(blockFacing.getOpposite());
        while (dist < FURTHEST_TORCH_DISTANCE && !isMinable(level, mutableTorchPos) && level.isLoaded(mutableTorchPos)) {
            mutableTorchPos.move(blockFacing.getOpposite());
            dist++;
        }
        if (level.getBlockState(mutableTorchPos).is(ACBlockRegistry.MAGNETIC_LIGHT.get())) {
            topRightTorch = mutableTorchPos.immutable();
        } else {
            topRightTorch = null;
            return false;
        }
        return true;
    }

    private void spawnLightningBetween(Level level, BlockPos pos1, BlockPos pos2) {
        if (pos1 != null && pos2 != null && level.random.nextInt(4) == 0) {
            Vec3 particleFrom;
            Vec3 particleTo;
            if (level.random.nextBoolean()) {
                particleFrom = Vec3.upFromBottomCenterOf(pos1, 0.4F);
                particleTo = Vec3.upFromBottomCenterOf(pos2, 0.4F);
            } else {
                particleFrom = Vec3.upFromBottomCenterOf(pos2, 0.4F);
                particleTo = Vec3.upFromBottomCenterOf(pos1, 0.4F);
            }
            level.addParticle(ACParticleRegistry.QUARRY_BORDER_LIGHTING.get(), particleFrom.x, particleFrom.y, particleFrom.z, particleTo.x, particleTo.y, particleTo.z);
        }
    }

    public Optional<BlockPos> findMinableBlock(Level level, double yStart) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos highest = null;
        for (int x = (int) (miningBox.minX + 1); x < (int) miningBox.maxX; x++) {
            for (int z = (int) (miningBox.minZ + 1); z < (int) miningBox.maxZ; z++) {
                mutableBlockPos.set(x, yStart, z);
                while (mutableBlockPos.getY() > level.getMinBuildHeight() + 1 && !isMinable(level, mutableBlockPos)) {
                    mutableBlockPos.move(0, -1, 0);
                }
                if (isMinable(level, mutableBlockPos) && (highest == null || highest.getY() < mutableBlockPos.getY())) {
                    highest = mutableBlockPos.immutable();
                }
            }
        }
        return Optional.ofNullable(highest);
    }

    public static boolean isMinable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.is(ACTagRegistry.UNMOVEABLE) && !state.isAir() && !state.canBeReplaced();
    }

    public AABB getMiningBox() {
        return miningBox;
    }

    public boolean isComplete() {
        return complete;
    }

    public float getGrindRotation(float partialTicks) {
        return previousRotation + (rotation - previousRotation) * partialTicks;
    }
}
