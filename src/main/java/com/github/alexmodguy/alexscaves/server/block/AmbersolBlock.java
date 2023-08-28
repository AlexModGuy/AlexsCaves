package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.blockentity.AmbersolBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class AmbersolBlock extends BaseEntityBlock {

    public AmbersolBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_YELLOW).requiresCorrectToolForDrops().strength(3F, 10.0F).randomTicks().sound(ACSoundTypes.AMBER).lightLevel((i) -> 15).emissiveRendering((state, level, pos) -> true));
    }

    public static BlockPos fillWithLights(BlockPos current, LevelAccessor level) {
        current = current.below();
        while (current.getY() > level.getMinBuildHeight() && AmbersolLightBlock.testSkylight(level, level.getBlockState(current), current)) {
            if (level.getBlockState(current).isAir()) {
                level.setBlock(current, ACBlockRegistry.AMBERSOL_LIGHT.get().defaultBlockState(), 3);
            }
            current = current.below();
        }
        return current;
    }


    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        fillWithLights(blockPos, levelAccessor);
        return super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
    }

    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource randomSource) {
        fillWithLights(pos, serverLevel);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        fillWithLights(pos, level);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack stack) {
        fillWithLights(pos, level);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AmbersolBlockEntity(pos, state);
    }

}