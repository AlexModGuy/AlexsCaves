package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class CrushedBlockEntity  extends AbstractMovingBlockEntity {

    private static final EntityDataAccessor<Float> CRUSH_PROGRESS = SynchedEntityData.defineId(CrushedBlockEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DROP_CHANCE = SynchedEntityData.defineId(CrushedBlockEntity.class, EntityDataSerializers.FLOAT);
    private float prevCrushProgress = 0.0F;
    private float crushProgress = 0.0F;
    private boolean droppedItems = false;

    public CrushedBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public CrushedBlockEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.CRUSHED_BLOCK.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CRUSH_PROGRESS, 0F);
        this.entityData.define(DROP_CHANCE, 1.0F);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.crushProgress = compound.getFloat("CrushProgress");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("CrushProgress", this.crushProgress);
    }

    public void tick() {
        super.tick();
        prevCrushProgress = this.crushProgress;
        if (this.crushProgress >= 1.0F) {
            BlockPos pos = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
            if (!level().isClientSide && !droppedItems) {
                for (MovingBlockData dataBlock : this.getData()) {
                    BlockPos offset = dataBlock.getOffset();
                    if (level().random.nextFloat() < this.getDropChance()) {
                        BlockPos dropAtPos = pos.offset(offset);
                        while (level().getBlockState(dropAtPos).isAir() && dropAtPos.getY() > level().getMinBuildHeight()) {
                            dropAtPos = dropAtPos.below();
                        }
                        createBlockDropAt(dropAtPos.above(), dataBlock.getState(), dataBlock.blockData);
                    }
                }
                droppedItems = true;
            }
            this.remove(RemovalReason.DISCARDED);
        }
        if(this.onGround()){
            this.crushProgress = Math.min(this.crushProgress + 0.334F, 1F);
        }else{
            this.setDeltaMovement(this.getDeltaMovement().add(0, -1F, 0));
        }
    }

    public float getCrushProgress(float partialTick) {
        return prevCrushProgress + (crushProgress - prevCrushProgress) * partialTick;
    }

    protected float getDropChance() {
        return this.entityData.get(DROP_CHANCE);
    }

    public void setDropChance(float f) {
        this.entityData.set(DROP_CHANCE, f);
    }

    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean movesEntities() {
        return false;
    }

}
