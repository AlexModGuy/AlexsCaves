package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MuckBlock extends FallingBlockWithColor {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public MuckBlock(Properties props) {
        super(props, 0X5C5C6E);
    }

    public void entityInside(BlockState state, Level level, BlockPos blockPos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getFeetBlockState().is(this)) {
            if (isOceanEntity(entity)) {
                entity.setPos(entity.getX(), blockPos.getY() + 1.0F, entity.getZ());
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.1D, 0.0D));
            } else {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.85D, 1.0D, 0.85D));
            }
        }
    }

    private boolean isOceanEntity(Entity entity) {
        return entity.getType().is(ACTagRegistry.SEAFLOOR_DENIZENS) || entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.FEET).is(ACItemRegistry.DIVING_BOOTS.get());
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() != null && (isOceanEntity(entityCollisionContext.getEntity()) || entityCollisionContext.getEntity() instanceof FallingBlockEntity) ? Shapes.block() : SHAPE;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos blockPos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.block();
    }

    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos blockPos) {
        return 0.2F;
    }

}
