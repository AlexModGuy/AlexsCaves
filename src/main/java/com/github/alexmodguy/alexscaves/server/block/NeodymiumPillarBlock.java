package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class NeodymiumPillarBlock extends Block {
    private boolean azure = false;
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public NeodymiumPillarBlock(boolean azure) {
        super(Properties.of().mapColor(DyeColor.WHITE).requiresCorrectToolForDrops().strength(2F, 6.0F).sound(ACSoundTypes.NEODYMIUM).lightLevel((i) -> 2).emissiveRendering((state, level, pos) -> true));
        this.registerDefaultState(this.defaultBlockState().setValue(TOP, Boolean.valueOf(true)).setValue(FACING, Direction.UP));
        this.azure = azure;
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        BlockState pillar = super.updateShape(state, direction, state1, levelAccessor, blockPos, blockPos1);
        if (levelAccessor.getBlockState(blockPos.relative(state.getValue(FACING))).getBlock() == getBlock()) {
            pillar = pillar.setValue(TOP, false);
        } else {
            pillar = pillar.setValue(TOP, true);
        }
        return pillar;
    }

    public Block getBlock() {
        return azure ? ACBlockRegistry.AZURE_NEODYMIUM_PILLAR.get() : ACBlockRegistry.SCARLET_NEODYMIUM_PILLAR.get();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState above = levelaccessor.getBlockState(blockpos.relative(context.getClickedFace()));
        return this.defaultBlockState().setValue(TOP, above.getBlock() != getBlock()).setValue(FACING, context.getClickedFace());
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(TOP, FACING);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        Vec3 center = Vec3.atCenterOf(pos);
        if (randomSource.nextInt(5) == 0) {
            level.addParticle(azure ? ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get() : ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(), center.x, center.y, center.z, center.x, center.y, center.z);
        }
    }
}
