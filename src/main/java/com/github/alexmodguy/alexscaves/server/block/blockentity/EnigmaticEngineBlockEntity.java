package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EnigmaticEngineBlockEntity extends BlockEntity {

    private int checkTime;

    public EnigmaticEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.ENIGMATIC_ENGINE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EnigmaticEngineBlockEntity entity) {
        if (entity.checkTime-- <= 0) {
            entity.checkTime = 30 + level.random.nextInt(30);
            entity.attemptAssembly();
        }
    }

    public boolean attemptAssembly() {
        Direction assembleIn = null;
        for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
            if (isAssembledInDirection(direction)) {
                assembleIn = direction;
                break;
            }
        }
        if (assembleIn != null) {
            for (BlockPos pos : BlockPos.betweenClosed(this.getBlockPos().getX() - 1, this.getBlockPos().getY() - 1, this.getBlockPos().getZ() - 1, this.getBlockPos().getX() + 1, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 1)) {
                if(level.getBlockState(pos).is(ACBlockRegistry.DEPTH_GLASS.get()) || level.getBlockState(pos).is(ACTagRegistry.SUBMARINE_ASSEMBLY_BLOCKS)  || level.getBlockState(pos).is(ACBlockRegistry.ENIGMATIC_ENGINE.get())){
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
            if(!level.isClientSide){
                SubmarineEntity submarine = ACEntityRegistry.SUBMARINE.get().create(level);
                Vec3 vec31 = Vec3.atCenterOf(this.getBlockPos()).add(0, -1, 0);
                submarine.setYRot(assembleIn.toYRot());
                submarine.setPos(vec31.x, vec31.y, vec31.z);
                submarine.setOxidizationLevel(0);
                level.addFreshEntity(submarine);
            }
            return true;
        }
        return false;
    }

    private boolean isAssembledInDirection(Direction direction) {
        List<BlockPos> windowPos = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                BlockPos at = getBlockPos().relative(direction).relative(direction.getClockWise(), i).above(j);
                if (level.getBlockState(at).is(ACBlockRegistry.DEPTH_GLASS.get())) {
                    windowPos.add(at);
                } else {
                    return false;
                }
            }
        }
        if (windowPos.size() == 6) {
            for (BlockPos pos : BlockPos.betweenClosed(this.getBlockPos().getX() - 1, this.getBlockPos().getY() - 1, this.getBlockPos().getZ() - 1, this.getBlockPos().getX() + 1, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 1)) {
                if (windowPos.contains(pos) || pos.equals(this.getBlockPos())) {
                    continue;
                } else if (!level.getBlockState(pos).is(ACTagRegistry.SUBMARINE_ASSEMBLY_BLOCKS)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
