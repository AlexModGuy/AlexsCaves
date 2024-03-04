package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.network.PlayMessages;

public class FallingTreeBlockEntity extends AbstractMovingBlockEntity {

    private static final EntityDataAccessor<Direction> FALL_DIRECTION = SynchedEntityData.defineId(FallingTreeBlockEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Float> FALL_PROGRESS = SynchedEntityData.defineId(FallingTreeBlockEntity.class, EntityDataSerializers.FLOAT);
    private float prevFallProgress = 0.0F;

    private boolean droppedItems = false;

    public FallingTreeBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public FallingTreeBlockEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.FALLING_TREE_BLOCK.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FALL_DIRECTION, Direction.NORTH);
        this.entityData.define(FALL_PROGRESS, 0F);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFallDirection(Direction.from3DDataValue(compound.getByte("FallDirection")));
        this.setFallProgress(compound.getFloat("FallProgress"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("FallDirection", (byte) this.getFallDirection().get3DDataValue());
        compound.putFloat("FallProgress", 0F);
    }

    public void tick() {
        super.tick();
        prevFallProgress = this.getFallProgress();
        if (this.getFallProgress() >= 1.0F) {
            BlockPos pos = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
            if (!level().isClientSide && !droppedItems) {
                for (MovingBlockData dataBlock : this.getData()) {
                    BlockPos offset = dataBlock.getOffset();
                    if (!offset.equals(BlockPos.ZERO)) {
                        BlockPos rotatedOffset = new BlockPos(offset.getX(), offset.getZ(), -offset.getY()).rotate(getRotationFromDirection(this.getFallDirection()));
                        BlockPos fallPos = pos.offset(rotatedOffset);
                        while (level().getBlockState(fallPos).isAir() && fallPos.getY() > level().getMinBuildHeight()) {
                            fallPos = fallPos.below();
                        }
                        createBlockDropAt(fallPos.above(), dataBlock.getState(), dataBlock.blockData);
                    }
                }
                droppedItems = true;
            }
            this.remove(RemovalReason.DISCARDED);
        }
        this.setFallProgress(Math.min(this.getFallProgress() + 0.05F, 1F));
    }

    public Rotation getRotationFromDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return Rotation.NONE;
            case EAST:
                return Rotation.CLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
        }
        return Rotation.NONE;
    }

    public Direction getFallDirection() {
        return this.entityData.get(FALL_DIRECTION);
    }

    public void setFallDirection(Direction direction) {
        this.entityData.set(FALL_DIRECTION, direction);
    }

    protected float getFallProgress() {
        return this.entityData.get(FALL_PROGRESS);
    }

    public float getFallProgress(float partialTick) {
        return prevFallProgress + (getFallProgress() - prevFallProgress) * partialTick;
    }

    public void setFallProgress(float f) {
        this.entityData.set(FALL_PROGRESS, f);
    }

    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean movesEntities() {
        return false;
    }
}