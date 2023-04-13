package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.server.block.CopperValveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CopperValveBlockEntity extends BlockEntity {

    private boolean movingDown;
    private float downProgress;
    private float prevDownProgress;
    public CopperValveBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.COPPER_VALVE.get(), pos, state);
        if(state.getValue(CopperValveBlock.TURNED)){
            movingDown = true;
            prevDownProgress = downProgress = 10.0F;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CopperValveBlockEntity entity) {
        entity.prevDownProgress = entity.downProgress;
        if (entity.movingDown && entity.downProgress < 10.0F) {
            entity.downProgress += 0.5F;
        } else if (!entity.movingDown && entity.downProgress > 0.0F) {
            entity.downProgress -= 0.5F;
        }
        if(entity.movingDown && entity.downProgress >= 10.0F && !state.getValue(CopperValveBlock.TURNED)){
            level.setBlockAndUpdate(blockPos, state.setValue(CopperValveBlock.TURNED, true));
            level.updateNeighborsAt(blockPos, state.getBlock());
            level.updateNeighborsAt(blockPos.relative(state.getValue(CopperValveBlock.FACING).getOpposite()), state.getBlock());
        }
        if((!entity.movingDown || entity.downProgress < 10.0F) && state.getValue(CopperValveBlock.TURNED)){
            level.setBlockAndUpdate(blockPos, state.setValue(CopperValveBlock.TURNED, false));
            level.updateNeighborsAt(blockPos, state.getBlock());
            level.updateNeighborsAt(blockPos.relative(state.getValue(CopperValveBlock.FACING).getOpposite()), state.getBlock());
        }
    }

    public void moveDown(boolean in){
        this.movingDown = in;
    }

    public boolean isMovingDown(){
        return this.movingDown;
    }

    public float getDownAmount(float partialTicks) {
        return (prevDownProgress + (downProgress - prevDownProgress) * partialTicks) * 0.1F;
    }
}
