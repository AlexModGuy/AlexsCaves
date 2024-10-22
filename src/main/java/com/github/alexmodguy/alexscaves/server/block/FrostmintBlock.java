package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingFrostmintEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingGuanoEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.FrostmintExplosion;
import com.github.alexmodguy.alexscaves.server.entity.util.MineExplosion;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class FrostmintBlock extends SlabBlock implements Fallable {
    public FrostmintBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(1.0F, 1.5F).sound(SoundType.STONE).instrument(NoteBlockInstrument.BASS));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        levelAccessor.scheduleTick(pos, this, this.getDelayAfterPlace());
        return super.updateShape(blockState, direction, blockState1, levelAccessor, pos, pos1);
    }

    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        while(level.getBlockState(blockPos.below()).is(this) && blockPos.getY() > level.getMinBuildHeight()){
            blockPos = blockPos.below();
        }
        level.scheduleTick(blockPos, this, this.getDelayAfterPlace());

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if ((isFree(level.getBlockState(blockPos.below())) || state.getValue(TYPE) == SlabType.TOP) && blockPos.getY() >= level.getMinBuildHeight()) {
            FallingFrostmintEntity.fall(level, blockPos, state);
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState blockState, boolean b) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    public static boolean isFree(BlockState belowState) {
        if (belowState.getBlock() instanceof FrostmintBlock && belowState.getValue(TYPE) == SlabType.BOTTOM) {
            return true;
        }
        return FallingBlock.isFree(belowState);
    }

    public void onBrokenAfterFall(Level level, BlockPos fallenOn, FallingBlockEntity fallingBlockEntity) {
    }

    public void onLand(Level level, BlockPos blockPos, BlockState blockState, BlockState blockState1, FallingBlockEntity fallingBlockEntity) {
        if(blockState1.getFluidState().getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get() && !level.isClientSide){
            FrostmintExplosion explosion = new FrostmintExplosion(level, fallingBlockEntity, blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F, 4.0F, Explosion.BlockInteraction.DESTROY_WITH_DECAY, false);
            explosion.explode();
            explosion.finalizeExplosion(true);
            for (Player player : level.getEntitiesOfClass(Player.class, new AABB(blockPos.offset(-32, -16, -32), blockPos.offset(32, 16, 32)))) {
                ACAdvancementTriggerRegistry.FROSTMINT_EXPLOSION.triggerForEntity(player);
            }
        }
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
    }

}
